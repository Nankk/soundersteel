(ns frontend.style.track-list
  (:require
   [garden.core :as g]
   [frontend.style.global :as style]))

(defn css []
  (g/css
   [:#track-list
    [:.track {:height "85px"}]
    [:.card {:background-color (style/colors :gray-dark)}]
    [:wave {:height "100%!important"
            :border-radius "5px"
            :background-color (style/colors :gray-darkest)}]
    [:.waveform-container {:height "100%"
                           :width  "100%"
                           :padding "1px 5px"}]

    ;; Control grid
    [:.grid-control {:height                "100%"
                     :width                 "200px"
                     :display               "grid"
                     :grid-template-rows    "2fr 3fr"
                     :grid-template-columns "1fr 1fr"
                     :grid-template-areas   (pr-str
                                             "display    display"
                                             "controller looper")}]
    [:.display {:grid-area "display"}]
    [:.controller {:grid-area "controller "}]
    [:.looper {:grid-area "looper "}]

    ;; Volume range slider
    [:.volume-range {:-webkit-appearance "none"
                     :appearance         "none"
                     :outline            "none"
                     :height             "2px"
                     :width              "80%"
                     :background         (style/colors :cyan)}
     [:&::-webkit-slider-thumb {:-webkit-appearance "none"
                                :background         (style/colors :cyan)
                                :width              "12px"
                                :height             "12px"
                                :border-radius      "50%"
                                }]]

    ;; A-B looper
    [:.looper-edge {:background-color (style/colors :primary)
                    :padding-left     "8px"
                    :width            "80%"
                    :border-radius    "5px"
                    :margin-bottom    "4px"}]
    ]))
