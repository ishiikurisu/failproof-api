(ns br.eng.crisjr.failproof.web
    (:gen-class
     :name br.eng.crisjr.failproof.web
     :methods [#^{:static true} [getLists [String] "[Ljava.lang.String;"]
               #^{:static true} [getLists [] "[Ljava.lang.String;"]
               #^{:static true} [getLinks [String] "[Ljava.lang.String;"]
               #^{:static true} [getLinks [] "[Ljava.lang.String;"]
               #^{:static true} [getList [String] "java.lang.String"]
               #^{:static true} [toLists ["[Ljava.lang.String;"] "[Ljava.lang.String;"]
               #^{:static true} [toLinks ["[Ljava.lang.String;"] "[Ljava.lang.String;"]
               #^{:static true} [getStuff [] "[Ljava.lang.String;"]])
    (:require [br.eng.crisjr.failproof.fetcher :as fetcher]
              [br.eng.crisjr.failproof.extractor :as extractor]
              [br.eng.crisjr.failproof.geologist :as geologist]))

;; MAIN FUNCTIONS
(defn obtain-raw-data
    "Let's get the lists on a web page for you"
    [arg]
    (-> arg fetcher/fetch fetcher/parse))

(defn extract-lists
    "Getting our lists"
    [stuff]
    (extractor/extract-lists stuff))

(defn extract-links
    "Let's transform these lists into links"
    [stuff]
    (extractor/extract-links stuff))

(defn get-all-lists
    [stuff]
    (let [lists (extractor/extract-lists stuff)
          links (extractor/extract-links stuff)]
    (loop [index 0
           limit (count lists)
           box (vector)]
        (if (= limit index)
            box
            (recur (inc index)
                   limit
                   (conj box (str (nth lists index) "\n"
                                  (fetcher/get-list (nth links index)) "\n")))))))

(defn get-stuff [link]
    (let [raw-data (obtain-raw-data link)]
        (let [lists (extract-lists raw-data)
              links (extract-links raw-data)]
            (for [i (range (count links))]
                (str (nth links i) "\n" (nth lists i))))))

;; INTERFACE
(def standard-link "https://raw.githubusercontent.com/ishiikurisu/checklists/master/lists.yml")
(defn -getLists
    ([inlet]
        (-> inlet obtain-raw-data extract-lists into-array))
    ([]
        (-> standard-link -getLists)))

(defn -getLinks
    ([inlet]
        (-> inlet obtain-raw-data extract-links into-array))
    ([]
        (-> standard-link -getLinks)))

(defn -getList
    [link]
    (fetcher/get-list link))

(defn -getStuff
    "Let's get stuff done"
    []
    (-> standard-link get-stuff into-array))

(defn -toLists
    [raw]
    (geologist/raw-to-lists raw))

(defn -toLinks
    [raw]
    (geologist/raw-to-links raw))

(defn -main
    "Let's get a web page for you now"
    [& args]
    (-> args (nth 0) obtain-raw-data get-all-lists println))
