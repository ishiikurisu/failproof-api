(ns br.eng.crisjr.web-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [br.eng.crisjr.failproof.web :as web]))

(defn not-nil? [etwas]
    (is (not (nil? etwas))))

(deftest load-data
  (testing "can it load something?"
    (not-nil? (-> web/standard-link
                  web/obtain-raw-data
                  web/extract-lists
                  into-array))))

(deftest load-links
    (testing "can it load links?"
        (not-nil? (web/-getLinks))))

(deftest load-list
    (testing "can it load a list from a link?"
        (not-nil? (web/-getList (nth (web/-getLinks) 0)))))

(deftest load-stuff
    (testing "can it load stuff?"
        (not-nil? (nth (web/-getStuff) 0))))

(deftest get-lists
    (testing "turning raw data into lists"
        (not-nil?
            (let [lists (web/-toLists (web/-getStuff))]
                (do (println (reduce #(str %1 " " %2) lists))
                    lists)
            )
        )
    )
)

(deftest get-links
    (testing "turning raw data into lists"
        (not-nil?
            (let [lists (web/-toLinks (web/-getStuff))]
                (do (println (reduce #(str %1 " " %2) lists))
                    lists)
            )
        )
    )
)
