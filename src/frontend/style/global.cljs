(ns frontend.style.global
  (:require
   [garden.core :as g]
   ))

(def colors
  {:blue          "#0090E7"
   :purple        "#8F5FE8"
   :red           "#FC424A"
   :yellow        "#FFAB00"
   :green         "#00D25B"
   :cyan          "#57c7d4"
   :white         "#ffffff"
   :gray          "#191C24"
   :gray-dark     "#12151E"
   :gray-darker   "#0F1015"
   :gray-darkest  "#0F0F0F"
   :gray-light    "#aab2bd"
   :gray-lighter  "#e8eff4"
   :gray-lightest "#e6e9ed"
   :black         "#000000"})

(defn css []
  (g/css
   [:html {:background-color (str (colors :black) "!important")}]
   [:body {:font-size "80%!important"}]
   [:i.fa.fa-trash {:color (colors :gray)}]
   [:.container {:height  "100%"
                 :margin  "0px!important"
                 :padding "10px!important"}]
   [:.scroll {:overflow "auto"}]
   [:.card-header {:padding "8px 8px"}]
   [:.card {:background-color (colors :gray)}]
   [:.card-body {:padding "8px 8px!important"}]
   [:.code {:font-family "Monospace"}]
   [:.rounded {:border-radius "5px"}]
   [:&::-webkit-scrollbar {:width "10px"}
    [:* {:background "transparent"}]]
   [:&::-webkit-scrollbar-thumb {:background      (colors :gray)
                                 :border          "2px solid transparent"
                                 :background-clip "content-box"
                                 :border-radius   "10px"
                                 :box-shadow      "none"}]
   [:.see-through {:color            "#444444"
                   :background-color "transparent"
                   :border-radius    0
                   :border           "none"
                   :border-bottom    (str "1px solid " "#444444")}]
   [:.num {:text-align "right"
           :padding    "0px 7px"}]
   [:.list-item {:background-color (colors :gray)
                 :border-color     (colors :gray-darkest)
                 :margin-bottom    "-1px"}]
   [:.selected {:background-color (colors :gray-darker)}]
   ))
