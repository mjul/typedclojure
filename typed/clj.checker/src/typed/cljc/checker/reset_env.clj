;;   Copyright (c) Ambrose Bonnaire-Sergeant, Rich Hickey & contributors.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns ^:no-doc typed.cljc.checker.reset-env
  (:require [typed.clj.checker.base-env :as bse-clj]
            [typed.cljc.checker.ns-options :as ns-opts]
            [clojure.core.typed.current-impl :as impl]
            [typed.clj.checker.mm-env :as mmenv]))

(def ^:private reset-cljs-envs! #(requiring-resolve 'typed.cljs.checker.base-env/reset-envs!))

(defn reset-envs!
  "Reset all environments for the current implementation."
  ([] (reset-envs! false))
  ([cljs?]
  (let []
    (impl/impl-case
      :clojure
      (do (bse-clj/reset-clojure-envs!)
          (mmenv/reset-mm-dispatch-env!)
          (ns-opts/reset-ns-opts!))
      :cljs
      (do
        (assert cljs? "No ClojureScript dependency")
        (when cljs?
          (reset-cljs-envs!)
          (ns-opts/reset-ns-opts!))))
    nil)))

(defn load-core-envs! 
  "Add core annotations to environments for the current implementation."
  ([] (load-core-envs! false))
  ([cljs?]
  (let []
    (impl/impl-case
      :clojure
      (do (bse-clj/refresh-core-clojure-envs!)
          ;(mmenv/reset-mm-dispatch-env!)
          ;(ns-opts/reset-ns-opts!)
          )
      :cljs
      nil
      #_
      (do
        (assert nil "load-core-envs! TODO CLJS")
        (assert cljs? "No ClojureScript dependency")
        (reset-envs! true)
        (when cljs?
          (reset-cljs-envs!)
          (ns-opts/reset-ns-opts!))))
    nil)))
