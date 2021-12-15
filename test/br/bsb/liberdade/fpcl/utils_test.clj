(ns br.bsb.liberdade.fpcl.utils-test
  (:require [clojure.test :refer :all]
            [br.bsb.liberdade.fpcl.utils :as utils]))

(def random-jwt
  "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTIzNH0.rWp8vvb4aDZAGcHEYjhCe9qaaf8mSyvyLeyC1QuZWU0")

(deftest jwt-handling
  (testing "can encode and decode data"
    (let [data {:data "a random payload"}
          encoded (utils/encode-secret data)
          decoded (utils/decode-secret encoded)]
      (is (= data decoded))
      (is (not (= data encoded)))))
  (testing "fails gracefully"
    (let [decoded (utils/decode-secret random-jwt)]
      (is (nil? decoded)))))

(deftest password-handling
  (testing "can hide the same password many times over"
    (let [p1 (utils/hide "password")
          p2 (utils/hide "password")]
      (is (= p1 p2)))))
