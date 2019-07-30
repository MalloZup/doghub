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

(defn comment-issue [issue]
  (println issue))

(defn iterate-over-issues []
 ;; itereate over all repositories from user
 (let [{:keys [repositories issue-days]} (c/get-config)]  
   (doseq [repo repositories]
    (log/info (str "getting issues for repo: " repo))
    ;; get all issues from a single repo
    (doseq [issue (get-issues repo)] 
       (log/info (str "comparing issue with tolleration time(days) :" issue-days))
       ;; check if issue is older then input days
       (when (compare-issue-with-tdays (:updated_at issue) issue-days) (comment-issue issue))))))

(defn get-issues [full-repo]
 "get all issues given a repo"
 (let [user (first (str/split full-repo #"/"))
       repo (last (str/split full-repo #"/"))]
 (i/issues user repo {:all-pages true
                      :oauth-token (get-in (c/get-config) [:github-config :token] )})))



(def datetime-form (f/formatters :date-time-no-ms))

(defn compare-issue-with-tdays [issue-time tolleration-days]
  "compare issue time with tollerated one, return true if issue-time is older 
  in this case it means write a comment on the issue/pr"
  (time/before? (f/parse datetime-form issue-time) ;; issue-datetm
               (time/minus (time/now) (time/days tolleration-days)) ;; tolleration-time
  ;; if it true, then write a comment to this issue
  ))



(defn -main []
 ;; deamon mode todo
 (println "main")
)

;;  (.getTime (java.util.Date.))
