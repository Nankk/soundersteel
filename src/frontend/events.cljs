(ns frontend.events
  (:require [frontend.config :as config]
            [frontend.db :as db]
            [re-frame.core :as rf]))

(def handlers
  {::set-cur-file-id  [:cur-file-id]
   ::set-cur-scene-id [:cur-scene-id]
   ::set-cur-track-id [:cur-track-id]
   ::add-scene        (fn [db [_ _]]
                        (update db :scenes conj {:id (random-uuid) :name "New scene" :tracks []}))
   ::initialize-db    (fn [_ _] (if config/debug? db/debug-db db/default-db))
   ::add-ipc-channel  (fn [db [_ k v]] (update-in db [:ipc-channels] assoc k v))})

(doseq [[ev-key item] handlers]
  (if (coll? item)
    (rf/reg-event-db ev-key
      (fn [db [_ v]] (assoc-in db (vec item) v)))
    (rf/reg-event-db ev-key item)))
