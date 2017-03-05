(ns br.eng.crisjr.failproof.geologist
    (:gen-class)
    (:require [clojure.string :as str]))

(defn raw-to-generic
    [raw index]
    (->> raw
         (map #(str/split %1 #":"))
         (map #(nth %1 index))))

(defn raw-to-lists
    "turn raw data into lists"
    [raw]
    (raw-to-generic raw 0))

(defn raw-to-links
    "turn raw data into links"
    [raw]
    (raw-to-generic raw 1))
