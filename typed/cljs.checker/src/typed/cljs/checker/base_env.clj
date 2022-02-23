;;   Copyright (c) Ambrose Bonnaire-Sergeant, Rich Hickey & contributors.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns typed.cljs.checker.base-env
  (:require [cljs.analyzer :as ana]
            [cljs.env :as env]
            [clojure.core.typed.base-env-helper-cljs :as h]
            [clojure.core.typed.current-impl :as impl]
            [clojure.set :as set]
            [typed.cljc.checker.base-env-common :refer [delay-and-cache-env] :as common]
            [typed.cljc.checker.datatype-env :as datatype-env]
            [typed.cljc.checker.declared-kind-env :as declared-kind-env]
            [typed.cljc.checker.name-env :as name-env]
            [typed.cljc.checker.protocol-env :as protocol-env]
            [typed.cljc.checker.var-env :as var-env]
            [typed.cljs.checker.jsnominal-env :as jsnominal]
            [typed.cljs.checker.util :as ucljs]))

(ucljs/with-cljs-typed-env
(ucljs/with-core-cljs-typed
(binding [ana/*cljs-ns* 'cljs.core.typed]
(delay-and-cache-env ^:private init-protocol-env
  (h/protocol-mappings
    
cljs.core/Fn [[]]
cljs.core/IFn [[]]
cljs.core/ICloneable [[]]
cljs.core/ICounted [[]]
cljs.core/IEmptyableCollection [[]]
cljs.core/ICollection [[[x :variance :covariant]]]
cljs.core/IIndexed [[]]
cljs.core/ASeq [[[x :variance :covariant]]]
cljs.core/ISeqable [[[x :variance :covariant]]]
cljs.core/ISeq [[[x :variance :covariant]]]
cljs.core/INext [[[x :variance :covariant]]]
cljs.core/ILookup [[[maxk :variance :covariant]
                    [v :variance :covariant]]]
cljs.core/IAssociative [[[m :variance :covariant]
                         [k :variance :covariant]
                         [v :variance :covariant]]]
cljs.core/IMap [[[k :variance :covariant]
                 [v :variance :covariant]]]
cljs.core/IMapEntry [[[k :variance :covariant]
                      [v :variance :covariant]]]
cljs.core/ISet [[[x :variance :covariant]]]
cljs.core/IStack [[[x :variance :covariant]]]
cljs.core/IVector [[[x :variance :covariant]]]
cljs.core/IDeref [[[x :variance :covariant]]]
cljs.core/IDerefWithTimeout [[[x :variance :covariant]]]
cljs.core/IMeta [[]]
cljs.core/IWithMeta [[]]
    ;TODO
;cljs.core/IReduce [[]]
;cljs.core/IKVReduce [[]]
cljs.core/IList [[[x :variance :covariant]]]
cljs.core/IEquiv [[]]
cljs.core/IHash [[]]
cljs.core/ISequential [[]]
cljs.core/Record [[]]
cljs.core/IReversible [[[x :variance :covariant]]]
cljs.core/ISorted [[]]
cljs.core/IWriter [[]]
cljs.core/IPrintWithWriter [[]]
cljs.core/IPending [[]]
    ;TODO
;cljs.core/IWatchable [[]]
    ;cljs.core/IEditableCollection [[]]
    ;cljs.core/ITransientCollection [[]]
    ;cljs.core/ITransientAssociative [[]]
    ;cljs.core/ITransientMap [[]]
    ;cljs.core/ITransientVector [[]]
    ;cljs.core/ITransientSet [[]]
cljs.core/IComparable [[]]
    ;cljs.core/IChunk [[]]
    ;cljs.core/IChunkedSeq [[]]
    ;cljs.core/IChunkedNext [[]]
cljs.core/INamed [[]]
cljs.core/Reduced [[[x :variance :covariant]]]
))

(defn reset-protocol-env! []
  (impl/with-cljs-impl
    (protocol-env/reset-protocol-env! (init-protocol-env))))

#_
(ann-jsclass js/Document
  :extends
  :implements 
  :properties
  {getElementById [cljs.core.typed/JSString -> (U nil js/HTMLElement)]}
  :static-properties
  )

(delay-and-cache-env ^:private init-jsnominals 
  (reset-protocol-env!)
  (h/jsnominal-mappings)
  #_
  (h/jsnominal-mappings

; seems like a good place to put this
;; FIXME this is actually js/String, delete
string [[]
        :fields
        {}
        :methods
        {toLowerCase [-> cljs.core.typed/JSstring]}]
    
js/Document [[]
          :fields
          {}
          :methods
          {getElementById [cljs.core.typed/JSstring -> (U nil js/HTMLElement)]}]

js/HTMLElement [[]
             :fields
             {innerHTML cljs.core.typed/JSstring
              tagName (U nil cljs.core.typed/JSstring)}]
    
    
js/Event [[]
       :methods
       {preventDefault [-> nil]}]


    ;http://dom.spec.whatwg.org/#interface-eventtarget
js/EventTarget [[]]
    
goog.events.Listenable [[]]
goog.events.EventTarget [[]]
    ))

(defn reset-jsnominal-env! []
  (impl/with-cljs-impl
    (jsnominal/reset-jsnominal!
     (init-jsnominals))))

;;; vars specific to cljs
(delay-and-cache-env ^:private init-var-env
  (reset-protocol-env!)
  (reset-jsnominal-env!)
  (h/var-mappings)
  #_
  (merge
   (common/parse-cljs-ann-map @common/common-var-annotations)
   (h/var-mappings

cljs.core/+ (IFn [cljs.core.typed/CLJSInteger * -> cljs.core.typed/CLJSInteger]
                 [cljs.core.typed/JSnumber * -> cljs.core.typed/JSnumber])
cljs.core/- (IFn [cljs.core.typed/CLJSInteger * -> cljs.core.typed/CLJSInteger]
                 [cljs.core.typed/JSnumber * -> cljs.core.typed/JSnumber])
cljs.core/* (IFn [cljs.core.typed/CLJSInteger * -> cljs.core.typed/CLJSInteger]
                 [cljs.core.typed/JSnumber * -> cljs.core.typed/JSnumber])
cljs.core/nth (All [x y]
                (IFn [(U nil (cljs.core/ISeqable x)) cljs.core.typed/CLJSInteger -> x]
                     [(U nil (cljs.core/ISeqable x)) cljs.core.typed/CLJSInteger y -> (U y x)]))

cljs.core/*flush-on-newline* cljs.core.typed/JSBoolean
cljs.core/*print-newline* cljs.core.typed/JSBoolean
cljs.core/*print-readably* cljs.core.typed/JSBoolean
cljs.core/*print-meta* cljs.core.typed/JSBoolean
cljs.core/*print-dup* cljs.core.typed/JSBoolean
cljs.core/*print-length* (U nil cljs.core.typed/CLJSInteger)

cljs.core/enable-console-print! [-> Any]

cljs.core/truth_ [Any -> Any]

cljs.core/coercive-= [Any Any -> cljs.core.typed/JSBoolean]

cljs.core/nil? (Pred nil)
cljs.core/undefined? (Pred JSUndefined)

cljs.core/array? (ReadOnlyArray Any)

cljs.core/object? [Any -> cljs.core.typed/JSBoolean]

cljs.core/native-satisfies? [Any Any -> Any]

cljs.core/is_proto_ [Any -> Any]

cljs.core/*main-cli-fn* (U nil [Any * -> Any])

cljs.core/missing-protocol [Any Any -> Any]
cljs.core/type->str [Any -> cljs.core.typed/JSString]

cljs.core/make-array (All [r] 
                          (IFn [cljs.core.typed/CLJSInteger -> (Array r)]
                               [Any cljs.core.typed/CLJSInteger -> (Array r)]))

cljs.core/array (All [r]
                     [r * -> (Array r)])

cljs.core/alength [(ReadOnlyArray Any) -> cljs.core.typed/CLJSInteger]

cljs.core/into-array (All [x] 
                          (IFn [(U nil (cljs.core/ISeqable x)) -> (Array x)]
                              [Any (U nil (cljs.core/ISeqable x)) -> (Array x)]))

cljs.core/pr-str* [Any -> cljs.core.typed/JSString]

cljs.core/clone [Any -> Any]

cljs.core/cloneable? (Pred cljs.core/ICloneable)


cljs.core/count
      ; TODO also accepts Counted
      ; FIXME should return integer
      [(U nil (cljs.core/ISeqable Any)) -> cljs.core.typed/CLJSInteger :object {:id 0, :path [Count]}]
cljs.core/prim-seq
      (All [x]
           [(cljs.core/ISeqable x) -> (U nil (cljs.core/ISeq x))])

cljs.core/key-test [Keyword Any -> cljs.core.typed/JSBoolean]

cljs.core/fn? [Any -> cljs.core.typed/JSBoolean]
cljs.core/ifn? [Any -> cljs.core.typed/JSBoolean]

;;pop needs to be defined here because
;;definition of List differs between clj and cljs
cljs.core/pop (All [x]
                      (IFn
                        [(IList x) -> (IList x)]
                        [(Vec x) -> (Vec x)]
                        [(Stack x) -> (Stack x)]))

cljs.core/clj->js [Any -> Any]
cljs.core/js->clj [Any -> Any]
cljs.core/js-obj  [Any * -> Any]

;;pseudo-private vars
cljs.core/-conj [Any Any -> Any]
;cljs.core.List.Empty (IList Any)
)))

(delay-and-cache-env ^:private init-var-nochecks
  (set (keys (init-var-env))))

(delay-and-cache-env init-jsvar-env
  (reset-protocol-env!)
  (reset-jsnominal-env!)
  (h/js-var-mappings)
  #_
  (h/js-var-mappings
;; js
    
js/document js/Document

;; goog.dom

goog.dom/setTextContent [js/Element (U cljs.core.typed/JSString cljs.core.typed/JSnumber) -> js/Window]
goog.dom/getElementsByTagNameAndClass 
      [(U nil cljs.core.typed/JSString) (U nil cljs.core.typed/JSString) (U nil js/Document js/Element) -> (cljs.core/ISeqable js/Element)]
goog.dom.classes/set [(U js/Node nil) cljs.core.typed/JSString -> Any]
goog.dom.classes/add [(U js/Node nil) (U nil cljs.core.typed/JSString) * -> cljs.core.typed/JSBoolean]
goog.dom.classes/remove [(U js/Node nil) (U nil cljs.core.typed/JSString) * -> cljs.core.typed/JSBoolean]
goog.style/getPageOffsetLeft [(U nil js/Element) -> cljs.core.typed/JSnumber]
goog.style/getPageOffsetTop [(U nil js/Element) -> cljs.core.typed/JSnumber]
goog.events/listen [(U nil js/EventTarget goog.events.EventTarget goog.events.Listenable)
                    (U nil cljs.core.typed/JSString (ReadOnlyArray cljs.core.typed/JSString)) -> cljs.core.typed/JSnumber]

goog.events.EventType.KEYUP   cljs.core.typed/JSString
goog.events.EventType.KEYDOWN cljs.core.typed/JSString
goog.events.EventType.KEYPRESS cljs.core.typed/JSString
goog.events.EventType.CLICK   cljs.core.typed/JSString
goog.events.EventType.DBLCLICK cljs.core.typed/JSString
goog.events.EventType.MOUSEOVER cljs.core.typed/JSString
goog.events.EventType.MOUSEOUT cljs.core.typed/JSString
goog.events.EventType.MOUSEMOVE cljs.core.typed/JSString
    ))

(delay-and-cache-env init-alias-env 
  (reset-protocol-env!)
  (reset-jsnominal-env!)
  (h/alias-mappings
  ^{:doc "A type that returns true for cljs.core/integer?"}
typed.clojure/AnyInteger typed.clojure/CLJSInteger

  ^{:doc "A type that returns true for cljs.core/integer?"}
typed.clojure/Integer typed.clojure/CLJSInteger

  ^{:doc "A type that returns true for cljs.core/integer?"}
typed.clojure/Int typed.clojure/CLJSInteger

  ^{:doc "A type that returns true for cljs.core/number?"}
typed.clojure/Number typed.clojure/JSnumber

  ^{:doc "A type that returns true for cljs.core/number?"}
typed.clojure/Num typed.clojure/JSnumber

  ^{:doc "A type that returns true for cljs.core/string?"}
typed.clojure/String typed.clojure/JSString

  ^{:doc "A type that returns true for cljs.core/string?"}
typed.clojure/Str typed.clojure/JSString

  ^{:doc "A type that returns true for cljs.core/boolean?"}
typed.clojure/Boolean typed.clojure/JSBoolean

  ^{:doc "vector -- alias for common anns"}
typed.clojure/Vec (TFn [[x :variance :covariant]]
                         (IVector x))

  ^{:doc "vector -- alias for common anns"}
typed.clojure/IPersistentVector (TFn [[x :variance :covariant]]
                                       (IVector x))

  ^{:doc "map -- alias for common anns"}
typed.clojure/Map (TFn [[k :variance :covariant]
                          [v :variance :covariant]]
                         (IMap k v))

  ^{:doc "map -- alias for common anns"}
typed.clojure/IPersistentMap (TFn [[k :variance :covariant]
                                     [v :variance :covariant]]
                         (IMap k v))

  ^{:doc "map -- alias for common anns"}
typed.clojure/APersistentMap (TFn [[k :variance :covariant]
                                     [v :variance :covariant]]
                         (IMap k v))

  ^{:doc "associative -- alias for common anns"}
typed.clojure/Associative IAssociative

  ^{:doc "An atom that can read and write type x."
    :forms [(Atom1 t)]}
typed.clojure/Atom1 (TFn [[x :variance :invariant]] 
                           (cljs.core/Atom x x))
  ^{:doc "An atom that can write type w and read type r."
    :forms [(Atom2 t)]}
typed.clojure/Atom2 (TFn [[w :variance :contravariant]
                            [r :variance :covariant]] 
                           (cljs.core/Atom w r))

  ^{:doc "sequential -- alias for common anns"}
typed.clojure/Sequential ISequential

  ^{:doc "set -- alias for common anns"}
typed.clojure/Set ISet

  ^{:doc "set -- alias for common anns"}
typed.clojure/IPersistentSet ISet


  ^{:doc "A type that can be used to create a sequence of member type x."}
typed.clojure/Seqable (TFn [[x :variance :covariant]]
                             (cljs.core/ISeqable x))

  ^{:doc "A persistent sequence of member type x."
    :forms [(Seq t)]}
typed.clojure/Seq (TFn [[x :variance :covariant]]
                         (cljs.core/ISeq x))

  ^{:doc "A persistent sequence of member type x with count greater than 0."
    :forms [(NonEmptySeq t)]}
typed.clojure/NonEmptySeq (TFn [[x :variance :covariant]]
                                 (I (cljs.core/ISeq x) (CountRange 1)))




   ;;copied from impl/init-aliases

   ;;Seqables
  ^{:doc "A type that can be used to create a sequence of member type x
with count 0."
    :forms [(EmptySeqable t)]}
typed.clojure/EmptySeqable (TFn [[x :variance :covariant]]
                                  (I (cljs.core/ISeqable x) (ExactCount 0)))

   ^{:doc "A type that can be used to create a sequence of member type x
with count greater than 0."
     :forms [(NonEmptySeqable t)]}
typed.clojure/NonEmptySeqable
    (TFn [[x :variance :covariant]]
         (I (cljs.core/ISeqable x) (CountRange 1)))

    ;;Option
  ^{:doc "A union of x and nil."
    :forms [(Option t)]}
typed.clojure/Option (TFn [[x :variance :covariant]] (U nil x))


  ^{:doc "A persistent collection with member type x."
    :forms [(Coll t)]}
typed.clojure/Coll (TFn [[x :variance :covariant]]
                          (cljs.core/ICollection x))

  ^{:doc "A persistent collection with member type x and count greater than 0."
    :forms [(NonEmptyColl t)]}
typed.clojure/NonEmptyColl (TFn [[x :variance :covariant]]
                                  (I (cljs.core/ICollection x)
                                     (CountRange 1)))

  ^{:doc "A sequential non-empty seq retured from clojure.core/seq"
    :forms [(NonEmptyASeq t)]}
typed.clojure/NonEmptyASeq
   (TFn [[x :variance :covariant]]
        (I (cljs.core/ASeq x)
           (cljs.core/ISeq x)
           (cljs.core/ISeqable x)
           cljs.core/ISequential
           ;(Iterable x)
           (cljs.core/ICollection x)
           (cljs.core/IList x)
           ;clojure.lang.IObj
           (CountRange 1)))


  ^{:doc "The type of all things with count 0. Use as part of an intersection.
           eg. See EmptySeqable."
    :forms [EmptyCount]}
typed.clojure/EmptyCount (ExactCount 0)

  ^{:doc "The type of all things with count greater than 0. Use as part of an intersection.
           eg. See NonEmptySeq"
     :forms [NonEmptyCount]}
typed.clojure/NonEmptyCount (CountRange 1)

  ^{:doc "A union of x and nil."
    :forms [(Nilable t)]}
typed.clojure/Nilable (TFn [[x :variance :covariant]] (U nil x))

  ^{:doc "A persistent vector returned from clojure.core/vector (and others)"
    :forms [(AVec t)]}
typed.clojure/AVec (TFn [[x :variance :covariant]]
                             (I (IVector x)
                                ;(java.lang.Iterable x)
                                (ICollection x)
                                (IList x)
                                ;clojure.lang.IObj
                                ))

  ^{:doc "A persistent vector returned from clojure.core/vector (and others) and count greater than 0."
    :forms [(NonEmptyAVec t)]}
typed.clojure/NonEmptyAVec (TFn [[x :variance :covariant]]
                                        (I (IVector x)
                                           ;(java.lang.Iterable x)
                                           (ICollection x)
                                           (IList x)
                                           ;clojure.lang.IObj
                                           (CountRange 1)))

  ^{:doc "The result of clojure.core/seq."
    :forms [(NilableNonEmptyASeq t)]}
typed.clojure/NilableNonEmptyASeq
   (TFn [[x :variance :covariant]]
        (U nil
           (I (cljs.core/ASeq x)
              (cljs.core/ISeq x)
              cljs.core/ISequential
              ;(Iterable x)
              (cljs.core/ICollection x)
              (cljs.core/IList x)
              ;clojure.lang.IObj
              (CountRange 1))))

  ^{:doc "A Clojure persistent list."
    :forms [(PersistentList t)]}
typed.clojure/PersistentList
   (TFn [[x :variance :covariant]]
        (cljs.core/IList x))

  ^{:doc "Collection"}
typed.clojure/Collection
   (TFn [[x :variance :covariant]]
        (cljs.core/ICollection x))

  ^{:doc "A Clojure stack."
    :forms [(Stack t)]}
typed.clojure/Stack
   (TFn [[x :variance :covariant]]
        (cljs.core/IStack x))

   ^{:doc "Reversible maps to IReversible"
     :forms [(Reversible t)]}
typed.clojure/Reversible
   (TFn [[x :variance :covariant]]
        (cljs.core/IReversible x))))


(defn reset-alias-env! []
  (let [alias-env (init-alias-env)]
    (name-env/reset-name-env! alias-env)))

(delay-and-cache-env init-declared-kinds {})

(delay-and-cache-env init-datatype-env
  (reset-protocol-env!)
  (reset-jsnominal-env!)
  (h/datatype-mappings

cljs.core/Atom [[[w :variance :contravariant]
                 [r :variance :covariant]]]
cljs.core/Symbol [[]]
cljs.core/Keyword [[]]
cljs.core/List [[[a :variance :covariant]]]
cljs.core/Reduced [[[x :variance :covariant]]]
    ))
)

(defn reset-envs! []
  (ucljs/with-cljs-typed-env
    (impl/with-cljs-impl
      (reset-alias-env!)
      (var-env/reset-var-type-env! (init-var-env) (init-var-nochecks))
      (var-env/reset-jsvar-type-env! (init-jsvar-env))
      (reset-protocol-env!)
      (declared-kind-env/reset-declared-kinds! (init-declared-kinds))
      (reset-jsnominal-env!)
      (datatype-env/reset-datatype-env! (init-datatype-env))))
  nil)
))

;;FIXME hack
(reset-envs!)
