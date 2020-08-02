(ns frontend.util)

(defn log [content]
  (.log js/console content))

(defn element [id]
  (.getElementById js/document id))
