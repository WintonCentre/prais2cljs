(ns ^:figwheel-always prais2.data
  (:require
    ;[rum.core :as rum]
    [cljsjs.jquery]
    [cljsjs.bootstrap]
    [cljs.core.async :refer [put! <! timeout]]
    [prais2.core :as core :refer [event-bus bs-popover bs-tooltip]]
    [prais2.content :as content]
    [prais2.utils :refer [px pc important key-with]]
    )
  (:require-macros [cljs.core.async.macros :refer [go]])
  )

;;;
;; datasource utilities
;;;
(defn colour-map [theme] (content/colour-map-options theme))


(defn index-by
  "create an index on a table"
  [table key-fn]
  (into {} (map (juxt key-fn identity) table)))


(defn add-markers [table-rows]
  (map-indexed
    (fn [index row]
      (let [lat (+ 50.7 (/ index 3))
            lon (+ -2.6 (* 0.8 (mod index 3)))]
        (assoc row :h-lat lat :h-lon lon)))
    table-rows))


(defn make-datasource [datasource]
  (into []
        (concat [content/header-row]
                (if (number? (js/parseInt (name datasource)))
                  (datasource content/datasources)
                  (add-markers (datasource content/datasources))))))


(defn table-data
  "datasource switchable hospital results table"
  [datasource]
  (memoize (fn [] (make-datasource datasource))))


