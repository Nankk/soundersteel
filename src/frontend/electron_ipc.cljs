(ns frontend.electron-ipc
  (:require
   [cljs.core.async :refer [<! chan]]
   [frontend.events :as events]
   [frontend.subs :as subs]
   [re-frame.core :as rf]
   [common.async :refer [defan]]))

(defan ipc
  [ipc-channel & args]
  (let [ech    (name ipc-channel)
        ipc-ch (or (get @(rf/subscribe [::subs/ipc-channels]) ech)
                   (chan))]
    (rf/dispatch-sync [::events/add-ipc-channel ech ipc-ch])
    (. (. js/window -api) toMain ech (clj->js args))
    (get (js->clj (<! ipc-ch)) "value")))
