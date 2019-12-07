(ns practicalli.youtube-website
  (:gen-class)
  (:require [ring.adapter.jetty :as adapter]
            [ring.util.response :refer [response]]
            [compojure.core     :refer [defroutes GET POST]]
            [compojure.route    :refer [not-found]]
            [clj-http.client    :as http-client]
            [clojure.data.json  :as json]))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;; Routing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defroutes webapp
  (GET "/"               [] (response "home-page"))

  (GET "/playlist/:name" [] (response "playlist"))

  (not-found
    "<h1>Page not found, I am very sorry.</h1>"))

