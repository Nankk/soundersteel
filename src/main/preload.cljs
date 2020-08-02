(ns main.preload
  (:require
   ["electron" :refer [contextBridge ipcRenderer]]
   [common.const :as const]))

;; Renderer can access functions of main process via 'window.api'
(. contextBridge exposeInMainWorld
   "api"
   (clj->js
    {:toMain (fn [channel data]
               (if (some #{channel} (concat
                                     (const/electron-ipc-channels :bidirectional)
                                     (const/electron-ipc-channels :to-main)))
                 (. ipcRenderer send channel data)
                 (throw (js/Error. (str "Invalid electron ipc channel was passed to 'toMain' " channel)))))
     :fromMain (fn [channel cbf]
                 (if (some #{channel} (concat
                                       (const/electron-ipc-channels :bidirectional)
                                       (const/electron-ipc-channels :from-main)))
                   (. ipcRenderer on channel (fn [event args]
                                               (cbf event channel args)))
                   (throw (js/Error. (str "Invalid electron ipc channel was passed to 'fromMain' " channel)))))}))

(defn main []
  (println "i am preload"))
