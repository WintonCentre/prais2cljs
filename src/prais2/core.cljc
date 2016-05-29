(ns ^:figwheel-always prais2.core
  #?(:cljs (:require-macros [cljs.core.async.macros :refer [go-loop]]))
  (:require
    [rum.core :as rum]
    [clojure.string :as str]
    #?(:cljs [cljsjs.jquery])
    #?(:cljs [cljsjs.bootstrap])
    #?(:cljs [secretary.core :as secretary])
    #?(:cljs [cljs.core.async :refer [chan <! pub put!]])
    [prais2.utils :as u :refer [key-with]]
    #?(:cljs [goog.events :as events])
    ))


;;;
;; define app state once so it doesn't re-initialise on reload.
;; figwheel counter is a placeholder for any state affected by figwheel live reload events
;;;
(defonce app (atom {:datasource               :2014
                    :pull-out                 false
                    :page                     :home
                    :section                  :map
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

;;;
;; Define an event bus carrying [topic message]
;; publication channels are based on topic - the first part of the data
;;;
#?(:cljs (def event-bus (chan)))
#?(:cljs (def event-bus-pub (pub event-bus first)))

(defn ensure-str [maybe-key]
  (if (keyword? maybe-key)
    (name maybe-key)
    (str maybe-key)))


#_(defn make-token [dispatch-key dispatch-value]
  (str "/#/" (ensure-str dispatch-key)
       (if (not= :top dispatch-value)
         (str "/" (ensure-str dispatch-value))
         "")))

;;;
;; generic click handler
;; we may need to add some touch handling here too. Probably enough to stopPropagation from touch-start to click
;;;
#?(:cljs (defn click->event-bus
           [event dispatch-key dispatch-value token]
           ;let [token (make-token dispatch-key dispatch-value)]
           (when token
             (do
               (prn (str "pushing " token))
               ; :todo: filter this for URL-only events?
               (.pushState js/history [] token (str "/#/" token))))
           (.preventDefault event)
           (.stopPropagation event)
           (if (and (= dispatch-key :data) (= dispatch-value :top))
             (prn "gotcha")
             (prn (str "click->event-bus " dispatch-key " " dispatch-value)))
           (put! event-bus [dispatch-key dispatch-value])
           ))
#?(:clj (defn click->event-bus
          [event dispatch-key dispatch-value token]
          nil))

;;
;; :todo convert this to a safe edn based reader
;;

;(def dispatch-keys #{"homes" "home" "intros" "intro" "data" "faqs" "faq"})
(def dispatch-0-keys #{"home" "intro" "data" "faqs"})
(def dispatch-1-keys #{"data"})
(def dispatch-2-keys #{"faq"})

(defn validate
  ([k]
   (if (dispatch-0-keys k) [k nil] nil))

  ([k v1]
   (if (dispatch-1-keys k)
     (if (or (= v1 "map") (= v1 "table") (= v1 "animation") [k v1])
       [k v1]
       [k nil])
     nil))

  ([k v1 v2]
    ))

#?(:cljs
   (defn fragment->key-values
     "fragments look like frag1 or frag1/frag2 or frag1/frag2/frag3"
     [fragment]
     (let [[k v1 v2] (filter #(not= % "/") (str/split fragment #"/"))]
       [(validate k) (validate k v1) (validate k v2)]
       )))

;;
;; url handling
;;
#?(:cljs
   (defn internal-link-handler [fragment]
     (fn [event]
       ; (.preventDefault event)
       ; :todo: route this dispatch through the event bus
       (prn (str "internal-link-handler " fragment))
       (let [[dispatch-key dispatch-value] (fragment->key-values fragment)]
         (click->event-bus event dispatch-key dispatch-value fragment)))
     ))

#?(:cljs
   (defn irl "internal resource locator"
     ([fragment]
      (irl fragment false))

     ([fragment static]
      (if static
        (str "/" fragment ".html")
        (str "/#/" fragment)))))                            ;; :todo remove #/ when hashbangs go

#?(:clj
   (defn irl "internal resource locator"
     ([fragment]
      (irl fragment true))

     ([fragment static]
      (str "/" fragment ".html"))))

(defn absolute-path? [path]
  (cond
    (= (subs path 0 4) "http") true
    (= (first path) "/") true
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
  [path]
  (merge {:href (irl path)}
         #?(:cljs {:on-click (internal-link-handler path)})))

(defn href

  ([path]
   (if (absolute-path? path)
     {:href path}
     (internal-ref path)))
  ;;(href "faq/1/2")
  ;;=> {:href "/#/faq/1/2"}

  ([path attrs] (merge (href path) attrs))
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
(rum/defc rum-wrap [& content]
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
