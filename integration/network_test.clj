(ns br.bsb.liberdade.fpcl.integration-test.network-test
  (:require [babashka.curl :as curl]
            [cheshire.core :as json]))

(def fpcl-url "http://localhost:3000")
(def test-notes "{:name 'Joe'}")
(def new-test-notes "{:name 'Joe Frank'}")

(defn- random-string [length]
  (loop [alphabet "abcdefghijklmnopqrstuvwxyz"
         i 0
         box ""]
    (if (>= i length)
        box
        (recur alphabet
               (inc i)
               (str box (nth alphabet (rand-int (count alphabet))))))))

(defn- create-user [username password notes]
  (let [url (str fpcl-url "/users/create")
        params {"username" username
                "password" password
                "notes" notes}
        response (curl/post url {:body (json/generate-string params)})
        body (json/parse-string (:body response))]
    body))

(defn- get-notes [auth-key]
  (let [url (str fpcl-url "/notes")
        query-params {"auth_key" auth-key}
        response (curl/get url {:query-params query-params})]
    (-> response
        (get :body)
        (json/parse-string)
        (get "notes"))))

(defn- update-notes [auth-key new-notes]
  (let [url (str fpcl-url "/notes")
        params {"auth_key" auth-key
                "notes" new-notes}
        response (curl/post url {:body (json/generate-string params)})
        body (-> response (get :body) (json/parse-string))]
    body))

(defn- compare-notes [auth-key expected-notes]
  (let [result-notes (get-notes auth-key)]
    (= expected-notes result-notes)))

(defn- fail-note-update []
  (try
    (do
      (update-notes (random-string 12) test-notes)
      false)
    (catch clojure.lang.ExceptionInfo e
      ; (println e)
      true)))

(defn- main []
  (let [username (random-string (+ 5 (rand-int 3)))
        password "supersecretpassword"
        auth-key (get (create-user username password test-notes) "auth_key")]
    ; happy cases
    (println (if (compare-notes auth-key test-notes)
                 "can get notes"
                 "can't get notes :/"))
    (update-notes auth-key new-test-notes)
    (println (if (compare-notes auth-key new-test-notes)
                 "can update notes"
                 "can't update notes :/"))
    ; failure cases
    (println (if (-> 12 (random-string) (get-notes) (nil?))
                 "sucessfully failed to get notes"
                 "how did you get notes here?"))
    (println (if (fail-note-update)
                 "successfully failed to update notes"
                 "how did you update notes here?"))))

(main)
