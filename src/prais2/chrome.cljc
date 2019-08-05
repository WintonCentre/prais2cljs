(ns ^:figwheel-always prais2.chrome
  (:require
    #?(:cljs [goog.string :refer [unescapeEntities]])
    [rum.core]
    #?(:cljs [cljsjs.jquery])
    #?(:cljs [cljsjs.bootstrap])
    [prais2.utils :refer [key-with]]
    #?(:cljs [prais2.core :as core :refer [event-bus]])
    #?(:clj
    [prais2.core :as core])
    [prais2.components.data-selector :refer [data-selector]]
    ))

(defn rgba-string
  "return CSS rgba string"
  [[r g b a]]
  (str "rgba(" r "," g "," b "," a))


(defrecord Nav-item [long-title short-title class icon token])

#?(:cljs (def nbsp (unescapeEntities "&nbsp;")))
#?(:clj (def nbsp " "))                                     ;; not yet sure how to handle entities in java rum hiccup

(def what-why (str "What," nbsp "why," nbsp "how?"))
(def everything-else (str "Everything" nbsp "else"))

(def nav-items {
                :home  (Nav-item. "Home" "Home" "nav-item home" "home" "home")
                :intro (Nav-item. what-why what-why "nav-item intro" "question" "intro")
                :data  (Nav-item. "Data" "Explore the data" "nav-item data" "table" "data/map")
                :faqs  (Nav-item. everything-else everything-else "nav-item faqs" "info" "faqs")})


(rum.core/defc bs-nav-link [active? nav-item click-handler]
  [:li
   [:a.navbar-btn {:on-click    click-handler
                   :class       (str (if active? " active " " ") (:class nav-item))
                   :data-toggle "collapse"
                   :data-target ".navbar-collapse.in"                            ;(window-data-target)
                   :href        (core/token->url (:token nav-item))}
    [:i.fa {:class (str "fa-" (:icon nav-item))}]
    (str " " (:short-title nav-item))]])


(defn nav-click-handler [nav-item-key]
  (fn [e] (core/click->event-bus e nav-item-key
                                 (if (= nav-item-key :data) :map nil)
                                 (if (= nav-item-key :data) "data/map" (str (name nav-item-key))))))

(rum.core/defc bs-fixed-navbar [active-key]
  [:nav.navbar.navbar-simple.navbar-fixed-top
   [:.navbar-inner
    [:.container
     [:.navbar-header {:key 1}
      [:button.navbar-toggle.collapsed {:key           1
                                        :type          "button"
                                        :data-toggle   "collapse"
                                        :data-target   "#navbar"
                                        :aria-expanded "false"
                                        :aria-controls "navbar"}
       [:span.sr-only {:key 1} "Toggle navigation"]
       [:span.icon-bar {:key 2}]
       [:span.icon-bar {:key 3}]
       [:span.icon-bar {:key 4}]]]
     [:#navbar.navbar-collapse.collapse {:key 2}
      [:ul.nav.navbar-nav.navbar-right {:key 1}

       #?(:cljs                                             ;only supply real button click handlers once we're loaded
          (map-indexed #(key-with %1 (bs-nav-link
                                       (= active-key %2)
                                       (%2 nav-items)
                                       (nav-click-handler %2)))
                       (keys nav-items)))]]]]])


(rum.core/defc header < rum.core/reactive []
  [:div
   (bs-fixed-navbar (:page (rum.core/react core/app)))
   [:.main-title-box
    [:a (core/href "/"
                   :on-touch-start (nav-click-handler :home)
                   :on-click (nav-click-handler :home)
                   )
     [:img.img-responsive.pull-left {:src   "/assets/logo3.png"
                                     :style {:margin-top     "-50px"
                                             :padding-bottom "10px"
                                             :padding-right  "20px"
                                             :margin-right   "0px"
                                             :transform "rotate(180deg)"}}]]
    [:.pull-left.main-title
     {:style {:margin-left "0px"
              :padding-left "0px"
              :padding-right "40px"}}
     "UNDERSTANDING CHILDREN'S HEART SURGERY OUTCOMES"]]])


(rum.core/defc footer []
  [:.container-fluid.partners
   [:.row.visible-xs-block
    [:.col-xs-offset-1
     [:h4 [:a.link (core/href "faq/4/0")
           [:span "Project partners "
            [:i.fa.fa-chevron-right]]]]
     [:ul {:style {:list-style-type "none"}}
      [:li "University College, London"]
      [:li "University of Cambridge"]
      [:li "King's College, London"]
      [:li "Sense about Science"]
      [:li "Children's Heart Federation"]]]]

   [:.row.hidden-xs
    ;.col-xs-1.col-md-offset-1.col-md-11 {:style {:vertical-align "middle" }}
    [:.col-sm-10.collab-logo                                ;.col-md-offset-1.col-md-11
     [:a.link (core/href "faq/4/0") "About us"] "| " [:a (core/href "faq/4/1") "Contact us"]]
    [:.col-sm-10.collab-logo.col-md-offset-1.col-md-11
     [:img
      (core/isrc "assets/ucl-logo.png" :style {:width "190px"})]
     [:img
      (core/isrc "assets/camlogo.png" :style {:width "220px"})]
     [:img
      (core/isrc "assets/KCLlogo.gif" :style {:width "130px"})]
     [:img
      (core/isrc "assets/sas-logo.png" :style {:width "80px"})]
     [:img
      (core/isrc "assets/chf-logo.png" :style {:width "80px"})]]]

   [:.row.footer
    [:.acks.col-md-offset-1
     ;[:.pull-right (logger/playback-controls)]             ;;WARNING - likely to break clj compilation
     [:h3
      "Funding acknowledgement"]
     [:p
      "This project was funded by the National Institute for Health Research Health Services and Delivery Research
      Programme\n(project number 14/19/13)"]
     [:h3
      "Department of Health disclaimer"]
     [:p
      "The views and opinions expressed therein are those of the authors and do not necessarily reflect those of the
      Health Services and Delivery Research Programme, NIHR, NHS or the Department of Health."]]]

   [:.row.footer.legal
    [:.col-md-offset-1
     [:p.copyright
      "© PRAIS2 website project team 2016, 2019"]
     [:p.cookies "This site uses cookies to help understand how users interact with the site.
     See " [:a {:href "https://developers.google.com/analytics/devguides/collection/analyticsjs/cookie-usage"}
                             "Google Analytics"]]]]])



