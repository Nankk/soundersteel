(ns frontend.components.file-list
  (:require [frontend.electron-ipc :refer [ipc]]
            [frontend.style.global :as style]
            [frontend.events :as events]
            [frontend.subs :as subs]
            [frontend.util :as util]
            [re-frame.core :as rf]
            [clojure.string :as str]))

(defn- file-list []
  [:div.grid-upper
   [:div.title
    [:h3 "Files"]]
   [:div.list.scroll {:on-drag-over (fn [e] (.preventDefault e))
                      :on-drop      (fn [e]
                                      (let [files (js->clj (-> e (.-dataTransfer) (.-files)))]
                                        (doseq [f files]
                                          (rf/dispatch-sync [::events/add-file
                                                             {:id (random-uuid) :path (.-path f)}]))))}
    (let [fs     @(rf/subscribe [::subs/files])
          cur-id @(rf/subscribe [::subs/cur-file-id])]
      (for [f fs]
        ^{:key f}
        [:div.card {:style {:background-color (if (= (f :id) cur-id)
                                                (style/colors :blue)
                                                (style/colors :gray-darker))}}
         [:div.card-body {:on-click #(rf/dispatch-sync [::events/set-cur-file-id (f :id)])}
          [:div.d-flex.flex-row.justify-content-between
           [:div (get (re-find #"(/|\\)([^/\\]+)$" (f :path)) 2)]
           [:i.fa.fa-trash {:on-click #(rf/dispatch-sync [::events/remove-file f])}]]
          ]]))]])

(defn- file-info []
  [:div.card.h-100
   [:div.card-body {:style {:background-color (style/colors :gray-dark)}}
    (let [f @(rf/subscribe [::subs/cur-file])]
      [:div (get f :path)])]])

(defn main []
  [:div#file-list.container
   [:div.grid
    [:div.upper
     [file-list]]
    [:div.lower
     [file-info]]]])
