(ns frontend.style.core
  (:require [frontend.style.file-list :as file-list]
            [frontend.style.global :as global]
            [frontend.style.scene-list :as scene-list]
            [frontend.style.status-bar :as status-bar]
            [frontend.style.track-list :as track-list]))

(defn- appendln [& ss]
  (str (apply str ss) "\n\n"))

(defn summarize []
  (-> ""
      (appendln (global/css))
      (appendln (file-list/css))
      (appendln (scene-list/css))
      (appendln (track-list/css))
      (appendln (status-bar/css))
      ))
