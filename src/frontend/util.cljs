(ns frontend.util)

(defn log [content]
  (.log js/console content))

(defn element [id]
  (.getElementById js/document id))

(defn drop-by-idx [v & indices]
  (loop [dropped v
         rmidcs  (reverse (sort indices))]
    (if (empty? rmidcs)
      dropped
      (recur (vec (concat (subvec dropped 0 (first rmidcs))
                          (subvec dropped (inc (first rmidcs)))))
             (rest rmidcs)))))

(defn first-idx [pred coll]
  (first (keep-indexed #(when (pred %2) %1) coll)))

(defn first-item [pred coll]
  (let [idx (first (keep-indexed #(when (pred %2) %1) coll))]
    (when idx (nth coll idx))))

(defn js<-id [id]
  (.getElementById js/document id))
