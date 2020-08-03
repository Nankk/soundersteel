(ns frontend.components.scene-list
  (:require [frontend.events :as events]
            [frontend.style.global :as style]
            [frontend.subs :as subs]
            ["react-split-pane" :as rsp]
            [re-frame.core :as rf]))

(def SplitPane (.-default rsp))

(defn main []
  [:div#scene-list.container
   [:div.grid
    [:div.title.d-flex.flex-row.justify-content-between.align-items-center
     [:h3 "Scenes"]
     [:i.fa.fa-plus {:on-click #(rf/dispatch-sync [::events/add-scene])}]]
    [:div.list.scroll
     (let [ss     @(rf/subscribe [::subs/scenes])
           cur-id @(rf/subscribe [::subs/cur-scene-id])]
       (for [s ss]
         ^{:key s}
         [:div.card {:style {:background-color (if (= (s :id) cur-id)
                                                 (style/colors :blue)
                                                 (style/colors :gray-dark))}}
          [:div.card-body
           [:div {:on-click #(rf/dispatch-sync [::events/set-cur-scene-id (s :id)])}
            (str (s :name))]]]))]]])
