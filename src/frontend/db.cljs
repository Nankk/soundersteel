(ns frontend.db)

(def debug-db
  (let [f-id  (random-uuid)
        s-id  (random-uuid)
        s-id2 (random-uuid)
        t-id  (random-uuid)
        l-id  (random-uuid)]
    {:master-volume 1
     :files         [{:id   f-id
                      :path "/home/user/Music/hogefuga.mp3"}]
     :scenes        [{:id     s-id
                      :name   "Piyo"
                      :tracks [t-id]}
                     {:id     s-id2
                      :name   "Poyo"
                      :tracks []}]
     :tracks        [{:id      t-id
                      :file-id f-id
                      :name    "hoge-fugue"
                      :loop    {:id         l-id
                                :loop       {:begin 10
                                             :end   60}
                                :volume     1
                                :wavesurfer nil}}]
     :cur-file-id   nil
     :cur-scene-id  nil
     :cur-track-id  nil}))

(def default-db
  {:name "re-frame"})
