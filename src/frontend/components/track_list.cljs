(ns frontend.components.track-list
  (:require ["wavesurfer.js" :as WaveSurfer]
            ["wavesurfer.js/src/plugin/regions/index.js" :as ws-region-raw]
            [frontend.events :as events]
            [frontend.subs :as subs]
            [re-frame.core :as rf]
            [reagent.core :as reagent]))

(def ws-region (.-default ws-region-raw))

(defn- display [t]
  [:div.display
   [:div.d-flex.flex-row.justify-content-between
    [:div
     (let [f @(rf/subscribe [::subs/file<-id (t :file-id)])]
       [:h5 (nth (re-find #"/([^/]+)$" (f :path)) 1)])]
    [:i.fa.fa-trash]]])

(defn- controller [t]
  [:div.contoller
   [:div.d-flex.flex-row.align-items-center.justify-content-center
    [:i.fas.fa-chevron-circle-left.fa-lg]
    [:span.fa-stack {:style {:vertical-align "top"}}
     [:i.far.fa-circle.fa-stack-2x]
     [:i.fas.fa-play.fa-stack-1x]]
    [:i.fas.fa-chevron-circle-right.fa-lg]]
   [:input.volume-range {:type     "range"
                         :min      0
                         :max      1
                         :step     0.01}]])

(defn- looper [t]
  [:div.looper
   [:div.looper-edge
    [:div "a"]]
   [:div.looper-edge "b"]])

(defn- wavesurfer-container [t]
  (reagent/create-class
   {:component-did-mount
    (fn [_]
      (println "did-mount!")
      (let [ws (.create WaveSurfer
                        (clj->js
                         {:container  (str "#" (t :id))
                          :responsive true
                          :plugins    [(.create ws-region (clj->js {}))]}))]
        (rf/dispatch-sync [::events/set-wavesurfer t ws])
        (let [f @(rf/subscribe [::subs/file<-id (t :file-id)])
              p "audio/PlasticAdventure.mp3"]
          (.load ws p)))
      )
    :reagent-render (fn []
                      [:div.waveform-container
                       [:div {:id (str (t :id))
                              :style {:width  "100%" :height "100%"}}]])}))

(defn main []
  [:div#track-list.container
   [:div.list.scroll
    (let [cur-s  @(rf/subscribe [::subs/cur-scene])
          ts-all @(rf/subscribe [::subs/tracks])
          ts     (filter #(= (get cur-s :id) (% :scene-id)) ts-all)]
      (for [t ts]
        ^{:key t}
        [:div.card
         [:div.card-body
          [:div.track.d-flex.flex-row
           [:div.track-control.grid-control
            [display t]
            [controller t]
            [looper t]]
           [wavesurfer-container t]]]]))]])
