(ns main.core
  (:require
   [async-interop.interop :refer-macros [<p!]]
   [cljs.core.async :as async :refer [>! <! go chan]]
   ["electron" :as electron :refer [app ipcMain Menu BrowserWindow dialog]]
   ["fs" :as fs]
   ["path" :as path]))

(def main-window (atom nil))
(def menu-template [{:label "File"
                     :submenu [{:type "separator"}
                               {:role "quit"}]}
                    {:label "Edit"
                     :submenu [{:role "undo"}
                               {:role "redo"}
                               {:role "cut"}
                               {:role "copy"}
                               {:role "delete"}
                               {:role "selectAll"}
                               ]}
                    {:label "View"
                     :submenu [{:role "reload"}
                               {:role "forcereload"}
                               {:type "separator"}
                               {:role "toggledevtools"}
                               {:type "separator"}
                               {:role "resetzoom"}
                               {:role "zoomin"}
                               {:role "zoomout"}
                               {:type "separator"}
                               {:role "togglefullscreen"}
                               ]}
                    {:label "Window"
                     :submenu [{:role "zoom"}
                               {:role "minimize"}
                               {:role "close"}]}])
(def handlers [["join" (fn [ch [p1 p2]] (go (>! ch (path/join p1 p2))))]
               ["dirname" (fn [ch [p]] (go (>! ch (path/dirname p))))]
               ["relative" (fn [ch [p1 p2]] (go (>! ch (path/relative p1 p2))))]
               ["absolute?" (fn [ch [p]] (go (>! ch (path/isAbsolute p))))]
               ["file-exist?" (fn [ch [p]] (go (>! ch (try (fs/statSync p) true (catch js/Object e false)))))]
               ["create-directory" (fn [ch [p]] (go (>! ch (fs/mkdirSync p))))]
               ["read-file" (fn [ch [p]] (go (>! ch (fs/readFileSync p))))]
               ["write-file" (fn [ch [p content]] (go (>! ch (fs/writeFileSync p content))))]
               ["open-file" (fn [ch [p flag]] (go (>! ch (fs/openSync p flag))))]
               ["show-open-dialog"
                (fn [ch [options]]
                  (println "show-open-dialog")
                  (go
                    (try
                      (let [opt-merged (merge {:properties  ["openFile"]
                                               :title       "Select file"
                                               :defaultPath "."
                                               :filters     [{:name "All files" :extensions ["*"]}]}
                                              options)
                            res        (<p! (. dialog showOpenDialog nil (clj->js opt-merged)))
                            pth        (or (get (js->clj (. res -filePaths)) 0) "")]
                        (>! ch pth))
                      (catch js/Error e
                        (println e)
                        (>! ch "")))))]
               ["show-save-dialog"
                (fn [ch [options]]
                  (println "show-save-dialog")
                  (go
                    (try
                      (let [opt-merged (merge {:properties ["openFile"]
                                               :title      "Save"
                                               :filters    [{:name "All files" :extensions ["*"]}]}
                                              options)
                            res        (<p! (. dialog showSaveDialog nil (clj->js opt-merged)))
                            pth        (. res -filePath)]
                        (>! ch (or pth "")))
                      (catch :default e
                        (println e)
                        (>! ch "")))))]])

(defn- set-ipc-handler [ipc-channel cbf]
  (.on ipcMain ipc-channel
       (fn [event args-js]
         (go
           (let [ch   (chan) ; for those that includes async operations
                 args (js->clj args-js)
                 _    (cbf ch args)
                 ret  (<! ch)]
             (-> @main-window
                 (. ,, -webContents)
                 (. ,, send ipc-channel
                       (clj->js {:value ret}))))))))

(defn- set-ipc-handlers []
  (doseq [[ipc-channel cbf] handlers]
    (set-ipc-handler ipc-channel cbf)))

(defn- init-browser []
  (reset! main-window (BrowserWindow.
                       (clj->js {:width 1200
                                 :height 800
                                 :webPreferences {:nodeIntegration false
                                                  :contextIsolation true
                                                  :enableRemoteModule false
                                                  :preload (path/join js/__dirname "scripts/preload.js")}})))

  ;; Application menu
  (let [menu (. Menu buildFromTemplate (clj->js menu-template))]
    (.setApplicationMenu Menu menu))

  (set-ipc-handlers)
  (.loadURL @main-window (str "file://" js/__dirname "/public/index.html"))
  (.on @main-window "closed" #(reset! main-window nil)))

(defn main []
  (.on app "window-all-closed"
       (fn []
         (when-not (= js/process.platform "darwin")
           (.quit app))))
  (.on app "ready" init-browser))
