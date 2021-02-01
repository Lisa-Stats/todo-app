(ns todo-app.interceptors
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]))

(defonce db-url
  (-> "config.edn"
      io/resource
      slurp
      ig/read-string
      :db/postgres
      :jdbc-url))

(def db-interceptor
  {:name ::db-interceptor
   :enter (fn [context]
            (let [db-conn (jdbc/with-options db-url {})]
              (assoc context :db-conn db-conn)))})
