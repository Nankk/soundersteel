(ns frontend.io
  (:require ["encoding-japanese" :as encode]
            [cljs.core.async :as async :refer [<! >! chan go]]
            [cljs.reader :as reader]
            [clojure.string :as str]
            [common.const :as const]
            [frontend.electron-ipc :refer [ipc]]
            [frontend.events :as events]
            goog.string.format
            [re-frame.core :as rf]))

(defn open-file [path]
  (println "open-file, :path " path)
  (let [ch (chan)]
    (go
      (let [file (if (not-empty path)
                   (let [buf      (<! (ipc :read-file path))
                         encoding (encode/detect buf)
                         text     (encode/convert buf (clj->js {:to "UNICODE" :type "string"}))
                         content  (if (not-empty path) text nil)]
                     {:id (random-uuid) :path path :content content :encoding encoding})
                   {})]
        (println "returning file to ch: " file)
        (>! ch file)))
    ch))

(defn open-project []
  (go
    (let [path (<! (ipc :show-open-dialog {:title "Open project"
                                           :filters [{:name (str const/project-name " project")
                                                      :extensions [const/project-extension]}
                                                     {:name "All files" :extensions ["*"]}]}))
          file (<! (open-file path))]
      (println "open-project")
      (when-let [edn (file :content)]
        (rf/dispatch-sync [::events/set-db (reader/read-string edn)])))))

(defn write-file [content options]
  (println "write-file")
  (go
    (let [path (<! (ipc :show-save-dialog options))]
      (when-not (str/blank? path)
        (let [ext (get-in options [:filters 0 :extensions 0])
              with-ext (if (re-find (re-pattern (str "\\." ext "$")) path)
                         path
                         (str path (when ext (str "." ext))))]
          (println "saving file to " with-ext "...")
          (<! (ipc :write-file with-ext content)))))))
