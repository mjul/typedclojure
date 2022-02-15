;;   Copyright (c) Ambrose Bonnaire-Sergeant, Rich Hickey & contributors.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

;;TODO migrate to typed.cljs
(ns cljs.core.typed
  "Internal functions for CLJS")

(defn ^:no-doc
  inst-poly
  "Internal use only. Use inst."
  [inst-of types-syn]
  inst-of)

(defn ^:no-doc
  loop>-ann
  "Internal use only. Use loop>"
  [loop-of bnding-types]
  loop-of)
