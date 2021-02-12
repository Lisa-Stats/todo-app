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
    (sql/delete! int/db-url :users {:user_id #uuid "51445ecd-579d-4dbe-b7e3-d7ce9e61fd32"})

  (sql/find-by-keys int/db-url :todo {:user_id #uuid "06512da8-43e0-489f-8ed9-9204ddc0a6b5"
                                      :todo_id #uuid  "6553a9e8-98eb-4986-92ea-69b4d5bc0899"})

  (sql/update! int/db-url :users {:email "jenna@mail.com"} {:user_id #uuid "2923f1f0-c25b-46e4-a3a4-5771e41863ab"})

 )
