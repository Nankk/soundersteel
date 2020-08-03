(ns frontend.style.file-list
  (:require
   [garden.core :as g]
   ))

(defn css []
  (g/css
   [:#file-list
    ;; Global grid
    [:.grid {:height              "100%"
             :width               "100%"
             :display             "grid"
             :grid-template-rows  "1fr 80px"
             :grid-template-areas (pr-str
                                   "upper"
                                   "lower")}]
    [:.upper {:grid-area "upper"}]
    [:.lower {:grid-area "lower"}]

    ;; Inner grid
    [:.grid-upper {:height              "100%"
                   :width               "100%"
                   :display             "grid"
                   :grid-template-rows  "30px 1fr"
                   :grid-template-areas (pr-str
                                         "title"
                                         "list")}]
    [:.title {:grid-area "title"}]
    [:.list {:grid-area "list"}]]))
