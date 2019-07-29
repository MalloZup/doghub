(ns doghub.core
  (:require [saint-build.config :as c]
            [tentacles.core :as t]
            [tentacles.issues :as i]
            [clojure.tools.logging :as log]
            [cheshire.core :refer :all])
  (:gen-class))

(defn get-issues []
 (i/issues "user" "repo")
)


(defn -main []
 (println "main")
)
