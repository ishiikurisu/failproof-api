(ns br.eng.crisjr.failproof.fetcher
  (:gen-class)
  (:require [clojure.string :as str]))

;; Initial fetch
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

(defn fetch
  "Gets the html from the chosen address"
  [source]
  (slurp source))

;; List fetch
(defn get-title
  [inlet]
  (subs inlet 0 (-> inlet count dec)))

(defn get-item
  [inlet]
  (subs inlet 2 (count inlet)))

(defn listify
  [inlet]
  (let [lines (str/split-lines inlet)]
    (reduce #(str %1 (get-item %2) "\n")
            (str (-> lines first get-title) "\n")
            (rest lines))))

(defn get-list
  [link]
  (-> (str "https://raw.githubusercontent.com/ishiikurisu/checklists/master/" link)
      fetch
      listify))
