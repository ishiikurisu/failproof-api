(ns br.eng.crisjr.failproof.checklists
    (:gen-class)
    (:require [clojure.string :as str]))

(defn parse-loop
    [line lines stuff]
    (if (nil? line)
        stuff
        (let [data (str/split line #":")]
            (parse-loop (first lines)
                        (rest lines)
                        (assoc stuff :lists (conj (:lists stuff)
                                                  (str/trim (nth data 0)))
                                     :links (conj (:links stuff)
                                                  (str/trim (nth data 1))))))))

(defn parse
    "Extract the lists from the yaml file line by line"
    [yaml]
    (let [lines (str/split-lines yaml)]
        (parse-loop (first lines) (rest lines) {:lists (vector) :links (vector)})))

(defn extract-lists
    [stuff]
    (:lists stuff))

(defn extract-links
    [stuff]
    (:links stuff))

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
    (map #(.substring %1 1) (rest (str/split checklist #"\n"))))
