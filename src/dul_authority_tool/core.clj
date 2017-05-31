(ns dul-authority-tool.core
  (:require [config.core :refer [env]]
            [clojure.data.json :as json]
            [event-data-common.storage.s3 :as s3]
            [event-data-common.storage.memory :as memory]
            [event-data-common.storage.store :as store]
            [clojure.set]))

(def required-config-keys
  #{:s3-key
    :s3-secret
    :s3-bucket-name
    :s3-region-name
    :public-base
    :cloudfront-distribution-id})

(def connection
  (delay
    (if (:test env)
      (memory/build)
      (s3/build (:s3-key env) (:s3-secret env) (:s3-region-name env) (:s3-bucket-name env)))))

(defn check-config
  "Check that config keys exist.
   Return nil or throw exception."
   []
   (when-let [missing-keys (not-empty (clojure.set/difference required-config-keys (-> env keys set)))]
    (doseq [key-name required-config-keys]
      (when-not (env key-name)
        (println "Missing config key:" (name key-name))))
    (prn "CC" missing-keys)
    (throw (new Exception "Config keys missing"))))

(def prefix
  "All producer information starts with this prefix."
  "p/")

; Storage contains two kinds of files:
;
; p/«PRODUCER_ID»/info.json
; Information about the producer. This indicates the existence of a Producer.
; 
; p/«PRODUCER_ID»/jku/«CERTIFICATE».json
; Any number of JWK public keys for the Producer

(defn all-producer-infos
  "List all Producer IDs and their info
   Return a map of producer id to metadata."
  []
  (let [producer-keys (store/keys-matching-prefix @connection prefix)
        producer-info-keys (filter #(.endsWith % "/info.json") producer-keys)
        infos (into {} (map #(vector
                               ; Get producer ID from key.
                               (first
                                 (clojure.string/split
                                   (.substring % (.length prefix) (dec (.length %)))
                                   #"/"))
                               (->> % (store/get-string @connection) json/read-str)) producer-info-keys))]
        infos))

(defn producer-id-exists?
  [producer-id]
  (boolean ((all-producer-infos) producer-id)))

(defn add-producer-info
  "Upload producer info data, entering producer into registry."
  [producer-id producer-info]
  (let [serialized (json/write-str producer-info)
        k (str prefix producer-id "/info.json")]
    (store/set-string @connection k serialized)))

(defn main-list
  "Print list of Producer IDs."
  []
  (let [producer-infos (all-producer-infos)]
    (println (count producer-infos) " Producers IDs:")
    (doseq [[id info] producer-infos]
      (println id)
      (doseq [[k v] info]
        (println " - " k ":" v)))))

(defn main-add
  "Interactively add Producer information ID."
  []
  (println "Enter Producer information or ^C to exit.")
  (let [id (do (println "ID:") (read-line))
        website (do (println "Website:") (read-line))
        nom (do (println "Name:") (read-line))
        email (do (println "Contact email:") (read-line))
        producer-info {:id id :website website :name nom :email email}]
    
    (when (clojure.string/blank? id)
      (throw (new Exception "id is blank")))

    (when (clojure.string/blank? website)
      (throw (new Exception "website is blank")))

    (when (clojure.string/blank? nom)
      (throw (new Exception "name is blank")))

    (when (producer-id-exists? id)
      (throw (new Exception "Producer ID already exists.")))
    (println "Saving...")
    (add-producer-info id producer-info)
    (println "Done")))

(defn -main
  [& args]
  (try
    (println "DUL Authority Tool")
    (check-config)

    (condp = (first args)
      "list" (main-list)
      "add" (main-add)

      (println "Error: didn't recognise command"))

    (catch Exception ex
      (do
        (println "Terminating with error:" (.getMessage ex))
        (System/exit 1)))))
