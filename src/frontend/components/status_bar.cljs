(ns frontend.components.status-bar
  (:require [frontend.subs :as subs]
            [re-frame.core :as rf]))

(defn main []
  [:div#status-bar.d-flex.flex-row.align-items-center.justify-content-end
   [:div @(rf/subscribe [::subs/status-message])]])