(defn rows-by-code
  "datasource switchable hospital results indexed by hospital code"
  [datasource]
  (memoize (fn [] (index-by ((table-data datasource)) #(keyword (:h-code %))))))


(defn sort-on-column
  "sort a column"
  [app column-key]
  (let [ap @app
        sort-column (:sort-by ap)
        sort-mode (:sort-ascending ap)]
    (if (= sort-column column-key)
      (swap! app #(assoc % :sort-ascending (not sort-mode)))
      (swap! app #(assoc % :sort-ascending true :sort-by column-key)))))


(defn handle-sort
  "handle sort click"
  [app column-key]
  (sort-on-column app column-key))


(def chart-width 100)


;;  "the minimum outer-low value across all rows"
(defn min-outer-low []
  (* 2 (int (/ (apply min (map :outer-low (rest ((table-data (:datasource @core/app)))))) 2))))


(defn bar-scale
  "value to pixel-width scale-factor controlled by slider in [0-1]"
  [slider]
  (/ chart-width (- 100 (* (min-outer-low) slider))))


(defn percent->screen
  "percent-value to slider compensated value"
  [slider value]
  (let [origin (* (min-outer-low) slider)]
    (* 100 (/ (- value origin) (- 100 origin)))))


(defn bar-width
  "return percentage-width for a bar"
  [slider value]
  (* value (bar-scale slider)))


;;;
;; graphic renders
;;;
(rum.core/defc bar < rum.core/static

  ([slider hi-val lo-val bar-type fill]

   [:div.bar {:style {:background-color fill
                      :width            (str (bar-width slider (- hi-val lo-val)) "%")}}]

   [:div.bar.btn {:style               {:background-color fill
                                        :border-radius    0
                                        :width            (str (bar-width slider (- hi-val lo-val)) "%")
                                        :text-align       "right"}
                  :data-toggle         "tooltip"
                  :data-original-title (str (pc lo-val) " - " (pc hi-val) "<br>" (bar-type content/bar-comments))
                  :data-delay          0
                  :data-html           true
                  :data-trigger        "hover"
                  :data-placement      "bottom"}])


  ([slider hi-val lo-val bar-type fill no-tip]
   (if (= fill "rgba(255,255,255,0)")
     [:div.bar {:style {:background-color fill
                        :width            (str (bar-width slider (- hi-val lo-val)) "%")}}]
     (if-not no-tip
       [:div.bar.btn {:style               {:background-color fill
                                            :border-radius    0
                                            :width            (str (bar-width slider (- hi-val lo-val)) "%")
                                            :text-align       "right"
                                            }
                      :data-toggle         "tooltip"
                      :data-original-title (str (pc lo-val) " - " (pc hi-val) "<br>" (bar-type content/bar-comments))
                      :data-delay          0
                      :data-html           true
                      :data-trigger        "hover"
                      :data-placement      "bottom"}]
       [:div.bar.btn {:style {:background-color fill
                              :border-radius    0
                              :width            (str (bar-width slider (- hi-val lo-val)) "%")
                              :text-align       "right"
                              }
                      }]))))


(rum.core/defc dot < rum.core/static rum.core/reactive bs-tooltip [slider size value dotty & [relative]]
  (let [px-size (px size)]
    [:div.dot.btn
     {:data-toggle         "tooltip"
      :data-placement      "bottom"
      :data-delay          0
      :data-trigger        "hover"
      :data-html           true
      :data-original-title (str (pc value) "<br>The survival rate")
      :style               {:background-color (:dot (colour-map (:theme (rum.core/react core/app))))
                            :display          (if dotty "inline-block" "none")
                            :width            px-size
                            :height           px-size
                            :top              (px (+ 10 (/ (- 25 size) 2)))
                            :position         (if relative "relative" "absolute")
                            :margin-left      (str "-" (Math/round (/ size 2)) "px")
                            :left             (str (percent->screen slider value) "%")
                            #_(str "calc("
                                   (percent->screen slider value)
                                   "% - "
                                   (Math/round (/ size 2))
                                   "px)")}}]))


(rum.core/defc dot-no-tip < rum.core/static rum.core/reactive [slider size value dotty & [relative]]
  (let [px-size (px size)]
    [:div.dot.btn
     {:style {:background-color (:dot (colour-map (:theme (rum.core/react core/app))))
              :display          (if dotty "inline-block" "none")
              :width            px-size
              :height           px-size
              :top              (px (+ 10 (/ (- 25 size) 2)))
              :position         (if relative "relative" "absolute")
              :left             (str "calc("
                                     (percent->screen slider value)
                                     "% - "
                                     (Math/round (/ size 2))
                                     "px)"
                                     )}}]))


(def extra-right 40)

(def last-pad-right (important (px extra-right)))

(defn dot-size [slider]
  (Math/round (- 12 (* 7 (- 1 slider)))))


(def chart-states [#{} #{:dot} #{:inner :dot} #{:inner :outer :dot} #{:inner :outer} #{:inner}])


(rum.core/defc chart-cell < bs-tooltip rum.core/reactive [row slider]
  (let [ap (rum.core/react core/app)
        colours (colour-map (:theme ap))
        bars (chart-states (:chart-state ap))
        dotty (:dot bars)
        dotless (disj bars :dot)]
    [:.chart-cell
     [:div.bar-chart
      (map-indexed key-with

                   (cond
                     (= dotless #{})
                     [(dot slider (dot-size slider) (:survival-rate row) dotty)]

                     (= dotless #{:inner})
                     [(bar slider (:outer-low row) (* (min-outer-low) slider) :low (:low colours))
                      (bar slider (:inner-low row) (:outer-low row) :outer-low (:low colours))
                      (bar slider (:inner-high row) (:inner-low row) :inner (:inner colours))
                      (bar slider (:outer-high row) (:inner-high row) :outer-high (:high colours))
                      (bar slider 100 (:outer-high row) :high (:high colours))
                      (dot slider (dot-size slider) (:survival-rate row) dotty)
                      ]

                     (= dotless #{:outer})
                     [(bar slider (:outer-low row) (* (min-outer-low) slider) :low (:low colours))
                      (bar slider (:inner-low row) (:outer-low row) :outer-low (:outer-low colours))
                      (bar slider (:inner-high row) (:inner-low row) :inner (:outer-low colours))
                      (bar slider (:outer-high row) (:inner-high row) :outer-high (:outer-high colours))
                      (bar slider 100 (:outer-high row) :high (:high colours))
                      (dot slider (dot-size slider) (:survival-rate row) dotty)
                      ]

                     (= dotless #{:inner :outer})
                     [(bar slider (:outer-low row) (* (min-outer-low) slider) :low (:low colours))
                      (bar slider (:inner-low row) (:outer-low row) :outer-low (:outer-low colours))
                      (bar slider (:inner-high row) (:inner-low row) :inner (:inner colours))
                      (bar slider (:outer-high row) (:inner-high row) :outer-high (:outer-high colours))
                      (bar slider 100 (:outer-high row) :high (:high colours))
                      (dot slider (dot-size slider) (:survival-rate row) dotty)
                      ]))]]))


(rum.core/defc annotated-chart-cell < bs-tooltip rum.core/reactive [row slider bars text]
  (let [ap (rum.core/react core/app)
        colours (colour-map (:theme ap))
        dotty (:dot bars)
        dotless (disj bars :dot)]
    [:div
     [:.annotation {:key 1} text]
     [:.chart-cell {:key 2}
      [:div.bar-chart
       (map-indexed key-with

                    (cond
                      (= dotless #{})

                      [(bar slider (:outer-low row) (* (min-outer-low) slider) :low "#ffffff" true)
                       (dot-no-tip slider (dot-size slider) (:survival-rate row) dotty)]

                      (= dotless #{:inner})
                      [(bar slider (:outer-low row) (* (min-outer-low) slider) :low "#ffffff" true)
                       (bar slider (:inner-low row) (:outer-low row) :outer-low "#ffffff" true)
                       (bar slider (:inner-high row) (:inner-low row) :inner (:inner colours) true)
                       (bar slider (:outer-high row) (:inner-high row) :outer-high "#ffffff" true)
                       (bar slider 100 (:outer-high row) :high "#ffffff" true)
                       (dot-no-tip slider (dot-size slider) (:survival-rate row) dotty)
                       ]

                      (= dotless #{:outer})
                      [(bar slider (:outer-low row) (* (min-outer-low) slider) :low "#ffffff" true)
                       (bar slider (:inner-low row) (:outer-low row) :outer-low (:outer-low colours) true)
                       (bar slider (:inner-high row) (:inner-low row) :inner (:outer-low colours) true)
                       (bar slider (:outer-high row) (:inner-high row) :outer-high (:outer-high colours) true)
                       (bar slider 100 (:outer-high row) :high "#ffffff" true)
                       (dot-no-tip slider (dot-size slider) (:survival-rate row) dotty)
                       ]

                      (= dotless #{:inner :outer})
                      [(bar slider (:outer-low row) (* (min-outer-low) slider) :low "#ffffff" true)
                       (bar slider (:inner-low row) (:outer-low row) :outer-low (:outer-low colours) true)
                       (bar slider (:inner-high row) (:inner-low row) :inner (:inner colours) true)
                       (bar slider (:outer-high row) (:inner-high row) :outer-high (:outer-high colours) true)
                       (bar slider 100 (:outer-high row) :high "#ffffff" true)
                       (dot-no-tip slider (dot-size slider) (:survival-rate row) dotty true)
                       ]))]]]))


(rum.core/defc tick < rum.core/static [baseline value tick-height]
  (let [percent (* 100 (/ (- value baseline) (- 100 baseline)))]
    (when (>= percent 0)
      [:.tick {:style
               {:left        (pc percent)
                :height      (px tick-height)
                :border-left (str "1px "
                                  (if (or (= percent 100) (= value 0))
                                    "solid "
                                    "dashed ")
                                  "black")}}
       [:span.tick-label (pc value)]])))


(rum.core/defc ticks < rum.core/static [slider-axis-value tick-count tick-height]
  (let [baseline (* (min-outer-low) slider-axis-value)
        raw-interval (/ (- 100 baseline) (inc tick-count))
        interval (cond
                   (> raw-interval 10) 20
                   (> raw-interval 5) 10
                   (> raw-interval 2) 5
                   :else 2)
        tick-values (range 100 (dec baseline) (- interval))]
    [:div
     (for [value tick-values]
       (rum.core/with-key (tick baseline value tick-height) value))]))


(rum.core/defc slider-labels []
  [:.slider-label
   [:div.pull-left {:key :left}
    [:i.fa.fa-long-arrow-left {:key :full}] " full view"]
   [:div.pull-right {:key :right}
    "full detail " [:i.fa.fa-long-arrow-right {:key :detail}]]])


;; returns a bootstrap slider mixin
(defn bs-slider [hashed-id change-key]
  {:did-mount      (fn [state]
                     (let [slider (new js/Slider hashed-id
                                       (clj->js {
                                                 :tooltip "hide"
                                                 :min     0
                                                 :max     1
                                                 :step    0.02
                                                 :value   (change-key @core/app)
                                                 }))
                           handler #(put! event-bus [change-key (.getValue slider)])]
                       (.on slider "change" handler)
                       (assoc state ::slider slider ::handler handler)))

   :transfer-state (fn [old new]
                     (assoc new ::slider (::slider old)))

   :will-unmount   (fn [state]
                     (let [slider (::slider state)
                           handler (::handler state)]
                       (when slider
                         (when handler (.off slider "change" handler))
                         (.destroy slider))
                       (dissoc state ::slider ::handler)))})


(rum.core/defcs slider-control < (bs-slider "#slider" :slider-axis-value)
                                 rum.core/static
  [state value]
  (let [s [:#slider.slider
           [:input {:type "text" :value value}]]
        slider (::slider state)]
    (when slider
      (.setValue slider value))
    s))


(rum.core/defcs detail-slider-control < (bs-slider "#detail-slider" :detail-slider-axis-value) rum.core/static [state value]
  #_(prn "called detail-slider-control with " value)
  (let [s [:#detail-slider.slider
           [:input {:type "text" :value value}]]
        slider (::slider state)]
    (when slider
      (.setValue slider value))
    s))

;;;
;; Note: multiple sliders on the same page will fail
;;;
(rum.core/defcs map-detail-slider-control < (bs-slider "#map-detail-slider" :detail-slider-axis-value) rum.core/static [state value]
  #_(prn "called map-detail-slider-control with " value)
  (let [s [:#map-detail-slider.slider
           [:input {:type "text" :value value}]]
        slider (::slider state)]
    (when slider
      (.setValue slider value))
    s))


(rum.core/defc axis-container < rum.core/static
  ([slider-axis-value]
   (axis-container slider-axis-value 69))

  ([slider-axis-value tick-height]
   [:.axis-container
    (rum.core/with-key (ticks slider-axis-value 3 tick-height) :ticks)]))


(rum.core/defc slider-title [headers]
  [:p {:key :p}
   (if (string? headers) headers (:title (:observed headers)))])


(rum.core/defc table-header < rum.core/static bs-popover [background ap header column-key]
  [:th {:on-click       #(when (:sortable header) (core/click->event-bus % :sort-toggle column-key nil))
        :on-touch-start #(when (:sortable header) (core/click->event-bus % :sort-toggle column-key nil))
        :style          {:max-width        (px (:width header))
                         :white-space      "normal !important"
                         :vertical-align   "top"
                         :cursor           (if (:sortable header) "pointer" "auto")
                         :background-color background
                         :color            "#ffffff !important"}}

   (when (:sortable header) [:i {:key   :icon
                                 :class (str "right fa fa-sort"
                                             (if (= column-key (:sort-by ap))
                                               (if (:sort-ascending ap) "-asc" "-desc") ""))
                                 :style {:pointer-events "none"
                                         :zoom           1.5
                                         :color          "#ffffff"}}])
   [:span {:key   :text
           :style {:background-color "none !important"
                   :color            "white !important"}}
    (when (not= column-key :h-name)
      (let [info-handler #(core/click->event-bus % :info-clicked column-key nil)]
        [:a.btn.btn-primary.btn-xs.info.pull-right
         {:on-click       info-handler
          :on-touch-start info-handler
          :role           "button"
          :tabIndex       -1
          :data-trigger   "focus"
          :data-toggle    "popover"
          :title          (:title header)
          :data-html      "true"
          :data-content   (:content header)
          :data-placement "bottom"
          :style          {:cursor "pointer"}} [:i.fa.fa-info {:key :i}]]))
    [:br {:key :br}]
    (:title header)]])


(rum.core/defc slider-widget < rum.core/static
  ([headers control slider-axis-value]
   (slider-widget headers control slider-axis-value 69))

  ([headers control slider-axis-value tick-height]
   [:div
    (map-indexed key-with
                 [(slider-title headers)
                  (slider-labels)
                  (control slider-axis-value 0 1 0.02)
                  (axis-container slider-axis-value tick-height)])]))


(rum.core/defc table-head < rum.core/static [ap headers column-keys slider-axis-value]
  [:thead
   [:tr
    (for [column-key column-keys :when (-> headers column-key :shown)]
      (rum.core/with-key (table-header (:header (colour-map (:theme ap)))
                                       ap
                                       (column-key headers)
                                       column-key)
                         [column-key "head"]))
    [:th
     {:key :last}
     [:.slider-container
      {:style {:height (px (:height (:observed headers)))}}
      (slider-widget headers slider-control slider-axis-value 748)
      ]]]])


(rum.core/defc table1-core [ap data sort-key sort-direction headers]
  [:table.table.table-bordered {:cell-spacing "0"}
   (rum.core/with-key (table-head ap headers (keys headers) (:slider-axis-value ap)) :thead)
   [:tbody {:key :tbody}
    (for [row (if sort-key
                (let [sorted (sort-by sort-key (rest data))]
                  (if sort-direction sorted (reverse sorted)))
                (rest data))
          :let [h-code (:h-code row)
                slider-axis-value (:slider-axis-value ap)
                ]]
      [:tr {:key   h-code
            :class (if (= (keyword h-code) (:selected-h-code ap)) "info" "")}
       (for [column-key (keys headers)
             :let [column-header (column-key headers)
                   info-handler #(core/click->event-bus % :open-hospital-modal h-code nil)]
             :when (:shown column-header)]
         [:td (merge {:key   [h-code column-key]
                      :class (if (= column-key :h-name) "h-name" "")
                      :style (merge {:max-width   (px (:width column-header))
                                     :white-space "normal"
                                     :height      (px (:height column-header))}
                                    )}
                     (when (= column-key :h-name)
                       {:on-touch-start info-handler
                        :on-click       info-handler}))
          [:div {:style {:display "inline-block"
                         :cursor  (if (= column-key :h-name) "pointer" "auto")
                         :width   (if (= column-key :h-name) "calc(100% - 50px)" "auto")}}
           (str (column-key row) (when (= column-key :survival-rate) " %" ""))
           #_(if (= column-key :h-name)
               [:a {:on-touch-start info-handler
                    :on-click       info-handler} (column-key row)]
               (str (column-key row) (when (= column-key :survival-rate) " %" "")))]

          (when (= column-key :h-name)
            [:span " " h-code " " [:i.fa.fa-chevron-right]]
            )])
       [:td {:key   [h-code :bars]
             :style {:background-color "#f0f0f0"}} (chart-cell row slider-axis-value)]])]])

(rum.core/defc table1 < rum.core/reactive [app data]
  [:.table-container
   [:div.data-table
    (table1-core (rum.core/react app)
                 data
                 (:sort-by (rum.core/react app))
                 (:sort-ascending (rum.core/react app))
                 (first data)
                 )]])

(rum.core/defc integer-option < rum.core/static [n]
  [:option {:value n} n])


(defn year-range [year m1 m2]
  (let [y1 (- year 3)]
    {:y1     y1
     :mrange (str m1 " " y1 " — " m2 " " year)
     :option (str y1 " — " year)
     }))

(defn end-year []
  (int (name (:datasource @core/app))))

(rum.core/defc key-option < rum.core/static [year]
  [:option {:value (str year)} (:option (year-range year "April" "March"))])

(rum.core/defc datasource-dropdown < rum.core/reactive [event-bus]

  [:.form-group
   [:label-for {:for "data-selector"} "Change reporting period"]
   [:select#data-selector.form-control.input-sm
    {:value     (name (:datasource (rum.core/react core/app)))
     :on-change #(put! event-bus [:change-datasource (keyword (.-value (.-target %)))])}
    (map-indexed key-with
                 (for [year (range 2013 2019)]              ; was 2017
                   (key-option year)))]
   ])

;;
;; configure title according to dataset
;;
(rum.core/defc datasource-title [prefix]
  [:h2 (str prefix (:mrange (year-range (end-year) "April" "March")))]
  )

(rum.core/defc list-tab < rum.core/reactive
                          (core/update-title "All hospitals")
                          (core/update-description "View all hospital child heart surgery survival data") [app data event-bus]
  [:div
   [:.col-sm-offset-1.col-sm-10

    [:.row
     [:.col-sm-9
      (datasource-title "Data for ")]
     [:.col-sm-3 (datasource-dropdown event-bus)]]


    [:p.col-sm-9
     "These are the hospitals in the UK and Ireland that performed heart surgery in children over this period
     (0-16 years old)."]

    [:p.col-sm-9
     "Clicking on a hospital will bring up specific information for
     that hospital along with an interpretation of its survival rate. It is " [:strong "only"] " valid to compare a hospital's survival
     rate to its predicted range of survival and not to other hospitals. Read more about this in "
     [:a (core/href "faqs") "Everything Else."]]

    [:p.col-sm-9
     [:a (core/href "faqs") "The Everything Else"] " section also tells you more about the predicted range and what
     happens if a hospital’s survival rate is outside its range."]

    [:p.col-sm-9 "You can hover over or tap on the charts to bring up more information."]]

   (table1 app data)])


(rum.core/defc interpretation
  [row close-modal]
  (let [survival-rate (:survival-rate row)]
    (cond
      (< survival-rate (:outer-low row))
      (content/outer-low-comment close-modal)

      (< survival-rate (:inner-low row))
      (content/low-comment close-modal)

      (<= survival-rate (:inner-high row))
      (content/inner-comment close-modal)

      (<= survival-rate (:outer-high row))
      (content/high-comment close-modal)

      (> survival-rate (:outer-high row))
      (content/outer-high-comment close-modal)

      :else
      "Oops - no text for this"
      )))

#_(rum.core/defc interpretation
    [row]
    [:span (let [survival-rate (:survival-rate row)]
             (cond
               (< survival-rate (:outer-low row))
               (:low content/dot-comments)

               (< survival-rate (:inner-low row))
               (:outer-low content/dot-comments)

               (<= survival-rate (:inner-high row))
               (:inner content/dot-comments)

               (<= survival-rate (:outer-high row))
               (:outer-high content/dot-comments)

               (> survival-rate (:outer-high row))
               (:high content/dot-comments)

               :else
               "Oops - no text for this"
               ))])


(rum.core/defc hospital-charities < rum.core/reactive [h-code]
  (let [meta (h-code content/hospital-metadata)
        [link1 link2 link3 link4 link5] meta]
    (when link1 [:div
                 [:h4 {:key 1} "Further web information"]
                 [:ul {:key 2}
                  [:li {:key 1} [:a (link1 1) (link1 2)]]
                  (when link2 [:li {:key 2} [:a (link2 1) (link2 2)]])
                  (when link3 [:li {:key 3} [:a (link3 1) (link3 2)]])
                  (when link4 [:li {:key 4} [:a (link4 1) (link4 2)]])
                  (when link5 [:li {:key 5} [:a (link5 1) (link5 2)]])]])))


(rum.core/defc sample-hospital-intro-text []
  [:i {:key :sintros}
   [:p.note {:key 1} "You can move the slider button left to see the rates plotted on the full 0-100% range of possible
   survival rates, or right to focus on the detail near 100%"]
   [:p.note {:key 2} "Mouse over or click on the chart bars and the dot for explanations of their meaning."]])


(rum.core/defc hospital-data < rum.core/reactive
  [h-code]
  (let [datasource (:datasource (rum.core/react core/app))
        selected-row (h-code ((rows-by-code datasource)))]

    [:.data-summary
     [:p {:key 0 :style {:color "orange"}} " Hover over or tap on the chart areas for more detail on this hospital. "]
     [:p {:key 1} "The hospital performed "
      [:b (:n-ops selected-row) "  operations. "]]
     [:p {:key 2} "Measured 30 days after surgery, "
      [:b {:key 1} (:n-survivors selected-row) " survivors "]
      "and "
      [:b {:key 2} (:n-deaths selected-row) " deaths"]
      " had been recorded. "]
     [:p {:key 3}
      "The hospital's 30 day survival rate was " [:b (:survival-rate selected-row) "%"] "."]]))


(rum.core/defc hospital-header < rum.core/static
  [selected-row]
  [:h3 (:h-name selected-row)])


(rum.core/defc legend < rum.core/reactive [selected-row close-modal]
  [:.legend
   [:.box
    [:p {:style {:color "orange"}} "Legend (See also: "
     [:a (core/href "data/animation"
                    :on-click #(do
                                (when close-modal (close-modal))
                                (core/click->event-bus % :data :animation "data/animation"))) [:i.fa.fa-video-camera] " two minute video"] ")"]
    (let [ap (rum.core/react core/app)]
      (map-indexed key-with
                   [(annotated-chart-cell selected-row (:detail-slider-axis-value ap) #{:dot}
                                          "Survival rate: The dot indicates the hospital's survival rate")
                    (annotated-chart-cell selected-row (:detail-slider-axis-value ap) #{:inner}
                                          "Predicted Range: We expect the hospital's survival rate to be inside this bar
                                          19 times out of 20")
                    (annotated-chart-cell selected-row (:detail-slider-axis-value ap) #{:outer}
                                          "Extended predicted range: We expect the hospital's survival rate to be inside this bar
                                          998 times out of a 1000")
                    ]))
    ]])

(defn open-hospital-modal
  [h-code]
  (swap! core/app #(assoc % :selected-h-code (keyword h-code)))
  (.modal (js/$ "#rowModal"))
  )


(defn close-hospital-modal
  []
  (swap! core/app #(assoc % :selected-h-code nil))
  (.modal (js/$ "#rowModal") "hide"))

(rum.core/defc hospital-detail < rum.core/reactive
  [h-code close-modal]
  (let [ap (rum.core/react core/app)]
    (if h-code
      [:#detail
       (when-let [selected-row (h-code ((rows-by-code (:datasource ap))))]
         (map-indexed key-with
                      [(hospital-header selected-row)
                       (slider-widget content/detail-slider-label detail-slider-control (:detail-slider-axis-value ap))
                       (chart-cell selected-row (:detail-slider-axis-value ap))
                       (hospital-data h-code)
                       (interpretation selected-row close-hospital-modal)
                       (legend selected-row close-hospital-modal)
                       (hospital-charities h-code)
                       ]))]
      [:#detail
       (let [selected-row content/sample-hospital]
         (map-indexed key-with
                      [(sample-hospital-intro-text)
                       (hospital-header selected-row)
                       (slider-widget content/detail-slider-label detail-slider-control (:detail-slider-axis-value ap))
                       (chart-cell selected-row (:detail-slider-axis-value ap))
                       (interpretation selected-row close-hospital-modal)]))])))



(rum.core/defc modal < rum.core/reactive []
  (let [ap (rum.core/react core/app)
        selected-h-code (:selected-h-code ap)
        close-handler #(core/click->event-bus % :close-hospital-modal selected-h-code nil)]
    [:#rowModal.modal.fade {:tab-index       -1
                            :role            "dialog"
                            :aria-labelledby "myModalLabel"
                            }
     [:.modal-dialog {:role "document"}
      [:.modal-content
       [:.modal-body {:key 1}
        (hospital-detail selected-h-code close-hospital-modal)]
       [:.modal-footer {:key 2}
        [:button.btn.btn-default
         {:type           "button"
          :on-click       close-handler
          :on-touch-start close-handler}
         "Close"]
        ]]]
     ]))


