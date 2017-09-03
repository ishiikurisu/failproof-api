(ns br.eng.crisjr.failproof.requests
    (:gen-class)
    (:require [clojure.string :as str]))

(defn fetch
    "Gets the html from the chosen address"
    [source]
    (slurp source))

(defn get-title
    [inlet]
    (subs inlet 0 (-> inlet count dec)))

(defn get-item
    [inlet]
    (subs inlet 2 (count inlet)))

(defn listify
    [inlet]
    (let [lines (str/split-lines inlet)]
        (reduce #(str %1 "-" (get-item %2) "\n")
                (str (-> lines first get-title) "\n")
                (rest lines))))

(defn get-list
    [link]
    (-> (str "https://failproof-checklists.5apps.com/checklists/" link)
        fetch
        listify))
