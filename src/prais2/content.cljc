(ns ^:figwheel-always prais2.content
  (:require [rum.core :as rum]
            [prais2.core :as core]))

;;;
;; About us mugshot list
;;;
(defn get-mugshots []
  ["christina" "david" "tim" "emily B" "mike" "emily J" "joanne"])

(def mugshot-data
  {
   :christina {:id "christina" :name "Christina" :org "UCL"}
   :david     {:id "david" :name "David" :org "Camb"}
   :mike      {:id "mike" :name "Mike" :org "Camb"}
   :tim       {:id "tim" :name "Tim" :org "KCL"}
   :emily-b   {:id "emily B" :name "Emily Blackshaw" :org "KCL"}
   :emily-j   {:id "emily J" :name "Emily Jesper" :org "SAS"}
   :joanne    {:id "joanne" :name "Joanne" :org "SAS"}})

;;;
;; table structure
;;;
(defrecord Header [title sortable shown width height content])

(defrecord Row [h-name h-code h-lat h-lon n-ops n-deaths n-survivors survival-rate outer-low inner-low inner-high outer-high observed])

;;;
;; Table headers with info texts
;;;
(def datasource-tab
  {:2018 {:title      "Reporting period"
          :label      "2015-2018"
          :long-label "April 2015 - March 2018"}
   :2017 {:title      "Reporting period"
          :label      "2014-2017"
          :long-label "April 2014 - March 2017"}
   :2016 {:title      "Reporting period"
          :label      "2013-2016"
          :long-label "April 2013 - March 2016"}
   :2015 {:title      "Reporting period"
          :label      "2012-2015"
          :long-label "April 2012 - March 2015"}
   :2014 {:title      "Reporting period"
          :label      "2011-2014"
          :long-label "April 2011 - March 2014"}
   :2013 {:title      "Reporting period"
          :label      "2010-2013"
          :long-label "April 2010 - March 2013"}
   })


