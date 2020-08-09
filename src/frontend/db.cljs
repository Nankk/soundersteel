(ns frontend.db
  (:require ["wavesurfer.js" :as WaveSurfer]
            ["wavesurfer.js/src/plugin/regions/index.js" :as ws-region-raw]))

(def ws-region (.-default ws-region-raw)) ; Just for debugging

(def debug-db
  (let [f-id  (str (random-uuid))
        s-id  (str (random-uuid))
        s-id2 (str (random-uuid))]
    {:master-volume 1.0
     :files         [{:id   f-id
                      :path "/home/nankk/projects/soundersteel/resources/public/audio/PlasticAdventure.mp3"}]
     :scenes        [{:id   s-id
                      :name "Piyo"}
                     {:id   s-id2
                      :name "Poyo"}]
     :tracks        [{:id          (str (random-uuid))
                      :file-id     f-id
                      :scene-id    s-id
                      :name        "Track1"
                      :wavesurfer  nil
                      :dom-element nil
                      :playing?    false
                      :volume      1.0
                      :loop?       true
                      :a           nil
                      :b           nil}
                     {:id          (str (random-uuid))
                      :file-id     f-id
                      :scene-id    s-id
                      :name        "Track2"
                      :wavesurfer  nil
                      :dom-element nil
                      :playing?    false
                      :volume      1.0
                      :loop?       true
                      :a           nil
                      :b           nil}]
     :cur-file-id   nil
     :cur-scene-id  nil
     :cur-track-id  nil}))

(def default-db
  {:name "re-frame"})
