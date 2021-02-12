(ns todo-app.interceptors
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]
            [io.pedestal.http :as http]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defonce db-url
  (-> "config.edn"
      io/resource
      slurp
      ig/read-string
      :db/postgres
      :jdbc-url))

(def db-interceptor
  {:name ::db-interceptor
   :enter (fn [context]
            (let [db-conn (jdbc/with-options db-url {})]
              (assoc context :db-conn db-conn)))})

(defn response [status body]
  {:status status :body body})

(def ok       (partial response 200))
(def created  (partial response 201))
(def accepted (partial response 202))
(def deleted  (partial response 204))
(def rejected (partial response 400))

(defn make-user [username pw email]
  {:username username
   :password pw
   :email email})

(def insert-user
  {:name ::insert-user
   :enter
   (fn [context]
     (let [username (-> context :request :json-params :username)
           pw       (-> context :request :json-params :password)
           email    (-> context :request :json-params :email)
           new-user (make-user username pw email)]
       (sql/insert! db-url :users new-user)
       (assoc context
              :response (created new-user))))})

(def delete-user
  {:name ::delete-user
   :enter
   (fn [context]
     (let [list-id  (-> context :request :path-params :list-id)
           user-id  (java.util.UUID/fromString (-> context :request :path-params :user-id))]
       (sql/delete! db-url list-id {:user_id user-id})
       (assoc context
              :response (deleted delete-user))))})

(def update-user
  {:name ::update-user
   :enter
   (fn [context]
     (let [list-id  (-> context :request :path-params :list-id)
           user-id  (java.util.UUID/fromString (-> context :request :path-params :user-id))
           json-params (-> context :request :json-params)]
       (sql/update! db-url list-id json-params {:user_id user-id})
       (assoc context
              :response (ok update-user))))})

(defn find-list-by-name
  [list-id]
  (sql/find-by-keys db-url list-id :all))

(def find-all-users
  {:name ::find-all-users
   :enter
   (fn [context]
     (if-let [list-id (-> context :request :path-params :list-id)]
       (if-let [the-list (find-list-by-name list-id)]
         (assoc context :response (ok the-list))
         context)
       context))})

(defn find-user-by-id
  [list-id user-id]
  (sql/find-by-keys db-url list-id {:user_id user-id}))

(def find-user
  {:name ::find-user
   :enter
   (fn [context]
     (if-let [list-id (-> context :request :path-params :list-id)]
       (if-let [user-id (java.util.UUID/fromString (-> context :request :path-params :user-id))]
         (if-let [item (find-user-by-id list-id user-id)]
           (assoc context :response (ok item))
           context)
         context)
       context))})

(defn find-user-todos
  [user-id]
  (sql/find-by-keys db-url :todo {:user_id user-id}))

(def find-all-todos
  {:name ::find-all-todos
   :enter
   (fn [context]
     (if-let [user-id (java.util.UUID/fromString (-> context :request :path-params :user-id))]
       (if-let [todos (find-user-todos user-id)]
         (assoc context :response (ok todos))
         context)
       context))})
