(ns ritz.repl-utils.clojure
  (:refer-clojure :exclude [with-redefs]))

(defn unmunge
  "Converts a javafied name to a clojure symbol name"
  ([^String name]
     (reduce (fn [^String s [to from]]
               (.replaceAll s from (str to)))
             name
             clojure.lang.Compiler/CHAR_MAP)))

(defn ns-path
  "Returns the path form of a given namespace"
  ([^clojure.lang.Namespace ns]
     (let [^String ns-str (name (ns-name ns))]
       (-> ns-str
           (.substring 0 (.lastIndexOf ns-str "."))
           (.replace \- \_)
           (.replace \. \/)))))

(defn symbol-name-parts
  "Parses a symbol name into a namespace and a name. If name doesn't
   contain a namespace, the default-ns is used (nil if none provided)."
  ([symbol]
     (symbol-name-parts symbol nil))
  ([^String symbol default-ns]
     (let [ns-pos (.indexOf symbol (int \/))]
       (if (= ns-pos -1) ;; namespace found?
         [default-ns symbol]
         [(.substring symbol 0 ns-pos) (.substring symbol (inc ns-pos))]))))

(defn resolve-ns [sym ns]
  (or (find-ns sym)
      (get (ns-aliases ns) sym)))

(def ^{:macro true :doc "Provide cross version support for with-redefs"}
  with-redefs
  (var-get
   (or
    (resolve 'clojure.core/with-redefs)
    (resolve 'clojure.core/binding))))

(def clojure-1-2-or-greater
  (let [{:keys [major minor]} *clojure-version*]
    (or (and (= major 1) (> minor 1)) (> major 1))))

(def clojure-1-3-or-greater
  (let [{:keys [major minor]} *clojure-version*]
    (or (and (= major 1) (> minor 2)) (> major 1))))

(def clojure-1-4-or-greater
  (let [{:keys [major minor]} *clojure-version*]
    (or (and (= major 1) (> minor 3)) (> major 1))))

(def ^{:doc "Predicate to test if the version of clojure has protocols"}
  protocols (ns-resolve 'clojure.core 'defprotocol))

(def ^{:doc "Predicate to test if the version of clojure has with-redefs"}
  redefs (ns-resolve 'clojure.core 'with-redefs))

(def
  ^{:doc "Predicate to test if the version of clojure has *compiler-options*"}
  compiler-options (ns-resolve 'clojure.core '*compiler-options*))

(defmacro feature-cond
  [& feature-expressions]
  (loop [[expr body] (take 2 feature-expressions)
         others (drop 2 feature-expressions)]
    (when expr
      (if (eval expr)
        body
        (recur (take 2 others) (drop 2 others))))))
