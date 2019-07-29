(ns doghub.config)

(def conf-file 
  (or (System/getenv "CONFIG_FILE_PATH") "doghub.edn" ))

(defn read-config []
  (clojure.edn/read-string (slurp conf-file)))

(defn get-config []
  (read-config))
