(ns user
  (:require [integrant.repl :as ig-repl]
            [todo-app.main :as td-main]))

(ig-repl/set-prep! (fn []
                     td-main/config))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)
