(ns br.bsb.liberdade.fpcl.db-test
  (:require [clojure.test :refer :all]
            [br.bsb.liberdade.fpcl.db :as db]))

(deftest create-users-test
  (testing "Creating users as expected"
    (do
      (db/drop-database)
      (db/setup-database)
      ; testing if creating a new user works
      (let [username "username"
            password "password"
            notes nil
            result (db/create-user username password notes)]
        (is (not (nil? (:auth-key result)))))
      ; testing if creating the user again fails
      (let [username "username"
            password "password"
            notes nil
            result (db/create-user username password notes)]
        (is (nil? (:auth-key result)))))))
