(ns frontend.const)

(def acceptable-extensions ["wav" "mp3" "m4a"])

(defn default-scene [] {:id (random-uuid) :name "New scene" :tracks []})
