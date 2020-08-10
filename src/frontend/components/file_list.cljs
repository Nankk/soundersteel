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
                                                             {:id (str (random-uuid)) :path (.-path f)}]))))}
    (let [fs     @(rf/subscribe [::subs/files])
          cur-id @(rf/subscribe [::subs/cur-file-id])]
      (for [f fs]
        ^{:key f}
        [:div.card {:class         (if (= (f :id) cur-id) "selected" "list-item")
                    :draggable     true
                    :on-drag-start (fn [e] (.setData (.-dataTransfer e) "text/plain" (pr-str f)))}
         [:div.card-body {:on-click #(rf/dispatch-sync [::events/set-cur-file-id (f :id)])}
          [:div.d-flex.flex-row.justify-content-between
           [:div.overflow-hidden.text-nowrap (get (re-find #"(/|\\)([^/\\]+)$" (f :path)) 2)]
           [:i.fa.fa-trash
            {:on-click
             (fn []
               (let [ts (filter #(= (% :file-id) (f :id)) @(rf/subscribe [::subs/tracks]))]
                 (doseq [t ts] (.destroy (t :wavesurfer)))
                 (rf/dispatch-sync [::events/remove-file f])))}]]
          ]]))]])

(defn- file-info []
  [:div.card.h-100
   [:div.card-body {:style {:background-color (style/colors :gray-darker)}}
    (let [f @(rf/subscribe [::subs/cur-file])]
      [:div.text-break (get f :path)])]])

(defn main []
  [:div#file-list.container {:style {:background-color (style/colors :gray)}}
   [:div.grid
    [:div.upper
     [file-list]]
    [:div.lower
     [file-info]]]])
