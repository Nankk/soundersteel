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
