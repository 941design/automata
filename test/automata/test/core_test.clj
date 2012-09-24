(ns automata.test.core-test
  (:use [automata.core])
  (:use [clojure.test]))

;;(load-file "src/automata/core_test.clj")

(deftest cmd-line-test
  (testing "Command line"
    (is (string? (last (parse-cmd-line ["-h"]))))
    (is (= "assert" ((first (parse-cmd-line ["-a" "assert"])) :automaton)))))
