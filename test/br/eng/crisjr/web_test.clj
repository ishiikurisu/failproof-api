(ns br.eng.crisjr.web-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [br.eng.crisjr.web :as fp]))

(deftest load-data
  (testing "testing if it can load something"
    (is (not (nil? (-> fp/standard-link
                       fp/obtain-raw-data
                       fp/extract-lists
                       into-array))))))
