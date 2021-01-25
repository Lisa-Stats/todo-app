(ns user
  (:require [integrant.repl :as ig-repl]
            [todo-app.server :as td-server]))

(ig-repl/set-prep! (fn []
                     td-server/config))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)
