(ns practicalli.youtube-website
  (:gen-class)
  (:require [ring.adapter.jetty :as adapter]
            [ring.util.response :refer [response]]
            [compojure.core     :refer [defroutes GET POST]]
            [compojure.route    :refer [not-found]]
            [clj-http.client    :as http-client]
            [clojure.data.json  :as json]))


;; Data model - YouTube API results
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Using def expression to cache API calls
;; as they will only change once a week or so.
;; No need to waste my data limits when developing :)

(def youtube-url-channel-practicalli
  (str "https://www.googleapis.com/youtube/v3/playlists?part=snippet,contentDetails&channelId=UCLsiVY-kWVH1EqgEtZiREJw&key=" (System/getenv "YOUTUBE_API_KEY")))


(def practicalli-channel-playlists-full-details
  (get (json/read-str
         (:body
          (http-client/get youtube-url-channel-practicalli)))
       "items"))



(def youtube-url-channel-practicalli-playlist-study-group
  (str "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet,id&playlistId=PLpr9V-R8ZxiDjyU7cQYWOEFBDR1t7t0wv&key=" (System/getenv "YOUTUBE_API_KEY")))

(def practicalli-playlist-study-group
  (get
    (json/read-str
      (:body
       (http-client/get youtube-url-channel-practicalli-playlist-study-group)))
    "items"))



;; Helper functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; A URL-builder for sending through the required URL parameters
;; when calling the YouTube API.
;; Refactor of these more specific functions

(defn playlist-names
  "Extract YouTube id and title for each Playlist found in the channel"
  [all-playlists]
  (into {}
        (for [playlist all-playlists
              :let     [id (get playlist "id")
                        title (get-in playlist ["snippet" "title"])]]
          {id title})))



(defn playlist-items
  "Get the important values for each video in the playlist

  `snippet`:`resourceId`:`videoId` - used for the URL address of the video
  `snippet`:`title` - title of the video
  `snippet`:`thumbnails` : `default` : `url` - full URL of thumbnail image"
  [playlist-details]
  (into {}
        (for [item playlist-details
              :let [id (get-in item ["snippet" "resourceId" "videoId"])
                    title (get-in item ["snippet" "title"])
                    thumbnail (get-in item ["snippet" "thumbnails" "default" "url"])]]
          {id [title thumbnail]})))


;; test the function
#_(playlist-items practicalli-playlist-study-group)


;; Handler functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn playlist
  "Display a playlist as defined by the parameter list"
  [request]
  ;; hard coded to study-group playlist for now
  (response
    (str (playlist-items practicalli-playlist-study-group))))


;; test the handler
#_(playlist {})


;; Routing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defroutes webapp
  (GET "/"               [] (response "home-page"))

  (GET "/study-group"    [] playlist)
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
