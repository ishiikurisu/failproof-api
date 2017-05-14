(ns br.eng.crisjr.failproof.web
    (:gen-class
     :name br.eng.crisjr.failproof.web
     :methods [#^{:static true} [getLists [String] "[Ljava.lang.String;"]
               #^{:static true} [getLists [] "[Ljava.lang.String;"]
               #^{:static true} [getList [String] "java.lang.String"]
               #^{:static true} [toTitles ["[Ljava.lang.String;"] "[Ljava.lang.String;"]
               #^{:static true} [toLinks ["[Ljava.lang.String;"] "[Ljava.lang.String;"]])
    (:require [br.eng.crisjr.failproof.fetcher :as fetcher]
              [br.eng.crisjr.failproof.extractor :as extractor]
              [br.eng.crisjr.failproof.geologist :as geologist]))

;; CONSTANTS
(def standard-link "https://failproof-checklists.5apps.com/checklists/lists.yml")

;; MAIN FUNCTIONS
(defn obtain-raw-data
    "Let's get the lists on a web page for you"
    [arg]
    (-> arg fetcher/fetch fetcher/parse))

(defn get-stuff [link]
    "Downloads info from a given link. Expects a "
    (let [raw-data (obtain-raw-data link)]
        (let [lists (extractor/extract-lists raw-data)
              links (extractor/extract-links raw-data)]
            (for [i (range (count links))]
                (str (nth lists i) ":" (nth links i))))))

(defn get-lists []
    "Downloads a list of pairs 'title:code'"
    (get-stuff standard-link))

(defn get-list [link]
    "Downloads a given checklist to memory"
    (fetcher/get-list link))

(defn to-titles [stuff]
    "Turns the id list into a list of titles"
    (geologist/raw-to-lists stuff))

(defn to-links [stuff]
    "Turns the id list into a list of links"
    (geologist/raw-to-links stuff))

;; INTERFACE TO JAVA
(defn -getLists
    ([] (into-array (get-lists)))
    ([inlet] (into-array (get-stuff inlet))))

(defn -getList
    [link]
    (get-list link))

(defn -getStuff
    "Let's get stuff done"
    []
    (-> standard-link get-stuff into-array))

(defn -toTitles
    [raw]
    (into-array (to-titles raw)))

(defn -toLinks
    [raw]
    (into-array (to-links raw)))

(defn -main
    "Let's get a web page for you now"
    [& args]
    (-> args (nth 0) obtain-raw-data println))
