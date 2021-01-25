(ns todo-app.server
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]
            [io.pedestal.http :as http]
            [todo-app.main :as main]))

(def config
  (-> "config.edn"
      io/resource
      slurp
      ig/read-string))

(defmethod ig/init-key :todo/app
  [_ {:keys [port] :as _options}]
  (let [service-map {::http/join? false
                     ::http/port port
                     ::http/routes main/routes
                     ::http/type :jetty}]
    {:server (-> service-map
                 http/create-server
                 http/start)}))

(defmethod ig/halt-key! :todo/app
  [_ {:keys [server] :as _server-info}]
  (http/stop server))

(defn -main
  []
  (ig/init config))
