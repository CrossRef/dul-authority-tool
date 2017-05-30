(ns dul-authority-tool.core
  (:require [config.core :refer [env]]
            [clojure.set]))

(def required-config-keys
  #{:s3-key
    :s3-secret
    :s3-bucket-name
    :s3-region-name
    :public-base
    :cloudfront-distribution-id})

(defn check-config
  "Check that config keys exist.
   Return nil or throw exception."
   []
   (when-let [missing-keys (clojure.set/difference (-> env keys set) required-config-keys)]
    (doseq [key-name required-config-keys]
      (when-not (env key-name)
        (println "Missing config key:" (name key-name))))
    (throw (new Exception "Config keys missing"))))

(defn -main
  [& args]
  (try
    (println "DUL Authority Tool")
    (check-config)

    (catch Exception ex
      (do
        (println "Terminating with error:" (.getMessage ex))
        (System/exit 1)))))
