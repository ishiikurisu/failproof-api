(ns br.bsb.liberdade.fpcl.api
  (:gen-class)
  (:require [clojure.data.json :as json]
            [clojure.string :as string]
            [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [jumblerg.middleware.cors :refer [wrap-cors]]
            [selmer.parser :refer :all]
            [br.bsb.liberdade.fpcl.db :as db]))

; #############
; # UTILITIES #
; #############
(defn- boilerplate [body]
  {:status 200
   :headers {"Content-Type" "text/json"
             "Access-Control-Allow-Origin" "*"
             "Access-Control-Expose-Headers" "*"}
   :body (str (json/write-str body))})

(defn- url-search-params [raw]
  (->> (string/split raw #"&")
       (map #(string/split % #"="))
       (reduce (fn [box [key value]] (assoc box key value)) {})))

; ##########
; # ROUTES #
; ##########
(defn create-users [req]
  (let [params (json/read-str (slurp (:body req)))
        username (get params "username")
        password (get params "password")
        notes (get params "notes")]
    (boilerplate (db/create-user username password notes))))

(defn auth-users [req]
  (let [params (json/read-str (slurp (:body req)))
        username (get params "username")
        password (get params "password")]
    (boilerplate (db/auth-user username password))))

(defn update-password [req]
  (let [params (-> req :body slurp json/read-str)
        username (get params "username")
        old-password (get params "old_password")
        new-password (get params "new_password")]
    (boilerplate (db/update-password username old-password new-password))))

(defn get-notes [req]
  (-> req
      :query-string
      (url-search-params)
      (get "auth_key")
      (db/get-notes)
      (boilerplate)))

(defn post-notes [req]
  (let [params (json/read-str (slurp (:body req)))
        auth-key (get params "auth_key")
        notes (get params "notes")]
    (boilerplate (db/update-notes auth-key notes))))

(defroutes app-routes
  (POST "/users/create" [] create-users)
  (POST "/users/auth" [] auth-users)
  (POST "/users/password" [] update-password)
  (GET "/notes" [] get-notes)
  (POST "/notes" [] post-notes))

; ###############
; # Entry point #
; ###############
(defn -main [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (db/setup-database)
    (server/run-server (wrap-cors #'app-routes #".*" (assoc site-defaults :security nil)) {:port port})
    (println (str "Listening at http://localhost:" port "/"))))
