(ns frontend.shortcuts
  (:require [frontend.events :as events]
            [frontend.subs :as subs]
            [frontend.util :as util]
            [re-frame.core :as rf]))

(defn- shift-scene [diff]
  (let [scenes   @(rf/subscribe [::subs/scenes])
        cur-sid  @(rf/subscribe [::subs/cur-scene-id])
        cur-sidx (util/first-idx #(= (% :id) cur-sid) scenes)
        new-sidx (rem (+ cur-sidx diff) (count scenes))
        new-sid (get-in scenes [new-sidx :id])]
    (rf/dispatch-sync [::events/set-cur-scene-id new-sid])))

(defn move-to-next-scene []
  (shift-scene 1))

(defn move-to-prev-scene []
  (shift-scene -1))

(defn- specified-track-in-cur-scene [tidx]
  (let [cur-sid @(rf/subscribe [::subs/cur-scene-id])
        ts-all  @(rf/subscribe [::subs/tracks])
        ts      (vec (filter #(= (% :scene-id) cur-sid) ts-all))]
    (when (< -1 tidx (count ts))
      (let [tid      (get-in ts [tidx :id])]
        @(rf/subscribe [::subs/track<-id tid])))))

(defn toggle-playing-state [tidx]
  (when-let [t (specified-track-in-cur-scene tidx)]
    (let [ws       (t :wavesurfer)
          playing? @(rf/subscribe [::subs/playing? t])]
      (if playing? (.pause ws) (.play ws))
      (rf/dispatch-sync [::events/update-playing? t]))))

(defn toggle-loop? [tidx]
  (when-let [t (specified-track-in-cur-scene tidx)]
    (rf/dispatch-sync [::events/toggle-loop t])))
