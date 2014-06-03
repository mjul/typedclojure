(ns clojure.core.typed.check-form
  (:require [clojure.core.typed.profiling :as p]
            [clojure.core.typed.check :as chk]
            [clojure.core.typed.utils :as u]
            [clojure.core.typed.reset-caches :as reset-caches]
            [clojure.core.typed.file-mapping :as file-map]
            [clojure.core.typed.analyze-clj :as ana-clj]
            [clojure.core.typed.collect-phase :as collect]
            [clojure.core.typed.type-rep :as r]
            [clojure.core.typed.util-vars :as vs]
            [clojure.core.typed.current-impl :as impl]
            [clojure.core.typed.errors :as err]
            [clojure.core.typed.parse-unparse :as prs])
  (:import (clojure.lang ExceptionInfo)))

(defn check-form-info
  [form & {:keys [expected-ret expected type-provided? profile file-mapping]}]
  (assert (not (and expected-ret type-provided?)))
  (p/profile-if profile
    (reset-caches/reset-caches)
    (if vs/*checking*
      (throw (Exception. "Found inner call to check-ns or cf"))
      (impl/with-clojure-impl
        (binding [vs/*checking* true
                  vs/*already-collected* (atom #{})
                  vs/*already-checked* (atom #{})
                  vs/*delayed-errors* (err/-init-delayed-errors)
                  vs/*analyze-ns-cache* (atom {})]
          (let [terminal-error? (atom nil)
                expected (or
                           expected-ret
                           (when type-provided?
                             (r/ret (prs/parse-type expected))))
                ast (ana-clj/ast-for-form form)
                c-ast (try
                        (do (collect/collect-ast ast)
                            (reset-caches/reset-caches)
                            (chk/check-expr ast expected))
                        (catch ExceptionInfo e
                          (when (err/tc-error? (ex-data e))
                            (reset! terminal-error? e))
                          nil))
                res (u/expr-type c-ast)]
            (merge
              {:delayed-errors (concat @vs/*delayed-errors*
                                       (when-let [e @terminal-error?]
                                         [e]))
               :ret (or res (r/ret r/-error))}
              (when file-mapping
                {:file-mapping (file-map/ast->file-mapping c-ast)}))))))))

(defn check-form*
  ([form expected type-provided?]
   (let [{:keys [delayed-errors ret]} (check-form-info form 
                                                       :expected expected 
                                                       :type-provided? type-provided?)]
     (if-let [errors (seq delayed-errors)]
       (err/print-errors! errors)
       (impl/with-clojure-impl
         (binding [vs/*checking* true]
           (prs/unparse-TCResult-in-ns ret *ns*)))))))
