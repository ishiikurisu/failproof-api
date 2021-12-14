(ns br.bsb.liberdade.fpcl.utils
  (:require [buddy.sign.jwt :as jwt]
            [buddy.core.hash :as hash]
            [buddy.hashers :as hashers]))

(def salt (hash/sha256 (or (System/getenv "SALT") "TODO REPLACE ME")))

(defn encode-secret [data]
  (jwt/encrypt data salt))

(defn decode-secret [secret]
  (jwt/decrypt secret salt))

(defn hide [secret]
  (hashers/derive secret))
