(defproject doghub "0.1.0-SNAPSHOT"
  :description "monitor GitHub staled PRs and issues"
  :url "git@github.com:MalloZup/doghub.git"
  :dependencies [[[org.clojure/clojure "1.10.0"]
                 [irresponsible/tentacles "0.6.4"]
                 [cheshire "5.8.1"]]]
  :repl-options {:init-ns doghub.core})
