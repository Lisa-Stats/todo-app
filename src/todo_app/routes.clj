(ns todo-app.routes
  (:require [io.pedestal.http.route :as route]
            [todo-app.interceptors :as int]))


(def respond-hello
  {:name ::respond-hello
   :enter (fn [context]
            (assoc context :response {:body "Hello, world!"
                                      :status 200}))})

(def app-routes
  (route/expand-routes
   #{["/" :get [int/db-interceptor respond-hello]]}))
