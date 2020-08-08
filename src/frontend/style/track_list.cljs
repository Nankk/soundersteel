(ns frontend.style.track-list
  (:require
   [garden.core :as g]
   [frontend.style.global :as style]))

(defn css []
  (g/css
   [:#track-list
    [:.master-volume {:width "200px"}]
    [:.track {:height "85px"}]
    [:wave {:height           "100%!important"
            :border-radius    "5px"
            :background-color (style/colors :gray-darkest)}]
    [:.waveform-container {:height  "100%"
                           :width   "100%"
                           :padding "1px 5px"}]

    ;; Control grid
    [:.grid-control {:height                "100%"
                     :display               "grid"
                     :grid-template-rows    "2fr 3fr"
                     :grid-template-columns "1fr 1fr"
                     :grid-template-areas   (pr-str
                                             "display    display"
                                             "controller looper")}]
    [:.display {:grid-area "display"}]
    [:.controller {:grid-area      "controller"
                   :display        "flex"
                   :flex-direction "vertical"
                   :align-items    "center"}]
    [:.looper {:grid-area "looper"}]
    [:.track-control {:width "240px"}]

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
    [:.looper-edge {:background-color (style/colors :gray-darkest)
                    :padding-left     "8px"
                    :width            "80%"
                    :border-radius    "5px"
                    :margin-bottom    "4px"}]
    [:.edge-toggle {:border-radius    "2px"
                    :cursor           "default"
                    :text-align       "center"
                    :width            "16px"
                    :height           "16px"
                    :font-size        "7pt"
                    :color            (style/colors :gray-lighter)
                    :border           "solid 1px"
                    :border-color     (style/colors :gray-lighter)
                    :background-color (style/colors :gray-light)
                    }]
    [:.toggle-on {:color (style/colors :white)}]
    [:.loop-toggle {:border-radius    "2px"
                    :text-align       "center"
                    :font-size        "7pt"
                    :width            "70px"
                    :height           "16px"
                    :border           "solid 1px"
                    :border-color     (style/colors :gray-lighter)
                    :background-color (style/colors :gray-light)}]
    [:.input-ab {:color (style/colors :gray-lighter)
                 :width "42px"
                 :type  "text"}]
    [:wave
     [:wave {:opacity 0.5}]]
    [:.wavesurfer-region {:width "1px!important"}]
    ]))
