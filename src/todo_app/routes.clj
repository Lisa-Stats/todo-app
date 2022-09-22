(ns todo-app.routes
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route :as route]
            [todo-app.interceptors :as int]))

(def respond-hello
  {:name ::respond-hello
   :enter (fn [context]
            (assoc context :response {:body "Hello, world!"
                                      :status 200}))})

(def app-routes
  (route/expand-routes
   #{["/"                       :get    [int/db-interceptor
                                         (body-params/body-params)
                                         http/json-body
                                         respond-hello]]
     ["/users"                  :post   [int/db-interceptor
                                         (body-params/body-params)
                                         http/json-body
                                         int/insert-user]]
     ["/users"                  :get    [int/db-interceptor
                                         http/json-body
                                         int/find-all-users]]
     ["/users/:user-id"         :get    [int/db-interceptor
                                         http/json-body
                                         int/find-user]]
     ["/users/:user-id"         :put    [int/db-interceptor
                                         (body-params/body-params)
                                         http/json-body
                                         int/update-user]]
     ["/users/:user-id"         :delete [int/db-interceptor
                                         int/delete-user]]
     ["/todo/:user-id"          :post   [int/db-interceptor
                                         (body-params/body-params)
                                         http/json-body
                                         int/insert-todo]]
     ["/todo/:user-id"          :get    [int/db-interceptor
                                         http/json-body
                                         int/find-all-todos]]
     ["/todo/:user-id/:todo-id" :get    [int/db-interceptor
                                         http/json-body
                                         int/find-todo-by-id]]
     ["/todo/:user-id/:todo-id" :put    [int/db-interceptor
                                         (body-params/body-params)
                                         http/json-body
                                         int/update-todo]]
     ["/todo/:user-id/:todo-id" :delete [int/db-interceptor
                                         int/delete-todo]]}))
