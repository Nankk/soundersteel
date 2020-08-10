(ns frontend.components.scene-list
  (:require ["react-split-pane" :as rsp]
            [frontend.util :as util]
            [frontend.const :as const]
            [frontend.events :as events]
            [reagent.core :as reagent]
            [frontend.subs :as subs]
            [re-frame.core :as rf]))

(def SplitPane (.-default rsp))

(defn- scene-panel [s]
  (reagent/create-class
   {:component-did-mount (fn []
                           (set! (.-value (util/js<-id (str (s :id) "-name-input"))) (s :name)))
    :reagent-render
    (fn []
      (let [cur-id @(rf/subscribe [::subs/cur-scene-id])]
        [:div.card {:class (if (= (s :id) cur-id) "selected" "list-item")}
         [:div.card-body {:on-click (fn []
                                      (when (not= (s :id) cur-id)
                                        (doseq [t @(rf/subscribe [::subs/tracks])]
                                          (when (t :wavesurfer)
                                            (.pause (t :wavesurfer))
                                            (rf/dispatch-sync [::events/update-playing? t])))
                                        (rf/dispatch-sync [::events/set-cur-scene-id (s :id)])))}
          [:div.d-flex.flex-row.justify-content-between
           [:input.see-through.w-75
            {:id (str (s :id) "-name-input")
             :type "text"
             :on-blur #(let [name (.-value (util/js<-id (str (s :id) "-name-input")))]
                         (rf/dispatch-sync [::events/set-scene-name s name]))}]
           [:i.fa.fa-trash
            {:on-click
             (fn []
               (let [ts (filter #(= (% :scene-id) (s :id)) @(rf/subscribe [::subs/tracks]))]
                 (doseq [t ts] (.destroy (t :wavesurfer)))
                 (rf/dispatch-sync [::events/remove-scene s])))}]]]]))}))

(defn main []
  [:div#scene-list.container
   [:div.h-100 {:style {:padding "14px"
                        :padding-bottom "0px"}}
    [:div.card.container
     [:div.grid
      [:div.title.d-flex.flex-row.justify-content-between.align-items-center
       [:h3 "Scenes"]
       [:i.fa.fa-plus {:on-click #(rf/dispatch-sync [::events/add-scene (const/default-scene)])}]]
      [:div.list.scroll
       (let [ss     @(rf/subscribe [::subs/scenes])]
         (for [s ss]
           ^{:key s}
           [scene-panel s]))]]]]])
