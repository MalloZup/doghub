(ns doghub.core
  (:require [doghub.config :as c]
            [tentacles.core :as t]
            [tentacles.issues :as i]
            [clj-time.core :as time]
            [clj-time.format :as f]
            [clojure.tools.logging :as log]
            [clojure.string :as str]
            [cheshire.core :refer :all])
  (:gen-class))


;;  (:updated_at (first data))

;; "2019-04-05T18:23:22Z"

(defn get-issues [full-repo]
 "get all issues given a repo"
 (let [user (first (str/split full-repo #"/"))
       repo (last (str/split full-repo #"/"))]
 (i/issues user repo {:all-pages true
                      :oauth-token (get-in (c/get-config) [:github-config :token] )})))



(def datetime-form (f/formatters :date-time-no-ms))

(defn parse-data [issue-time tolleration-datetime]
  "tolleration-datetime is actual timemestamp - the days user give as tolleration"
  (time/before? (f/parse datetime-form "2019-02-05T18:23:22Z") ;; issue-datetm
               (f/parse datetime-form "2019-04-05T18:23:23Z") ;; tolleration-time

  ;; if it true, then write a comment to this issue
  ))



(defn -main []
 ;; deamon mode todo
 (println "main")
)

;;  (.getTime (java.util.Date.))
