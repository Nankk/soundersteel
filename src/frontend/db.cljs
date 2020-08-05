(ns frontend.db)

(def debug-db
  (let [f-id  (random-uuid)
        s-id  (random-uuid)
        s-id2 (random-uuid)
        t-id  (random-uuid)]
    {:master-volume 1
     :files         [{:id   f-id
                      :path "audio/PlasticAdventure.mp3"}]
     :scenes        [{:id   s-id
                      :name "Piyo"}
                     {:id   s-id2
                      :name "Poyo"}]
     :tracks        [{:id         t-id
                      :file-id    f-id
                      :scene-id   s-id
                      :name       "hoge-fugue"
                      :wavesurfer nil
                      :loop       {:begin 10
                                   :end   60}}]
     :cur-file-id   nil
     :cur-scene-id  nil
     :cur-track-id  nil}))

(def default-db
  {:name "re-frame"})
