(ns frontend.events
  (:require [frontend.config :as config]
            [frontend.db :as db]
            [re-frame.core :as rf]
            [frontend.util :as util]
            [frontend.const :as const]))

(def handlers
  {::set-cur-file-id  [:cur-file-id]
   ::set-cur-scene-id [:cur-scene-id]
   ::set-cur-track-id [:cur-track-id]
   ::add-file         (fn [db [_ f]]
                        (let [ext (get (re-find #"\.([^\./\\]+)$" (f :path)) 1)]
                          (if (and (not (some #{(f :path)} (map :path (db :files))))
                                   (some #{ext} const/acceptable-extensions))
                            (update db :files conj f)
                            db)))
   ::remove-file      (fn [db [_ f]]
                        (let [frmidcs (keep-indexed #(when (= (%2 :id) (f :id)) %1) (db :files))
                              nfiles  (apply util/drop-by-idx (db :files) frmidcs)
                              trmidcs (keep-indexed #(when (= (%2 :file-id) (f :id)) %1) (db :tracks))
                              ntracks (apply util/drop-by-idx (db :tracks) trmidcs)]
                          (-> db
                              (assoc ,, :files nfiles)
                              (assoc ,, :tracks ntracks))))
   ::add-scene        (fn [db [_ s]]
                        (update db :scenes #(conj % s)))
   ::remove-scene     (fn [db [_ s]]
                        (let [srmidcs (keep-indexed #(when (= (%2 :id) (s :id)) %1) (db :scenes))
                              nscenes (apply util/drop-by-idx (db :scenes) srmidcs)
                              trmidcs (keep-indexed #(when (= (%2 :scene-id) (s :id)) %1) (db :tracks))
                              ntracks (apply util/drop-by-idx (db :tracks) trmidcs)]
                          (-> db
                              (assoc ,, :scenes nscenes)
                              (assoc ,, :tracks ntracks))))
   ::initialize-db    (fn [_ _] (if config/debug? db/debug-db db/default-db))
   ::add-ipc-channel  (fn [db [_ k v]] (update-in db [:ipc-channels] assoc k v))})

(doseq [[ev-key item] handlers]
  (if (coll? item)
    (rf/reg-event-db ev-key
      (fn [db [_ v]] (assoc-in db (vec item) v)))
    (rf/reg-event-db ev-key item)))
