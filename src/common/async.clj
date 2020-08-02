(ns common.async)

(defmacro defan
  "Defines an async function that returns a channel and will put the result of exps."
  [fname args & exps]
  `(defn ~fname [~@args]
     (let [ch# ~'(cljs.core.async/chan)]
       (cljs.core.async/go (cljs.core.async/>! ch# (do ~@exps)))
       ch#)))

(defmacro defan-
  "Defines a private async function that returns a channel and will put the result of exps."
  [fname args & exps]
  `(defn ~(vary-meta fname assoc :private true) [~@args]
     (let [ch# (cljs.core.async/chan)]
       (cljs.core.async/go (cljs.core.async/>! ch# (do ~@exps)))
       ch#)))
