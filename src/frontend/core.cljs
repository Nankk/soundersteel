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
   [common.const :as const]))

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

(defn- register-handler-bidirectional [ipc-channel]
  (. (. js/window -api) fromMain ipc-channel
     (fn [event channel args]
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
  (mount-root))
