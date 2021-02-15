(ns todo-app.routes
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.params :as params]
            [io.pedestal.http.route :as route]
            [todo-app.interceptors :as int]))

(def respond-hello
  {:name ::respond-hello
   :enter (fn [context]
            (let [json-params (-> context :request :json-params)]
              (assoc context :response {:body json-params
                                        :status 200})))})

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
                                         int/find-all-users]]
     ["/users/:user-id"         :get    [int/db-interceptor
                                         int/find-user]]
     ["/users/:user-id"         :put    [int/db-interceptor
                                         (body-params/body-params)
                                         http/json-body
                                         int/update-user]]
     ["/users/:user-id"         :delete [int/db-interceptor
                                         int/delete-user]]
     ["/todo/:user-id"          :get    [int/db-interceptor
                                         int/find-all-todos]]}))
