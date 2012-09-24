(ns automata.test.dfa-test
  (:use [automata.automata])
  (:use [automata.dfa])  
  (:use [clojure.test]))

;; (load-file "src/automata/automata.clj")
;; (load-file "src/automata/dfa.clj")
;; (load-file "test/automata/test/dfa_test.clj")

(deftest transitions
  (create-transition-functions #{'(:s1 [0] :s1)
                                 '(:s1 [1] :s2)
                                 '(:s2 [0] :s2)
                                 '(:s2 [1] :s1)
                                 '(:s3 [0 1] :s1)
                                 })
  (testing "Transitions"
    (is (= (follow :s1 '(0 1)) :s2))
    (is (= (follow :s1 '(0)) :s1))
    (is (= (follow :s1 '(1)) :s2))
    (is (= (follow :s1 '(0 1 1)) :s1))
    (is (= (follow :s3 '(0)) :s1))
    (is (= (follow :s3 '(1)) :s1))))

(deftest one-one-test
  (let [one-one {:name "one-one"
                 :states #{:s1 :s2}
                 :alphabet #{0 1}
                 :start-state :s1
                 :accepting-states #{:s2}
                 :transitions #{'(:s1 [0] :s1)
                                '(:s1 [1] :s2)
                                '(:s2 [0] :s2)}}]
    (dfa-create one-one)
    (testing "Validation"
      (is (empty? (dfa-validation-results one-one))))
    (testing "Accepts"
      (is (accepts? one-one '(1))))
    (testing "Rejects"
      (is (not (accepts? one-one '())))
      (is (not (accepts? one-one '(0))))
      (is (not (accepts? one-one '(0 0))))
      (is (not (accepts? one-one '(0 1 1)))))))

(deftest even-ones-test
  (let [even-ones {:name "even-ones"
                   :states #{:s1 :s2}
                   :alphabet #{0 1}
                   :start-state :s1
                   :accepting-states #{:s1}
                   :transitions #{'(:s1 [0] :s1)
                                  '(:s1 [1] :s2)
                                  '(:s2 [0] :s2)
                                  '(:s2 [1] :s1)}}]
    (dfa-create even-ones)
    (testing "Validation"
      (is (empty? (dfa-validation-results even-ones))))
    (testing "Accepts"
      (is (accepts? even-ones '()))
      (is (accepts? even-ones '(0 0 0)))
      (is (accepts? even-ones '(1 0 1)))
      (is (accepts? even-ones '(0 1 0 1 0 0)))
      (is (accepts? even-ones '(1 1 1 1))))
    (testing "Rejects"
      (is (not (accepts? even-ones '(0 1))))
      (is (not (accepts? even-ones '(0 1 1 1 0))))
      (is (not (accepts? even-ones '(1 0))))
      (is (not (accepts? even-ones '(1 1 1 0 0))))
      (is (not (accepts? even-ones '(1 1 1 1 1)))))))

(deftest return-on-any
  {:doc "tests multiple attributes in transition"}
  (let [return-on-any {:name "return-on-any"
                       :states #{:s1 :s2}
                       :alphabet #{0 1}
                       :start-state :s1
                       :accepting-states #{:s1}
                       :transitions #{'(:s1 [0] :s1)
                                      '(:s1 [1] :s2)
                                      '(:s2 [0 1] :s1)}}]
    (dfa-create return-on-any)
    (testing "Validation"
      (is (empty? (dfa-validation-results return-on-any))))
    (testing "Accepts"
      (is (accepts? return-on-any '()))
      (is (accepts? return-on-any '(0)))
      (is (accepts? return-on-any '(0 0 0 0)))
      (is (accepts? return-on-any '(1 1 0 0 0))))
    (testing "Rejects"
      (is (not (accepts? return-on-any '(1))))
      (is (not (accepts? return-on-any '(1 0 1))))
      (is (not (accepts? return-on-any '(1 1 1)))))))

;;--------------------------------------------------------
;;
;; (doseq [n (all-ns)] (print (str n "\n")))
;; (use '[clojure.string :only (join split)])
;; (print (clojure.string/join "\n" (sort (ns-map *ns*))))

