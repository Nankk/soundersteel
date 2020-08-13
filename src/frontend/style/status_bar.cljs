(ns frontend.style.status-bar
  (:require [frontend.style.global :as global]
            [garden.core :as g]))

(defn css []
  (g/css
   [:#status-bar {:width "100%"
                  :height "100%"
                  :background-color (global/colors :gray-darkest)
                  :padding "0px 18px"
                  :text-align "right"}
    [:* {:color (global/colors :gray-lighter)}]]))
