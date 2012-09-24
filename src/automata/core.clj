(ns automata.core
  (:require [clojure.tools.cli :refer [cli]])
  (:use [automata.automata])
  (:use [automata.dfa])  
  (:use [automata.dfa-printing])
  (:gen-class))

;;(load-file "src/automata/core.clj")
;;(load-file "src/automata/dfa.clj")
;;(load-file "src/automata/dfa_printing.clj")

;; (use '[clojure.string :only (join split)])
;; (print (clojure.string/join "\n" (sort (ns-map *ns*))))
 
;; TODO - catch exceptions!
;; (def parse-cmd-line nil)
(defmulti parse-cmd-line empty?)

(defmethod parse-cmd-line true [args]
  {:doc "empty command line args => help"}
  (parse-cmd-line ["-h"]))

(defmethod parse-cmd-line false [args]
  {:doc "Returns map with keyvals for explicit arg and value. see doc ..."}
  (cli args
       ["-a" "--automaton" "specify automaton file name" :default "a.clj"]
       ["-d" "--dot" "specify .dot file name"]
       ["-w" "--word" "double-quoted sequence to match, e.g.: \"(0 1 0)\"."]
       ["-i" "--interactive" "enter words from command line" :default false]
       ["-h" "--help" "show this help" :flag true]))
;; (parse-cmd-line [])

;; #TODO - test with test input file
;; (parse-cmd-line ["-a" "a.clj"])
;; (parse-cmd-line ["-a" "a.clj" "-i"])
;; this will probably not work:
;; (parse-cmd-line ["-w" "(1 2 3)"])

(defn signal-and-exit [msg]
  {:doc :TODO}
  (do (println (str "Error " msg))
      (System/exit 1)))

(defn signal-all-and-exit [msgs]
  {:doc :TODO}
  (do (doseq [m msgs] (println (str "Error " m)))
      (System/exit 1)))

(defn read-automaton [f]
  {:doc "Reads and returns automaton from file."}
  (let [a (try (read-string (slurp f))
               (catch Exception e (signal-and-exit (.getMessage e))))
        res (dfa-validation-results a)]
    (if (empty? res) a
        (signal-all-and-exit res))))
;; (read-automaton "atest.clj")

(defn validate-and-exit [f]
  {:doc :TODO}
  (do (read-automaton f)
      (println "automaton is valid")
      (System/exit 0)))

;; #TODO - validate entered word (sets are collections), word must be in alphabet
(defn read-eval-print [a]
  {:doc :TODO}
  (let [w (read-line)]    
    (if (= "exit" w) (System/exit 0)
        (let [res (try (accepts? a (read-string w))
                       (catch Exception e (.getMessage e)))]
          (do (println (str w " >> " res))
              (recur a))))))

(defn read-validate-create-and-loop [in]
  {:doc :TODO}
  (let [a (read-automaton in)]
    (do (dfa-create a)
        (read-eval-print a))))

(defn read-validate-match-and-exit [in word]
  {:doc :TODO}
  (let [a (read-automaton in)
        w (read-string word)]
    (do (println (str  word " - " (accepts? a w)))
        (System/exit 0))))

;; #TODO - pass out-file name!
(defn read-validate-write-and-exit [out in]
  {:doc "args => [.dot-file .clj-automaton-file]"}
  (dfa-write (read-automaton in) out))
;; (write-dot-desc "foo" "atest.clj")

(defn -main [& args]
  {:doc "(Empty args cause output of help-string)."}
  (let [coll (parse-cmd-line args)
        m (first coll)
        ;; trailing-argument (second obj)
        help-str (last coll)]
    (cond
      (m :help) (println help-str)
      (m :dot) (read-validate-write-and-exit (m :dot) (m :automaton))
      (m :word) (read-validate-match-and-exit (m :automaton) (m :input))
      (not (false? (m :interactive))) (read-validate-create-and-loop (m :automaton))
      (m :automaton) (validate-and-exit (m :automaton))
      :else (println help-str))))

;;(accepts? (read-automaton "btest.clj") '(1))