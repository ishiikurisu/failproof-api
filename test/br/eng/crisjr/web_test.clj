(ns br.eng.crisjr.web-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [br.eng.crisjr.failproof.web :as web]))

(defn not-nil? [etwas]
    (is (not (nil? etwas))))

;; CLOJURE TESTS
(deftest load-data
  (testing "can it load something?"
    (not-nil? (-> web/standard-link
                  web/obtain-raw-data
                  web/extract-lists
                  into-array))))

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

;; JAVA TESTS
;; TODO Define Java tests
