(ns frontend.components.track-list
  (:require ["wavesurfer.js" :as WaveSurfer]
            ["wavesurfer.js/src/plugin/regions/index.js" :as ws-region-raw]
            [cljs.core.async :refer [go <!]]
            [frontend.electron-ipc :refer [ipc]]
            [frontend.events :as events]
            [frontend.subs :as subs]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [frontend.util :as util]
            [frontend.style.global :as style]))

(def ws-region (.-default ws-region-raw))

(defn- display [t]
  [:div.display
   [:div.d-flex.flex-row.justify-content-between
    [:div.overflow-hidden.text-nowrap
     (let [f @(rf/subscribe [::subs/file<-id (t :file-id)])]
       [:h5 (nth (re-find #"/([^/]+)$" (f :path)) 1)])]
    [:i.fa.fa-trash]]])

(defn- controller [t]
  (let [ws (t :wavesurfer)]
    [:div.contoller
     [:div.d-flex.flex-row.align-items-center.justify-content-center
      [:i.fas.fa-chevron-circle-left.fa-lg]
      [:span.fa-stack {:style {:vertical-align "top"}}
       [:i.far.fa-circle.fa-stack-2x]
       (if @(rf/subscribe [::subs/playing? t])
         [:i.fas.fa-pause.fa-stack-1x
          {:on-click (fn []
                       (.pause ws)
                       (rf/dispatch-sync [::events/update-playing? t]))}]
         [:i.fas.fa-play.fa-stack-1x
          {:on-click (fn []
                       (.play ws)
                       (rf/dispatch-sync [::events/update-playing? t]))}])]
      [:i.fas.fa-chevron-circle-right.fa-lg]]
     [:input.volume-range {:id       (str (t :id) "-volume")
                           :type     "range"
                           :min      0
                           :max      1
                           :step     0.01
                           :on-input (fn []
                                       (let [mvol @(rf/subscribe [::subs/master-volume])
                                             tvol (.-value (util/js<-id (str (t :id) "-volume")))
                                             _    (println "mvol: " mvol "  tvol: " tvol)
                                             nvol (* mvol tvol)]
                                         ;; TODO Range slider re-drawed and position is reset here
                                         (rf/dispatch-sync [::events/set-volume t tvol])
                                         (.setVolume ws nvol)))}]]))

(defn- looper [t]
  [:div.looper
   [:div.looper-edge
    [:div "a"]]
   [:div.looper-edge "b"]])

(defn- wavesurfer-container [t]
  (reagent/create-class
   {:component-did-mount (fn [_]
                           (println "track " (t :id) " mounted!" (when (t :dom-element) " (w/ dom elem)"))
                           (when (t :dom-element)
                             (let [container (util/js<-id (t :id))
                                   ws        (t :wavesurfer)]
                               (.appendChild container (t :dom-element))
                               (.drawBuffer ws)
                               )))
    :reagent-render (fn []
                      [:div.waveform-container
                       [:div {:id    (str (t :id))
                              :style {:width "100%" :height "100%"}}]])}))

(defn- master-volume []
  [:div.d-flex.flex-row
   [:div "Master Volume: "]
   [:input.volume-range {:id       "master-volume"
                         :type     "range"
                         :min      0
                         :max      1
                         :step     0.01
                         :on-input (fn []
                                     (let [mvol (.-value (util/js<-id "master-volume"))
                                           ts   @(rf/subscribe [::subs/tracks])]
                                       (rf/dispatch-sync [::events/set-master-volume mvol])
                                       (doseq [t ts]
                                         (let [tvol @(rf/subscribe [::subs/volume t])
                                               nvol (* mvol tvol)
                                               _ (println "mvol: " mvol "  tvol: " tvol)
                                               ws   (t :wavesurfer)]
                                           (.setVolume ws nvol)))))}]])

(defn- create-wavesurfer-instance
  "Create wavesurfer instance & containing DOM element and register them to db."
  [f]
  (let [tid (str (random-uuid))
        fid (f :id)
        sid (@(rf/subscribe [::subs/cur-scene]) :id)
        el  (.createElement js/document "div")
        ws  (.create WaveSurfer
                     (clj->js
                      {:container     el
                       :responsive    true
                       :backend       "MediaElement"
                       :hideScrollbar false
                       :plugins       [(.create ws-region (clj->js {}))]}))]
    (.load ws (f :path))
    (rf/dispatch-sync [::events/add-track {:id          tid
                                           :file-id     fid
                                           :scene-id    sid
                                           :name        (nth (re-find #"\.([^\.]+)$" (f :path)) 1)
                                           :wavesurfer  ws
                                           :dom-element el
                                           :play-info   {:playing? false
                                                         :loop     {:begin nil
                                                                    :end   nil}}}])))

(defn main []
  [:div#track-list.container
   [:div.card.container {:style {:background-color (style/colors :gray-darker)}
                         :on-drop create-wavesurfer-instance}
    [master-volume]
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
            [wavesurfer-container t]]]]))]]])
