(ns automata.dfa-printing
  (:use [automata.automata])
  (:gen-class))

;; :TODO - this file could be cleaned up by segmenting partial strings further!

(def virtual-start-state "VS")

(def personal-comment "// Created with *don't have a name for this, yet*
// 
// author: Markus Rother
// contact: informatik@markusrother.de
// created: 09/2012
// url: https://github.com/941design/automata.git
// license: MIT
//
// Do not hesitate to question or comment.
")

(def dot-template "
%s
digraph %s {
\n\trankdir = LR;
\n\tnode [style = invis]; // invisible virtual start state
\t\t%s;
\n\tnode [shape = doublecircle style = solid]; // accepting state layout
\t\t%s
\n\tnode [shape = circle style = solid]; // default state layout
\n\tedge [arrowtail = none arrowhead = open dir = forward];
\n%s // invisible edge to 'real' start state
%s
\n}\n")

(defn print-state [label]
  (cond
    (keyword? label) (subs (str label) 1)
    (string? label) label
    :else "unrecognized label type"))

(defn transition-maps [desc]
  {:doc "Groups transitions by to-, and from-state then merges attributes."}
  (let [;; compares (to-state, from-state) tuples to group by:
        pred (fn [td] (list (first td) (last td)))
        ;; receives transition descriptions and merges attributes:
        coll-attr (fn [seq] (apply concat (map second seq)))
        ;; turns '((fs ts) (attr)) into map:
        create-map (fn [tuple] {:from-state (first (first tuple))
                                :to-state (second (first tuple))
                                :attributes (coll-attr (second tuple))})]
    ;; creates list of maps:
    (map create-map (group-by pred (:transitions desc)))))
;; (transition-maps {:transitions #{[:s1 [0] :s2]}})

(defn format-transition-maps [lst]
  (apply str (map (fn [m] (format "\n\t\t%s -> %s [label = \"%s\"];"
                                  (print-state (:from-state m))
                                  (print-state (:to-state m))
                                  (apply str
                                         (first (:attributes m))
                                         (map (fn [e] (format ",%s" e))
                                              (rest (:attributes m))))))
                  lst)))
;; (format-transition-maps (transition-maps dfa))

(defn format-virtual-start [ss]
  {:doc :TODO}
  (format "\n\t%s -> %s;" (print-state virtual-start-state) (print-state ss)))

(defn format-accepting-states [s]
  {:doc :TODO}
  (apply str (map #(format "%s; " (print-state %)) s)))
;; (format-accepting-states #{:s1 :s2})

(defn dfa-write
  {:doc :TODO}
  ([dfa] (dfa-write dfa (:name dfa)))
  ([dfa file-name]
     (spit file-name
           (format dot-template
                   personal-comment
                   (:name dfa)          
                   virtual-start-state
                   (format-accepting-states (:accepting-states dfa))
                   (format-virtual-start (:start-state dfa))
                   (format-transition-maps (transition-maps dfa))))))
