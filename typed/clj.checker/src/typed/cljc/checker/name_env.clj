;;   Copyright (c) Ambrose Bonnaire-Sergeant, Rich Hickey & contributors.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns ^:no-doc typed.cljc.checker.name-env
  (:require [typed.clojure :as t]
            [clojure.core.typed.contract-utils :as con]
            [clojure.core.typed.current-impl :as impl]
            [clojure.core.typed.errors :as err]
            [typed.clj.checker.rclass-env :as rcls]
            [typed.cljc.checker.datatype-env :as dtenv]
            [typed.cljc.checker.declared-kind-env :as kinds]
            [typed.cljc.checker.protocol-env :as prenv]
            [typed.cljc.checker.type-ctors :as c]
            [typed.cljc.checker.type-rep :as r]
            [typed.cljc.runtime.env :as env]))

(t/defalias NameEnv
  "Environment mapping names to types. Keyword values are special."
  (t/Map t/Sym (t/U t/Kw r/Type)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Type Name Env

(t/ann temp-binding t/Kw)
(def temp-binding ::temp-binding)

(t/tc-ignore
(doseq [k [impl/declared-name-type impl/protocol-name-type impl/datatype-name-type]]
  (derive k temp-binding))
  )

(t/ann ^:no-check name-env? [t/Any -> t/Any])
(def name-env? (con/hash-c? (every-pred (some-fn namespace 
                                                 #(some #{\.} (str %)))
                                        symbol?)
                            (some-fn r/Type? #(isa? % temp-binding))))

(t/ann ^:no-check name-env [-> NameEnv])
(defn name-env []
  (get (env/deref-checker) impl/current-name-env-kw {}))

(t/ann ^:no-check reset-name-env! [NameEnv -> nil])
(defn reset-name-env! [nme-env]
  (env/swap-checker! assoc impl/current-name-env-kw nme-env)
  nil)

(t/ann ^:no-check find-type-name-entry [t/Sym -> (t/Nilable (t/MapEntry t/Sym (t/U t/Kw (t/Delay r/Type))))])
(defn find-type-name-entry [sym]
  (or (find (name-env) sym)
      (when-some [sym-nsym ((requiring-resolve (impl/impl-case
                                                 :clojure 'typed.clj.checker.parse-unparse/ns-rewrites-clj
                                                 :cljs 'typed.clj.checker.parse-unparse/ns-rewrites-cljs))
                            (some-> sym namespace symbol))]
        (find (name-env)
              (symbol (name sym-nsym) (name sym))))))

(t/ann ^:no-check get-type-name [t/Sym -> (t/U nil t/Kw r/Type)])
(defn get-type-name 
  "Return the name with var symbol sym.
  Returns nil if not found."
  [sym]
  {:pre [(symbol? sym)]
   :post [(or (assert (or (nil? %)
                          (keyword? %)
                          (r/Type? %))
                      (pr-str %))
              true)]}
  (some-> (find-type-name-entry sym) val force))

(t/ann ^:no-check add-type-name [t/Sym (t/U t/Kw r/Type) -> nil])
(def add-type-name impl/add-tc-type-name)

(t/ann ^:no-check declare-name* [t/Sym -> nil])
(def declare-name* impl/declare-name*)

(t/ann declared-name? [t/Sym -> t/Bool])
(defn declared-name? [sym]
  (= (t/tc-ignore impl/declared-name-type)
     (get-type-name sym)))

(t/ann ^:no-check declare-protocol* [t/Sym -> nil])
(def declare-protocol* impl/declare-protocol*)

(t/ann ^:no-check declared-protocol? [t/Sym -> t/Bool])
(defn declared-protocol? [sym]
  (= (t/tc-ignore impl/protocol-name-type) (get-type-name sym)))

(t/ann ^:no-check declare-datatype* [t/Sym -> nil])
(def declare-datatype* impl/declare-datatype*)

(t/ann declared-datatype? [t/Sym -> t/Bool])
(defn declared-datatype? [sym]
  (= (t/tc-ignore impl/datatype-name-type)
     (get-type-name sym)))

(t/ann ^:no-check resolve-name* [t/Sym -> r/Type])
(defn resolve-name* [sym]
  {:pre [(symbol? sym)]
   :post [(r/Type? %)]}
  (let [t (get-type-name sym)
        tfn ((some-fn dtenv/get-datatype
                      prenv/get-protocol
                      (impl/impl-case :clojure #(or (rcls/get-rclass %)
                                                    (when (class? (resolve %))
                                                      (c/RClass-of-with-unknown-params %)))
                                      :cljs (requiring-resolve 'typed.cljs.checker.jsnominal-env/get-jsnominal))
                      ; during the definition of RClass's that reference
                      ; themselves in their definition, a temporary TFn is
                      ; added to the declared kind env which is enough to determine
                      ; type rank and variance.
                      kinds/declared-kind-or-nil) 
             sym)]
    (or tfn
      (cond
        (= impl/protocol-name-type t) (prenv/resolve-protocol sym)
        (= impl/datatype-name-type t) (dtenv/resolve-datatype sym)
        (= impl/declared-name-type t) (throw (IllegalArgumentException. (str "Reference to declared but undefined name " sym)))
        (r/Type? t) (vary-meta t assoc :source-Name sym)
        :else (err/int-error (str "Cannot resolve name " (pr-str sym)
                                  (when t
                                    (str " (Resolved to instance of)" (pr-str (class t))))))))))
