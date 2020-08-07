(ns frontend.style.scene-list
  (:require
   [garden.core :as g]

   [frontend.style.global :as style]))

(defn css []
  (g/css
   [:#scene-list
    [:.grid {:height                "100%"
             :width                 "100%"
             :display               "grid"
             :grid-template-rows    "30px 1fr"
             :grid-template-areas   (pr-str
                                     "title"
                                     "list")}]
    [:.title {:grid-area "title"}]
    [:.list {:grid-area "list"}]
    ]))
