(ns br.bsb.liberdade.fpcl.utils
  (:require [buddy.sign.jwt :as jwt]
            [buddy.core.hash :as hash]
            [buddy.core.codecs :as codecs]))

(def salt (hash/sha256 (or (System/getenv "SALT") "TODO REPLACE ME")))

(defn encode-secret [data]
  (jwt/encrypt data salt))

(defn decode-secret [secret]
  (try
    (jwt/decrypt secret salt)
    (catch clojure.lang.ExceptionInfo e nil)))

(defn- cover [secret]
  (-> (hash/sha256 (str secret salt))
      (codecs/bytes->hex)))

(defn hide [secret]
  (loop [i 0
         outlet (cover secret)]
    (if (> i 9081)
      (cover outlet)
      (recur (+ 1 i)
             (cover outlet)))))
