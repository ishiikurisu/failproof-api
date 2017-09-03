(ns br.eng.crisjr.failproof.tools
    (:gen-class
     :name br.eng.crisjr.failproof.tools
     :methods [#^{:static true} [getLists [String] "[Ljava.lang.String;"]
               #^{:static true} [getLists [] "[Ljava.lang.String;"]
               #^{:static true} [getList [String] "java.lang.String"]
               #^{:static true} [toTitles ["[Ljava.lang.String;"] "[Ljava.lang.String;"]
               #^{:static true} [toLinks ["[Ljava.lang.String;"] "[Ljava.lang.String;"]
               #^{:static true} [getTitle [String] String]
               #^{:static true} [getItems [String] "[Ljava.lang.String;"]
               #^{:static true} [getChecks [String] "[Ljava.lang.Boolean;"]
               #^{:static true} [setCheck [String Integer Boolean] String]])
    (:require [br.eng.crisjr.failproof.requests :as requests]
              [br.eng.crisjr.failproof.checklists :as checklists]))

;; CONSTANTS
(def standard-link "https://failproof-checklists.5apps.com/checklists/lists.yml")

;; AUXILIAR FUNCTIONS
(defn obtain-raw-data
    "Let's get the lists on a web page for you."
    [arg]
    (-> arg requests/fetch checklists/parse))

(defn get-stuff [link]
    "Downloads info from a given link. Expects a *.yml file name from the webserver."
    (let [raw-data (obtain-raw-data link)]
        (let [lists (checklists/extract-lists raw-data)
              links (checklists/extract-links raw-data)]
            (for [i (range (count links))]
                (str (nth lists i) ":" (nth links i))))))

;; MAIN FUNCTIONS
(defn get-lists []
    "Downloads a list of pairs 'title:code'."
    (get-stuff standard-link))

(defn get-list [link]
    "Downloads a given checklist to memory."
    (requests/get-list link))

(defn to-titles [stuff]
    "Turns the id list into a list of titles."
    (checklists/raw-to-lists stuff))

(defn to-links [stuff]
    "Turns the id list into a list of links."
    (checklists/raw-to-links stuff))

(defn get-title [checklist]
    "Extracts the title from a checklist in API format."
    (checklists/get-title checklist))

(defn get-items [checklist]
    "Extracts the item name in a checklist in API format."
    (checklists/get-items checklist))

(defn get-checks [checklist]
    "Extracts the state of the checks in a checklist."
    (checklists/get-checks checklist))

(defn set-check [checklist n state]
    "Sets the value on a checklist item"
    (checklists/set-check checklist n state))

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

(defn -getTitle
    [checklist]
    (get-title checklist))

(defn -getItems
    [checklist]
    (-> checklist get-items into-array))

(defn -getChecks
    [checklist]
    (-> checklist get-checks into-array))

(defn -setCheck
    [checklist where what]
    (set-check checklist where what))

; IDEA Create a terminal utility for this thing.
(defn -main
    "Don't run it!"
    [& args]
    (-> args (nth 0) obtain-raw-data println))
