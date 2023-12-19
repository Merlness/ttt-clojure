(ns ttt-clojure.board
  (:require [clojure.string :as str]))

(defn separate [row]
  (str/join " | " row))

(defn display [grid]
  (->> grid
       (partition 3)
       (map separate)
       (str/join "\n")))

(defn winning-combo? [grid combo letter]
  (every? #(= (nth grid %) letter) combo))

(def winning-combos
  [
   [0 1 2]
   [3 4 5]
   [6 7 8]
   [0 3 6]
   [1 4 7]
   [2 5 8]
   [0 4 8]
   [2 4 6]
   ])

(defn winner? [grid letter]
  (some #(winning-combo? grid % letter) winning-combos))

(defn x-wins [grid] (winner? grid "X"))
(defn o-wins [grid] (winner? grid "O"))
(defn tie [grid] (not-any? integer? grid))
