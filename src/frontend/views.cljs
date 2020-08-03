(ns frontend.views
  (:require
   ["react-split-pane" :as rsp]
   [frontend.components.file-list :as file-list]
   [frontend.components.scene-list :as scene-list]
   [frontend.components.track-list :as track-list]
   ))

(def SplitPane (.-default rsp))

(defn main-panel []
  [:> SplitPane {:split "vertical" :primary "second" :minSize 50 :defaultSize "35%"}
   [:> SplitPane {:split "horizontal" :minSize 50 :defaultSize "35%"}
    [scene-list/main]
    [track-list/main]]
   [file-list/main]])
