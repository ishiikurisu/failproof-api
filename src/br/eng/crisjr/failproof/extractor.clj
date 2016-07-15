(ns br.eng.crisjr.failproof.extractor
  (:gen-class))

(defn extract-lists
  [stuff]
  (:lists stuff))

(defn extract-links
  [stuff]
  (:links stuff))
