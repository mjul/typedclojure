;;   Copyright (c) Ambrose Bonnaire-Sergeant, Rich Hickey & contributors.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns ^:no-doc typed.cljc.ext.clojure.core.typed__pred
  "Type rule for clojure.core.typed/pred."
  (:require [clojure.core.typed.util-vars :as vs]
            [typed.cljc.checker.check.utils :as cu]
            [typed.clj.checker.parse-unparse :as prs]
            [typed.cljc.checker.tvar-env :as tvar-env]
            [typed.cljc.checker.dvar-env :as dvar-env]
            [clojure.core.typed.errors :as err]
            [typed.cljc.checker.check-below :as below]
            [typed.cljc.checker.type-rep :as r]
            [typed.cljc.checker.utils :as u]))

(defn -unanalyzed-special__pred
  [{[_ tsyn :as form] :form :keys [env] :as expr} expected]
  {:post [(-> % u/expr-type r/TCResult?)]}
  (let [nargs (dec (count form))
        _ (when-not (= 1 nargs)
            (err/int-error (str "Wrong arguments to pred: Expected 1, found " nargs)))
        ptype
        ; frees are not scoped when pred's are parsed at runtime,
        ; so we simulate the same here.
        (binding [tvar-env/*current-tvars* {}
                  dvar-env/*dotted-scope* {}]
          (prs/with-parse-ns (cu/expr-ns expr)
            (prs/parse-type tsyn)))]
    (assoc expr
           u/expr-type (below/maybe-check-below
                         (r/ret (prs/predicate-for ptype))
                         expected))))
