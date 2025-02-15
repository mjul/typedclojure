(ns typed.dev.helpers
  (:require [clojure.string :as str]))

(def ^String repo-root "..")

(defn typedclojure-stable-version []
  (-> (str repo-root "/stable-version")
      slurp
      str/trim))

(defn typedclojure-current-version []
  (-> (str repo-root "/current-version")
      slurp
      str/trim))

(def selmer-input-map
  {:clojure-mvn-version "1.11.1"
   :clojars-url "https://clojars.org/repo"
   :sonatype-oss-public-url "https://oss.sonatype.org/content/groups/public/"
   :clojurescript-mvn-version "1.11.54"
   :typedclojure-alpha-spec-mvn-version "0.2.177-typedclojure-2"
   :typedclojure-git-https-url "https://github.com/typedclojure/typedclojure"
   :typedclojure-alpha-spec-git-sha "9da58ec60f5a4a3bfc61fa19f54bf1d160b49dfc"
   :typedclojure-group-id "org.typedclojure"
   :selmer-mvn-version "1.12.50"
   :core-memoize-mvn-version "1.0.257"
   :nrepl-mvn-version "0.8.3"
   :reply-mvn-version "0.5.1"
   :parsley-mvn-version "0.9.3"
   :piggieback-mvn-version "0.5.3"
   :tools-namespace-mvn-version "1.3.0"
   :cider-nrepl-mvn-version "0.28.2"
   :asm-mvn-version "9.2"
   :tools-analyzer-mvn-version "1.1.0"
   :tools-analyzer-jvm-mvn-version "1.2.2"
   :potemkin-mvn-version "0.4.5"
   :math-combinatorics-mvn-version "0.1.6"
   :tools-reader-mvn-version "1.3.6"
   :core-cache-mvn-version "1.0.225"
   :core-async-mvn-version "1.5.648"
   ;; FIXME clj.checker fails on higher
   :tools-nrepl-mvn-version "0.2.6"
   :test-check-mvn-version "1.1.1"
   :test-chuck-mvn-version "0.2.12"
   :core-logic-mvn-version "1.0.1"
   :cognitect-test-runner-coordinates "io.github.cognitect-labs/test-runner"
   :cognitect-test-runner-git-url "https://github.com/cognitect-labs/test-runner"
   :cognitect-test-runner-sha "dd6da11611eeb87f08780a30ac8ea6012d4c05ce"
   :codox-mvn-version "0.10.7"
   :kaocha-git-url "https://github.com/lambdaisland/kaocha.git"
   ;; 1.60.972
   :kaocha-sha "23d7bf426c8bc2027d0da2fc2a5420f5c6474740"
   :typedclojure-homepage "https://typedclojure.org"
   :malli-mvn-version "0.8.9"
   :malli-git-url "https://github.com/metosin/malli.git"
   :malli-snapshot-git-sha "400dc0c79805028a6d85413086d4d6d627231940"
   :beholder-mvn-version "1.0.0"
   :process-mvn-version "0.1.7"
   })
