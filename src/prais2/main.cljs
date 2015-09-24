(ns ^:figwheel-always prais2.main
    (:require-macros [jayq.macros :refer [ready]]
                     [cljs.core.async.macros :refer [go-loop]])
    (:require [rum :as r]
              [cljs.reader :as reader]
              [clojure.set :refer (intersection)]
              [cljsjs.react]
              [cljs.core.async :refer [chan <! pub sub]]
              [prais2.core :as core]
              [prais2.routes]
              [prais2.content :as content]
              [prais2.data :as data]
              [jayq.core :refer ($)])
    )



(enable-console-print!)

(defn el [id] (.getElementById js/document id))


(defn select
  "Return the first matching DOM element selected by the CSS selector. "
  [selector]
  (.querySelector js/document selector))

(defn selectAll
  "Returns a NodeList object containing all matching DOM elements."
  [selector]
  (.querySelectorAll js/document selector))


;;;
;;  "debug app-state"
;;;
(r/defc debug < r/reactive []
  [:div
   [:p (str (r/react core/app))]]
  )

;;;
;; Define an event bus carrying [topic message] data
;; publication channels are based on topic - the first part of the data
;;;
(def event-bus (chan))
(def event-bus-pub (pub event-bus first))

(r/defc para < r/static [text]
  [:p text])

;; mixin to initialise bootstrap popover code
(def bs-popover
  {:did-mount (fn [state]
                (ready
                 (.popover ($ "[data-toggle=\"popover\"]")))
                state)
   })

;;
;; Contains the app user interface in here
;;
(r/defc app-container < bs-popover r/reactive []
  [:.box
   (map-indexed
    #(r/with-key %2 %1)
    [(data/modal)
     (data/table1 core/app content/table1-data event-bus)
     (data/option-controls event-bus)
     (para "")
     (debug)])])

;;
;; mount main component on html app element
;;
(r/mount (app-container) (el "app"))


;;;
;; Read events off the event bus and handle them
;;;
(defn dispatch
  "listen on a published event feed, handling events with the given key"
  [event-feed event-key handle]
  (let [in-chan (chan)]
    (sub event-feed event-key in-chan)
    (go-loop []
      (let [event (<! in-chan)]
        (handle event))
      (recur)))
  )

(defn dispatch-central
  "centralised dispatch of all events"
  []

  (dispatch event-bus-pub :slider-axis-value
            (fn [[_ slider-value]] (swap! core/app #(assoc % :slider-axis-value slider-value))))

  (dispatch event-bus-pub :sort-toggle
            (fn [[_ column-key]] (data/handle-sort core/app column-key)))

  (dispatch event-bus-pub :change-theme data/change-theme)

  (dispatch event-bus-pub :cycle-chart-state
            (fn [[_ direction]]
              (swap! core/app
                     #(assoc % :bars (data/cycle-chart-state (:bars @core/app) direction)))))

  )

(dispatch-central)

;;
;; optionally do something on app reload
;;
(defn on-js-reload []
  (prn "Hi")
)
