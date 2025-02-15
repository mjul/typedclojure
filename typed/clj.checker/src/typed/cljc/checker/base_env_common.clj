;;   Copyright (c) Ambrose Bonnaire-Sergeant, Rich Hickey & contributors.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns typed.cljc.checker.base-env-common
  "Utilities for all implementations of the type checker")

(defmacro delay-and-cache-env [sym & body]
  `(def ~sym
     (let [bfn# (bound-fn [] (let [res# (do ~@body)] res#))
           dl# (delay (bfn#))]
       (fn [] @dl#))))
