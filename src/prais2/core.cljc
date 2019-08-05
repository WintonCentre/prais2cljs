(ns ^:figwheel-always prais2.core
  #?(:cljs (:require-macros [cljs.core.async.macros :refer [go-loop]]))
  (:require
    [rum.core]
    [clojure.string :as str]
    #?(:cljs [cljsjs.jquery])
    #?(:cljs [cljsjs.bootstrap])
    #?(:cljs [secretary.core :as secretary])
    #?(:cljs [cljs.core.async :refer [chan <! pub put!]])
    [prais2.utils :refer [key-with]]
    #?(:cljs [goog.events :as events])
    #?(:cljs [goog.dom :as dom])
    #?(:cljs [goog.dom.query])
    ))

;;;
;; js interop
;;;
#?(:cljs
   (extend-type js/NodeList
     ISeqable
     (-seq [array] (array-seq array 0)))

   )

;;;
;; define app state once so it doesn't re-initialise on reload.
;; figwheel counter is a placeholder for any state affected by figwheel live reload events
;;;
(def app (atom {:datasource               :2018
                #_#_:datasource               :2016
                :pull-out                 false
                :page                     :home
                :section                  nil
                :sort-by                  nil
                :sort-ascending           true
                :slider-axis-value        1.0
                :detail-slider-axis-value 1.0
                :chart-state              3
                :theme                    12
                :selected-h-code          nil
                :map-h-code               nil
                :show-nicor               false
                :need-a-push              false}))

;;
;; routing prefix
;;
;;(def prefix "#")                                            ;for hash fragment routing
(def prefix "/")                                            ; for hash-free routing


(defn token->url [token]
  (if (= prefix "#")
    (str "/" prefix "/" token)
    (if (= token "home") "/" (str "/" token))))             ; :home-edit


;;;
;; window location to token:
;;;
#?(:cljs
   (defn get-token! []
     (if (= prefix "#")
       (do
         (str/replace-first (.-hash (.-location js/window)) "#/" ""))
       (.-pathname (.-location js/window)))))


;;;
;; Define an event bus carrying [topic message]
;; publication channels are based on topic - the first part of the data
;;;
#?(:cljs (def event-bus (chan)))
#?(:cljs (def event-bus-pub (pub event-bus first)))

;;;
;; generic click handler
;; we may need to add some touch handling here too. Probably enough to stopPropagation from touch-start to click
;;;
#?(:cljs (defn click->event-bus
           [event dispatch-key dispatch-value token]
           (when token
             (do
               ;(prn (str "pushing " token))
               ; Filter this for URL-only events?
               ;; No.
               ;; If we are here, then we are routing on an internal click
               ;; rather than a back/forward navigation.
               ;; So it is safe to push.
               ;;
               (.pushState js/history [] token (token->url token))))
           #_(when-not token
             (prn "NIL TOKEN SENT TO core/click-event-bus")
             )
           (.preventDefault event)
           (.stopPropagation event)
           ;(prn (str "click->event-bus " dispatch-key " " dispatch-value))
           (put! event-bus [dispatch-key dispatch-value])

           ))
#?(:clj (defn click->event-bus [_ _ _ _]
          nil))


;:todo: inject this or generate programmatically
(def token-dispatch-table
  {"home"           [:home nil]
   "intro"          [:intro nil]
   "data"           [:data nil]
   "data/map"       [:data :map]
   "data/table"     [:data :table]
   "data/animation" [:data :animation]
   "faqs"           [:faq nil]
   "faq/0"          [:faq [0]]
   "faq/0/0"        [:faq [0 0]]
   "faq/0/1"        [:faq [0 1]]
   "faq/0/2"        [:faq [0 2]]
   "faq/0/3"        [:faq [0 3]]
   "faq/1"          [:faq [1]]
   "faq/1/0"        [:faq [1 0]]
   "faq/1/1"        [:faq [1 1]]
   "faq/1/2"        [:faq [1 2]]
   "faq/1/3"        [:faq [1 3]]
   "faq/1/4"        [:faq [1 4]]
   "faq/1/5"        [:faq [1 5]]
   "faq/1/6"        [:faq [1 6]]
   "faq/2"          [:faq [2]]
   "faq/2/0"        [:faq [2 0]]
   "faq/2/1"        [:faq [2 1]]
   "faq/2/2"        [:faq [2 2]]
   "faq/2/3"        [:faq [2 3]]
   "faq/2/4"        [:faq [2 4]]
   "faq/3"          [:faq [3]]
   "faq/3/0"        [:faq [3 0]]
   "faq/3/1"        [:faq [3 1]]
   "faq/3/2"        [:faq [3 2]]
   "faq/4"          [:faq [4]]
   "faq/4/0"        [:faq [4 0]]
   "faq/4/1"        [:faq [4 1]]
   "faq/4/2"        [:faq [4 2]]
   "faq/4/3"        [:faq [4 3]]
   "faq/5"          [:faq [5]]
   "faq/5/0"        [:faq [5 0]]
   "faq/5/1"        [:faq [5 1]]})

;;
;; url handling
;;
#?(:cljs
   (defn internal-link-handler [token before-hook]
     (fn [event]
       ;(prn (str "internal-link-handler " token))
       (when before-hook (before-hook))
       (let [[dispatch-key dispatch-value] (token-dispatch-table token)]
         (click->event-bus event dispatch-key dispatch-value token)))
     ))

