(ns doghub.core
  (:require [doghub.config :as c]
            [tentacles.core :as t]
            [tentacles.issues :as i]
            [tentacles.pulls :as p]
            [clj-time.core :as time]
            [clj-time.format :as f]
            [clojure.tools.logging :as log]
            [clojure.string :as str]
            [cheshire.core :refer :all])
  (:gen-class))

(def github-options {:all-pages true :oauth-token (get-in (c/get-config) [:github-config :token] )})

(defn comment-issue [full-repo issue prefix-msg options issue-days]
  (let [user (first (str/split full-repo #"/"))
       repo (last (str/split full-repo #"/"))
       text (str prefix-msg " " "issue older then " issue-days " days. Please update the issue or close it")]
      (try 
         (log/info (str "commenting old issue from repo:" repo " " (:number issue) "" (:url issue)))
         (i/create-comment user repo (:number issue) text options)
      (catch Exception e (log/error (str "exception by commenting issue: " (.getMessage e)))))))

(defn comment-pr[full-repo pr prefix-msg options t-days]
  (let [user (first (str/split full-repo #"/"))
       repo (last (str/split full-repo #"/"))
       text (str prefix-msg " " "pr older then " t-days " days. Please update the PR or close it")]
      (try 
         (log/info (str "commenting old pull-request from repo:" repo " " (:number pr) "" (:url pr)))
         (i/create-comment user repo (:number pr) text options)
      (catch Exception e (log/error (str "exception by commenting issue: " (.getMessage e)))))))


(defn get-issues [full-repo] "get all issues given a repo"
 (let [user (first (str/split full-repo #"/"))
       repo (last (str/split full-repo #"/"))]
   (i/issues user repo github-options)))

(defn get-pulls [full-repo] "get all prs given a repo"
 (let [user (first (str/split full-repo #"/"))
       repo (last (str/split full-repo #"/"))]
   (p/pulls user repo github-options)))


(def datetime-form (f/formatters :date-time-no-ms))

(defn compare-issue-pr-with-tdays [issue-time tolleration-days]
  "compare issue time with tollerated one, return true if issue-time is older 
  in this case it means write a comment on the issue/pr"
  (try
    (time/before? (f/parse datetime-form issue-time) ;; issue-datetime
                  (time/minus (time/now) (time/days tolleration-days)))
  (catch Exception e (log/error (str "exception by commenting datetime of issue: " (.getMessage e))))))  

(defn comment-all-old-issues []
 "given a list repos, comment all older issues"
 (let [{:keys [repositories issue-days prefix-msg]} (c/get-config)]  
   (doseq [repo repositories]
     (log/info (str "getting issues for repo: " repo))
      ;; get all issues from a single repo
      (doseq [issue (get-issues repo)] 
        (log/info (str "comparing issue with tolleration time(days) :" issue-days))
         ;; check if issue is older then input days
         (when (compare-issue-pr-with-tdays (:updated_at issue) issue-days)
           (future (comment-issue repo issue prefix-msg github-options issue-days)))))))

(defn comment-all-old-prs []
 (let [{:keys [repositories prs-days prefix-msg]} (c/get-config)]  
   (doseq [repo repositories]
     (log/info (str "getting pulls for repo: " repo))
      ;; get all prs from a single repo
      (doseq [pull (get-pulls repo)] 
        (log/info (str "comparing prs with tolleration time(days) :" prs-days))
         ;; check if prs is older then input days
         (when (compare-issue-pr-with-tdays (:updated_at pull) prs-days)
           (deref(future (comment-pr repo pull prefix-msg github-options prs-days))))))))

(defn -main []
 (while true
   (comment-all-old-issues)
   (comment-all-old-prs)
   (log/info "sleeping for 5 minutes")
   (Thread/sleep (* 5 60 1000))))
