(ns todo-app.main-test
  (:require [clojure.test :refer [deftest is]]
            [todo-app.main :as main]
            [todo-app.routes :as routes]
            [todo-app.interceptors :as inter]
            [io.pedestal.test :refer [response-for]]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            ))

(def url-for (route/url-for-routes
              (route/expand-routes routes/app-routes)))

(def service (::http/service-fn (http/create-servlet service-map)))

(deftest service-test
  (is (= 200
         (:status (response-for service :get (url-for ::inter/find-all-users))))))
