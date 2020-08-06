(ns frontend.db
  (:require
   ;; Just for debugging
   ["wavesurfer.js" :as WaveSurfer]
   ["wavesurfer.js/src/plugin/regions/index.js" :as ws-region-raw]))

(def ws-region (.-default ws-region-raw)) ; Just for debugging

(def debug-db
  (let [f-id  (str (random-uuid))
        s-id  (str (random-uuid))
        s-id2 (str (random-uuid))
        t-id  (str (random-uuid))
        track (let [el (.createElement js/document "div")
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
                 :playing? false
                 :volume   1.0
                 :loop     {:begin nil
                            :end   nil}})]
    {:master-volume 1.0
     :files         [{:id   f-id
                      :path "/home/nankk/projects/soundersteel/resources/public/audio/PlasticAdventure.mp3"}]
     :scenes        [{:id   s-id
                      :name "Piyo"}
                     {:id   s-id2
                      :name "Poyo"}]
     :tracks        [track
                     ;; {:id          t-id
                     ;;  :file-id     f-id
                     ;;  :scene-id    s-id
                     ;;  :name        "hoge-fugue"
                     ;;  :wavesurfer  nil ; Wavesurfer instance
                     ;;  :dom-element nil ; Container
                     ;;  :play-info   {:playing? false
                     ;;                :loop     {:begin 10
                     ;;                           :end   60}}}
                     ]
     :cur-file-id   nil
     :cur-scene-id  nil
     :cur-track-id  nil}))

(def default-db
  {:name "re-frame"})
