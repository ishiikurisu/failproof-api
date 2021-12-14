(ns br.bsb.liberdade.fpcl.utils-test
  (:require [clojure.test :refer :all]
            [br.bsb.liberdade.fpcl.utils :as utils]))

(deftest jwt-handling
  (testing "can encode and decode data"
    (let [data {:data "a random payload"}
          encoded (utils/encode-secret data)
          decoded (utils/decode-secret encoded)]
      (is (= data decoded))
      (is (not (= data encoded))))))
