(ns frontend.style.global
  (:require
   [garden.core :as g]
   ))

(def colors
  {:blue          "#5E50F9"
   :indigo        "#6610f2"
   :purple        "#6a008a"
   :pink          "#E91E63"
   :red           "#f96868"
   :orange        "#f2a654"
   :yellow        "#f6e84e"
   :green         "#46c35f"
   :teal          "#58d8a3"
   :cyan          "#57c7d4"
   :white         "#ffffff"
   :gray          "#6c757d"
   :gray-dark     "#243033"
   :gray-darker   "#12151e"
   :gray-darkest  "#0d0d0d"
   :gray-light    "#aab2bd"
   :gray-lighter  "#e8eff4"
   :gray-lightest "#e6e9ed"
   :black         "#000000"
   :primary       "#0090e7"
   :secondary     "#e4eaec"
   :success       "#00d25b"
   :info          "#8f5fe8"
   :warning       "#ffab00"
   :danger        "#fc424a"
   :light         "#ffffff"})

(defn css []
  (g/css
   [:html {:background-color "#090909!important"}]
   [:.container {:height  "100%"
                 :margin  "0px!important"
                 :padding "10px!important"}]
   [:.scroll {:overflow "auto"}]
   [:.card-header {:padding "5px 8px"}]
   [:.card-body {:padding "5px 8px!important"}]
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
           :padding    "0px 7px"}]))
