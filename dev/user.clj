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
  (jdbc/execute! int/db-url ["SELECT * FROM users WHERE user_id = ?::uuid" "bb360dbf-82e5-4b75-b456-02959800da3d"])

  (sql/get-by-id int/db-url :users #uuid "bb360dbf-82e5-4b75-b456-02959800da3d" :user_id {})

  (sql/query int/db-url ["SELECT * FROM users WHERE user_id = ?::uuid" "bb360dbf-82e5-4b75-b456-02959800da3d"])


 )
