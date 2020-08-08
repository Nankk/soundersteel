(ns frontend.const)

(def acceptable-extensions ["wav" "mp3" "m4a"])

(defn default-scene [] {:id (random-uuid) :name "New scene" :tracks []})

(def op-tolerance-sec 0.5)
