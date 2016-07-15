(ns br.eng.crisjr.failproof.web
  (:gen-class
    :name br.eng.crisjr.failproof.web
    :methods [#^{:static true} [getLists [String] "[Ljava.lang.String;"]
              #^{:static true} [getLists [] "[Ljava.lang.String;"]
              #^{:static true} [getLinks [String] "[Ljava.lang.String;"]
              #^{:static true} [getLinks [] "[Ljava.lang.String;"]
              #^{:static true} [getList [String] "java.lang.String"]])
  (:require [br.eng.crisjr.failproof.fetcher :as fetcher]
            [br.eng.crisjr.failproof.extractor :as extractor]))

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
  (-> link fetcher/get-list))

(defn -main
  "Let's get a web page for you now"
  [& args]
  (-> args (nth 0) obtain-raw-data get-all-lists println))
