(ns doghub.core
  (:require [doghub.config :as c]
            [tentacles.core :as t]
            [tentacles.issues :as i]
            [clojure.tools.logging :as log]
            [cheshire.core :refer :all])
  (:gen-class))




(defn get-issues []

 (let [repo (get-in (c/get-config) [:repos])]
 (i/issues :oauth-token (get-in (c/get-config) [:github-config :token] ) repo)))


(defn -main []
 (println "main")
)
