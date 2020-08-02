(ns frontend.style.core
  (:require
   [frontend.style.global :as global]
   ))

(defn- appendln [& ss]
  (str (apply str ss) "\n\n"))

(defn summarize []
  (-> ""
      (appendln (global/css))
      ))
