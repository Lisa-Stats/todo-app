(ns todo-app.main
  (:require [io.pedestal.http.route :as route]))

(def respond-hello
  {:name ::respond-hello
   :enter (fn [context]
            (assoc context :response {:body "Hello, world!"
                                      :status 200}))})

(def routes
  (route/expand-routes
   #{["/" :get respond-hello]}))
