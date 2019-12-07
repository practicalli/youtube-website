(ns practicalli.youtube-website
  (:gen-class)
  (:require [ring.adapter.jetty :as adapter]
            [ring.util.response :refer [response]]
            [compojure.core     :refer [defroutes GET POST]]
            [compojure.route    :refer [not-found]]
            [clj-http.client    :as http-client]
            [clojure.data.json  :as json]))



;; Routing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defroutes webapp
  (GET "/"               [] (response "home-page"))

  (GET "/playlist/:name" [] (response "playlist"))

  (not-found
    "<h1>Page not found, I am very sorry.</h1>"))

;; System
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn jetty-shutdown-timed
  "Shutdown server after specific time,
  allows time for threads to complete.
  Stops taking new requests immediately by
  closing the HTTP listener and freeing the port."
  [server]
  (.setStopTimeout server 1000)
  (.setStopAtShutdown server true))


;; Define a single instance of the embedded Jetty server
(defonce server
  (adapter/run-jetty
    #'webapp
    {:port         8000
     :join?        false
     :configurator jetty-shutdown-timed}))

;; In the REPL
;; (.start server) ;; starts the Jetty embedded server
;; (.stop server)  ;; stops the Jetty embedded server
