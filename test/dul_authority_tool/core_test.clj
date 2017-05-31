(ns dul-authority-tool.core-test
  (:require [clojure.test :refer :all]
            [config.core :refer [env]]
            [dul-authority-tool.core :as core]
            [event-data-common.storage.store :as store]))

(defn clear
  []
  (doseq [k (store/keys-matching-prefix @core/connection "")]
    (store/delete @core/connection k)))

(deftest producer-info
  (testing "Producer info can be updated and retrieved."
    (clear)
    (is (empty? (core/all-producer-infos)) "Producer info is empty to start with.")
    (is (false? (core/producer-id-exists? "PRODUCER-123")) "Producer doesn't exist to start.")
    (is (false? (core/producer-id-exists? "PRODUCER-987")) "Producer doesn't exist to start.")

    (core/add-producer-info "PRODUCER-123" {:name "Producer 123" :email "info@example.com"})
    (core/add-producer-info "PRODUCER-987" {:name "Producer 987" :email "info@example.net"})

    (is (true? (core/producer-id-exists? "PRODUCER-123")) "Producer should now exist.")
    (is (true? (core/producer-id-exists? "PRODUCER-987")) "Producer should now exist.")

    (let [producer-infos (core/all-producer-infos)]
      (is (not-empty producer-infos) "Producer info should now have data.")
      (is (= (count producer-infos) 2) "Producer info should now have only those added.")
      (is (= producer-infos
            {"PRODUCER-123" {"name" "Producer 123", "email" "info@example.com"}
             "PRODUCER-987" {"name" "Producer 987", "email" "info@example.net"}})
          "Producer infos should have complete info for both Producer IDs."))
    (clear)))

(deftest verify-cert-bad
  "Verify Cert should reject bad certificates"
  (is (thrown? Exception
    (core/verify-cert "resources/test/doesnt-exist.json")
    "Exception should be thrown if there's no file at the certificate path."))

  (is (thrown? Exception
    (core/verify-cert "resources/test/corrupted-good-256.json")
    "Exception should be thrown if file isn't valid JSON."))

  (is (thrown? Exception
    (core/verify-cert "resources/test/es-256.json")
    "Exception should be thrown if wrong algorithm type (Elliptic curve) is used."))

  (is (thrown? Exception
    (core/verify-cert "resources/test/hs-256.json")
    "Exception should be thrown if wrong algorithm type (HMAC) is used."))

  (is (thrown? Exception
    (core/verify-cert "resources/test/keypair.json")
    "Exception should be thrown if wrong cerficiate type (public/private key pair) is used."))

  (is (thrown? Exception
    (core/verify-cert "resources/test/public-384.json")
    "Exception should be thrown if wrong algorithm variant is used.")))

(deftest verify-cert-good
  "Verify Cert should accept good certificates"
  (is (true? (core/verify-cert "resources/test/good-256.json"))
    "Should return true when certificate is what we expect."))

(deftest upload-cert-problems
  (testing "Uploading a certificate with a non-existent Producer ID should throw an error.")
  (clear)

  (is (thrown? Exception
    (core/upload-cert "PRODUCER-123" "resources/test/good-256.json"))
    "Should throw exception when producer ID doesn't exist.")

  (core/add-producer-info "PRODUCER-123" {:name "Producer 123" :email "info@example.com"})
  (is (core/upload-cert "PRODUCER-123" "resources/test/good-256.json")
    "Should succeed with good certificate when the Producer ID exists.")

  (is (thrown? Exception
        (core/upload-cert "PRODUCER-123" "resources/test/es-256.json"))
    "Should throw exception when certificate is invalid and Producer ID exists."))

(deftest upload-cert-ok
  (testing "Uploading certificate results in a URL on success"
    (clear)
    (core/add-producer-info "PRODUCER-123" {:name "Producer 123" :email "info@example.com"})
    (let [result (core/upload-cert "PRODUCER-123" "resources/test/good-256.json")]
      (is result "On success should return something")
      (is (new java.net.URL result) "Returned value should be URL")
      (is (=
            (store/get-string @core/connection
                         (.substring result
                                     (inc (.length (:public-base env)))
                                     (.length result)))
            (slurp "resources/test/good-256.json"))
          "Certificate should exist in storage and be the same as input file."))))

