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
   :gray-light    "#2b313e"
   :gray-lighter  "#aab2bd"
   :gray-lightest "#e6e9ed"
   :black         "#000000"
   :accent1       "#1e364a"
   :accent2       "#3b6f8c"
   :a             "#005c28"
   :b             "#7e2125"})

(defn css []
  (g/css
   [:html {:background-color (str (colors :black) "!important")}]
   [:body {:font-size "80%!important"}
    [:* {:user-select "none"}]]
   [:i {:color (colors :gray-lighter)}]
   [:.container {:height  "100%"
                 :margin  "0px!important"
                 :padding "10px!important"}]
   [:.scroll {:overflow "auto"}]
   [:.card-header {:padding "8px 8px"}]
   [:.card {:background-color (colors :gray)}]
   [:.card-body {:padding "8px 8px!important"}]
   [:.code {:font-family "Monospace"}]
   [:.form-check {:margin-top    "0px!important"
                  :margin-bottom "0px!important"}]
   [:.rounded {:border-radius "5px"}]
   [:&::-webkit-scrollbar {:width "10px"}
    [:* {:background "transparent"}]]
   [:&::-webkit-scrollbar-thumb {:background      (colors :gray)
                                 :border          "2px solid transparent"
                                 :background-clip "content-box"
                                 :border-radius   "10px"
                                 :box-shadow      "none"}]
   [:.see-through {:color            (colors :gray-lightest)
                   :background-color "transparent"
                   :border-radius    0
                   :border           "none"
                   :border-bottom    (str "1px solid " "#444444")}]
   [:.num {:text-align "right"
           :user-select "auto"}]
   [:.list-item {:background-color (colors :gray-light)
                 :margin-bottom    "-1px"}]
   [:.selected {:background-color (colors :accent1)
                :border           "solid 1.5px"
                :border-color     (colors :accent2)}]
   ))
