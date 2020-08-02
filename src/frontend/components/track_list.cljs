(ns frontend.components.track-list
  (:require
   ["wavesurfer.js" :as WaveSurfer]
   ["wavesurfer.js/src/plugin/regions/index.js" :as ws-region-raw]
   [reagent.core :as reagent]
   ))

(def ws-region (.-default ws-region-raw))

(defn- main-render []
  [:div "Track list"])

(defn main []
  (reagent/create-class
   {:component-did-mount
    (fn [_]
      (println "did-mount!"))
    :reagent-render
    (fn [] [main-render])}))
