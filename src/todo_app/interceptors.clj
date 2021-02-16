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
     (let [user-id (java.util.UUID/fromString (-> context :request :path-params :user-id))
           {:next.jdbc/keys [update-count]} (sql/delete! db-url :users {:user_id user-id})]
       (if (= 1 update-count)
         (assoc context :response (deleted delete-user))
         (assoc context :response (rejected delete-user)))))})

(def update-user
  {:name ::update-user
   :enter
   (fn [context]
     (let [user-id  (java.util.UUID/fromString (-> context :request :path-params :user-id))
           json-params (-> context :request :json-params)
           {:next.jdbc/keys [update-count]} (sql/update! db-url :users json-params {:user_id user-id})]
       (if (= 1 update-count)
         (assoc context :response (ok json-params))
         (assoc context :response (rejected json-params)))))})

(def find-all-users
  {:name ::find-all-users
   :enter
   (fn [context]
     (let [user-return (sql/find-by-keys db-url :users :all)]
       (sql/find-by-keys db-url :users :all)
       (assoc context :response (ok user-return))))})

(defn find-user-by-id
  [user-id]
  (sql/find-by-keys db-url :users {:user_id user-id}))

(def find-user
  {:name ::find-user
   :enter
   (fn [context]
     (let [user-id (java.util.UUID/fromString (-> context :request :path-params :user-id))
           item (find-user-by-id user-id)]
       (assoc context :response (ok item))))})

(defn insert-todo!
  [todo-name todo-body user-id]
  {:todo_name todo-name
   :todo_body todo-body
   :user_id user-id})

(def insert-todo
  {:name ::insert-todo
   :enter
   (fn [context]
     (let [todo-name (-> context :request :json-params :todo-name)
           todo-body (-> context :request :json-params :todo-body)
           user-id (java.util.UUID/fromString (-> context :request :path-params :user-id))
           new-todo (insert-todo! todo-name todo-body user-id)]
       (sql/insert! db-url :todo new-todo)
       (assoc context :response (created new-todo))))})

(def delete-todo
  {:name ::delete-todo
   :enter
   (fn [context]
     (let [user-id (java.util.UUID/fromString (-> context :request :path-params :user-id))
           todo-id (java.util.UUID/fromString (-> context :request :path-params :todo-id))
           {:next.jdbc/keys [update-count]} (sql/delete! db-url :todo {:user_id user-id
                                                                       :todo_id todo-id})]
       (if (= 1 update-count)
         (assoc context :response (deleted delete-user))
         (assoc context :response (rejected delete-user)))))})

(def update-todo
  {:name ::update-todo
   :enter
   (fn [context]
     (let [user-id (java.util.UUID/fromString (-> context :request :path-params :user-id))
           todo-id (java.util.UUID/fromString (-> context :request :path-params :todo-id))
           json-params (-> context :request :json-params)
           {:next.jdbc/keys [update-count]} (sql/update! db-url :todo json-params {:user_id user-id
       :todo_id todo-id})]
       (if (= 1 update-count)
         (assoc context :response (ok json-params))
         (assoc context :response (rejected json-params)))))})

(defn find-user-todos
  [user-id]
  (sql/find-by-keys db-url :todo {:user_id user-id}))

(def find-all-todos
  {:name ::find-all-todos
   :enter
   (fn [context]
     (let [user-id (java.util.UUID/fromString (-> context :request :path-params :user-id))
           todos (find-user-todos user-id)]
       (assoc context :response (ok todos))))})

(defn find-todo
  [user-id todo-id]
  {:user_id user-id
   :todo_id todo-id})

(def find-todo-by-id
  {:name ::find-todo-by-id
   :enter
   (fn [context]
     (let [user-id (java.util.UUID/fromString (-> context :request :path-params :user-id))
           todo-id (java.util.UUID/fromString (-> context :request :path-params :todo-id))
           find-todo-data (find-todo user-id todo-id)
           get-todo (sql/find-by-keys db-url :todo find-todo-data)]
       (assoc context
              :response (ok get-todo))))})
