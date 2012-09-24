(ns automata.dfa
 ;; (:use [automata.automata]) ;; for what?
  ;; (:gen-class) ;; for what?
  )

;; (load-file "src/automata/dfa.clj")

;; #TODO - create tests for all functions

;; #TODO - create macro - move
(defn in? [seq elm]  
  {:doc "true if seq contains elm"}  
  (some #(= elm %) seq))
;; (in? [:a :b :c] :a)

;; #TODO - create macro - move
(defn conform-all? [func coll]
  (if
    (empty? coll) true
    (if (not (func (first coll))) false
        (recur func (rest coll)))))
;; (conform-all? identity '(true true false))
;; (conform-all? identity '())
;; (conform-all? identity '(nil))

(defn validate-literal [l]
  (if ((some-fn number? keyword? string?) l) true
      (list :? (format "invalid name: %s" l))))
;; (validate-literal :foo)
;; (validate-literal "foo")
;; (validate-literal nil)
;; (validate-literal '())

(defn validate-name [a]
  {:doc "arg => automaton"}
  (let [n (a :name)]
    (if (nil? n) (list :name)
        (list :name (validate-literal n)))))
;; (validate-name {:name :foo})
;; (validate-name {})

(defn validate-in-states [a s]
  (let [st (a :states)]
    (if (not (in? st s)) (list :in-states? (format "not in states: %s" s))
        (list :in-states? (validate-literal s)))))

(defn validate-start-state [a]
  {:doc "arg => automaton"}
  (let [ss (a :start-state)]
    (if (nil? ss) (list :start-state "no start-state")
        (list :start-state (validate-in-states a ss)))))
;; (validate-start-state {:start-state nil})
;; (validate-start-state {:start-state :s1})
;; (validate-start-state {:start-state '(:foo)})

(defn validate-states [a]
  {:doc "arg => automaton"}
  (let [st (a :states)]
    (if (not (coll? st)) (list :states "not a collection")
        (list :states (map validate-literal st)))))
;; (validate-states {:states #{:a :b :c}})
                                
(defn validate-accepting-states [a]
  {:doc "arg => automaton"}
  (let [ack (a :accepting-states)
        all (a :states)]    
    (if (not (coll? ack)) (list :accepting-states "not a collection")
        (list :accepting-states
              (map #(validate-in-states a %) ack)))))
;; (validate-accepting-states {:accepting-states [:foo]})
;; (validate-accepting-states {:accepting-states #{:s :foo} :states #{:s}})
;; (validate-accepting-states {:accepting-states #{:s} :states [:s]})
;; (validate-accepting-states {:accepting-states [:s] :states #{:s}})
;; (validate-accepting-states {:accepting-states [:s] :states [:s]})

(defn validate-alphabet [a]
  {:doc "arg => automaton"}
  (let [alpha (a :alphabet)]
    (if (not (coll? alpha)) (list :alphabet (format "not a collection %s" alpha))
        (list :alphabet (map validate-literal alpha)))))
;; (validate-alphabet dfa)

(defn validate-in-alphabet [a l]
  (list :in-alphabet? (if (in? (a :alphabet) l) true
                          (format "not in alphabet: %s" l))))
;; (validate-in-alphabet {:alphabet [0]} 1)
;; (validate-in-alphabet {:alphabet [0]} 0)

(defn validate-attributes [a attr]
  {:doc "args => automaton attributes"}  
  (if (not (coll? attr)) (list :attr "not a collection")
      (map #(validate-in-alphabet a %) attr)))
;; (validate-attributes {:alphabet [0]} [0 1])

(defn validate-transition [a t]
  {:doc "args => automaton transition"}
  (if (not= (count t) 3) (list (format "invalid transition format: %s" t))
      (concat (list (validate-in-states a (first t)))
              (list (validate-attributes a (second t)))
              (list (validate-in-states a (last t))))))
;; (validate-transition {:alphabet [0]} '(:s [0] :s))
;; (validate-transition {} '(:s [1] :s))

(defn validate-transitions [a]
  {:doc "arg => automaton"}
  (let [trans (a :transitions)]
    (if (not (coll? trans)) (list :transitions (format "not a collection %s" trans))
        (list :transitions (map #(validate-transition a %) trans)))))
;; (validate-transitions dfa)

(defn validate-dfa [a]
  {:doc "arg => automaton"}
  ;; NOTE: chaining methods with eval did not compile properly to .jar!
  (list :dfa (concat (validate-name a)
                     (validate-alphabet a)
                     (validate-states a)
                     (validate-start-state a)
                     (validate-accepting-states a)
                     (validate-transitions a))))
;; (validate-dfa dfa)
;; (filter string? (flatten (validate-dfa dfa)))

(defn dfa-validation-results [a]
  (set (filter string? (flatten (validate-dfa a)))))
;;(dfa-validation-results dfa)

(defn valid-dfa? [a]
  (empty? (dfa-validation-results a)))
;; (valid-dfa? dfa)


;;------------------------

(def dfa {:name "foo"
          :alphabet #{0 1}
          :start-state :s1
          :states #{:s1 :s2}
          :accepting-states #{:s1}
          :transitions #{'(:s1 [0] :s2)
                         '(:s1 [1] :s2)
                         ;;'(:s2 [2] :s3) ;;foo
                         }})
