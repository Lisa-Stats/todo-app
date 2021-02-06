(ns user
  (:require [integrant.repl :as ig-repl]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [todo-app.interceptors :as int]
            [todo-app.main :as td-main]))

(ig-repl/set-prep! (fn []
                     td-main/config))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(comment
  (jdbc/execute! int/db-url ["SELECT * FROM users"])

  (sql/find-by-keys int/db-url :users :all)

  )
