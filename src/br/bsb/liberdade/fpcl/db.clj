(ns br.bsb.liberdade.fpcl.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [clojure.java.io :as io]
            [br.bsb.liberdade.strint :as strint]
            [br.bsb.liberdade.fpcl.utils :as utils]))

(defn- read-resource [resource]
  (with-open [reader (io/reader (str "resources/db/sql/" resource))]
    (slurp reader)))

(defn- load-sql [fns]
  (reduce (fn [box [key value]]
            (assoc box key (read-resource value)))
          {}
          fns))

(def sql-files
  {:auth-user "auth_user.sql"
   :create-tables "create_tables.sql"
   :create-user "create_user.sql"
   :export-users "export_users.sql"
   :get-notes "get_notes.sql"
   :update-notes "update_notes.sql"
   :drop-database "drop_database.sql"})
(def sql (load-sql sql-files))
(def db (or (System/getenv "JDBC_DATABASE_URL")
            "jdbc:postgresql://localhost:5433/fpcl?user=fpcl&password=password"))
(def ds (jdbc/get-datasource db))
(Class/forName "org.postgresql.Driver")  ; required to get the driver working properly

(defn drop-database []
  (jdbc/execute! ds [(get sql :drop-database)]))

(defn setup-database []
  (jdbc/execute! ds [(get sql :create-tables)]))

(defn create-user [username password notes]
  (let [params {"admin" "off"
                "notes" (utils/encode-secret {:notes (or notes "")})
                "password" (utils/hide password)
                "username" username}
        query (strint/strint (get sql :create-user) params)
        result (jdbc/execute! ds [query] {:builder-fn rs/as-unqualified-lower-maps})
        user-id (-> result first (get :id))]
    {:auth-key (if (nil? user-id)
                  user-id
                  (utils/encode-secret {:user-id user-id}))}))
