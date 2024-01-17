(ns ttt-clojure.board
  (:require [clojure.string :as str]))

(defn size [grid]
  (int (Math/sqrt (count grid))))

(defn rows [grid]
  (partition (size grid) grid))

(defn column [index rows]
  (map #(nth % index) rows))

(defn rows->columns [rows]
  (let [indices (range (count rows))]
    (map #(column % rows) indices)))

(defn columns [grid]
  (rows->columns (rows grid)))

(defn back-diagonal [grid]
  (let [size (size grid)
        indices (range size)]
    (vec (map #(nth grid (* % (inc size))) indices))))

(defn front-diagonal [grid]
  (let [size (size grid)
        indices (map inc (range size))]
    (vec (map #(nth grid (* % (- size 1))) indices))))

(defn diagonals [grid]
  (conj [(back-diagonal grid)]
        (front-diagonal grid)))

(defn separate [row]
  (str/join " | " row))

(defn display [grid]
  (->> grid
       rows
       (map separate)
       (str/join "\n")))

(defn winning-combo? [grid combo letter]
  (every? #(= (nth grid %) letter) combo))


(defn winning-lines? [letter lines]
  (some #(every? #{letter} %) lines))

(defn winner? [grid letter]
  (or (winning-lines? letter (rows grid))
      (winning-lines? letter (diagonals grid))
      (winning-lines? letter (columns grid))))


(defn x-wins [grid] (winner? grid "X"))
(defn o-wins [grid] (winner? grid "O"))
(defn tie [grid] (not-any? integer? grid))
