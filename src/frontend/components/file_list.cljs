(ns frontend.components.file-list
  (:require
   [frontend.subs :as subs]
   [frontend.util :as util]
   [re-frame.core :as rf]))

(defn- file-list []
  [:div.upper
   [:div.grid-upper
    [:div.title
     [:h3 "Files"]]
    [:div.list
     [:div.card
      [:div.card-body
       (let [fs @(rf/subscribe [::subs/files])]
         (for [f fs]
           ^{:key f}
           [:div
            [:div (str "ID " (f :id))]
            [:div (str "Path "(f :path))]]
           ))]]]]])

(defn main []
  [:div#file-list.container
   [:div.grid
    [file-list]
    [:div.lower
     [:div "File info"]]]])
