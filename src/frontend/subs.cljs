(ns frontend.subs
  (:require
   [re-frame.core :as rf]

   [frontend.util :as util]))

(def subscriptions
  {::ipc-channels  [:ipc-channels]
   ::files         [:files]
   ::scenes        [:scenes]
   ::tracks        [:tracks]
   ::file<-id      (fn [db [_ fid]] (util/first-item #(= (% :id) fid) (db :files)))
   ::track<-id     (fn [db [_ tid]] (util/first-item #(= (% :id) tid) (db :tracks)))
   ::cur-file-id   [:cur-file-id]
   ::cur-scene-id  [:cur-scene-id]
   ::cur-track-id  [:cur-track-id]
   ::cur-file      (fn [db _] (first (filter #(= (% :id) (db :cur-file-id)) (db :files))))
   ::cur-scene     (fn [db _] (first (filter #(= (% :id) (db :cur-scene-id)) (db :scenes))))
   ::cur-track     (fn [db _] (first (filter #(= (% :id) (db :cur-track-id)) (db :tracks))))
   ::playing?      (fn [db [_ t]]
                     (let [idx (util/first-idx #(= (% :id) (t :id)) (db :tracks))]
                       (get-in db [:tracks idx :playing?])))
   ::master-volume [:master-volume]
   })

(doseq [[sub-key item] subscriptions]
  (if (coll? item)
    (rf/reg-sub sub-key
      (fn [db _] (get-in db (vec item))))
    (rf/reg-sub sub-key item)))
