(ns automata.automata)

;;(load-file "src/automata/automata.clj")
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

;; #TODOs
;; - namespacing (with prefix for all automata: dfa-)
;; - error when letter not in alphabet

;; :TODO - follow has no business in this namespace, as it returns nil although no automaton was ever created!
(defmulti follow
  {:doc "Dispatches on tuple of '(from-state next-token) to dynamically created function."}
  (fn [fs word] (list fs (first word))))

(defmethod follow :default [fs word]
  {:doc "Returns current state if word is empty"}
  ;; #TODO - also returns non-existing state symbols
  ;; #TODO - returns nil instead of error on non-alphabet token
  (if (empty? word) fs nil))

;; (def create-follow-method nil)
(defmulti create-follow-method
  {:doc "Dispatches on amount of arguments."}
  (fn [desc] (if (empty? (second desc)) :empty)))

(defmethod create-follow-method :empty [desc]
  {:doc "follow method for empty transitions #TODO (does not follow chain, does not keep recursive set)"}
  ;; #TODO - allow empty transitions (nfa)
  :TODO)

;; #TODO - rename to create-transition
(defmethod create-follow-method :default [desc]
  {:doc "follow method for labelled transitions"}
  (let [fs (nth desc 0)
        attr (first (nth desc 1)) ;; #TODO allow multiple attributes
        ts (nth desc 2)]
    (defmethod follow (list fs attr) [void word]      
      (cond
        (= (first word) attr) (follow ts (rest word))))))
;; #TODO - move to test
;; (create-follow-method '(:s1 [0] :s2))
;; (follow :s1 '())
;; (follow :s1 '(0))
;; (follow :s3 '()) #TODO - not recognized as invalid state

(defn create-transition-functions-in [ns desc]
  {:doc "Creates functions from description in namespace."}
  (let [prev-ns *ns*]
    (do
      ;; #TODO - switch namespaces
      ;;(in-ns ns)
      (doseq [td desc] (create-follow-method td))
      ;;(ns prev-ns)
      )))
;; #TODO - move to test
;; (create-transition-functions-in 'foo #{'(:s1 [1] :s2)})
;; (follow :s1 '(1))

(defn dfa-create [dfa]
  {:doc "Creates dfa's transition-functions in described namespace"}
  (do
    ;; #TODO - namespace switching here !!!
    ;; #TODO - create defmulti stub for follow in new namespace
    (create-transition-functions-in (dfa :name) (dfa :transitions))
    dfa
    ))

(defn accepts? [dfa word]
  {:doc "Returns true if given automaton accepts input."}
  ;; #TODO - switch namespace when calling follow!
  (do (println (class word))
      (contains? (dfa :accepting-states) (follow (dfa :start-state) word))))
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
