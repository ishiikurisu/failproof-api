(ns br.eng.crisjr.web-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [br.eng.crisjr.failproof.web :as web]))

(defn not-nil? [etwas]
    (is (not (nil? etwas))))

;; CLOJURE TESTS
(deftest load-lists
    (testing "can it load the lists in the correct format?"
        (not-nil? (let [stuff (web/get-lists)]
            (do (println (reduce #(str %1 " " %2) stuff))
                stuff)))))

(deftest load-list
    (testing "can it load a list?"
        (not-nil? (let [list (web/get-list "apps.yml")]
            (do (println list)
                list)))))

(deftest lists-to-titles
    (testing "can I turn the id list into a list of titles?"
        (not-nil? (let [stuff (web/get-lists)
                        titles (web/to-titles stuff)]
            (do (println "titles:")
                (println (reduce #(str %1 "-" %2 "\n") "" titles))
                titles)))))

(deftest lists-to-links
    (testing "can I turn the id list into a list of links?"
        (not-nil? (let [stuff (web/get-lists)
                        links (web/to-links stuff)]
            (do (println "links:")
                (println (reduce #(str %1 "-" %2 "\n") "" links))
                links)))))

;; JAVA TESTS
;; TODO Define Java tests
