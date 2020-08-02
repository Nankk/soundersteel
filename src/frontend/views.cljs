(ns frontend.views
  (:require ["react-split-pane" :as rsp]
            ["wavesurfer.js" :as WaveSurfer]
            ["wavesurfer.js/src/plugin/regions/index.js" :as ws-region-raw]
            [frontend.events :as events]
            [frontend.subs :as subs]
            [frontend.util :as util]
            [re-frame.core :as rf]
            [reagent.core :as reagent]))

(def SplitPane (.-default rsp))
(def ws-region (.-default ws-region-raw))

(defn- main-panel-render []
  [:> SplitPane {:split "vertical" :primary "second" :minSize 50 :defaultSize "35%"}
   [:> SplitPane {:split "horizontal" :minSize 50 :defaultSize "35%"}
    [:div "p"]
    [:div "p"]]
   [:div "p"]])

(defn main-panel []
  (reagent/create-class
   {:component-did-mount
    (fn [_]
      (println "did-mount!"))
    :reagent-render
    (fn [] [main-panel-render])}))