(def header-row
  (Row. (Header. "Hospital" true true 300 50
                 "The hospital name")
        (Header. "Hospital Code" false false 77 50
                 "Hospital code as used by NCHDA")
        (Header. "Latitude" false false 0 0
                 "")
        (Header. "Longitude" false false 0 0
                 "")
        (Header. "Number of Operations" true true 95 50
                 (str "The number of heart operations carried out on under-16s at each hospital during " (:long-label ((:datasource @core/app) datasource-tab))
                      ". Operations that occur within 30 days of each other are treated as a single operation."))
        (Header. "Number of Deaths" false true 75 50
                 "The number of operations where the child died within 30 days of their operation, from any cause")
        (Header. "Number of Survivors" false true 85 50
                 "The number of operations where the child survived at least 30 days after their operation")
        (Header. "Survival Rate %" false true 86 50
                 "<p>The percentage of operations where the child survived at least 30 days after their
                 operation.</p>
                 Some hospitals specialise in certain conditions, meaning that some hospitals tend to
                 operate on children with a lower chance of survival. Therefore we would not expect all
                 hospitals to have the same survival rates each year, and so it is not possible to sort
                 hospitals in this list on the basis of their survival rates.")
        (Header. "Outer Low" false false 130 50
                 "A dot within the left hand light blue band means that there is some evidence that chances of survival in the hospital were lower than predicted")
        (Header. "Inner Low" false false 130 50
                 "A dot in this area means that there is no evidence that the hospital’s survival rate is different from what is predicted")
        (Header. "Inner High" false false 130 50
                 "A dot within the right hand light blue band means that there is some evidence that chances of survival in the hospital were higher than predicted")
        (Header. "Outer High" false false 130 50
                 nil)
        (Header. "Survival with predicted range" false false 0 100
                 nil)
        ))

(def detail-slider-label "The survival rate with predicted range of survival")

;;;
;; Sample hospital
;;;
(def sample-hospital
  (Row. "Sample Hospital" "H" 52 -2 976 24 952 97.5 96.5 97.1 98.9 99.2 nil))


;;;
;; The 2015 results
;; todo: derive all numbers that are derivative
;; todo: read data from file
;; todo: Add Title into dataset
;;;
;;(Row. "Belfast, Royal Victoria Hospital"	                "RVB"	54.594167 -5.953666 204	        2 	202	99.0 	95.1	96.6  100     100.0 nil)

;; (defrecord Row [h-name h-code h-lat h-lon n-ops n-deaths n-survivors survival-rate outer-low inner-low inner-high outer-high observed])

(comment
  ;From 2013-2016 spreadsheet

  ;HSC	332	 8	324	 97.6	95.5	96.7	99.4	100.0
  ;GRL	671	 11	660	 98.4	96.3	97.0	99.1	99.6
  ;FRE	657	 18	639	 97.3	94.7	95.6	98.2	98.8
  ;RHS	724	 20	704	 97.2	96.5	97.2	99.0	99.4
  ;BRC	841	 15	826	 98.2	96.6	97.1	98.9	99.4
  ;SGH	872	 21	851	 97.6	96.0	96.7	98.6	99.1
  ;LGI	1086 17	1069 98.4	97.2	97.7	99.2	99.5
  ;OLS	947	 20	927	 97.9	96.6	97.1	98.9	99.3
  ;NHB	1126 28	1098 97.5	96.4	97.0	98.7	99.1
  ;ACH	1068 9	1059 99.2	96.4	97.0	98.7	99.2
  ;GUY	1247 44	1203 96.5	95.9	96.5	98.2	98.6
  ;BCH	1381 37	1344 97.3	96.0	96.5	98.2	98.6
  ;GOS	1894 16	1878 99.2	97.0	97.5	98.7	99.0
  )

(comment
  ;From 2014-2017 spreadsheet
  ;HSC	246	10	236	95.9	98.3	95.1	96.3	99.6	100.0
  ;FRE	679	13	666	98.1	97.2	95.0	95.9	98.4	99.0
  ;GRL	727	6	721	99.2	98.1	96.4	97.1	99.0	99.4
  ;RHS	698	15	683	97.9	98.0	96.1	96.8	99.0	99.4
  ;BRC	855	11	844	98.7	97.9	96.3	96.8	98.8	99.2
  ;SGH	926	18	908	98.1	97.8	96.2	96.8	98.7	99.1
  ;OLS	922	22	900	97.6	97.7	96.0	96.6	98.6	99.0
  ;ACH	1075	12	1063	98.9	97.9	96.5	97.0	98.7	99.1
  ;LGI	1029	17	1012	98.3	98.5	97.2	97.7	99.2	99.5
  ;NHB	1068	28	1040	97.4	97.9	96.4	97.0	98.7	99.2
  ;GUY	1231	36	1195	97.1	97.3	95.8	96.3	98.1	98.6
  ;BCH	1363	39	1324	97.1	96.9	95.4	96.0	97.8	98.2
  ;GOS	1885	16	1869	99.2	98.1	97.1	97.5	98.7	99.0
  )

(comment
  ;From 2015-2018 spreadsheet
  ;HSC	210	8	202	96.2	98.3	94.8	96.2	100.0	100.0
  ;FRE	681	12	669	98.2	97.4	95.3	96.2	98.5	99.1
  ;GRL	769	5	764	99.4	98.1	96.4	97.0	99.0	99.3
  ;RHS	718	13	705	98.2	98.4	96.7	97.4	99.2	99.6
  ;BRC	868	9	859	99.0	97.9	96.2	96.9	98.7	99.2
  ;SGH	964	17	947	98.2	98.0	96.5	97.1	98.9	99.3
  ;OLS	906	15	891	98.3	97.9	96.4	96.9	98.8	99.2
  ;ACH	1027	11	1016	98.9	97.9	96.4	97.0	98.7	99.1
  ;LGI	984	12	972	98.8	98.3	97.0	97.5	99.1	99.4
  ;NHB	1002	21	981	97.9	98.0	96.5	97.1	98.8	99.2
  ;GUY	1226	33	1193	97.3	97.6	96.2	96.7	98.4	98.9
  ;BCH	1361	33	1328	97.6	97.1	95.5	96.1	97.9	98.4
  ;GOS	1812	12	1800	99.3	98.3	97.2	97.6	98.8	99.1
  )


(def datasources
  {:2018
   [
    (Row. "London, Harley Street Clinic" "HSC" 51.520348 -0.147726                       210	8	  202	  96.2	94.8	96.2	100.0	100.0 nil)
    (Row. "Newcastle, Freeman Hospital" "FRE" 55.002386 -1.593643                        681	12	669	  98.2	95.3	96.2	98.5	99.1 nil)
    (Row. "Leicester, Glenfield Hospital" "GRL" 52.654229 -1.179836                      769	5	  764	  99.4	96.4	97.0	99.0	99.3 nil)
    (Row. "Glasgow, Royal Hospital for Children" "RHS" 55.862745 -4.342357               718	13	705	  98.2	96.7	97.4	99.2	99.6 nil)
    (Row. "Bristol Royal Hospital for Children" "BRC" 51.457899 -2.597014                868	9	  859	  99.0	96.2	96.9	98.7	99.2 nil)
    (Row. "Southampton, Wessex Cardiothoracic Centre" "SGH" 50.932846 -1.432731          964	17	947	  98.2	96.5	97.1	98.9	99.3 nil)
    (Row. "Dublin, Our Lady's Children's Hospital" "OLS" 53.326005 -6.317399             906	15	891	  98.3	96.4	96.9	98.8	99.2 nil)
    (Row. "Liverpool, Alder Hey Hospital" "ACH" 53.419566 -2.900560                      1027	11	1016	98.9	96.4	97.0	98.7	99.1 nil)
    (Row. "Leeds General Infirmary" "LGI" 53.802109 -1.550870                            984	12	972	  98.8	97.0	97.5	99.1	99.4 nil)
    (Row. "London, Royal Brompton Hospital" "NHB" 51.489012 -0.170759                    1002	21	981	  97.9	96.5	97.1	98.8	99.2 nil)
    (Row. "London, Evelina London Children's Hospital" "GUY" 51.498044 -0.118835         1226	33	1193  97.3	96.2	96.7	98.4	98.9 nil)
    (Row. "Birmingham Children’s Hospital" "BCH" 52.484946 -1.894566                     1361	33	1328	97.6	95.5	96.1	97.9	98.4 nil)
    (Row. "London, Great Ormond Street Hospital for Children" "GOS" 51.522549 -0.120923  1812	12	1800	99.3	97.2	97.6	98.8	99.1 nil)]
   :2017
   [
    (Row. "London, Harley Street Clinic" "HSC" 51.520348 -0.147726                        246	 10 236	 95.9	95.1	96.3	99.6	100.0  nil)
    (Row. "Newcastle, Freeman Hospital" "FRE" 55.002386 -1.593643                         679	 13 666	 98.1	95.0	95.9	98.4	99.0 nil)
    (Row. "Leicester, Glenfield Hospital" "GRL" 52.654229 -1.179836                       727	 6	721	 99.2	96.4	97.1	99.0	99.4 nil)
    (Row. "Glasgow, Royal Hospital for Children" "RHS" 55.862745 -4.342357                698	 15 683	 97.9	96.1	96.8	99.0	99.4 nil)
    (Row. "Bristol Royal Hospital for Children" "BRC" 51.457899 -2.597014                 855	 11 844	 98.7	96.3	96.8	98.8	99.2 nil)
    (Row. "Southampton, Wessex Cardiothoracic Centre" "SGH" 50.932846 -1.432731           926	 18 908	 98.1	96.2	96.8	98.7	99.1 nil)
    (Row. "Dublin, Our Lady's Children's Hospital" "OLS" 53.326005 -6.317399              922	 22 900	 97.6	96.0	96.6	98.6	99.0 nil)
    (Row. "Liverpool, Alder Hey Hospital" "ACH" 53.419566 -2.900560                       1075 12 1063 98.9	96.5	97.0	98.7	99.1 nil)
    (Row. "Leeds General Infirmary" "LGI" 53.802109 -1.550870                             1029 17 1012 98.3	97.2	97.7	99.2	99.5 nil)
    (Row. "London, Royal Brompton Hospital" "NHB" 51.489012 -0.170759                     1068 28 1040 97.4	96.4	97.0	98.7	99.2 nil)
    (Row. "London, Evelina London Children's Hospital" "GUY" 51.498044 -0.118835          1231 36 1195 97.1	95.8	96.3	98.1	98.6 nil)
    (Row. "Birmingham Children’s Hospital" "BCH" 52.484946 -1.894566                      1363 39 1324 97.1	95.4	96.0	97.8	98.2 nil)
    (Row. "London, Great Ormond Street Hospital for Children" "GOS" 51.522549 -0.120923   1885 16 1869 99.2	97.1	97.5	98.7	99.0 nil)]

   :2016
   [
    (Row. "London, Harley Street Clinic" "HSC" 51.520348 -0.147726                        332	 8	324	 97.6	95.5	96.7	99.4	100.0  nil)
    (Row. "Newcastle, Freeman Hospital" "FRE" 55.002386 -1.593643                         657	 18	639	 97.3	94.7	95.6	98.2	98.8 nil)
    (Row. "Leicester, Glenfield Hospital" "GRL" 52.654229 -1.179836                       671	 11	660	 98.4	96.3	97.0	99.1	99.6 nil)
    (Row. "Glasgow, Royal Hospital for Children" "RHS" 55.862745 -4.342357                724	 20	704	 97.2	96.5	97.2	99.0	99.4 nil)
    (Row. "Bristol Royal Hospital for Children" "BRC" 51.457899 -2.597014                 841	 15	826	 98.2	96.6	97.1	98.9	99.4 nil)
    (Row. "Southampton, Wessex Cardiothoracic Centre" "SGH" 50.932846 -1.432731           872	 21	851	 97.6	96.0	96.7	98.6	99.1 nil)
    (Row. "Dublin, Our Lady's Children's Hospital" "OLS" 53.326005 -6.317399              947	 20	927	 97.9	96.6	97.1	98.9	99.3 nil)
    (Row. "Liverpool, Alder Hey Hospital" "ACH" 53.419566 -2.900560                       1068 9	1059 99.2	96.4	97.0	98.7	99.2 nil)
    (Row. "Leeds General Infirmary" "LGI" 53.802109 -1.550870                             1086 17	1069 98.4	97.2	97.7	99.2	99.5 nil)
    (Row. "London, Royal Brompton Hospital" "NHB" 51.489012 -0.170759                     1126 28	1098 97.5	96.4	97.0	98.7	99.1 nil)
    (Row. "London, Evelina London Children's Hospital" "GUY" 51.498044 -0.118835          1247 44	1203 96.5	95.9	96.5	98.2	98.6 nil)
    (Row. "Birmingham Children’s Hospital" "BCH" 52.484946 -1.894566                      1381 37	1344 97.3	96.0	96.5	98.2	98.6 nil)
    (Row. "London, Great Ormond Street Hospital for Children" "GOS" 51.522549 -0.120923   1894 16	1878 99.2	97.0	97.5	98.7	99.0 nil)]

   :2015
   [
    (Row. "London, Harley Street Clinic" "HSC" 51.520348 -0.147726 418 5 413 98.8 94.5 95.7 98.8 99.3 nil)
    (Row. "Leicester, Glenfield Hospital" "GRL" 52.654229 -1.179836 607 14 593 97.7 95.2 96.0 98.5 99.2 nil)
    (Row. "Newcastle, Freeman Hospital" "FRE" 55.002386 -1.593643 668 15 653 97.8 95.2 96.1 98.5 99.1 nil)
    (Row. "Glasgow, Royal Hospital for Children" "RHS" 55.862745 -4.342357 760 27 733 96.3 95.5 96.3 98.6 99.1 nil)
    (Row. "Bristol Royal Hospital for Children" "BRC" 51.457899 -2.597014 835 14 821 98.3 95.8 96.5 98.6 99.0 nil)
    (Row. "Southampton, Wessex Cardiothoracic Centre" "SGH" 50.932846 -1.432731 829 14 815 98.3 95.1 95.8 98.1 98.7 nil)
    (Row. "Leeds General Infirmary" "LGI" 53.802109 -1.550870 1038 22 1016 97.9 96.3 96.9 98.8 99.1 nil)
    (Row. "Dublin, Our Lady's Children's Hospital" "OLS" 53.326005 -6.317399 983 23 960 97.7 95.6 96.2 98.3 98.8 nil)
    (Row. "London, Royal Brompton Hospital" "NHB" 51.489012 -0.170759 1094 19 1075 98.3 96.2 96.8 98.5 99.0 nil)
    (Row. "Liverpool, Alder Hey Hospital" "ACH" 53.419566 -2.900560 1132 20 1112 98.2 95.5 96.1 98.1 98.6 nil)
    (Row. "London, Evelina London Children's Hospital" "GUY" 51.498044 -0.118835 1220 35 1185 97.1 95.3 96.0 97.9 98.4 nil)
    (Row. "Birmingham Children’s Hospital" "BCH" 52.484946 -1.894566 1457 36 1421 97.5 94.9 95.5 97.4 97.9 nil)
    (Row. "London, Great Ormond Street Hospital for Children" "GOS" 51.522549 -0.120923 1892 19 1873 99.0 96.5 96.9 98.4 98.7 nil)]

   :2014
   [(Row. "Belfast, Royal Victoria Hospital" "RVB" 54.594167 -5.953666 204 2 202 99.0 95.1 96.6 100 100.0 nil)
    (Row. "London, Harley Street Clinic" "HSC" 51.520348 -0.147726 482 7 475 98.5 94.8 95.9 98.8 99.4 nil)
    (Row. "Leicester, Glenfield Hospital" "GRL" 52.654229 -1.179836 582 11 571 98.1 95.5 96.4 98.8 99.3 nil)
    (Row. "Newcastle, Freeman Hospital" "FRE" 55.002386 -1.593643 678 15 663 97.8 95.3 96 98.5 99.0 nil)
    (Row. "Glasgow, Royal Hospital for Children" "RHS" 55.862745 -4.342357 787 28 759 96.4 95.7 96.4 98.6 99.1 nil)
    (Row. "Bristol Royal Hospital for Children" "BRC" 51.457899 -2.597014 835 19 816 97.7 96.0 96.8 98.7 99.2 nil)
    (Row. "Southampton, Wessex Cardiothoracic Centre" "SGH" 50.932846 -1.432731 890 17 873 98.1 95.4 96.2 98.3 98.8 nil)
    (Row. "Leeds General Infirmary" "LGI" 53.802109 -1.550870 976 23 953 97.6 96.5 97.1 98.9 99.3 nil)
    (Row. "Dublin, Our Lady's Children's Hospital" "OLS" 53.326005 -6.317399 1056 23 1033 97.8 96.2 96.8 98.6 99.1 nil)
    (Row. "London, Royal Brompton Hospital" "NHB" 51.489012 -0.170759 1107 12 1095 98.9 96.5 97 98.7 99.1 nil)
    (Row. "Liverpool, Alder Hey Hospital" "ACH" 53.419566 -2.900560 1146 27 1119 97.6 95.8 96.4 98.3 98.7 nil)
    (Row. "London, Evelina London Children's Hospital" "GUY" 51.498044 -0.118835 1204 39 1165 96.8 95.6 96.2 98.1 98.5 nil)
    (Row. "Birmingham Children’s Hospital" "BCH" 52.484946 -1.894566 1481 30 1451 98.0 95.2 95.8 97.6 98.1 nil)
    (Row. "London, Great Ormond Street Hospital for Children" "GOS" 51.522549 -0.120923 1881 30 1851 98.4 96.6 97.0 98.4 98.7 nil)]

   :2013
   [(Row. "Belfast, Royal Victoria Hospital" "RVB" 54.594167 -5.953666 232 4 228 98.3 95.3 96.6 99.6 100.0 nil)
    (Row. "London, Harley Street Clinic" "HSC" 51.520348 -0.147726 483 10 473 97.9 94.6 95.7 98.6 99.2 nil)
    (Row. "Leicester, Glenfield Hospital" "GRL" 52.654229 -1.179836 570 12 558 97.9 95.1 96.1 98.6 99.3 nil)
    (Row. "Newcastle, Freeman Hospital" "FRE" 55.002386 -1.593643 704 16 688 97.7 95 95.9 98.3 98.9 nil)
    (Row. "Glasgow, Royal Hospital for Children" "RHS" 55.862745 -4.342357 817 26 791 96.8 95.7 96.5 98.5 99 nil)
    (Row. "Bristol Royal Hospital for Children" "BRC" 51.457899 -2.597014 886 21 865 97.6 96.5 97.2 98.9 99.3 nil)
    (Row. "Southampton, Wessex Cardiothoracic Centre" "SGH" 50.932846 -1.432731 914 14 900 98.5 96 96.6 98.6 99 nil)
    (Row. "Leeds General Infirmary" "LGI" 53.802109 -1.550870 919 32 887 96.5 96.2 96.8 98.7 99.1 nil)
    (Row. "Dublin, Our Lady's Children's Hospital" "OLS" 53.326005 -6.317399 738 22 716 97 95.9 96.6 98.8 99.2 nil)
    (Row. "London, Royal Brompton Hospital" "NHB" 51.489012 -0.170759 1117 18 1099 98.4 96.5 97.1 98.7 99.1 nil)
    (Row. "Liverpool, Alder Hey Hospital" "ACH" 53.419566 -2.900560 1195 40 1155 96.7 95.7 96.3 98.2 98.7 nil)
    (Row. "London, Evelina London Children's Hospital" "GUY" 51.498044 -0.118835 1165 42 1123 96.4 95.6 96.2 98.1 98.5 nil)
    (Row. "Birmingham Children’s Hospital" "BCH" 52.484946 -1.894566 1467 44 1423 97 95 95.6 97.5 98 nil)
    (Row. "London, Great Ormond Street Hospital for Children" "GOS" 51.522549 -0.120923 1828 32 1796 98.3 96.7 97 98.4 98.7 nil)]
   ;;
   ;; followed by sample datasources
   ;;

   :baseline
   [(Row. "Hospital A" "A" 0 0 204 4 200 98.0 95.1 96.6 100.0 100.0 nil)
    (Row. "Hospital B" "B" 0 0 482 11 471 97.7 94.8 95.9 98.8 99.4 nil)
    (Row. "Hospital C" "C" 0 0 582 14 568 97.6 95.4 96.2 98.8 99.3 nil)
    (Row. "Hospital D" "D" 0 0 678 18 660 97.3 95.1 96.0 98.4 99.0 nil)
    (Row. "Hospital E" "E" 0 0 787 28 759 96.4 95.7 96.3 98.5 99.0 nil)
    (Row. "Hospital F" "F" 0 0 835 22 813 97.4 96.0 96.8 98.7 99.2 nil)
    (Row. "Hospital G" "G" 0 0 890 22 868 97.5 95.5 96.2 98.3 98.8 nil)
    (Row. "Hospital H" "H" 0 0 976 24 952 97.5 96.5 97.1 98.9 99.2 nil)
    (Row. "Hospital I" "I" 0 0 1056 26 1030 97.5 96.3 96.9 98.7 99.1 nil)
    (Row. "Hospital J" "J" 0 0 1107 17 1090 98.5 96.5 97.0 98.7 99.1 nil)
    (Row. "Hospital K" "K" 0 0 1146 31 1115 97.3 95.8 96.4 98.3 98.7 nil)
    (Row. "Hospital L" "L" 0 0 1204 41 1163 96.6 95.6 96.3 98.1 98.6 nil)
    (Row. "Hospital M" "M" 0 0 1481 36 1445 97.6 95.3 95.9 97.7 98.1 nil)
    (Row. "Hospital N" "N" 0 0 1881 38 1843 98.0 96.5 97.0 98.4 98.7 nil)]

   :variation-1
   [(Row. "Hospital A" "A" 0 0 204 4 200 98.04 95.10 96.57 100.00 100.0 nil)
    (Row. "Hospital B" "B" 0 0 482 11 471 97.72 94.81 95.85 98.76 99.38 nil)
    (Row. "Hospital C" "C" 0 0 582 14 568 97.59 95.36 96.22 98.80 99.31 nil)
    (Row. "Hospital D" "D" 0 0 678 18 660 97.35 95.13 96.02 98.38 98.97 nil)
    (Row. "Hospital E" "E" 0 0 787 35 752 95.55 95.68 96.32 98.48 98.98 nil)
    (Row. "Hospital F" "F" 0 0 835 22 813 97.37 96.05 96.77 98.68 99.16 nil)
    (Row. "Hospital G" "G" 0 0 890 22 868 97.53 95.51 96.18 98.31 98.76 nil)
    (Row. "Hospital H" "H" 0 0 976 24 952 97.54 96.52 97.13 98.87 99.18 nil)
    (Row. "Hospital I" "I" 0 0 1056 26 1030 97.54 96.31 96.88 98.67 99.05 nil)
    (Row. "Hospital J" "J" 0 0 1107 17 1090 98.46 96.48 97.02 98.74 99.10 nil)
    (Row. "Hospital K" "K" 0 0 1146 31 1115 97.29 95.81 96.42 98.25 98.69 nil)
    (Row. "Hospital L" "L" 0 0 1204 41 1163 96.59 95.60 96.26 98.09 98.59 nil)
    (Row. "Hospital M" "M" 0 0 1481 36 1445 97.57 95.34 95.88 97.70 98.11 nil)
    (Row. "Hospital N" "N" 0 0 1881 38 1843 97.98 96.54 97.02 98.35 98.67 nil)]

   :variation-2a
   [(Row. "Hospital A" "A" 0 0 204 3 201 98.5 95.1 96.6 100.0 100.0 nil)
    (Row. "Hospital B" "B" 0 0 482 11 471 97.7 94.8 95.9 98.8 99.4 nil)
    (Row. "Hospital C" "C" 0 0 582 14 568 97.6 95.4 96.2 98.8 99.3 nil)
    (Row. "Hospital D" "D" 0 0 678 18 660 97.3 95.1 96.0 98.4 99.0 nil)
    (Row. "Hospital E" "E" 0 0 787 28 759 96.4 95.7 96.3 98.5 99.0 nil)
    (Row. "Hospital F" "F" 0 0 835 17 818 98.0 96.0 96.8 98.7 99.2 nil)
    (Row. "Hospital G" "G" 0 0 890 22 868 97.5 95.5 96.2 98.3 98.8 nil)
    (Row. "Hospital H" "H" 0 0 976 19 957 98.1 96.5 97.1 98.9 99.2 nil)
    (Row. "Hospital I" "I" 0 0 1056 20 1036 98.1 96.3 96.9 98.7 99.1 nil)
    (Row. "Hospital J" "J" 0 0 1107 17 1090 98.5 96.5 97.0 98.7 99.1 nil)
    (Row. "Hospital K" "K" 0 0 1146 24 1122 97.9 95.8 96.4 98.3 98.7 nil)
    (Row. "Hospital L" "L" 0 0 1204 34 1170 97.2 95.6 96.3 98.1 98.6 nil)
    (Row. "Hospital M" "M" 0 0 1481 36 1445 97.6 95.3 95.9 97.7 98.1 nil)
    (Row. "Hospital N" "N" 0 0 1881 38 1843 98.0 96.5 97.0 98.4 98.7 nil)]

   :variation-2b
   [(Row. "Hospital A" "A" 0 0 204 3 201 98.5 95.1 96.6 100.0 100.0 nil)
    (Row. "Hospital B" "B" 0 0 482 8 474 98.3 94.8 95.9 98.8 99.4 nil)
    (Row. "Hospital C" "C" 0 0 582 11 571 98.1 95.4 96.2 98.8 99.3 nil)
    (Row. "Hospital D" "D" 0 0 678 15 663 97.8 95.1 96.0 98.4 99.0 nil)
    (Row. "Hospital E" "E" 0 0 787 28 759 96.4 95.7 96.3 98.5 99.0 nil)
    (Row. "Hospital F" "F" 0 0 835 17 818 98.0 96.0 96.8 98.7 99.2 nil)
    (Row. "Hospital G" "G" 0 0 890 17 873 98.1 95.5 96.2 98.3 98.8 nil)
    (Row. "Hospital H" "H" 0 0 976 19 957 98.1 96.5 97.1 98.9 99.2 nil)
    (Row. "Hospital I" "I" 0 0 1056 20 1036 98.1 96.3 96.9 98.7 99.1 nil)
    (Row. "Hospital J" "J" 0 0 1107 10 1097 99.1 96.5 97.0 98.7 99.1 nil)
    (Row. "Hospital K" "K" 0 0 1146 24 1122 97.9 95.8 96.4 98.3 98.7 nil)
    (Row. "Hospital L" "L" 0 0 1204 34 1170 97.2 95.6 96.3 98.1 98.6 nil)
    (Row. "Hospital M" "M" 0 0 1481 27 1454 98.2 95.3 95.9 97.7 98.1 nil)
    (Row. "Hospital N" "N" 0 0 1881 27 1854 98.6 96.5 97.0 98.4 98.7 nil)]

   :variation-3
   [(Row. "Hospital A" "A" 0 0 204 4 200 98.0 95.1 96.6 100.0 100.0 nil)
    (Row. "Hospital B" "B" 0 0 482 11 471 97.7 94.8 95.9 98.8 99.4 nil)
    (Row. "Hospital C" "C" 0 0 582 14 568 97.6 95.4 96.2 98.8 99.3 nil)
    (Row. "Hospital D" "D" 0 0 678 18 660 97.3 95.1 96.0 98.4 99.0 nil)
    (Row. "Hospital E" "E" 0 0 787 28 759 96.4 95.7 96.3 98.5 99.0 nil)
    (Row. "Hospital F" "F" 0 0 835 22 813 97.4 96.0 96.8 98.7 99.2 nil)
    (Row. "Hospital G" "G" 0 0 890 22 868 97.5 95.5 96.2 98.3 98.8 nil)
    (Row. "Hospital H" "H" 0 0 976 24 952 97.5 96.5 97.1 98.9 99.2 nil)
    (Row. "Hospital I" "I" 0 0 1056 26 1030 97.5 96.3 96.9 98.7 99.1 nil)
    (Row. "Hospital J" "J" 0 0 1107 11 1096 99.0 96.5 97.0 98.7 99.1 nil)
    (Row. "Hospital K" "K" 0 0 1146 31 1115 97.3 95.8 96.4 98.3 98.7 nil)
    (Row. "Hospital L" "L" 0 0 1204 41 1163 96.6 95.6 96.3 98.1 98.6 nil)
    (Row. "Hospital M" "M" 0 0 1481 36 1445 97.6 95.3 95.9 97.7 98.1 nil)
    (Row. "Hospital N" "N" 0 0 1881 38 1843 98.0 96.5 97.0 98.4 98.7 nil)]

   :variation-4
   [(Row. "Hospital A" "A" 0 0 204 4 200 98.0 95.1 96.6 100.0 100.0 nil)
    (Row. "Hospital B" "B" 0 0 482 11 471 97.7 94.8 95.9 98.8 99.4 nil)
    (Row. "Hospital C" "C" 0 0 582 14 568 97.6 95.4 96.2 98.8 99.3 nil)
    (Row. "Hospital D" "D" 0 0 678 18 660 97.3 95.1 96.0 98.4 99.0 nil)
    (Row. "Hospital E" "E" 0 0 787 35 752 95.6 95.7 96.3 98.5 99.0 nil)
    (Row. "Hospital F" "F" 0 0 835 22 813 97.4 96.0 96.8 98.7 99.2 nil)
    (Row. "Hospital G" "G" 0 0 890 22 868 97.5 95.5 96.2 98.3 98.8 nil)
    (Row. "Hospital H" "H" 0 0 976 24 952 97.5 96.5 97.1 98.9 99.2 nil)
    (Row. "Hospital I" "I" 0 0 1056 26 1030 97.5 96.3 96.9 98.7 99.1 nil)
    (Row. "Hospital J" "J" 0 0 1107 13 1094 98.8 96.5 97.0 98.7 99.1 nil)
    (Row. "Hospital K" "K" 0 0 1146 31 1115 97.3 95.8 96.4 98.3 98.7 nil)
    (Row. "Hospital L" "L" 0 0 1204 47 1157 96.1 95.6 96.3 98.1 98.6 nil)
    (Row. "Hospital M" "M" 0 0 1481 27 1454 98.2 95.3 95.9 97.7 98.1 nil)
    (Row. "Hospital N" "N" 0 0 1881 38 1843 98.0 96.5 97.0 98.4 98.7 nil)]

   :variation-5
   [(Row. "Hospital A" "A" 0 0 200 6 194 97.0 95.0 96.5 100.0 100.0 nil)
    (Row. "Hospital B" "B" 0 0 482 11 471 97.7 94.8 95.9 98.8 99.4 nil)
    (Row. "Hospital C" "C" 0 0 582 14 568 97.6 95.4 96.2 98.8 99.3 nil)
    (Row. "Hospital D" "D" 0 0 678 18 660 97.3 95.1 96.0 98.4 99.0 nil)
    (Row. "Hospital E" "E" 0 0 787 28 759 96.4 95.7 96.3 98.5 99.0 nil)
    (Row. "Hospital F" "F" 0 0 835 22 813 97.4 96.0 96.8 98.7 99.2 nil)
    (Row. "Hospital G" "G" 0 0 890 22 868 97.5 95.5 96.2 98.3 98.8 nil)
    (Row. "Hospital H" "H" 0 0 976 24 952 97.5 96.5 97.1 98.9 99.2 nil)
    (Row. "Hospital I" "I" 0 0 1056 26 1030 97.5 96.3 96.9 98.7 99.1 nil)
    (Row. "Hospital J" "J" 0 0 1107 17 1090 98.5 96.5 97.0 98.7 99.1 nil)
    (Row. "Hospital K" "K" 0 0 1146 31 1115 97.3 95.8 96.4 98.3 98.7 nil)
    (Row. "Hospital L" "L" 0 0 1204 41 1163 96.6 95.6 96.3 98.1 98.6 nil)
    (Row. "Hospital M" "M" 0 0 1481 36 1445 97.6 95.3 95.9 97.7 98.1 nil)
    (Row. "Hospital N" "N" 0 0 1881 67 1814 96.4 96.5 97.0 98.4 98.7 nil)]})


(def hospital-metadata
  {:RVB [[:a (core/href "http://belfasttrust-cardiacsurgery.hscni.net/about-cardiac-surgery/location/" :target "_blank")
          "Belfast, Royal Victoria Hospital"]
         [:a (core/href "http://www.childrensheartbeattrust.org/" :target "_blank") "Children’s Heartbeat Trust"]]

   :HSC [[:a (core/href "http://theharleystreetclinic.co.uk/cardiac/congenital-heart-care" :target "_blank")
          "London, Harley Street Clinic"]]

   :GRL [[:a (core/href "http://www.leicestershospitals.nhs.uk/ " :target "_blank")
          "Leicester, Glenfield Hospital"]
         [:a (core/href "http://www.heartlink-glenfield.org.uk/" :target "_blank") "Heart Link"]]

   :FRE [[:a (core/href "http://www.newcastle-hospitals.org.uk/services/cardiothoracic_services_childrens-heart-unit.aspx" :target "_blank")
          "Newcastle, Freeman Hospital"]
         [:a (core/href "http://chuf.org.uk/" :target "_blank") "Newcastle Children’s Heart Unit Fund"]]

   :RHS [[:a (core/href "http://www.nhsggc.org.uk/locations/hospitals/the-royal-hospital-for-children-glasgow/"
                        :target "_blank")
          "Glasgow, Royal Hospital for Children"]
         [:a (core/href "http://www.youngheart.info/" :target "_blank")
          "The Scottish Association for Children with Heart Disorders (SACHD)"]]

   :BRC [[:a (core/href "http://www.uhbristol.nhs.uk/patients-and-visitors/your-hospitals/bristol-royal-hospital-for-children/the-paediatric-cardiac-service/"
                        :target "_blank") "Bristol Royal Hospital for Children"]
         [:a (core/href "http://www.heartfamilies.org.uk/" :target "_blank") "Heart Families South West"]
         [:a (core/href "http://www.heartcircle.org/" :target "_blank") "South West Children’s Heart Circle"]]

   :SGH [[:a (core/href "http://www.uhs.nhs.uk/OurServices/Childhealth/Childrenscongenitalcardiacservices/Childrenscongenitalcardiacservices.aspx"
                        :target "_blank") "Southampton, Wessex Cardiothoracic Centre (Southampton Children’s Hospital)"]
         [:a (core/href "http://www.oceanward.co.uk/" :target "_blank") "Families of Ocean Ward"]]

   :LGI [[:a (core/href "http://www.leedsth.nhs.uk/a-z-of-services/childrens-cardiology/" :target "_blank") "Leeds General Infirmary"]
         [:a (core/href "http://chsf.org.uk/" :target "_blank") "The Childrens Heart Surgery Fund"]]

   :OLS [[:a (core/href "http://www.olchc.ie/" :target "_blank")
          "Dublin, Our Lady's Children's Hospital"]
         [:a (core/href "http://www.heartchildren.ie/our-lady%E2%80%99s-children%E2%80%99s-hospital-crumlin" :target "_blank")
          "Heart Children Ireland"]]

   :NHB [[:a (core/href "http://www.rbht.nhs.uk/" :target "_blank") "London, Royal Brompton Hospital"]
         [:a (core/href "http://www.thebromptonfountain.org.uk/" :target "_blank") "The Brompton Fountain"]]

   :ACH [[:a (core/href "http://www.alderhey.nhs.uk/departments/cardiac/" :target "_blank") "Liverpool, Alder Hey Hospital"]
         [:a (core/href "http://www.alderheycharity.org/" :target "_blank") "Alder Hey Charity"]
         [:a (core/href "https://heartchild.info/web/" :target "_blank") "Children’s Heart Association"]]

   :GUY [[:a (core/href "http://www.evelinalondon.nhs.uk/our-services/hospital/heart-services/overview.aspx" :target "_blank")
          "London, Evelina London Children's Hospital"]
         [:a (core/href "http://www.echo-evelina.org.uk/" :target "_blank") "ECHO – Evelina Children’s Heart Organisation"]]

   :BCH [[:a (core/href "http://www.bch.nhs.uk" :target "_blank") "Birmingham Children’s Hospital"]
         [:a (core/href "http://www.youngatheart.org.uk/" :target "_blank") "Young at Heart"]
         [:a (core/href "http://www.bch.org.uk" :target "_blank") "Birmingham Children’s Hospital Charity"]]

   :GOS [[:a (core/href "http://www.gosh.nhs.uk/medical-information/clinical-specialties/cardiothoracic-surgery-information-parents-and-visitors"
                        :target "_blank") "London, Great Ormond Street Hospital for Children"]
         [:a (core/href "http://www.gosh.org/" :target "_blank") "Great Ormond Street Hospital Charity GOSHCC"]]})

(defn active-h-codes []
  (map (comp keyword :h-code) ((:datasource @core/app) datasources)))

(defn sorted-active-h-codes []
  (map first (sort-by second (map (fn [hosp-meta] [(keyword (:h-code hosp-meta)), (:h-name hosp-meta)]) ((:datasource @core/app) datasources))))
  )

(def unassoc-charity-list
  [
   [:span [:a (core/href "//www.amelia-matters.org.uk" :target "_blank") "Amelia Matters:"] " supporting children born with congenital
    heart disease."]

   [:span [:a (core/href "//www.arc-uk.org" :target "_blank") "Antenatal Results and Choices (ARC):"] " helping parents and healthcare
    professionals through antenatal screening and its consequences"]

   [:span [:a (core/href "//www.heartrhythmcharity.org.uk" :target "_blank") "Arrhythmia Alliance:"] " improving the diagnosis, treatment
    and quality of life for all those affected by arrhythmias."]

   [:span [:a (core/href "//www.benwilliamstrust.org.uk" :target "_blank") "Ben Williams Trust:"] " supports children with abnormal heart
    rhythms (arrhythmias) and their families "]

   [:span [:a (core/href "//www.bhf.org.uk" :target "_blank") "British Heart Foundation:"] " working to fight against cardiovascular
    disease."]

   [:span [:a (core/href "//www.c-r-y.org.uk" :target "_blank") "Cardiac Risk in the Young (CRY):"] " preventing young sudden cardiac
    deaths through awareness, screening and research, and supporting affected families."]

   [:span [:a (core/href "//www.cardiomyopathy.org" :target "_blank") "Cardiomyopathy UK:"] " providing support to patients and families
    and promoting research for the disease cardiomyopathy"]

   [:span [:a (core/href "//www.heartchild.info" :target "_blank") "Children’s Heart Association:"] " supporting families of children
    with a heart condition."]

   [:span [:a (core/href "//www.chfed.org.uk" :target "_blank") "Children’s Heart Federation:"] " the main umbrella body for British
    congenital heart disease charities and voluntary organisations."]

   [:span [:a (core/href "//www.chd-uk.co.uk" :target "_blank") "Congenital Heart Defects UK:"] " educating and raising awareness of
    congenital heart defects and supporting patients and families."]

   [:span [:a (core/href "//www.cafamily.org.uk" :target "_blank") "Contact a family:"] " a national charity for families with disabled
    children, providing information, advice and support."]

   [:span [:a (core/href "//www.dhg.org.uk" :target "_blank") "Down’s Heart Group:"] " a charity offering support and information
    relating to heart conditions associated with Down's Syndrome."]

   [:span [:a (core/href "//www.heartline.org.uk" :target "_blank") "Heartline:"] " supports children with heart disorders and their
    families, whatever the condition wherever it is treated"]

   [:span [:a (core/href "//www.lagans.org.uk" :target "_blank") "Lagan’s Foundation:"] " offers home respite and support services for
     young children with heart defects or feeding issues."]

   [:span [:a (core/href "//www.lhm.org.uk" :target "_blank") "Little Hearts Matter:"] " offers support and information, and raises
    awareness of those affected when a child has only half a heart."]

   [:span [:a (core/href "//www.maxappeal.org.uk" :target "_blank") "Max Appeal:"] " Supports families affected by DiGeorge syndrome,
     VCFS and 22q11.2 deletion."]

   [:span [:a (core/href "//www.younghearts.org.uk" :target "_blank") "Oxford Young Hearts:"] "  support children with heart conditions
     and their families in the counties served by the Oxford University Hospitals NHS Trust. "]

   [:span [:a (core/href "//www.patchesheartgroup.org" :target "_blank") "Patches Heart Group:"] " a support group for children with
    congenital heart defects / aquired heart conditions and their families in the community."]

   [:span [:a (core/href "//www.youngheart.info" :target "_blank") "The Scottish Association for Children with Heart Disorders
     (SACHD):"] " supports children and young adults with congenital heart disorders, and the families in Scotland."]

   [:span [:a (core/href "//www.thesf.org.uk" :target "_blank") "Somerville Foundation:"] " supports young people and adults born with a
    heart condition."]

   [:span [:a (core/href "//www.22crew.org" :target "_blank") "The 22 Crew:"] "  A charity providing resources for patients with 22q
     deletion and their families."]

   [:span [:a (core/href "//www.tinytickers.org" :target "_blank") "Tiny tickers:"] " improving the detection, care and treatment of
    babies with serious heart conditions"]
   ])

(defn render-charity-list []
  [:div
   [:h3 "Charities"]
   [:ul.charities
    (for [charity unassoc-charity-list]
      [:li charity])]

   [:h3 "Hospitals and hospital-associated charities"]
   [:ul.charities
    (for [h-code (sorted-active-h-codes)]
      [:li [:b (first (h-code hospital-metadata))]
       [:ul
        (for [charity (rest (h-code hospital-metadata))]
          [:li charity])]])]])

;;;
;; Comment on the meaning of each range. These texts appear in bar-chart tooltips
;;;

(def bar-comments
  {:inner      "If the black dot is in this area then there is no evidence that chances of survival in the hospital were
   different from what is predicted"

   :outer-high "If the black dot is in this area then there is some evidence that chances of survival in the hospital
    were higher than predicted"                             ; " [add link]"

   :outer-low  "If the black dot is in this area then there is some evidence that chances of survival in the hospital
    were lower than predicted."                             ; " [add link]"

   :low        "If the black dot is in this area then there is strong evidence that chances of survival in the hospital
    were lower than predicted."                             ; " [add link]"

   :high       "If the black dot is in this area then there is strong evidence that chances of survival in the hospital
    were higher than predicted."                            ; " [add link]"
   })

;;;
;; Comment on the meaning of the dot when it appears in the given range.
;; These texts appear in a concluding remark in each hospital's popup.
;;;
#_(def dot-comments
    {:inner      "There is no evidence that chances of survival in the hospital were different from predicted."

     :outer-high "There is some evidence that chances of survival in the hospital were higher than predicted." ; " [;todo 1/2]"

     :outer-low  "There is some evidence that chances of survival in the hospital were lower than predicted." ; " [:todo to 1/4]"

     :low        "There is strong evidence that chances of survival in the hospital were lower than predicted." ; " [:todo to 1/4]"

     :high       "There is strong evidence that chances of survival in the hospital were higher than predicted." ; " [todo: to 1/2]"
     })

;;;
;; Comment on the meaning of the dot when it appears in the given range.
;; These texts appear in a concluding remark in each hospital's popup.
;;;
(rum/defc inner-comment [_]
  [:span "There is no evidence that chances of survival in the hospital were different from predicted."])

(rum/defc high-comment [close-modal]
  [:span "There is some evidence that chances of survival in the hospital were "
   [:a (core/href "faq/1/2" close-modal) "higher than predicted."]])

(rum/defc low-comment [close-modal]
  [:span "There is some evidence that chances of survival in the hospital were "
   [:a (core/href "faq/1/4" close-modal) "lower than predicted."]])

(rum/defc outer-low-comment [close-modal]
  [:span "There is strong evidence that chances of survival in the hospital were "
   [:a (core/href "faq/1/4" close-modal) "lower than predicted."]])

(rum/defc outer-high-comment [close-modal]
  [:span "There is strong evidence that chances of survival in the hospital were "
   [:a (core/href "faq/1/2" close-modal) "higher than predicted."]])




(def glossary
  {:risk-adjustment
   {:title "Risk Adjustment"
    :body  "Since hospitals treat different children and different children have different chances of survival, we cannot
    not expect hospitals to have the same survival rate. To understand a hospital’s survival rate we need to put its
    rate into the context of what children that hospital treated. Combining different risk factors (such as age,
    weight, complexity of heart disease) into a single formula to calculate the predicted range means we can put a
    hospital’s survival rate into context. This overall process is called “risk adjustment” and the formula is a
    “risk adjustment method”. "}

   :survival-rate
   {:title "Survival rate"
    :body  "The percentage of operations where the child survived at least 30 days after their operation."}

   :unforeseen-factors
   {:title "Unforeseeable factors"
    :body  [:div
            [:p "It is impossible to predict precisely what is going to happen in an individual operation. This is partly due
    to the inevitable inability to predict the future with certainty – all people are physically unique and will react
    slightly differently to medicines, anaesthetic, surgery and no heart problem is exactly the same as another."]

            [:p "There are also factors that we suspect may influence the outcome but cannot be included in the statistical
  formula because no routine audit data on them is collected, for instance the size of a hole in the heart."]
            [:p "Together, we call these all “unforeseeable factors”."]]}

   :predicted-range
   {:title "Predicted and Extended Predicted range:"
    :body  [:div
            [:p "We expect a hospital's overall survival rate to lie within its " [:em "predicted range"] " 19 times out of 20. "]
            [:p "We expect a hospital's overall survival rate to lie within its " [:em "extended predicted range"] " 998 times out of 1000. "]
            [:p "An illustration of how we present a hospital's survival rate (black dot) in
                                     the context of its predicted range (dark blue bar) and extended prediction range
                                     (light blue bar) is given below:"]]}
   })

(def title "Everything else")

(def faq-sections
  [{:section "Background"
    :faqs
             [{:title       "Why do some children need heart surgery?"
               :short-title "Why the need?"
               :glossary    []
               :body
                            [:div
                             [:p "About 6500—7500 babies are born with a heart defect (called congenital heart disease) in the UK and
       Ireland every year. Congenital heart disease covers a wide range of problems from the relatively minor (such as a
       small hole in the heart) to more severe conditions where a child needs specialist hospital care. About half of
       all children born with a heart defect will need heart surgery at some stage in their childhood. Children can also
       develop problems with their heart as they grow up which require hospital care (called acquired heart disease). "]
                             [:p "Read more about "
                              [:a (core/href "http://www.chfed.org.uk/how-we-help/information-service/heart-conditions/" :target "_blank")
                               "different heart conditions"] " and caring for children with heart conditions on the "
                              [:a (core/href "http://www.chfed.org.uk/how-we-help/information-service/caring-for-heart-children/" :target "_blank")
                               "Children’s Heart Federation’s website."]]]}



              {:title        "Why are survival rates after children's heart surgery monitored and published?"
               :short-title  "Why monitor survival?"
               :glossary     [:survival-rate]
               :short-answer "The NHS monitors survival rates after children’s heart surgery to check if recent survival rates at
      any hospital are much lower than predicted."
               :body
                             [:div
                              [:p [:a (core/href "http://webarchive.nationalarchives.gov.uk/20090811143745/http:/www.bristol-inquiry.org.uk/final_report/the_report.pdf"
                                                 :target "_blank")]
                               [:img.thumbnail.pull-left.w100 (core/isrc "assets/bristol.png")]
                               "In the 1990s, problems were found with the standard of care for children having heart surgery at the Bristol
                               Royal Infirmary. The proportion of children who died after surgery at Bristol was much higher than other UK
                               hospitals. There was a formal inquiry into what happened ("
                               [:a (core/href "http://webarchive.nationalarchives.gov.uk/20090811143745/http:/www.bristol-inquiry.org.uk/final_report/the_report.pdf"
                                              :target "_blank") "The Bristol Inquiry 2001"]
                               "), which led to a number of changes, including a new compulsory national reporting
                               system. Since 2007, the proportions of children surviving to 30 days after heart surgery
                               for most types of operations have been published every year. The overall survival rates
                               after children’s heart surgery for each hospital have been published since 2013. "
                               [:a (core/href "faq/1/4") "When there is some, or strong, evidence"] " that chances of
                               survival at a hospital were lower than expected, the results are
                               checked further by the hospital and the national audit body ("
                               [:a (core/href "http://www.ucl.ac.uk/nicor/audits/congenital") "NCHDA"] ")."]

                              [:p "The UK and Ireland now have one of the strongest monitoring programmes in the world. Since reporting started, "
                               [:a (core/href "http://www.bbc.co.uk/news/health-32162803" :target "_blank") "survival rates have been improving"] "
        and now " [:strong "over 97% of children survive to at least one month after surgery."]]]}

              {:title        "How are survival rates monitored?"
               :short-answer [:p "Every year the national audit body (" [:a (core/href "http://www.ucl.ac.uk/nicor/audits/congenital" :target "_blank")
                                                                         "National Congenital Heart Disease Audit"] ") publishes overall survival rates for each
               hospital for the previous 3 years. Each hospital’s survival rates are compared to what is predicted
               for that hospital by a statistical formula."]
               :glossary     [:survival-rate :predicted-range]
               :body
                             [:div
                              [:p [:a (core/href "http://www.ucl.ac.uk/nicor/patients" :target "_blank")
                                   [:img.thumbnail.pull-left.w100 (core/isrc "assets/nicor.png")]]
                               "Since 2007, the national audit body (" [:a (core/href "http://www.ucl.ac.uk/nicor/audits/congenital" :target "_blank") "NCHDA"] ") has published survival rates for certain
                               " [:a (core/href "https://nicor4.nicor.org.uk/CHD/an_paeds.nsf/WBenchmarksYears?openview&RestrictToCategory=2014&start=1&count=500"
                                                :target "_blank") "types of procedure"] " (now over 70 different procedures) at each hospital. But the "
                               [:a (core/href "http://www.ucl.ac.uk/nicor/audits/congenital") "NCHDA"] " did not
                               publish overall survival rates for each hospital until 2013, because there was no clear way to put
                               overall survival rates for each hospital into context
                               (see " [:a (core/href "intro") "What, Why and How?"] ").

                               However "
                               [:a (core/href "https://www.ucl.ac.uk/operational-research/AnalysisTools/PRAiS" :target "_blank") "researchers"]
                               " have now made this possible by creating a statistical formula that gives a predicted
                               chance of a child’s 30-day survival, taking the complexity of their medical problems
                               into account. So since 2013, NCHDA has used this formula to calculate the predicted
                               and extended predicted ranges of survival for each hospital, so that the hospital’s
                               survival rate can be interpreted in the context of the children it treated
                               (see " [:a (core/href "intro") "What, Why and How?"] ").  "

                               ]
                              [:p "The predicted range
                              is the range in which we expect to see each hospital’s survival rate the majority of
                              the time (see our video " [:a (core/href "faqs") "How is the predicted range calculated?"] "). "
                               "The predicted range is calculated using the " [:strong "same"] " statistical formula for all hospitals and
       this prediction is " [:strong "not"] " influenced by what the survival rate at a hospital actually was (see our short video "
                               [:a (core/href "faqs") [:i.fa.fa-video-camera] " How is the predicted range calculated?"] "). " [:a (core/href "faq/1/4") "See what happens if
                               the survival rate falls"] " below its predicted range."]

                              [:p "Operations that occur within 30 days of each other are treated as a single operation when reporting overall
       survival."]
                              [:p [:a (core/href "http://www.ucl.ac.uk/nicor/audits/congenital") "NCHDA"] " publishes all its reports "
                               [:a (core/href "https://nicor4.nicor.org.uk/chd/an_paeds.nsf/vwContent/Analysis%20Documents?Opendocument" :target "_blank") "here"] ". "]]}

              {:title        "Where is this data from?"
               :short-answer "The data on each child’s operation and what was wrong with their heart comes from the hospital’s
      records. The data on whether a child survived to 30 days after their operation comes from both the hospital and
      the Office of National Statistics."
               :glossary     []
               :body
                             [:div
                              [:p "Each hospital in the UK and Ireland must collect data on every surgery or intervention carried out on a
       child under 16 years old. Every three months, hospitals must submit this data to the national audit body, "
                               [:a (core/href "http://www.ucl.ac.uk/nicor/audits/congenital" :target "_blank") "NCHDA"] " (The National Institute for
        Cardiovascular Outcomes Research). NCHDA sets out exactly what data is collected and each hospital undergoes
        independent checks of the quality of their submitted data. NCHDA also reports to the UK Department of Health,
        the Care Quality Commission (CQC) and other NHS regulatory bodies."]

                              [:p [:a (core/href "http://www.ucl.ac.uk/nicor/audits/congenital" :target "_blank") "NCHDA"]
                               " tracks the survival of these children by linking to the national register of deaths
                               using patients’ NHS number (in England and Wales) and also from hospital records."]]}]}

   {:section "Understanding the predicted range"
    :faqs
             [{:title        "Why is a different survival range predicted for each hospital?"
               :short-title  "Why do predictions differ?"
               :short-answer "The predicted range of survival is the range in which we expect to see each hospital’s
               survival rate the majority of the time. It depends only on the complexity of the medical problems of the
               children who had heart operations. Each hospital treats different children and so each hospital will have
               a different predicted range."
               :glossary     [:survival-rate :predicted-range]
               :body
                             [:div
                              [:p "Heart disease in children covers a wide range of disorders, from relatively minor to more serious
       conditions. This affects a child's chances of survival, as do other factors such as age, weight and other health
       problems."]

                              [:p "Some hospitals take on more cases that are particularly complicated, meaning they tend to operate on
       children with a lower chance of survival. Each hospital will also treat different children from year to year,
       so we expect any hospital’s survival rate to vary from year to year. We therefore would not expect all
       hospitals to have the same survival rates and it is not valid to simply compare hospitals' survival rates.
       It is " [:i "only valid"] " to compare a hospital’s survival rate to its own predicted range."]

                              (comment "*** POSSIBLE IMAGES ***** - a snapshot from animation from factors going into the formula/machine and
       coming out with a predicted range? ")]}

              {:title        "Why does the width of the predicted range differ between hospitals?"
               :glossary     [:unforeseen-factors :survival-rate :predicted-range]
               :short-answer "If a hospital does fewer operations, unforeseeable factors have a bigger influence on its overall
      survival rate. Therefore the predicted range is wider for hospitals that do fewer operations."

               :body
                             [:div
                              [:p "A predicted range of survival is calculated individually for each hospital, taking
                              into account the children it treated.  If the chances of survival at that hospital are
                              as predicted, then we expect the hospital’s survival rate to lie within the predicted
                              range 19 times out of 20. The width of the predicted range varies with the number of
                              operations a hospital has done, because of unforeseeable factors that occur. We know
                              that sometimes unforeseeable factors will affect a child’s survival – though, of course,
                              we cannot predict exactly what they will be and when they will occur. In a smaller
                              hospital that performs 100 operations per year, even if one more or one fewer child
                              survives due to unforeseeable factors, this could make a big difference to that
                              hospital’s overall survival rate. However such unforeseeable factors would have less
                              influence on the overall survival rate of a larger hospital performing 1000 operations.
                              This is why hospitals doing more operations have a narrower predicted range than hospitals
                              doing fewer operations."]

                              #_[:p "The predicted range for each hospital shows the range where we expect to see the observed survival rate if
       the hospital is performing as predicted by the formula, regardless of how many operations it did or what children
       it treated. The predicted range varies with the number of operations a hospital has done, and has to do with
       the unforeseeable factors that occur. We know that sometimes unforeseeable factors will affect a child’s
       survival – though, of course, we cannot predict exactly what they will be and when they will occur. In a
       smaller hospital performing 100 operations per year, even one more or fewer child surviving due to unforeseeable
       factors could make a big difference to its overall survival rate, whereas it would have less impact on the
       overall survival rate of a larger hospital performing 1000 operations. This is why hospitals doing more operations
       have a narrower predicted range than hospitals doing fewer operations."]]}

              {:title        "When looking at just one hospital, what does it mean if its survival is outside its
              predicted range?"
               :short-answer "There are four possible reasons why a hospital might be outside its predicted range:
               inaccurate data, a formula that for some reason doesn’t work well for that hospital, just by chance
               (1 time in 20), or because the chances of survival at that hospital are different to what is predicted. "
               :glossary     [:survival-rate :predicted-range]
               :body
                             [:div

                              [:p "Possible reasons for a hospital’s survival rate being outside its predicted range of survival:"]

                              [:h4 "1. Inaccurate data"]
                              [:p "Each hospital and the Office of National Statistics supply data on each child to the national audit body.
       Although the " [:a (core/href "faq/2/1") "data submitted is of very high quality"] ", there will always be some
       inaccuracies in such large datasets. If a hospital submits data where some of the data is very wrong or missing, then this will result in a wrong predicted range."]

                              [:h4 "2. The formula doesn’t work well for that hospital"]
                              [:p "The statistical formula is applied to all operations at that hospital to calculate its overall predicted
       range of survival. Although " [:a (core/href "faq/2/0") "the formula is as good as we can currently get it"] ", it is not perfect.
       There will always be unique features about a child that affect their
       chance of survival that are not captured by national data collection and so cannot be part of a formula (e.g. the
       size of a hole in the heart). We will never be able to capture the whole medical picture of a child in a single
       formula. That means that the predicted range is the best possible guess for what the predicted range should be.
       If a hospital happens to have operated on a lot of children with unique features that affect their chances of
       survival but are not captured by the formula, the predicted range might be unfairly high for that hospital."]

                              [:h4 "3. Chance"]
                              [:p "If the hospital’s data contains no errors and there’s no reason to think that the
                              formula shouldn’t apply well to that hospital, then in 19 times out of 20, we’d expect
                              to see a hospital’s overall survival range within the predicted range. This means that
                              the hospital’s survival rate could be outside its predicted range just by chance
                              (1 time in 20). (You can also watch " [:a (core/href "faqs") [:i.fa.fa-video-camera] " our short video"]
                               " on how the predicted range is calculated.)"]
                              #_[:p "If the hospital’s data contains no errors and there’s no reason to think that the formula shouldn’t apply
       well to that hospital, then in 19 times out of 20, we’d expect to see a hospital’s overall survival range within
       the predicted range. The means that the hospital’s survival rate could be outside its predicted range just by
       chance (1 time in 20)."]

                              [:h4 "4. The chances of survival at that hospital are different to what are predicted by the formula."]
                              [:p "The chances of survival for children at the hospital could genuinely be much higher or lower than what is
       predicted, which would make it more likely that the hospital’s survival rate would lie outside its predicted
       range. The national audit process is intended to check that no hospital in the UK and Ireland has chances of
       survival much lower than predicted. See " [:a (core/href "faq/1/4") "What happens if a hospital’s survival is below its predicted range"] " to
       find out what happens in such situations."]]}

              {:title        "When looking at ALL hospitals simultaneously, what does it mean if any of the hospitals have a survival
     rate outside their predicted range?"
               :short-answer "When looking at all 13 hospitals at once, we expect at least one hospital’s survival rate to be
      outside its predicted range about half the time."
               :glossary     [:survival-rate :predicted-range]
               :body
                             [:div
                              [:p "If we were looking at only one hospital, we’d expect its survival rate to fall
                              outside its predicted range rarely if chances of survival at the hospital match the
                              formula: only 1 time in 20 just by chance. But, if we are looking at all 13 hospitals
                              at once, we’d actually expect that at least one hospital will fall outside its range
                              just by chance about 9 times in 20."]
                              #_[:p "If we were looking at only one hospital, we’d expect its survival rate to fall outside its predicted range
       rarely if chances of survival at the hospitals match the formula: just 1 time in 20. But, if we are looking at
       all 14 hospitals at once, we’d actually expect that at least one hospital will fall outside its range just by
       chance about 8 times in 20."]

                              [:p "This is similar to the situation where you hear a friend won a good prize in a raffle. This may be
       surprising, but it becomes a lot less surprising when you hear they bought 100 tickets. Just as this gave them
       more chances of winning, the more hospitals we look at, the more likely it becomes that at least one will fall
       outside its range, even if survival at all the hospitals matches the formula."]

                              [:p "So, on average, we’d anticipate about half of NCHDA’s annual reports to have at least one hospital outside
       its range, either above or below, by chance alone."]

                              [:p "See " [:a (core/href "faq/1/4") "what happens if a hospital falls below its predicted or extended predicted range."]]

                              [:p "Considering now the " [:strong [:i "extended predicted range"]] ", if we look at all 13 hospitals at once, we’d only
       expect " [:i "any"] " one of them to be outside their extended range very rarely, just 1 time in 40. This is why
       a hospital’s survival rate being outside the extended predicted range is considered strong evidence that the
       chances of a patient surviving at that hospital are different to what is expected."]

                              #_[:p "NOTE: As of 2016, Royal Victoria Hospital in Belfast no longer operates on children with congenital heart
       disease, so there are now 13 hospitals in the UK and Ireland that perform these operations."]]}

              {:title        "What actually happens if a hospital’s survival rate is below its predicted range?"
               :short-answer "The national audit body works with the hospital to check the quality of the data. If this is fine,
      the hospital and the relevant national health service explore the processes of care at that hospital. If these
      bring to light any concerns, the health service and the hospital work together to improve care, which might
      include temporarily suspending heart surgery at that centre. "
               :glossary     [:survival-rate :predicted-range]
               :body
                             [:div
                              [:p " If a hospital’s survival rate is below its predicted range (either the main or
                              extended predicted range), everyone wants to
       be sure that there is not a potential problem in the pathway of care. It is important to either rule this out or
       start to improve care if it’s decided that this is the reason. (See also "
                               [:a (core/href "faq/1/2") "What does it mean if a hospital is outside its predicted range?"] ")."]



                              [:p "If a hospital's survival rate is below the predicted range, the "
                               [:a (core/href "https://www.ucl.ac.uk/nicor/audits/congenital/governance" :target "_blank") "National Congenital
        Heart Disease Audit Steering Committee "]
                               " is notified. The Committee in turn notifies the relevant national health service’s Medical Director and the
                               lead doctor for congenital heart disease at that hospital and a detailed examination of the hospital’s results
                               takes place. There are established and published procedures which can be put into action if the detailed
                               assessment raises concerns about care. In England, for instance, this would involve the
                               " [:a (core/href "http://www.bcs.com/pages/page_affiliate.asp?PageID=321" :target "_blank") "British
                               Congenital Cardiac Association"] " and/or the "
                               [:a (core/href "http://www.cqc.org.uk/" :target "_blank") "Care Quality Commission."]]

                              [:p " There are two main steps:"]

                              [:h4 "Step 1"]
                              [:p "The hospital is asked to recheck the data it submitted for any errors."]

                              [:h4 "Step 2"]
                              [:p "With the corrected data:"]
                              [:p "If the hospital’s survival rate is still below its predicted range but within the extended predicted range
       (like this " [:img (core/isrc "assets/below-predicted.png" :style {:height "18px"})] "), then an internal hospital review is conducted to understand whether there is cause
       for concern. "]
                              [:p "If the hospital’s survival rate is below the extended predicted range (like this " [:img (core/isrc "/assets/below-extended.png" :style {:height "18px"})] " ),
       then an external review of the hospital’s processes and results would be instigated."]

                              [:p "In all such cases, the reviews (whether internal or external) would be published online by NCHDA at the same
       time as the Annual Report."]

                              [:p "NOTE: It is quite common for one of the 13 hospitals to be outside its predicted
                              range, but it is rare for any of them to be outside the extended predicted range.
                              (See also: " [:a (core/href "faq/1/3") "Looking at ALL hospitals"] ")"]

                              [:p "The NCHDA also publishes additional reports from any hospital which has a survival
                              rate below its predicted range. These are "
                               [:a (core/href "https://nicor4.nicor.org.uk/chd/an_paeds.nsf/vwContent/Analysis%20Documents?Opendocument") "available here"]
                               ", along with the NCHDA annual reports."]

                              ]}

              {:title        "What actually happens if a hospital’s survival rate is above its predicted range?"
               :short-answer "Healthcare services are currently looking into the best way to capture learning from hospitals
               where there is strong evidence that chances of survival were higher than predicted using the formula. "
               :glossary     []
               :body         nil
               }

              {:title    "Where did the formula used to calculate the predicted range come from?"
               :body     nil

               :glossary []
               :short-answer
                         [:div
                          [:p "The national audit body uses a formula developed by researchers at Great Ormond Street Hospital and
       University College London called PRAiS (Partial Risk Adjustment in Surgery - see also the "
                           [:a (core/href "intro") "What, why, how?"] " section). The underlying methodology of this method is published in
        the " [:a (core/href "https://www.ucl.ac.uk/operational-research/AnalysisTools/PRAiS" :target "_blank") "academic
        literature"] " if you are interested in learning more details."]

                          (comment "*** picture of formula churning away at PRAIS risk factors?***")
                          ]}]}


   {:section "Limitations of these results and the data"
    :faqs
             [{:title        "Are there any limitations to using a formula?"
               :short-answer "Yes – a child’s chances of survival can never be completely captured in a single formula. "
               :glossary     []
               :body
                             [:div
                              [:p "Yes there are. While using a formula to take into account the complexity of each
                              child’s medical problems allows for fairer assessment of a hospital’s survival rate,
                              it still cannot make it completely fair. There will always be information about
                              important factors that affect a child’s chances of survival that are not routinely
                              collected for national bodies and so cannot be captured by a formula that was developed
                              using national data."]

                              [:p "Any statistical formula has to be developed on existing data and so the data will be typically at least a
       year out of date. So risk adjustment cannot adjust or account for future changes to the way data is collected
       (for instance more complete data) or new methods of surgical or medical management. Often, these statistical
       formulas are updated every few years with more up to date information (in 2016, we updated the PRAiS statistical model for the third time). "]]}


              {:title        "How reliable are the data?"
               :short-answer "The data are of very high quality and some of the best in the world."
               :glossary     []
               :body
                             [:div
                              [:p "The data come from the National Congenital Heart Disease Audit ("
                               [:a (core/href "http://www.ucl.ac.uk/nicor/audits/congenital" :target "_blank") "NCHDA"] ")
                               which is managed by the National Institute of Cardiovascular Outcomes Research ("
                               [:a (core/href "https://www.ucl.ac.uk/nicor") "NICOR"] "). "
                               ;" which collects national data for the " [:a (core/href "http://www.ucl.ac.uk/nicor/audits/congenital" :target "_blank") "UK National Heart Disease Audits"] "."
                               "All hospitals in the UK and Ireland performing heart
       surgery in children have to submit their data in a standard format to NCHDA. All hospitals are independently
       audited each year as part of a data validation process (to check the quality of the data submitted) to ensure
       that the data are of high quality."]

                              [:p "While no large dataset is perfect (it is inevitable that a few records will not be 100% accurate), this
       dataset is among the most detailed and complete in the world for children’s heart surgery."]]}

              {:title        "What are the limitations of the data?"
               :short-answer "These data do not capture all the potential medical problems a child has and only represent a
      snapshot of survival outcomes. They cannot capture the full picture of the quality of care provided by a hospital."
               :glossary     [:unforeseen-factors]
               :body
                             [:div
                              [:p "Apart from occasional inaccuracies in the data, there are other limits to what the data can tell us about
       surgery outcomes. There are factors that affect a child’s chances of survival that are not routinely collected
       for by national bodies and so cannot be captured by a formula that was developed using national data. These data are
       also snapshots in time of what happened at each hospital. A run of unforeseeable factors could cause a very good
       hospital to have worse outcomes than predicted, so we need to be careful about reading too much into results from
       any single time period."]
                              [:p "The data also can’t tell us about how or why a hospital achieved the recorded
                              results, so it cannot, by itself, tell us whether one hospital offers better or worse
                              quality care than any other. These data cannot tell you what the results are likely to be
                              next year. It also cannot tell us anything about what happens to children who never get
                              operated on for whatever reason, since data on these children is not currently submitted
                              to national audit."]]}

              {:title        "What about longer term survival and quality of life?"
               :short-answer "At the moment these are not reported. There is active research underway to work out how to report
      long term survival and quality of life for children after heart surgery but it will be some years before this
      becomes part of routine national audit."
               :glossary     []
               :body
                             [:div
                              [:p "As of 2018, national audit only monitors what happens shortly after surgery (30 days). These data cannot
       tell us about longer term (e.g. 5 year) survival, or other outcomes such as post-surgery complication rates or
       the impact of surgery on the child or their family. There is a lot of "
                               [:a (core/href "http://www.gosh.nhs.uk/medical-information/clinical-specialties/cardiothoracic-surgery-information-parents-and-visitors/research/complications-after-heart-surgery-children"
                                              :target "_blank") "active research"]
                               " going on right now (due to finish around 2018) investigating how to
             capture, interpret and publish longer term survival and complication rates so hopefully this information
             will become available in the next 5 years."]

                              #_[:p "The data also can’t tell us about how or why a hospital achieved the recorded results, so it cannot, by
       itself, tell us whether one hospital offers better or worse quality care than any other. These data cannot tell
       you what the results are likely to be next year. It also cannot tell us anything about what happens to children
       who never get operated on for whatever reason, since data on these children is not currently submitted to
       national audit. "]]
               }

              {:title        "Why is survival to 30 days after surgery used as the main survival measure?"
               :short-answer "Survival to 30 days after surgery is a more objective measure than survival to hospital discharge,
      since different types of hospital have different treatment pathways for very sick patients (for instance some will
      transfer patients to another hospital or some to local palliative care). "
               :glossary     []
               :body
                             [:div
                              [:p "Two very common measures used for looking at a hospital’s outcomes are “survival at discharge from hospital”
       and “30 day survival” after an operation. Survival at discharge is often used in other contexts as it’s the
       easiest to obtain – it is included within standard hospital records. However, survival to discharge does depend
       on the discharge procedures at that hospital. For instance, some hospitals will transfer very sick patients to
       other, more specialised hospitals, meaning that more patients will be alive at discharge from that hospital than
       the specialised hospital. Some hospitals might have close links with local palliative care services and so might
       discharge patients to these services if further treatment is considered unavailing. Again, such hospitals with
       have more patients alive at discharge compared to a hospital that look after the sickest patients until they die. "]

                              [:p "Therefore, if it is available, “30 day survival” after surgery is considered a more objective and preferable
       statistic than “survival to hospital discharge”, since it does not depend on the hospital’s discharge procedures."]

                              [:p "While " [:a (core/href "faq/2/3") "longer term survival"] " is extremely important, 30-day survival post
       surgery was initially chosen as it is more straightforward to link this outcome to a child’s surgery and
       post-operative care than a longer range survival  period which might, for instance, incorporate further treatment
       at different hospitals. The other important aspect of monitoring 30-day survival, particularly within hospitals,
       is that any worrying outcomes can be investigated very quickly since the survival outcomes are available
       (locally) within 30 days of a child’s operation."]]}]}

   {:section "My family or child"
    :faqs
             [{:title        "Can this data inform my choice of hospital?"
               :short-answer [:div
                              [:p "You can use this website to see how a hospital’s survival rate compares to its
                              predicted range, but the data cannot tell you which hospital to go to. Please discuss
                              this with your child’s clinical team or access the support offered by specialist
                              charities such as the " [:a (core/href "http://www.chfed.org.uk/" :target "_blank") "Children’s Heart Federation."]]]
               :glossary     []
               :body
                             [:div
                              [:p "On this site, you can " [:a (core/href "data") "explore the data"] " to see how the
                              different hospitals’ survival rates compare to their own predicted ranges for 3 year report periods. You can also use the "
                               [:a (core/href "https://nicor4.nicor.org.uk/CHD/an_paeds.nsf/WBenchmarksYears?openview&RestrictToCategory=2014&start=1&count=500"
                                              :target "_blank") "national audit website"]
                               " to explore how many operations of each type a hospital does and survival outcomes for each of these. However,
                               this website cannot, in itself, tell you which hospital you should go to and does not provide proof that one hospital is
                               “better” than any other. The safety or otherwise of a hospital " [:strong "cannot"] " be determined from
        survival data alone."]

                              [:p "When considering which hospital, there are many factors to take into account, including how well the
       clinical team know your child and his or her medical history, any particular medical issues that your child has
       (for instance, some hospitals specialise in treating children  with particularly complex medical problems) and
       how far the hospital is from your home."]

                              [:p "You should discuss your child’s care with their specialist cardiologist to determine the best treatment
       option for your child."]
                              [:p "You can also access the support available from national charities such as the "
                               [:a (core/href "http://www.chfed.org.uk/documents/2012/11/second-opinion-factsheet.pdf" :target "_blank") "Children’s Heart Federation"]
                               " or " [:a (core/href "http://www.lhm.org.uk/" :target "_blank") "Little Hearts Matter"]
                               " or " [:a (core/href "faq/3/2") "local charities for your specialist children’s hospital"] ".
                               These guides on "
                               [:a (core/href "http://www.chfed.org.uk/documents/2015/02/talking-to-doctors-pdf-factsheet.pdf" :target "_blank") "speaking to your child’s surgeon"]
                               " or " [:a (core/href "http://www.chfed.org.uk/documents/2012/11/second-opinion-factsheet.pdf" :target "_blank") "seeking a second opinion"]
                               ", written by the Children’s Heart Federation, might also be helpful."]]}

              {:title        "Can the published data tell me about the risks for my child?"
               :short-answer "No. Your child’s clinical team will always know more about your child than the statistical formula can."
               :glossary     []
               :body
                             [:div
                              [:p "No, the published data cannot tell you about the risk for your child specifically – this will depend on
       other factors that are not necessarily captured in the national data. Your child’s specialist cardiologist and/or
       cardiac surgeon will be able to discuss this with you. These guides on "
                               [:a (core/href "http://www.chfed.org.uk/documents/2015/02/talking-to-doctors-pdf-factsheet.pdf" :target "_blank") "speaking to your child’s surgeon"]
                               " or " [:a (core/href "http://www.chfed.org.uk/documents/2012/11/second-opinion-factsheet.pdf" :target "_blank") "seeking a second opinion"]
                               ", written by the Children’s Heart Federation, might also be helpful."]]}

              {:title    "Charities and support groups for families of children with heart problems"
               :glossary []
               :body
                         [:div
                          [:p "We have collated a list of charities and support groups for families of children with heart problems (all
       active as of 2016). Some of them specialise in certain heart conditions while others are only active in certain
       places."]

                          (render-charity-list)

                          [:p "NOTE: As of 2016, Royal Victoria Hospital in Belfast no longer operates on children with congenital heart
       disease."]]}]}

   {:section "Who developed this site and how"
    :faqs
             [{:title    "About us"
               :glossary []
               :body     [:div
                          [:.image-para
                           [:p [:img.pull-left.w150 (core/isrc "assets/ucl-logo-black.png")]
                            [:a (core/href "http://ucl.ac.uk/coru" :target "_blank") "University College London"] ": "
                            [:a (core/href "http://www.ucl.ac.uk/operational-research/the_team/#christina" :target "_blank") "Dr Christina Pagel"]
                            " is a Reader in Operational Research (a branch of applied mathematics) at University College London, applying
                            maths to problems in the NHS. She works very closely with doctors and other clinical staff, mainly at Great
                            Ormond Street Hospital, to help them use routinely collected data to improve NHS services. Her work currently
                            focuses on two areas: 1) care for children requiring heart surgery and 2) how specialist intensive care is
                            organised for children who need it (for whatever reason). "]

                           [:p "Christina helped developed a statistical method called PRAiS to let hospitals and the national audit body
        easily monitor survival outcomes after heart surgery in children. In this project, Christina worked with Sense
        about Science, the University of Cambridge, King’s College London and the Children’s Heart Federation to develop
        these online resources to help people interpret the audit body’s published results. In particular, she wrote a
        lot of the content, helped enormously by feedback from the team and the user workshops. Thanks to "
                            [:a (core/href " http://www.ucl.ac.uk/operational-research/the_team/#andrew" :target "_blank") " Dr Andrew Wilshere"] " for designing our logo. "]]

                          [:.image-para
                           [:p [:img.pull-left.w150 (core/isrc "assets/camlogo-old.png")]
                            [:a (core/href "http://understandinguncertainty.org" :target "_blank") "Professor David Spiegelhalter"]
                            " is a statistician from Cambridge University.   He has worked for many years with doctors from Great Ormond
                            Street Hospital on monitoring outcomes following surgery for congenital heart disease, and led the statistical
                            team at the Bristol Royal Infirmary Inquiry.  He is particularly interested in transparent communication, and
                            was part of the team that drew up the new national patient information leaflets for breast cancer screening.
                            For this project, David helped write the content, especially the language around the statistical formula and
                            predicted range.  "]
                           [:p
                            [:a (core/href "http://nrich.maths.org/2714" :target "_blank") "Mike Pearson"] " has created many visualisations and interactives
                            for " [:a (core/href "http://understandinguncertainty.org/" :target "_blank") "Understanding Uncertainty"] ", "
                            [:a (core/href "http://nrich.maths.org" :target "_blank") "NRICH"] ", " [:a (core/href "http://plus.maths.org" :target "_blank") "PLUS"] ", and
                            " [:a (core/href "http://wild.maths.org") "Wild.maths.org"] ". He developed this website and its
                            data visualisations, attending the focus groups and working closely with Sense about Science and the video animation team, "
                            [:a (core/href "https://qudos.com" :target "_blank") "Qudos"] "."]]

                          [:.image-para
                           [:p [:img.pull-left.w100 (core/isrc "assets/KCLlogo.gif")]
                            [:a (core/href "https://kclpure.kcl.ac.uk/portal/tim.rakow.html") "Dr Tim Rakow"] " is a Reader in Psychology
                            who studies how people make choices and how best to provide information to help people make decisions.
                            He has previously worked with the cardiologists and heart surgeons at Great Ormond Street looking at
                            their decisions about surgery, and with Professor Spiegelhalter on the public’s understanding of
                            complex statistical information. For this project, he worked with "
                            [:a (core/href "https://kclpure.kcl.ac.uk/portal/emily.1.blackshaw.html") "Emily Blackshaw"] ", another psychologist
                            at King’s College London, to run experiments that examined which types of explanation and graphics
                            would be the best ones for us to include in this website."]]

                          [:.image-para
                           [:p [:img.pull-left.w100 (core/isrc "assets/sas-logo.png")]
                            [:a (core/href "http://www.senseaboutscience.org/") "Sense About Science"] " is an
                            independent campaigning charity that challenges the misrepresentation of science and scientific
                            evidence in public life. They advocate openness and honesty about research findings and work
                            to ensure the public interest in sound science and evidence is represented and recognised in
                            public discussion and policy making."]
                           [:p "Sense about Science has collaborated on a number of highly successful Public Engagement
                           Partnerships, to help scientists to communicate their research findings simply and accurately
                           by co-developing resources with the intended audience. Two of their team, Emily Jesper, Head
                           of Partnerships and Governance and Joanne Thomas, Projects and Events Coordinator were
                           involved in this project. They provided expertise in communicating complex scientific
                           information and facilitated 8 user-testing workshops with parents, other interested users
                           and members of the public to test that the information and animations on the website are
                           clear, accessible and coherent."]]

                          [:.image-para
                           [:p [:img.pull-left.w100 (core/isrc "assets/chf-logo.png")]
                            [:a (core/href "http://www.chfed.org.uk/") "The Children’s Heart Federation"] " is the main umbrella body for British CHD charities and
         voluntary organisations. They publicised this project among their members and coordinated the involvement of
         parents of children who had heart surgery in our workshops."]]]}

              {:title    "Contact us"
               :glossary []
               :body
                         [:div
                          [:p "For general questions and comments about this website please email "
                           [:a {:href "mailto:enquiries@senseaboutscience.org"} "enquiries@senseaboutscience.org"] "."]
                          [:p "For questions about the mathematical formula used, or the predicted range, please email "
                           [:a {:href "mailto:maths@childrensheartsurgery.info"} "maths@childrensheartsurgery.info."]]
                          ; Add David's institute
                          [:p "For reporting technical problems, please email "
                           [:a {:href "mailto:technical@childrensheartsurgery.info"} "technical@childrensheartsurgery.info."]]
                          [:p "For questions about the audit process or any individual hospital's results, please contact the national audit body "
                           [:a (core/href "http://www.ucl.ac.uk/nicor/audits/congenital " :target "_blank") "NCHDA"]]]}

              {:title    "How we developed this site"
               :glossary []
               :body
                         [:div
                          [:p "We developed this site in just over a year from April 2015 until launch in June 2016. "]

                          [:p "The academic mathematicians and statisticians wrote first drafts of the content in May 2015, with feedback
       from communication experts at Sense about Science and the academic psychologists at King’s College London. Over
       the next 10 months, Sense about Science held four sets of two workshops, to test the web content. At each stage,
       one workshop was with interested potential users (e.g. press officers; health journalists; policy professionals)
       and the other workshop was with parents of children who had heart surgery. At each stage, we invited different
       individuals to attend each workshop in order to test the updated content with people who had not seen the previous
       versions. We also asked participants from past workshops to provide feedback on the animations and website as they developed. "]

                          [:p "As the website took shape in the autumn of 2015, the academic psychologists at King’s College London did
       formal tests of specific sentences and concepts discussed in the website to see if one way of explaining the
       concepts worked better than another and to pinpoint where there was possibility for confusion. We also shared
       the evolving website with the clinical community including surgeons, intensive care doctors, the national audit
       body, data experts and cardiologists, to incorporate their feedback too. "]

                          [:p "Over time, the website took shape, evolving a great deal over the year in response to the workshops, the
       psychology experiments and other feedback. We user tested everything from layout to colours to language to page
       navigation and very little remains from the original draft content. "]

                          [:p "Sense about Science have also produced a "
                           [:a (core/href "http://senseaboutscience.org/activities/public-engagement-guide/"
                                          :target "_blank") "free practical guide"] " for researchers on how to engage the public
                                based on the model we used for this website."]

                          [:p "Looking back, we cannot thank all those who gave feedback enough – this website is immeasurably better with
       their input than it would otherwise have been. Any niggles that remain are our responsibility alone.  "]

                          [:p "We would also like to thank our funder, the UK National Institute of Health Research, for encouraging us to
       think ambitiously about how to develop this site and giving us the time and money to do it. "]

                          [:h4 "Explanatory videos"]
                          [:p "Very many thanks to " [:a (core/href "http://www.therichardstonepartnership.co.uk/artist-details/leighton_pugh"
                                                                    :target "_blank") "Leighton Pugh"] " for donating his time and expertise to narrating both
                          videos. The artwork and production for both videos was with developed in collaboration with
                          " [:a (core/href "http://www.qudos-animations.co.uk/" :target "_blank") "Qudos Animations"] ". We thank them
                          for their great work and responsiveness in working to tight deadlines."]]}

              {:title        "In the media"
               :short-answer "The site launch generated a lot of media interest, some of which we've collected together here."
               :glossary     []
               :body         [:div
                              [:ul.charities
                               [:li [:a (core/href "https://www.theguardian.com/science/sifting-the-evidence/2016/jun/21/making-nhs-data-public-is-not-the-same-as-making-it-accessible-we-can-and-should-do-better")
                                     "Guardian article:"]
                                " “Making NHS data public is not the same as making it accessible – we can and should
                                do better.” by Christina Pagel, UCL and David Spiegelhalter, University of Cambridge."]

                               ]
                              [:h4 {:style {:margin-left "40px"}} "Other coverage"]
                              [:ul.charities
                               [:li "Sense about Science discuss their new public engagement guide based on this website work " [:a (core/href "https://www.nihr.ac.uk/blogs/five-steps-to-help-you-involve-the-public-in-communicating-research/7359" :target "_blank") " in this NIHR Blog."]]
                               [:li [:a (core/href "https://www.nihr.ac.uk/blogs/my-journey-through-the-five-steps-of-public-engagement-an-academics-story/7429 ") "Christina Pagel discusses"] " the five steps of public engagement and how they applied to this website in this NIHR blog."]
                               [:li [:a (core/href "http://www.bmj.com/content/353/bmj.i3539" :target "_blank") "The BMJ/News:"]
                                " “Children’s heart surgery website aims to end confusion over survival rates”"]
                               [:li [:a (core/href "http://www.thelancet.com/pdfs/journals/lancet/PIIS0140-6736(16)30888-1.pdf"
                                                   :target "_blank") " The Lancet/Editorial:"]
                                " “Communicating risk about children’s heart surgery well”"]

                               #_[:li " David and Alex Smith, a heart parent, spoke about the project on  "
                                  [:a (core/href "http://www.bbc.co.uk/programmes/b07gct24" :target "_blank") " BBC World at One, Radio 4 on 21st June (32 mins 10 secs)."]
                                  " Huge thanks to Alex for doing this great interview. "]
                               #_[:li "David also spoke on " [:a (core/href "http://www.bbc.co.uk/programmes/p03xmzf7" :target "_blank") "BBC Radio Cambridge"] " on Wed 22nd June (1hr 40 mins). "]
                               [:li "Sense about Science wrote about why involving the patients and families, and involving them early, matters, on "
                                [:a (core/href "http://www.amrc.org.uk/blog/involving-parents-and-patients-in-public-engagement-a-humbling-and-invaluable-experience"
                                               :target "_blank") "the Association of Medical Research Charities’s blog"] "."]
                               [:li "Christina shared her insights from the experience and provides tips for other researchers in a "
                                [:a (core/href "https://www.nihr.ac.uk/05-development/newsroom/blog-DO-NOT-DELETE/insights-on-building-a-website-to-help-people-interpret-childrens-heart-surgery-data.htm"
                                               :target "_blank") "National Institute for Health Research (NIHR) blog"] "."]
                               [:li [:a (core/href "https://www.statslife.org.uk/news/2919-new-online-tool-makes-heart-surgery-data-more-accessible"
                                                   :target "_blank") "The Royal Statistical Society"]
                                " blogged about the project."]
                               [:li "Tim Rakow, KCL shared his experience in a "
                                [:a (core/href "http://www.kcl.ac.uk/ioppn/news/records/2016/June/Parent-led-tool-opens-up-NHS-childrens-heart-surgery-data-to-families-.aspx" :target "_blank")
                                 "KCL news piece"] "."]
                               [:li
                                [:a (core/href "http://www.cam.ac.uk/research/news/parent-led-tool-opens-up-nhs-childrens-heart-surgery-data-to-families" :target "_blank")
                                 "University of Cambridge"] " “Parent-led tool opens up NHS children's heart surgery data to families” "]
                               [:li
                                [:a (core/href "http://www.chfed.org.uk/understanding-childrens-heart-surgery-outcomes/" :target "_blank") "Children’s Heart Federation explain:"]
                                " “ …parents’ views have been at the heart of developments that today launch a parent-led tool opening up NHS children’s heart surgery data to families.”"]
                               [:li
                                [:a (core/href "https://www.rcsed.ac.uk/news-public-affairs/news/2016/june/understanding-children-s-heart-surgery-outcomes" :target "_blank") "Royal College of Surgeons of Edinburgh:"] " “Understanding Children’s Heart Surgery Outcomes”"]
                               [:li
                                [:a (core/href "http://www.insight.mrc.ac.uk/2016/07/29/beyond-open-making-data-accessible/" :target "_blank") "Insight: Medical Research Council"] " “Beyond open: making your data accessible”"]
                               [:li
                                [:a (core/href "http://www.mumsnet.com/Talk/guest_posts/2699023-Guest-post-I-hope-weve-helped-others-preparing-for-their-childs-heart-surgery" :target "_blank") "Mumsnet guest post:"] " “I hope we've helped others preparing for their child's heart surgery” by Alex Smith"]
                               [:li
                                [:a (core/href "https://understandinguncertainty.org/exploring-language-chance-sensitive-context" :target "_blank") "Understanding Uncertainty:"] " “Exploring the language of chance in a sensitive context” by David Spiegelhalter"]
                               [:li
                                [:a (core/href "https://www.youtube.com/watch?v=-NebRpbMTK8" :target "_blank") "Communicating Risks Reactively:"] " A video of Mike Pearson's talk about the development of this site which was presented at Euroclojure 2016."]
                               [:li [:a (core/href "https://www.telegraph.co.uk/health-fitness/doctors-diary/doctors-diary-theres-another-side-to-the-closure-of-childrens-he/amp/")
                                     "Telegraph article:"]
                                " “Doctor's Diary: There's another side to the closure of children's heart surgery wards” by Michael Fitzpatrick"]
                               ]
                              ]
               }]}

   #_{:section "Further resources about understanding clinical data"
      :faqs
               [{:title    "Suggestions welcome"
                 :glossary []
                 :body
                           [:div
                            [:p "Yet to be written."]]}]}

   {:section     "Glossary of terms used"
    :is-glossary true
    :faqs
                 [{:glossary-entry :survival-rate
                   :title          "Survival rate"
                   :body           "The percentage of operations where the child survived at least 30 days after their operation."}
                  {:glossary-entry :unforeseen-factors
                   :title          "Unforeseeable factors"
                   :body           "It is impossible to predict precisely what is going to happen in an individual operation. This is partly
      due to the inevitable inability to predict the future with certainty – all people are physically unique and will
      react slightly differently to medicines, anaesthetic, surgery and no heart problem is exactly the same as another. 
      Our inability to predict precisely is also partly because there are factors that we suspect may influence the
      outcome but cannot be included in the statistical method either because these factors are difficult to define or
      no routine data on them is collected. Together, we call these all “unforeseeable factors”."}
                  {:glossary-entry :predicted-range
                   :title          "Predicted and Extended Predicted Range:"
                   :body           [:div
                                    [:p "We expect a hospital's overall survival rate to lie within its predicted range 19 times out of 20. "]
                                    [:p "We expect a hospital's overall survival rate to lie within its " [:strong "extended"] " predicted range 998 times out of 1000. "]
                                    [:p "An illustration of how we present a hospital's survival rate (black dot) in
                                     the context of its predicted range (dark blue bar) and extended prediction range
                                     (light blue bar) is given below:"]]}]}])

;;;
;; These theme colours are selectable in the footer (at least till we decide on one)
;;
;; keep :low and :high invisible - anything else looks wrong.
;;;
(def colour-map-options
  [;; theme 0
   {:low        "rgba(255,255,255,0)"
    :inner      "#fc8d59"
    :outer-low  "#efdf11"
    :outer-high "#efdf11"
    :high       "rgba(255,255,255,0)"
    :header     "#91bfdb"
    :dot        "black"
    }
   ;; theme 1
   {:low        "rgba(255,255,255,0)"
    :inner      "#efdf11"
    :outer-low  "#fc8d59"
    :outer-high "#fc8d59"
    :high       "rgba(255,255,255,0)"
    :header     "#fc8d59"
    :dot        "black"}
   ;; theme 2
   {:low        "rgba(255,255,255,0)"
    :inner      "#7fcdbb"
    :outer-low  "#2c7fb8"
    :outer-high "#2c7fb8"
    :high       "rgba(255,255,255,0)"
    :header     "#2c7fb8"
    :dot        "black"}
   ;; theme 3
   {:low        "rgba(255,255,255,0)"
    :inner      "#3c8fc8"
    :outer-low  "#7fcdbb"
    :outer-high "#7fcdbb"
    :high       "rgba(255,255,255,0)"
    :header     "#3c8fc8"
    :dot        "black"}
   ;; theme 4
   {:low        "rgba(255,255,255,0)"
    :inner      "#8FB4E1"
    :outer-low  "#578FD2"
    :outer-high "#578FD2"
    :high       "rgba(255,255,255,0)"
    :header     "#578FD2"
    :dot        "black"}
   ;; theme 5
   {:low        "rgba(255,255,255,0)"
    :inner      "#578FD2"
    :outer-low  "#8FB4E1"
    :outer-high "#8FB4E1"
    :high       "rgba(255,255,255,0)"
    :header     "#578FD2"
    :dot        "black"
    }
   ;; theme 6
   {:low        "rgba(255,255,255,0)"
    :inner      "#fc8d59"
    :outer-low  "#efdf11"
    :outer-high "#92DEDB"
    :high       "rgba(255,255,255,0)"
    :header     "#91bfdb"
    :dot        "black"
    }
   ;; theme 7
   {:low        "rgba(255,255,255,0)"
    :inner      "#efdf11"
    :outer-low  "#fc8d59"
    :outer-high "#7FCDBB"
    :high       "rgba(255,255,255,0)"
    :header     "#fc8d59"
    :dot        "black"}
   ;; theme 8
   {:low        "rgba(255,255,255,0)"
    :inner      "#7fcdbb"
    :outer-low  "#2c7fb8"
    :outer-high "#319470"
    :high       "rgba(255,255,255,0)"
    :header     "#2c7fb8"
    :dot        "black"}
   ;; theme 9ra
   {:low        "rgba(255,255,255,0)"
    :inner      "#8FB4E1"
    :outer-low  "#578FD2"
    :outer-high "#599464"
    :high       "rgba(255,255,255,0)"
    :header     "#578FD2"
    :dot        "black"}
   ;; theme 10
   {:low        "rgba(255,255,255,0)"
    :inner      "#3c8fc8"
    :outer-low  "#7fcdbb"
    :outer-high "#9BE45C"
    :high       "rgba(255,255,255,0)"
    :header     "#3c8fc8"
    :dot        "black"}
   ;; theme 11
   {:low        "rgba(255,255,255,0)"
    :inner      "#578FD2"
    :outer-low  "#8FB4E1"
    :outer-high "#9AE1A7"
    :high       "rgba(255,255,255,0)"
    :header     "#578FD2"
    :dot        "black"
    }
   ;; theme 12
   {:low        "#f0f0f0"
    :inner      "#566AF9"
    :outer-low  "#C9CDFC"
    :outer-high "#C9CDFC"
    :high       "#f0f0f0"
    :header     "#4F4763"
    :dot        "black"
    }
   ])
