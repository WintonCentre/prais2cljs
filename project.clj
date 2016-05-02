(defproject prais2 "0.5.1"
  :description "Communicating the risks of infant surgery"
  :url "http://understandinguncertainty.org/files/animations/standalone/PRAIS2v0-5/index.html"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}


  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [org.clojure/core.async "0.2.374"]
                 [cljs-ajax "0.5.1"]
                 [cljsjs/openlayers "3.5.0-1"]
                 [cljsjs/moment "2.10.6-0"]
                 [cljsjs/papaparse "4.1.1-1"]

                 [cljsjs/jquery "1.9.1-0"]
                 [cljsjs/bootstrap "3.3.6-0"]

                 [secretary "1.2.3"]
                 [devcards "0.2.1"]
                 [rum "0.8.3"]
                 [figwheel-sidecar "0.5.1"]

                 [com.stuartsierra/component "0.3.1"]
                 ]

  :plugins [[lein-cljsbuild "1.1.1"]
            ;[lein-figwheel "0.5.0-1"]
            [lein-sass "0.3.7"]
            ]

  :sass {:src "sass"
         :output-directory "resources/public/styles"

         ;; other options (provided are default values):
         ;; :delete-output-dir true
         :source-maps true
         :style :compressed
         }

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"
                                    "resources/public/css-c"]
  :profiles {
             :dev {:source-paths ["pages"]
                   :dependencies
                   [;[cljsjs/react-dom-server "15.0.1-1"]
                    ]}
             :perf {:source-paths ["perf"]
                    :dependencies
                    [[enlive "1.1.6"]
                     [criterium "0.4.4"]
                     [hiccup "1.0.5"]]}}

  :aliases {"package" ["do" ["clean"] ["cljsbuild" "once" "pages"] ["run" "-m" "prais2.examples-page"]]
            "perf"    ["with-profile" "perf" "run" "-m" "rum.perf"]}

  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src"]

                        :figwheel {:websocket-host "localhost"
                                   :on-jsload "prais2.main/on-js-reload"
                                   :debug false}

                        :compiler {:main "prais2.main"
                                   :externs ["externs/bootstrap.js"
                                             "externs/jquery.js"
                                             "externs/sliders.js"
                                             ]
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/prais2.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :warnings {:single-segment-namespace false}
                                   :source-map-timestamp true
                                   :optimizations :none}}

                       {:id "pages"
                        :source-paths ["src"]
                        :compiler {:output-to "resources/public/js/compiled/examples.js"
                                   :output-dir "resources/public/js/compiled/"
                                   :main "prais2.examples"
                                   :externs ["externs/bootstrap.js"
                                             "externs/jquery.js"
                                             "externs/sliders.js"]
                                   :optimizations :advanced
                                   :source-map "resources/public/js/compiled/examples.js.map"
                                   :compiler-stats true
                                   :pretty-print false
                                   :parallel-build true
                                   :closure-warnings {:non-standard-jsdoc :off}
                                   :warnings {:externs-validation :off
                                              :non-standard-jsdoc :off}
                                   }}

                       {:id "devcards"
                        :source-paths ["src"]
                        :figwheel {:devcards true
                                   :on-jsload "prais2.main/on-js-reload"}
                        :compiler { :main       "prais2.devcards"
                                   :asset-path "js/compiled/devcards_out"
                                   :output-to  "resources/public/js/compiled/devcards.js"
                                   :output-dir "resources/public/js/compiled/devcards_out"
                                   :source-map-timestamp true }}

                       {:id "min"
                        :source-paths ["src"]
                        :compiler {:output-to "resources/public/js/compiled/prais2.js"
                                   :main "prais2.main"
                                   :externs ["externs/bootstrap.js"
                                             "externs/jquery.js"
                                             "externs/sliders.js"]
                                   :optimizations :advanced
                                   :closure-warnings {:non-standard-jsdoc :off}
                                   :warnings {:externs-validation :off
                                              :non-standard-jsdoc :off}
                                   }}]}

  :figwheel {
             ;; :http-server-root "public" ;; default and assumes "resources"
             ;; :server-port 3449 ;; default
             ;; :server-ip "127.0.0.1"

             :css-dirs ["resources/public/css"] ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process
             ;; :nrepl-port 7888

             ;; Server Ring Handler (optional)
             ;; if you want to embed a ring handler into the figwheel http-kit
             ;; server, this is for simple ring servers, if this
             ;; doesn't work for you just run your own server :)
             ;; :ring-handler hello_world.server/handler

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             :open-file-command "open-in-intellij"          ;"emacs -n $2 $1"

             ;; if you want to disable the REPL
             ;; :repl false

             ;; to configure a different figwheel logfile path
             ;; :server-logfile "tmp/logs/figwheel-logfile.log"
             })
