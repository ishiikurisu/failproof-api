(ns br.bsb.liberdade.fpcl.api
  (:gen-class)
  (:require [clojure.data.json :as json]
            [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [selmer.parser :refer :all]))

; ##########
; # ROUTES #
; ##########
(defn index-page [req]
  {:status 200
   :headers {"Content-Type" "text/json"}
   :body (str (json/write-str {:error "not defined yet"}))})

(defroutes app-routes
  (GET "/" [] index-page))

; ###############
; # Entry point #
; ###############
(defn -main [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
    (println (str "Listening at http://localhost:" port "/"))))
