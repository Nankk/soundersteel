(ns frontend.subs
  (:require
   [re-frame.core :as rf]
   ))

(def subscriptions
  {::ipc-channels [:ipc-channels]
   ::files        [:files]
   ::scenes       [:scenes]
   ::tracks       [:tracks]
   ::cur-file-id  [:cur-file-id]
   ::cur-scene-id (fn [db _]
                    (println "subs/cur-scene-id")
                    (get-in db [:cur-scene-id]))
   ::cur-track-id [:cur-track-id]
   ::cur-file (fn [db _] (first (filter #(= (% :id) (db :cur-file-id)) (db :files))))
   ::cur-scene (fn [db _] (first (filter #(= (% :id) (db :cur-scene-id)) (db :scenes))))
   ::cur-track (fn [db _] (first (filter #(= (% :id) (db :cur-track-id)) (db :tracks))))})

(doseq [[sub-key item] subscriptions]
  (if (coll? item)
    (rf/reg-sub sub-key
      (fn [db _] (get-in db (vec item))))
    (rf/reg-sub sub-key item)))
