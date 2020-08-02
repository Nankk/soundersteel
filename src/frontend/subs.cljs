(ns frontend.subs
  (:require
   [re-frame.core :as rf]
   ))

(rf/reg-sub
  ::name
  (fn [db _]
    (db :name)))

(rf/reg-sub ::ipc-channels
  (fn [db [_ _]]
    (get-in db [:ipc-channels])))
