(ns todo-app.main
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]
            [io.pedestal.http :as http]
            [next.jdbc :as jdbc]
            [todo-app.routes :as routes]))

(def config
  (-> "config.edn"
      io/resource
      slurp
      ig/read-string))

(defmethod ig/init-key :db/postgres
  [_ {:keys [jdbc-url] :as _options}]
  (jdbc/with-options jdbc-url {}))

(defmethod ig/init-key :todo/app
  [_ {:keys [port] :as _options}]
  (let [service-map {::http/join? false
                     ::http/port port
                     ::http/routes routes/app-routes
                     ::http/type :jetty
                     ::http/allowed-origins {:creds true :allowed-origins (constantly true)}}]
    {:server (-> service-map
                 http/create-server
                 http/start)}))

(defmethod ig/halt-key! :todo/app
  [_ {:keys [server] :as _server-info}]
  (http/stop server))

(defn -main
  []
  (ig/init config))
