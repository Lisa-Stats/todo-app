(ns todo-app.interceptors
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]
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

(defn ok-response
  [context body]
  (assoc context :response {:status 200
                            :body body}))

(defn created-response
  [context body]
  (assoc context :response {:status 201
                            :body body}))

(defn deleted-response
  [context body]
  (assoc context :response {:status 204
                            :body body}))

(defn rejected-response
  [context body]
  (assoc context :response {:status 400
                            :body body}))

(defn get-id-by-type
  [context type]
  (java.util.UUID/fromString (-> context :request :path-params type)))

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
       (created-response context new-user)))})

(def delete-user
  {:name ::delete-user
   :enter
   (fn [context]
     (let [user-id (get-id-by-type context :user-id)
           {:next.jdbc/keys [update-count]} (sql/delete! db-url :users {:user_id user-id})]
       (if (= 1 update-count)
         (deleted-response context delete-user)
         (rejected-response context delete-user))))})

(def update-user
  {:name ::update-user
   :enter
   (fn [context]
     (let [user-id (get-id-by-type context :user-id)
           json-params (-> context :request :json-params)
           {:next.jdbc/keys [update-count]} (sql/update! db-url :users json-params {:user_id user-id})]
       (if (= 1 update-count)
         (ok-response context json-params)
         (rejected-response context json-params))))})

(def find-all-users
  {:name ::find-all-users
   :enter
   (fn [context]
     (let [user-return (sql/find-by-keys db-url :users :all)]
       (ok-response context user-return)))})

(defn find-user-by-id
  [user-id]
  (sql/find-by-keys db-url :users {:user_id user-id}))

(def find-user
  {:name ::find-user
   :enter
   (fn [context]
     (let [user-id (get-id-by-type context :user-id)
           item (find-user-by-id user-id)]
       (ok-response context item)))})

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
           user-id   (get-id-by-type context :user-id)
           new-todo  (insert-todo! todo-name todo-body user-id)]
       (sql/insert! db-url :todo new-todo)
       (created-response context new-todo)))})

(def delete-todo
  {:name ::delete-todo
   :enter
   (fn [context]
     (let [user-id (get-id-by-type context :user-id)
           todo-id (get-id-by-type context :todo-id)
           {:next.jdbc/keys [update-count]} (sql/delete! db-url :todo {:user_id user-id
                                                                       :todo_id todo-id})]
       (if (= 1 update-count)
         (deleted-response context delete-user)
         (rejected-response context delete-user))))})

(def update-todo
  {:name ::update-todo
   :enter
   (fn [context]
     (let [user-id (get-id-by-type context :user-id)
           todo-id (get-id-by-type context :todo-id)
           json-params (-> context :request :json-params)
           {:next.jdbc/keys [update-count]} (sql/update! db-url :todo json-params {:user_id user-id
       :todo_id todo-id})]
       (if (= 1 update-count)
         (ok-response context json-params)
         (rejected-response context json-params))))})

(defn find-user-todos
  [user-id]
  (sql/find-by-keys db-url :todo {:user_id user-id}))

(def find-all-todos
  {:name ::find-all-todos
   :enter
   (fn [context]
     (let [user-id (get-id-by-type context :user-id)
           todos (find-user-todos user-id)]
       (ok-response context todos)))})

(defn find-todo
  [user-id todo-id]
  {:user_id user-id
   :todo_id todo-id})

(def find-todo-by-id
  {:name ::find-todo-by-id
   :enter
   (fn [context]
     (let [user-id (get-id-by-type context :user-id)
           todo-id (get-id-by-type context :todo-id)
           find-todo-data (find-todo user-id todo-id)
           get-todo (sql/find-by-keys db-url :todo find-todo-data)]
       (ok-response context get-todo)))})
