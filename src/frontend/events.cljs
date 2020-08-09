(ns frontend.events
  (:require [frontend.config :as config]
            [frontend.db :as db]
            [re-frame.core :as rf]
            [frontend.util :as util]
            [frontend.const :as const]))

(def handlers
  {::set-db            (fn [_ [_ new-db]] new-db)
   ::initialize-db     (fn [_ _] (if config/debug? db/debug-db db/default-db))
   ::add-ipc-channel   (fn [db [_ k v]] (update-in db [:ipc-channels] assoc k v))
   ::set-cur-file-id   [:cur-file-id]
   ::set-cur-scene-id  [:cur-scene-id]
   ::set-cur-track-id  [:cur-track-id]
   ::add-file          (fn [db [_ f]]
                         (let [ext (get (re-find #"\.([^\./\\]+)$" (f :path)) 1)]
                           (if (and (not (some #{(f :path)} (map :path (db :files))))
                                    (some #{ext} const/acceptable-extensions))
                             (update db :files conj f)
                             db)))
   ::remove-file       (fn [db [_ f]]
                         (let [frmidcs (keep-indexed #(when (= (%2 :id) (f :id)) %1) (db :files))
                               nfiles  (apply util/drop-by-idx (db :files) frmidcs)
                               trmidcs (keep-indexed #(when (= (%2 :file-id) (f :id)) %1) (db :tracks))
                               ntracks (apply util/drop-by-idx (db :tracks) trmidcs)]
                           (-> db
                               (assoc ,, :files nfiles)
                               (assoc ,, :tracks ntracks))))
   ::add-scene         (fn [db [_ s]]
                         (update db :scenes #(conj % s)))
   ::remove-scene      (fn [db [_ s]]
                         (let [srmidcs (keep-indexed #(when (= (%2 :id) (s :id)) %1) (db :scenes))
                               nscenes (apply util/drop-by-idx (db :scenes) srmidcs)
                               trmidcs (keep-indexed #(when (= (%2 :scene-id) (s :id)) %1) (db :tracks))
                               ntracks (apply util/drop-by-idx (db :tracks) trmidcs)]
                           (-> db
                               (assoc ,, :scenes nscenes)
                               (assoc ,, :tracks ntracks))))
   ::add-track         (fn [db [_ t]] (update db :tracks conj t))
   ::remove-track      (fn [db [_ t]]
                         (let [trmidcs (keep-indexed #(when (= (%2 :id) (t :id)) %1) (db :tracks))
                               ntracks (apply util/drop-by-idx (db :tracks) trmidcs)]
                           (assoc db :tracks ntracks)))
   ::update-track      (fn [db [_ t]]
                         (let [tidx (util/first-idx #(= (% :id) (t :id)) (db :tracks))]
                           (assoc-in db [:tracks tidx] t)))
   ::push-down-track   (fn [db [_ t]]
                         (let [ts  (vec (filter #(= (% :scene-id) (db :cur-scene-id)) (db :tracks)))
                               idx (util/first-idx #(= (t :id) (% :id)) ts)]
                           (if (not (= idx (dec (count ts))))
                             (let [ntracks (vec (concat (subvec ts 0 idx)
                                                        (list (ts (inc idx)))
                                                        (list (ts idx))
                                                        (when (> (count ts) 2) (subvec ts (+ idx 2)))))]
                               (assoc db :tracks ntracks))
                             db)))
   ::pull-up-track     (fn [db [_ t]]
                         (let [ts  (vec (filter #(= (% :scene-id) (db :cur-scene-id)) (db :tracks)))
                               idx (util/first-idx #(= (t :id) (% :id)) ts)]
                           (if (not (= idx 0))
                             (let [ntracks (vec (concat (subvec ts 0 (dec idx))
                                                        (list (ts idx))
                                                        (list (ts (dec idx)))
                                                        (when (> (count ts) 2) (subvec ts (inc idx)))))]
                               (assoc db :tracks ntracks))
                             db)))
   ::update-playing?   (fn [db [_ t]]
                         (let [idx (util/first-idx #(= (% :id) (t :id)) (db :tracks))
                               ws  (t :wavesurfer)]
                           (assoc-in db [:tracks idx :playing?]
                                     (if ws
                                       (not (.isPaused (.-backend ws)))
                                       false))))
   ::set-master-volume [:master-volume]
   ::set-volume        (fn [db [_ t vol]]
                         (let [tidx (util/first-idx #(= (% :id) (t :id)) (db :tracks))]
                           (assoc-in db [:tracks tidx :volume] vol)))
   ::toggle-loop       (fn [db [_ t]]
                         (let [tidx (util/first-idx #(= (% :id) (t :id)) (db :tracks))]
                           (update-in db [:tracks tidx :loop?] #(not %))))
   ::set-a-b           (fn [db [_ t a-b time]]
                         (let [tidx (util/first-idx #(= (% :id) (t :id)) (db :tracks))]
                           (assoc-in db [:tracks tidx a-b] time)))
   ::remove-a-b        (fn [db [_ t a-b]]
                         (let [tidx (util/first-idx #(= (% :id) (t :id)) (db :tracks))]
                           (assoc-in db [:tracks tidx a-b] nil)))
   })

(doseq [[ev-key item] handlers]
  (if (coll? item)
    (rf/reg-event-db ev-key
      (fn [db [_ v]] (assoc-in db (vec item) v)))
    (rf/reg-event-db ev-key item)))
