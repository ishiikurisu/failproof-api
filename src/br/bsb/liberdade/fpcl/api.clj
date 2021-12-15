(ns br.bsb.liberdade.fpcl.api
  (:gen-class)
  (:require [clojure.data.json :as json]
            [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [selmer.parser :refer :all]
            [br.bsb.liberdade.fpcl.db :as db]))

; #############
; # UTILITIES #
; #############

(defn- boilerplate [body]
  {:status 200
   :headers {"Content-Type" "text/json"}
   :body (str (json/write-str body))})

; ##########
; # ROUTES #
; ##########
(defn index-page [req]
  {:status 200
   :headers {"Content-Type" "text/json"}
   :body (str (json/write-str {:error "not defined yet"}))})

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

(defn get-notes [req]
  (let [params (:params req)
        auth-key (:auth_key params)]
    (boilerplate (db/get-notes auth-key))))

(defroutes app-routes
  (POST "/users/create" [] create-users)
  (POST "/users/auth" [] auth-users)
  (GET "/notes" [] get-notes))

; ###############
; # Entry point #
; ###############
(defn -main [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (db/setup-database)
    (server/run-server (wrap-defaults #'app-routes (assoc site-defaults :security nil)) {:port port})
    (println (str "Listening at http://localhost:" port "/"))))
