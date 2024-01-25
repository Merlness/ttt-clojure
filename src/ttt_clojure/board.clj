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

(defn separate [row]
  (str/join " | " row))

(defn separate-3-3 [grid]
  (partition 9 grid))
;change this

(defn display [grid]
  (->> grid
       rows
       (map separate)
       (str/join "\n")))

(defn display-3-3 [grid]
  (->> grid
       separate-3-3
       (map display)
       (str/join "\n\n")))

(defn winning-lines? [letter lines]
  (some #(every? #{letter} %) lines))

(defn winner? [grid letter] ; [game player]
  ; player {:token "X" :kind :ai :human}
  ; game {:size 4 :dimensions 2 :board [0 1 2 3]}
  (or (winning-lines? letter (rows grid))
      (winning-lines? letter (diagonals grid))
      (winning-lines? letter (columns grid))))

(defn winning-combo? [grid combo letter]
  (every? #(= (nth grid %) letter) combo))

(defn winner?-3d [grid letter]
  (let [faces (partition 9 grid)]
    (or
      (some #(winner? % letter) faces)
      (some #(winning-combo? grid % letter) edge-cases-3d)
      (winning-lines? letter (same-space-all-faces grid))
      (winning-lines? letter (back-diagonal-through grid))
      (winning-lines? letter (back-diagonal-across grid))
      (winning-lines? letter (front-diagonal-across grid))
      (winning-lines? letter (front-diagonal-through grid)))))

(defn two-dimensional? [board]
  (> 17 (count board)))

(defn- player-wins? [player game]
  (winner? game player))

(defn x-wins [grid]
  (if (two-dimensional? grid)
   (winner? grid "X")
   (winner?-3d grid "X")))

(defn o-wins [grid]
  (if (two-dimensional? grid)
   (winner? grid "O")
   (winner?-3d grid "O")))
;multi methods, deftypes/protocols
;koans

(defn tie [grid] (not-any? integer? grid))

; size= (cuberoot (count grid)) =3
;same space all faces
; 0<=i<=(size^2-1)
;index i + (size^2)*0, i + (size^2)*1, i + (size^2)*2

;(comment
;  diagonally across all faces-ordered
;places [1 11 21] index [0 10 20] 0 + 10*0, 0 + 10*1, 0 + 10*2
;index 4x4 ^ [0 17 34 51]
;places [4 14 24] index [3 13 23] 3 + 10*0, 3 + 10*1, 3 + 10*2
;places [7 17 27] index [6 16 26] 6 + 10*0, 6 + 10*1, 6 + 10*2
;
; size= (cuberoot (count grid)) =3
;
;[(size*0 + (size^2+1)*0) (size*0 + (size^2+1)*1) (size*0 + (size^2+1)*2)]
;[(size*1 + (size^2+1)*0) (size*1 + (size^2+1)*1) (size*1 + (size^2+1)*2)]
;[(size*2 + (size^2+1)*0) (size*2 + (size^2+1)*1) (size*2 + (size^2+1)*2)])
;;
;
;(comment
;places [3 11 19] index [2 10 18] 2 + 8*0, 2 + 8*1, 2 + 8*2
;index 4x4 ^ [3 18 33 48]
;places [6 14 22] index [5 13 21] 5 + 8*0, 5 + 8*1, 5 + 8*2
;places [9 17 25] index [8 16 24] 8 + 8*0, 8 + 8*1, 8 + 8*2
;[(size*1-1 + (size^2-1)*0) ((size*1)-1 + (size^2-1)*1) ((size*1)-1 + (size^2-1)*2)]
;[(size*2-1 + (size^2-1)*0) ((size*2)-1 + (size^2-1)*1) ((size*2)-1 + (size^2-1)*2)]
;[(size*3-1 + (size^2-1)*0) ((size*3)-1 + (size^2-1)*1) ((size*3)-1 + (size^2-1)*2)])


;diagonally across front-side faces
;places [1 13 25] index [0 12 24] 3*0, 3*4, 3*8
;index 4x4 ^ [0 20 40 60]
;places [2 14 26] index [1 13 25]
;places [3 15 27] index [2 14 26] 2 + 3*0, 2 + 3*4, 2 + 3*8

; [(0 + (size * (size+1) * 0))   (0 + (size * (size+1) * 1))   (0 + (size * (size+1) * 2))]
; [(1 + (size * (size+1) * 0))   (1 + (size * (size+1) * 1))   (1 + (size * (size+1) * 2))]
; [(2 + (size * (size+1) * 0))   (2 + (size * (size+1) * 1))   (2 + (size * (size+1) * 2))]


;diagonally across back-side faces

;places [7 13 19] index [6 12 18] 0 + 3*2* 1, 0+ 3*2* 2, 0 + 3*2* 3
;index 4x4 ^ [12 24 36 48] 0 + size(size-1)*1
;places [8 14 20] index [7 13 19] 1 + 3*2* 1, 1+ 3*2* 2, 1 + 3*2* 3
;places [9 15 21] index [8 14 20] 2 + 3*2* 1, 2+ 3*2* 2, 2 + 3*2* 3

;[(0 + size(size-1)*1) (0 + size(size-1)*2) (0 + size(size-1)*3)]
;[(1 + size(size-1)*1) (1 + size(size-1)*2) (1 + size(size-1)*3)]
;[(2 + size(size-1)*1) (2 + size(size-1)*2) (2 + size(size-1)*3)]

;diagonally through the middle
;places [1 14 27] index [0 13 26] 0 + 13*0, 0 + 13*1, 0 + 13*2
;                      [0 21 42 63] 0 + size^3/(size-1)*0,
;places [3 14 25] index [2 13 24] 2 + 11*0, 2 + 11*1, 2 + 11*2

;places [7 14 21] index [6 13 20] 6 + 7*0, 6 + 7*1, 6 + 7*2
;                     [12 25 38 51]
;places [9 14 19] index [8 13 18] 8 + 5*0, 8 + 5*1, 8 + 5*2

;[((size-size) + 0*((size^2)+(size +1))    ((size-size) + 1*((size^2)+(size +1))     ((size-size) + 2*((size^2)+(size +1))]
;[((size-1) + 0*((size^2)+(size -1))       ((size-1)+ 1*((size^2)+(size -1))         ((size-1) + 2*((size^2)+(size -1))]

;[((size^2-size) + 0*((size^2)-(size -1))  ((size^2-size) + 1*((size^2)-(size -1))  ((size^2-size) + 2*((size^2)-(size -1))]
;[((size^2-1) + 0*((size^2)-(size +1))     ((size^2-1) + 1*((size^2)-(size +1))      ((size^2-1) + 2*((size^2)-(size +1))]
