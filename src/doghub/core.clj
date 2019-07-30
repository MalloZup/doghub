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

(def github-options {:all-pages true
                      :oauth-token (get-in (c/get-config) [:github-config :token] )})

(defn comment-issue [full-repo issue prefix-msg options issue-days]
  (let [user (first (str/split full-repo #"/"))
       repo (last (str/split full-repo #"/"))
       text (str prefix-msg " " "issue older then " issue-days "days. Please update the issue or close it")]
  (log/info (str "commenting old issue from repo:" repo " " (:number issue) " " (:url issue)))
  (i/create-comment user repo (:number issue) text options)))

(defn get-issues [full-repo]
 "get all issues given a repo"
 (let [user (first (str/split full-repo #"/"))
       repo (last (str/split full-repo #"/"))]
 (i/issues user repo github-options)))



(def datetime-form (f/formatters :date-time-no-ms))

(defn compare-issue-with-tdays [issue-time tolleration-days]
  "compare issue time with tollerated one, return true if issue-time is older 
  in this case it means write a comment on the issue/pr"
  (time/before? (f/parse datetime-form issue-time) ;; issue-datetm
               (time/minus (time/now) (time/days tolleration-days)) ;; tolleration-time
  ;; if it true, then write a comment to this issue
  ))

(defn comment-all-old-issues []
 ;; itereate over all repositories from user
 (let [{:keys [repositories issue-days prefix-msg]} (c/get-config)]  
   (doseq [repo repositories]
    (log/info (str "getting issues for repo: " repo))
    ;; get all issues from a single repo
    (doseq [issue (get-issues repo)] 
       (log/info (str "comparing issue with tolleration time(days) :" issue-days))
       ;; check if issue is older then input days
       (when (compare-issue-with-tdays (:updated_at issue) issue-days)
             (future (comment-issue repo issue prefix-msg github-options issue-days)))))))

(defn comment-all-old-ors []
"comment all old prs"
 (println "to implement")
)

(defn -main []
 (while true
   (comment-all-old-issues)
   ;; todo: comment-all-old-prs (implenment that)
   (log/info "sleeping for 5 minutes")
   (Thread/sleep (* 5 60 1000))
 )
)

;;  (.getTime (java.util.Date.))