#?(:cljs
   (defn irl "internal resource locator"
     ([fragment]
      (irl fragment false))

     ([fragment static]
       ;; home should not appear in an actual URL
      (let [frag (if (= fragment "home") "" fragment)]
        (if static
          (str "/" frag)
          (if (= prefix "#")
            (str "/#/" frag)
            (str "/" frag)))))))

#?(:clj
   (defn irl "internal resource locator"
     ([fragment _]
      (str "/" (if (= fragment "home") "" fragment)))

     ([fragment]
      (irl fragment true))
     ))

(defn absolute-path? [path]
  (cond
    (= (first path) "/") true
    (and (>= (count path) 4) (= (subs path 0 4) "http")) true
    :else false))

(defn isrc
  ([path] {:src (if (absolute-path? path) path (str "/" path))})
  ;;(core/isrc "assets/foo.png")
  ;;=> {:src "/assets/foo.png"}

  ([path attrs] (merge (isrc path) attrs))
  ;;(core/isrc "/assets/foo.png" {:style {:color "red"}})
  ;;=> {:src "/assets/foo.png", :style {:color "red"}}

  ([path key value & key-values]
   (merge (isrc path) (hash-map key value) (apply hash-map key-values)))
  ;;(core/isrc "/assets/foo.png" :style {:color "red"})
  ;;=> {:src "/assets/foo.png", :style {:color "red"}}
  ;;
  ;;(core/isrc "/assets/foo.png" :style {:color "red"} :width 3)
  ;;=> {:src "/assets/foo.png", :style {:color "red"}, :width 3}
  )

(defn internal-ref
  "add in local handler for an internal token"
  ([token close-modal]
   (merge {:href (irl token)}
          #?(:cljs {:on-click       (internal-link-handler token close-modal)
                    :on-touch-start (internal-link-handler token close-modal)})))
  ([token]
   (internal-ref token nil)))

(defn href

  ([path]
   (if (absolute-path? path)
     {:href path}
     (internal-ref path)))
  ;;(href "faq/1/2")
  ;;=> {:href "/#/faq/1/2"}

  ([path attrs]
   (if (map? attrs)
     (merge (href path) attrs)
     (internal-ref path attrs)))
  ;;(href "faq/1/2" {:style {:color "red"}})
  ;;=> {:href "/assets/foo.png", :style {:color "red"}}

  ([path key value & key-values]
   (merge (href path) (hash-map key value) (apply hash-map key-values)))
  ;;(core/isrc "/assets/foo.png" :style {:color "red"})
  ;;=> {:src "/assets/foo.png", :style {:color "red"}}
  ;;
  ;;(core/isrc "/assets/foo.png" :style {:color "red"} :width 3)
  ;;=> {:src "/assets/foo.png", :style {:color "red"}, :width 3}
  )


#?(:cljs
   (defn ready [handler]
     (.ready (js/$ js/document) handler)))


;;;
;; wraps raw content in a div and returns a rum react element
;;;
(rum.core/defc rum-wrap [& content]
  (apply conj [:div] content))

;; mixin to initialise bootstrap collapse code
#?(:cljs (def bs-collapse
           {:did-mount (fn [state]
                         (ready
                           #(.collapse (js/$ "[data-toggle=\"collapse\"]")))
                         state)
            }))
#?(:clj (def bs-collapse {}))

;; mixin to initialise bootstrap tooltip code
#?(:cljs (def bs-tooltip
           {:did-mount (fn [state]
                         (ready
                           #(.tooltip (.tooltip (js/$ "[data-toggle=\"tooltip\"]"))))
                         state)
            }))
#?(:clj (def bs-tooltip {}))

;; mixin to initialise bootstrap t code code
#?(:cljs (def bs-popover
           {:did-mount (fn [state]
                         (ready
                           #(.popover (js/$ "[data-toggle=\"popover\"]")))
                         state)
            }))
#?(:clj (def bs-popover {}))

;; mixin to monitor react state changes
(defn monitor-react
  ([label]
   {
    :did-mount    #(do (prn (str label " did-mount " %1)) %1)
    :will-unmount #(do (prn (str label " will-unmount " %1)) %1)
    })
  ([label enabled]
   {
    :did-mount    #(do (if enabled (prn (str label " did-mount " %1))) %1)
    :will-unmount #(do (if enabled (prn (str label " will-unmount " %1))) %1)
    }))


;; get element by id
#?(:cljs (defn el [id] (.getElementById js/document id)))

;;
;; change page title mixin
;;
(def title-prefix "Child Heart Surgery: ")

#?(:cljs (defn meta-description []
           (first (filter #(= (.-name %) "description") (goog.dom.query "meta")))))

(defn update-title [title-postfix]
  {:did-mount #?(:cljs (fn [state]
                         (set! (.-title js/document)
                               (str title-prefix
                                    (if (string? title-postfix)
                                      title-postfix
                                      (title-postfix state))))

                         state)
                 :clj  identity)})
;;
;; change page metadata mixin
;;
(defn update-description [description]
  {:did-mount #?(:cljs (fn [state]
                         (set! (.-content (meta-description))
                               (if (string? description)
                                 description
                                 (description state)))
                         state)
                 :clj  identity)})