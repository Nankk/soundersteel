(ns frontend.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as rf]
   [frontend.subs :as subs]
   [frontend.events :as events]
   [frontend.views :as views]
   [frontend.config :as config]
   [frontend.style.core :as style.core]
   [cljs.core.async :refer [>! go]]
   [common.const :as const]
   [frontend.io :as io]))

(defn- compile-garden []
  (println "Compiling garden...")
  (let [css-text (style.core/summarize)
        css-elem (. js/document getElementById "garden")]
    (set! (. css-elem -textContent) css-text)))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (compile-garden)
  (rdom/render [views/main-panel]
               (.getElementById js/document "app")))

(defn- register-handlers-from-main []
  ;; Never return nil from cbfs cause it'll be passed to core.async's ch
  (. (. js/window -api) fromMain "open-project"
     (fn [_ _]
       (io/open-project)
       "Project opened."))
  (. (. js/window -api) fromMain "save-project-as"
     (fn [_ _]
       (let [db-raw @(rf/subscribe [::subs/db])
             db     (-> db-raw
                        (update ,, :tracks (fn [ts] (vec (for [t ts] (assoc t :wavesurfer nil :dom-element nil)))))
                        (assoc ,, :ipc-channels {}))]
         (io/write-file (pr-str db) {:title   "Save project"
                                     :filters [{:name       (str const/project-name " project")
                                                :extensions [const/project-extension]}]})
         "Project saved."))))

(defn- register-handler-bidirectional [ipc-channel]
  (. (. js/window -api) fromMain ipc-channel
     (fn [_ channel args]
       (let [ch (get @(rf/subscribe [::subs/ipc-channels]) channel)]
         (when ch
           (go (>! ch (js->clj args))))))))

(defn- register-handlers-bidirectional []
  (doseq [c (const/electron-ipc-channels :bidirectional)]
    (println "registering cbf for " c)
    (register-handler-bidirectional c)))

(defn init []
  (rf/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (register-handlers-bidirectional)
  (register-handlers-from-main)
  (mount-root))
