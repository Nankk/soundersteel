(ns frontend.db
  (:require ["wavesurfer.js" :as WaveSurfer]
            ["wavesurfer.js/src/plugin/regions/index.js" :as ws-region-raw]))

(def ws-region (.-default ws-region-raw)) ; Just for debugging

(defn- debug-track [t-id f-id s-id]
  (let [el (.createElement js/document "div")
        _  (.setAttribute el "class" "h-100")
        ws (.create WaveSurfer
                    (clj->js
                     {:container     el
                      :responsive    true
                      :backend       "MediaElement"
                      :hideScrollbar false
                      :plugins       [(.create ws-region (clj->js {}))]}))]
    (.load ws  "/home/nankk/projects/soundersteel/resources/public/audio/PlasticAdventure.mp3")
    {:id          t-id
     :file-id     f-id
     :scene-id    s-id
     :name        "Track1"
     :wavesurfer  ws
     :dom-element el
     :playing?    false
     :volume      1.0
     :loop?       true
     :a           nil
     :b           nil
     }))

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
     :tracks        [(debug-track (str (random-uuid)) f-id s-id)
                     (debug-track (str (random-uuid)) f-id s-id)]
     :cur-file-id   nil
     :cur-scene-id  nil
     :cur-track-id  nil}))

(def default-db
  {:name "re-frame"})
