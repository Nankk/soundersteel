(ns main.core
  (:require
   [async-interop.interop :refer-macros [<p!]]
   [cljs.core.async :as async :refer [>! <! go chan]]
   ["electron" :as electron :refer [app ipcMain Menu BrowserWindow dialog globalShortcut]]
   ["fs" :as fs]
   ["path" :as path]))

(def main-window (atom nil))
(def menu-template [{:label "File"
                     :submenu [{:label "Open project"
                                :click #(. (. @main-window -webContents) send "open-project")
                                :accelerator "CmdOrCtrl + O"}
                               {:type "separator"}
                               {:label "Save project as..."
                                :click #(. (. @main-window -webContents) send "save-project-as")
                                :accelerator "CmdOrCtrl + S"}
                               {:type "separator"}
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
                     :submenu [{:type "separator"}
                               {:role "reload"}
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
(def global-shortcuts
  [["alt+super+q" "move-to-prev-scene"]
   ["alt+super+w" "move-to-next-scene"]
   ["alt+super+1" "toggle-playing-state" {:tidx 0}]
   ["alt+super+2" "toggle-playing-state" {:tidx 1}]
   ["alt+super+3" "toggle-playing-state" {:tidx 2}]
   ["alt+super+4" "toggle-playing-state" {:tidx 3}]
   ["alt+super+5" "toggle-playing-state" {:tidx 4}]
   ["alt+super+6" "toggle-playing-state" {:tidx 5}]
   ["alt+super+7" "toggle-playing-state" {:tidx 6}]
   ["alt+super+8" "toggle-playing-state" {:tidx 7}]
   ["alt+super+9" "toggle-playing-state" {:tidx 8}]
   ["alt+super+0" "toggle-playing-state" {:tidx 9}]
   ["alt+shift+super+1" "toggle-loop?" {:tidx 0}]
   ["alt+shift+super+2" "toggle-loop?" {:tidx 1}]
   ["alt+shift+super+3" "toggle-loop?" {:tidx 2}]
   ["alt+shift+super+4" "toggle-loop?" {:tidx 3}]
   ["alt+shift+super+5" "toggle-loop?" {:tidx 4}]
   ["alt+shift+super+6" "toggle-loop?" {:tidx 5}]
   ["alt+shift+super+7" "toggle-loop?" {:tidx 6}]
   ["alt+shift+super+8" "toggle-loop?" {:tidx 7}]
   ["alt+shift+super+9" "toggle-loop?" {:tidx 8}]
   ["alt+shift+super+0" "toggle-loop?" {:tidx 9}]])
(def handlers [["join" (fn [ch [p1 p2]] (go (>! ch (path/join p1 p2))))]
               ["dirname" (fn [ch [p]] (go (>! ch (path/dirname p))))]
               ["basename" (fn [ch [p]] (go (>! ch (path/basename p))))]
               ["relative" (fn [ch [p1 p2]] (go (>! ch (path/relative p1 p2))))]
               ["absolute?" (fn [ch [p]] (go (>! ch (path/isAbsolute p))))]
               ["file-exist?" (fn [ch [p]] (go (>! ch (try (fs/statSync p) true (catch js/Object e false)))))]
               ["create-directory" (fn [ch [p]] (go (>! ch (fs/mkdirSync p))))]
               ["read-file" (fn [ch [p]] (go (>! ch (fs/readFileSync p))))]
               ["write-file" (fn [ch [p content]] (go (>! ch (do (fs/writeFileSync p content) "ok"))))]
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
       (fn [_ args-js]
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

(defn- register-global-shortcuts []
  (doseq [gs global-shortcuts]
    (.register globalShortcut
               (gs 0)
               (fn []
                 (.send (.-webContents @main-window ) (gs 1) (clj->js (get gs 2)))))))

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
  ;; Global shortcuts
  (register-global-shortcuts)
  ;; Electron ipc
  (set-ipc-handlers)
  ;; GUI
  (.loadURL @main-window (str "file://" js/__dirname "/public/index.html"))
  (.on @main-window "closed" #(reset! main-window nil)))

(defn main []
  (.on app "window-all-closed"
       (fn []
         (when-not (= js/process.platform "darwin")
           (.quit app))))
  (.on app "ready" init-browser)
  (.on app "will-quit" (fn [] (.unregisterAll globalShortcut))))
