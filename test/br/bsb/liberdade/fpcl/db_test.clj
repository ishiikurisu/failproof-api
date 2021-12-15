(ns br.bsb.liberdade.fpcl.db-test
  (:require [clojure.test :refer :all]
            [br.bsb.liberdade.fpcl.db :as db]))

(def test-note
  (str "# Introduction Note\n"
       "- List item #1\n"
       "- List item #2\n"
       "A paragraph as example\n"))

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
        (is (not (nil? (get result "auth_key")))))
      ; testing if creating the user again fails
      (let [username "username"
            password "password"
            notes nil
            result (db/create-user username password notes)]
        (is (nil? (get result "auth_key")))))))

(deftest auth-users-test
  (testing "Authenticating (or not) users as expected"
    (do
      (db/drop-database)
      (db/setup-database)
      ; testing if creating a new user works
      (let [username "username"
            password "password"
            notes test-note
            creation-result (db/create-user username password notes)
            result (db/auth-user username password)]
        (is (not (nil? (get creation-result "auth_key"))))
        (is (not (nil? (get result "auth_key"))))
        (is (= notes (:notes result))))
      (let [username "username"
            password "wrong password"
            result (db/auth-user username password)]
        (is (nil? (get result "auth_key")))
        (is (nil? (:notes result))))
      (let [username "inexistent username"
            password "password"
            result (db/auth-user username password)]
        (is (nil? (get result "auth_key")))
        (is (nil? (:notes result)))))))
