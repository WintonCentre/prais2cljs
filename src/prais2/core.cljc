(ns ^:figwheel-always prais2.core
    #?(:cljs (:require-macros [cljs.core.async.macros :refer [go-loop]]))
  (:require
    [rum.core :as rum]
    #?(:cljs [cljsjs.jquery])
    #?(:cljs [cljsjs.bootstrap])
    #?(:cljs [cljs.core.async :refer [chan <! pub put!]])
    [prais2.utils :as u :refer [key-with]]
    #?(:cljs [goog.events :as events])
            ))

;;;
;; define app state once so it doesn't re-initialise on reload.
;; figwheel counter is a placeholder for any state affected by figwheel live reload events
;;;

#?(:cljs
   (defn ready [handler]
     (.ready (js/$ js/document) handler)))

;;
;; todo: configure this?
;;
(defonce app (atom {:datasource :2014
                    :page :home
                    :section :map
                    :sort-by nil
                    :sort-ascending true
                    :slider-axis-value 1.0
                    :detail-slider-axis-value 1.0
                    :chart-state 3
                    :theme 12
                    :selected-h-code nil
                    :map-h-code nil
                    :show-nicor false}))

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
    :did-mount #(do (prn (str label " did-mount " %1)) %1)
    :will-unmount #(do (prn (str label " will-unmount " %1)) %1)
    })
  ([label enabled]
   {
    :did-mount #(do (if enabled (prn (str label " did-mount " %1))) %1)
    :will-unmount #(do (if enabled (prn (str label " will-unmount " %1))) %1)
    }))

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
           [event dispatch-key dispatch-value]
           (if (and (= dispatch-key :data) (= dispatch-value :top))
             (prn "gotcha")
             (prn (str "click->event-bus " dispatch-key dispatch-value)))
           (put! event-bus [dispatch-key dispatch-value])
           (.preventDefault event)
           (.stopPropagation event)
           ))
#?(:clj (defn click->event-bus
          [event dispatch-key dispatch-value]
          nil))

;; get element by id
#?(:cljs (defn el [id] (.getElementById js/document id)))