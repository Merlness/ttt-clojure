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
    (vec (map
           #(nth grid (* % (inc size))) indices))))

(defn front-diagonal [grid]
  (let [size (size grid)
        indices (map inc (range size))]
    (vec (map
           #(nth grid (* % (- size 1)))
           indices))))

;(defn diagonal [grid step-fn]
;  (let [size (count grid)
;        indices (if (= step-fn inc)
;                  (range size)
;                  (map inc (range size)))]
;    (vec (map
;           #(nth grid (* % (step-fn size))) indices))))
;
;(defn back-diagonal [grid]
;  (diagonal grid inc))
;
;(defn front-diagonal [grid]
;  (diagonal grid dec))

(defn diagonals [grid]
  (conj [(back-diagonal grid)]
        (front-diagonal grid)))

(defn size-3d [grid]
  (int (Math/cbrt (count grid))))

(defn helper-3d [grid index-equation]
  (let [size (size-3d grid)
        indices (range size)]
    (for [y-index indices]
      (for [x-index indices]
        (nth grid (index-equation y-index x-index size))))))

(defn back-diagonal-through-index [y-index x-index size]
  (+ (* (inc y-index) size) -1 (* (- (Math/pow size 2) 1) x-index)))

(defn back-diagonal-through [grid]
  (helper-3d grid back-diagonal-through-index))

(defn front-diagonal-through-index [y-index x-index size]
  (+ (* y-index size) (* (+ 1 (Math/pow size 2)) x-index)))

(defn front-diagonal-through [grid]
  (helper-3d grid front-diagonal-through-index))

(defn front-diagonal-across-index [y-index x-index size]
  (+ y-index (* size (inc size) x-index)))

(defn front-diagonal-across [grid]
  (helper-3d grid front-diagonal-across-index))

(defn back-diagonal-across-index [y-index x-index size]
  (+ y-index (* size (dec size) (inc x-index))))

(defn back-diagonal-across [grid]
  (helper-3d grid back-diagonal-across-index))

(defn same-space-all-faces [grid]
  (let [size (size-3d grid)
        indices (range (Math/pow size 2))]
    (for [y-index indices]
      (for [x-index (range size)]
        (nth grid (+ y-index (* x-index (Math/pow size 2))))))))

(def edge-cases-3d
  [[0 13 26]
   [2 13 24]
   [6 13 20]
   [8 13 18]])

(defn separate [row] (str/join " | " row))

(defn two-dimensional? [board] (> 17 (count board)))

(defn board-type [board] (if (two-dimensional? board) :2d :3d))

(defmulti display board-type)

(defmethod display :2d [board]
  (->> board
       rows
       (map separate)
       (str/join "\n")))

(defmethod display :3d [board]
  (->> board
       (partition 9)
       (map display)
       (str/join "\n\n")))

(defn winning-lines? [letter lines]
  (some #(every? #{letter} %) lines))

(defn winning-combo? [grid combo letter]
  (every? #(= (nth grid %) letter) combo))

(defmulti winner? (fn [board _letter] (board-type board)))

(defmethod winner? :2d [grid letter]
  (or (winning-lines? letter (rows grid))
      (winning-lines? letter (diagonals grid))
      (winning-lines? letter (columns grid))))

(defmethod winner? :3d [grid letter]
  (let [faces (partition 9 grid)]
    (or
      (some #(winner? % letter) faces)
      (some #(winning-combo? grid % letter) edge-cases-3d)
      (winning-lines? letter (same-space-all-faces grid))
      (winning-lines? letter (back-diagonal-through grid))
      (winning-lines? letter (back-diagonal-across grid))
      (winning-lines? letter (front-diagonal-across grid))
      (winning-lines? letter (front-diagonal-through grid)))))
; [game player]
;  ; player {:token "X" :kind :ai :human}
;  ; game {:size 4 :dimensions 2 :board [0 1 2 3]}

(defn x-wins [grid] (winner? grid "X"))
(defn o-wins [grid] (winner? grid "O"))
(defn tie [grid] (not-any? integer? grid))

(defn token-wins [grid token] (winner? grid token))

(defn game-over? [grid max-token min-token]
  (or
    (token-wins grid max-token)
    (token-wins grid min-token)
    (tie grid)))
