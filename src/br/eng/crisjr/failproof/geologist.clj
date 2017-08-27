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

(defn get-title
    "Gets the title from a checklist in API format."
    [checklist]
    (nth (str/split checklist #"\n") 0))

(defn get-items
    "Extracts the item name in a checklist in API format."
    [checklist]
    (rest (str/split checklist #"\n")))
