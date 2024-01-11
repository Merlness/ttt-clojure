(ns ttt-clojure.board
  (:require [clojure.string :as str]))

(defn separate [row]
  (str/join " | " row))

(defn display [grid]
  (let [columns (int (Math/sqrt (count grid)))]
    (->> grid
         (partition columns)
         (map separate)
         (str/join "\n"))))

(defn winning-combo? [grid combo letter]
  (every? #(= (nth grid %) letter) combo))

(def winning-combos
  [[0 1 2]
   [3 4 5]
   [6 7 8]
   [0 3 6]
   [1 4 7]
   [2 5 8]
   [0 4 8]
   [2 4 6]])

(def winning-combos-4-4
  [[0 1 2 3]
   [4 5 6 7]
   [8 9 10 11]
   [12 13 14 15]
   [0 4 8 12]
   [1 5 9 13]
   [2 6 10 14]
   [3 7 11 15]
   [0 5 10 15]
   [3 6 9 12]])

(defn winner? [grid letter]
  (if (= 9 (count grid))
   (some #(winning-combo? grid % letter) winning-combos)
  (some #(winning-combo? grid % letter) winning-combos-4-4)))

(defn x-wins [grid] (winner? grid "X"))
(defn o-wins [grid] (winner? grid "O"))
(defn tie [grid] (not-any? integer? grid))
