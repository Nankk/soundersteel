(ns frontend.events
  (:require
   [re-frame.core :as rf]
   [frontend.db :as db]))

(rf/reg-event-db
  ::initialize-db
  (fn [_ _]
    (println db/default-db)
    db/default-db))

(rf/reg-event-db ::add-ipc-channel
  (fn [db [_ k v]]
    (update-in db [:ipc-channels] assoc k v)))
