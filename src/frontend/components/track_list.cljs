(ns frontend.components.track-list
  (:require ["wavesurfer.js" :as WaveSurfer]
            ["wavesurfer.js/src/plugin/regions/index.js" :as ws-region-raw]
            [cljs.core.async :refer [<! go]]
            [cljs.reader :as reader]
            [frontend.const :as const]
            [frontend.electron-ipc :refer [ipc]]
            [frontend.events :as events]
            [frontend.style.global :as style]
            [frontend.subs :as subs]
            [frontend.util :as util]
            [goog.string :as gstring]
            goog.string.format
            [re-frame.core :as rf]
            [reagent.core :as reagent]))

(def ws-region (.-default ws-region-raw))

(defn- display [t]
  [:div.display
   [:div.d-flex.flex-row.justify-content-between
    [:div.overflow-hidden.text-nowrap
     (let [f @(rf/subscribe [::subs/file<-id (t :file-id)])]
       [:h5 (nth (re-find #"/([^/]+)$" (f :path)) 1)])]
    [:i.fa.fa-trash]]])

(defn- volume-slider [t]
  (reagent/create-class
   {:component-did-mount (fn []
                           (let [el (util/js<-id (str (t :id) "-volume"))]
                             (set! (.-value el) (t :volume))))
    :reagent-render
    (fn []
      [:input.volume-range {:id          (str (t :id) "-volume")
                            :type        "range"
                            :min         0
                            :max         1
                            :step        0.01
                            :on-mouse-up (fn []
                                           (let [tvol (.-value (util/js<-id (str (t :id) "-volume")))]
                                             (rf/dispatch-sync [::events/set-volume t tvol])))
                            :on-input    (fn []
                                           (let [mvol @(rf/subscribe [::subs/master-volume])
                                                 tvol (.-value (util/js<-id (str (t :id) "-volume")))
                                                 nvol (* mvol tvol)]
                                             (.setVolume (t :wavesurfer) nvol)))}])}))

(defn- controller [t]
  (let [ws (t :wavesurfer)]
    [:div.contoller
     [:div.d-flex.flex-row.align-items-center.justify-content-center
      [:i.fas.fa-chevron-circle-left.fa-lg
       {:on-click (fn []
                    (let [ws           (t :wavesurfer)
                          break-points (reverse [(t :a) (+ (or (t :b) 0) 0.01) (.getDuration ws)])
                          cur-time     (- (.getCurrentTime ws) const/op-tolerance-sec)
                          ntime        (first (filter #(> cur-time %) break-points))]
                      (.setCurrentTime ws ntime)))}]
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
      [:i.fas.fa-chevron-circle-right.fa-lg
       {:on-click (fn []
                    (let [ws           (t :wavesurfer)
                          break-points [(t :a) (+ (or (t :b) 0) 0.01) (.getDuration ws)]
                          cur-time     (.getCurrentTime ws)
                          ntime        (first (filter #(< cur-time %) break-points))]
                      (.setCurrentTime ws ntime)))}]]
     [volume-slider t]
     ]))

(defn- add-region [t a-b time]
  (println "add-region " (t :id) " " a-b " " time)
  (let [ws (t :wavesurfer)
        r  (.addRegion ws (clj->js {:id     (name a-b)
                                    :start  time
                                    :end    (+ time 0.01)
                                    :resize false
                                    :loop   false
                                    :drag   true
                                    :color  (style/colors a-b)}))]
    (rf/dispatch-sync [::events/set-a-b t a-b time])
    (when (= a-b :b)
      (.on r "out" #(when (t :loop?) (.setCurrentTime ws (or (t :a) 0)))))))

(defn- remove-region [t a-b]
  (println "remove-region " t " " a-b)
  (let [ws     (t :wavesurfer)
        rs     (js->clj (.-list (.-regions ws)))
        _ (util/log (.-list (.-regions ws)))
        _      (println rs)
        target (get rs (name a-b))
        _      (println target)]
    (rf/dispatch-sync [::events/remove-a-b t a-b])
    (when target
      (.remove target))))

(defn- looper [t]
  [:div.looper
   ;; Enable loop
   [:div.d-flex.flex-row.align-items-center.w-100
    [:div.loop-toggle.mb-1 {:style    (when (t :loop?) {:background-color (style/colors :accent2)
                                                        :border-color     (style/colors :accent2)})
                            :on-click #(rf/dispatch-sync [::events/toggle-loop t])}
     (if (t :loop?) "Loop: on" "Loop: off")]]

   ;; A edge
   [:div.d-flex.flex-row.align-items-center.w-100
    [:div.edge-toggle {:class    (when (t :a) "toggle-on")
                       :style    (when (t :a) {:background-color (style/colors :a)
                                               :border-color     (style/colors :a)})
                       :on-click (if (t :a)
                                   #(remove-region t :a)
                                   #(add-region t :a (.getCurrentTime (t :wavesurfer))))}
     "A"]
    [:input.input-ab.see-through.num {:value (if-let [val (t :a)]
                                               (gstring/format "%.2f" val)
                                               0)}]]

   ;; B edge
   [:div.d-flex.flex-row.align-items-center.w-100
    [:div.edge-toggle {:class    (when (t :b) "toggle-on")
                       :style    (when (t :b) {:background-color (style/colors :b)
                                               :border-color     (style/colors :b)})
                       :on-click (if (t :b)
                                   #(remove-region t :b)
                                   #(add-region t :b (.getCurrentTime (t :wavesurfer))))}
     "B"]
    [:input.input-ab.see-through.num {:value (if-let [val (t :b)]
                                               (gstring/format "%.2f" val)
                                               0)}]]])

(defn- wavesurfer-container [t]
  (reagent/create-class
   {:component-did-mount (fn [_]
                           (println "track " (t :id) " mounted!" (when (t :dom-element) " (w/ dom elem)"))
                           (when (t :dom-element)
                             (let [container (util/js<-id (t :id))
                                   ws        (t :wavesurfer)]
                               (.appendChild container (t :dom-element))
                               (.drawBuffer ws))))
    :reagent-render (fn []
                      [:div.waveform-container
                       [:div {:id    (str (t :id))
                              :style {:width "100%" :height "100%"}}]])}))

(defn- master-volume []
  [:div.d-flex.flex-row.align-items-center.master-volume.mb-2
   [:div.text-nowrap.mr-2 "Master Volume "]
   [:input.volume-range.w-100 {:id          "master-volume"
                               :type        "range"
                               :min         0
                               :max         1
                               :step        0.01
                               :on-input    (fn []
                                              (let [mvol (.-value (util/js<-id "master-volume"))
                                                    ts   @(rf/subscribe [::subs/tracks])]
                                                (rf/dispatch-sync [::events/set-master-volume mvol])
                                                (doseq [t ts]
                                                  (let [tvol (.-value (util/js<-id (str (t :id) "-volume")))
                                                        nvol (* mvol tvol)
                                                        ws   (t :wavesurfer)]
                                                    (.setVolume ws nvol)))))}]])

(defn- header []
  [:div.d-flex.flex-row.justify-content-between
   [master-volume]
   [:div.d-flex.flex-row
    [:i.fas.fa-chevron-circle-down.mr-2]
    [:i.fas.fa-chevron-circle-up]]])

(defn- create-wavesurfer-instance
  "Create wavesurfer instance & containing DOM element and register them to db."
  [f]
  (let [tid     (str (random-uuid))
        fid     (f :id)
        cur-sid @(rf/subscribe [::subs/cur-scene-id])
        el      (.createElement js/document "div")
        ws      (.create WaveSurfer
                         (clj->js
                          {:container     el
                           :responsive    true
                           :backend       "MediaElement"
                           :hideScrollbar false
                           :plugins       [(.create ws-region (clj->js {}))]}))
        nt      {:id          tid
                 :file-id     fid
                 :scene-id    cur-sid
                 :name        (nth (re-find #"/([^/]+)$" (f :path)) 1)
                 :wavesurfer  ws
                 :dom-element el
                 :playing?    false
                 :loop        true
                 :a           nil
                 :b           nil}]
    (.on ws "finish" #(rf/dispatch-sync [::events/update-playing? nt]))
    (.setAttribute el "class" "h-100")
    (println "File added: " (f :path))
    (.load ws (f :path))
    (rf/dispatch-sync [::events/add-track nt])))

(defn main []
  [:div#track-list.container
   [:div.h-100 {:style {:padding     "14px"
                        :padding-top "0px"}}
    [:div.card.container {:on-drag-over (fn [e] (.preventDefault e))
                          :on-drop      (fn [e]
                                          (let [data (.getData (.-dataTransfer e) "text/plain")
                                                f    (reader/read-string data)]
                                            (create-wavesurfer-instance f)))}
     [header]
     [:div.list.scroll
      (let [cur-sid @(rf/subscribe [::subs/cur-scene-id])
            ts-all  @(rf/subscribe [::subs/tracks])
            ts      (filter #(= cur-sid (% :scene-id)) ts-all)
            cur-id  @(rf/subscribe [::subs/cur-track-id])]
        (for [t ts]
          ^{:key t}
          [:div.card {:class (if (= (t :id) cur-id) "selected" "list-item")}
           [:div.card-body {:on-click #(rf/dispatch-sync [::events/set-cur-track-id (t :id)])}
            [:div.track.d-flex.flex-row
             [:div.track-control.grid-control
              [display t]
              [controller t]
              [looper t]]
             [wavesurfer-container t]]]]))]]]])
