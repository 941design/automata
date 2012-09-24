;;
;; Nomenclatura
;; ============
;;
;; a  <= automaton
;; s  <= state
;; ss <= start-state
;; fs <= from-state
;; ts <= to-state
;; t  <= transition
;; ns <= namespace
;; desc <= (partial) automaton description

(ns automata.automata
  (:use [automata.dfa]))

;; (load-file "src/automata/automata.clj")

;; :TODOs
;; - namespacing (with prefix for all automata: dfa-)
;; - error when letter not in alphabet

;; :TODO - follow has no business in this namespace, as it returns nil although no automaton was ever created!
(defmulti follow
  {:doc "dispatches on tuple '(from-state next-token) to dynamically created function"}
  (fn [fs word] (list fs (first word))))

(defmethod follow :default [fs word]
  {:doc "returns current state if word is empty"}
  ;; :TODO - also returns non-existing state symbols
  ;; :TODO - returns nil instead of error on non-alphabet token
  (if (empty? word) fs nil))

(defmulti create-transition-function
  {:doc "dispatches on amount of arguments."}
  (fn [fs attr ts] (count attr)))

(defmethod create-transition-function 0 [fs attr ts]
  {:doc "description contains no attributes"}
  ;; does not follow chain, does not keep recursive set
  ;; :TODO - allow empty transitions (nfa)
  :TODO)

(defmethod create-transition-function 1 [fs attr ts]
  {:doc "description contains one attribute"}
  (let [at (first attr)]
    (defmethod follow (list fs at) [void word]      
      (if (= (first word) at) (follow ts (rest word))))))
;; :TODO - move to test
;; (create-transition-function '(:s1 [0] :s2))
;; (follow :s1 '())
;; (follow :s1 '(0))
;; (follow :s3 '()) :TODO - not recognized as invalid state

(defmethod create-transition-function :default [fs attr ts]
  {:doc "description contains more than one attribute"}
  (doseq [at attr]
    (defmethod follow (list fs at) [void word]      
      (if (= (first word) at) (follow ts (rest word))))))

(defn create-transition-functions [desc]
  (doseq [t desc] (create-transition-function (first t) (second t) (last t))))
;; (create-transition-function #{'(:s1 [1] :s2)})
;; (follow :s1 '(1))

(defn dfa-create [dfa]
  {:doc "Creates dfa's transition-functions in described namespace"}
  (let [prev-ns *ns*]
    (do
      ;; :TODO - validate here?
      ;; :TODO - namespace switching here !!!
      ;; :TODO - switch namespaces
      ;;(in-ns ns)
      ;; :TODO - create defmulti stub for follow in new namespace
      (create-transition-functions (dfa :transitions))
      ;;(ns prev-ns)
      dfa
      )))

(defn accepts? [dfa word]
  {:doc "Returns true if given automaton accepts input."}
  ;; :TODO - switch namespace before calling follow!
  (contains? (dfa :accepting-states) (follow (dfa :start-state) word)))

;; (dfa-create dfa)
;; (accepts? dfa [0 1])
;;
;; Attention Clojure inconsitency!?
;; (:foo nil)
;; nil
;; (nil :foo)
;; Error!
;; ({:foo :bar} :foo)
;; :bar
;;------------------------------------------------
;; :TODO test:
;; (use 'clojure.tools.trace)
;; show namespace:
;; (use '[clojure.string :only (join split)])
;; (print (clojure.string/join "\n" (sort (ns-map 'user))))
