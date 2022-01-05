(ns br.bsb.liberdade.fpcl.db-test
  (:require [clojure.test :refer :all]
            [br.bsb.liberdade.fpcl.db :as db]))

(def test-note
  (str "# Introduction Note\n"
       "- List item #1\n"
       "- List item #2\n"
       "A paragraph as example\n"))
(def another-test-note
  (str "Another test note, nothing serious"))
(def random-auth-key
  "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTIzNH0.rWp8vvb4aDZAGcHEYjhCe9qaaf8mSyvyLeyC1QuZWU0")

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
      (let [username "username"
            password "password"
            notes test-note
            creation-result (db/create-user username password notes)
            result (db/auth-user username password)]
        (is (not (nil? (get creation-result "auth_key"))))
        (is (not (nil? (get result "auth_key"))))
        (is (= notes (:notes result))))))
    (testing "Fails with wrong username"
      (let [username "username"
            password "wrong password"
            result (db/auth-user username password)]
        (is (nil? (get result "auth_key")))
        (is (nil? (:notes result)))))
    (testing "Fails with inexistent username"
      (let [username "inexistent username"
            password "password"
            result (db/auth-user username password)]
        (is (nil? (get result "auth_key")))
        (is (nil? (:notes result))))))

(deftest get-notes
  (testing "No notes from inexistent users"
    (do
      (db/drop-database)
      (db/setup-database)
      (let [result (db/get-notes random-auth-key)]
        (is (nil? (:notes result))))))
  (testing "Getting notes from existing users"
    (do
      (db/drop-database)
      (db/setup-database)
      (db/create-user "username" "password" test-note)
      (let [auth (db/auth-user "username" "password")
            result (db/get-notes (get auth "auth_key"))
            notes (:notes result)]
        (is (= notes test-note))))))

(deftest update-notes
  (testing "updating notes"
    (do
      (db/drop-database)
      (db/setup-database)
      (db/create-user "username" "password" test-note)
      (let [auth (db/auth-user "username" "password")
            update-result (db/update-notes (get auth "auth_key") another-test-note)
            new-notes (-> auth (get "auth_key") (db/get-notes) :notes)]
        (is (nil? (:error update-result)))
        (is (= new-notes another-test-note)))))
  (testing "fails update gracefully"
    (let [update-result (db/update-notes random-auth-key another-test-note)]
      (-> update-result :error (nil?) (not) (is)))))

(deftest update-password
  (testing "fails to update password"
    (do
      (db/setup-database)
      (db/create-user "username" "password" test-note)
      (let [auth (db/auth-user "username" "password")
            auth-key (get auth "auth_key")
            update-result (db/update-password auth-key
                                              "wrong password"
                                              "newPassword")]
        (is (not (nil? (:error update-result)))))
      (db/drop-database)))
  (testing "update password"
    (do
      (db/setup-database)
      (db/create-user "username" "password" test-note)
      (let [auth (db/auth-user "username" "password")
            auth-key (get auth "auth_key")
            update-result (db/update-password auth-key "password" "newPassword")
            new-auth (db/auth-user "username" "newPassword")]
        (is (nil? (:error update-result)))
        (is (nil? (:error new-auth))))
      (db/drop-database))))
