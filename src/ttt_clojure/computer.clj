(ns ttt-clojure.computer
  (:require [ttt-clojure.ui :as ui])
  (:require [ttt-clojure.minimax :as mm])
  (:require [ttt-clojure.easy-comp :as ec]))

;multimethods
;single play game function

(defn grid-after-comp [x-turn? grid move]
  (if x-turn?
    (do (ui/my-turn-statement)
        (ui/place-xo grid move "X"))
    (ui/update-board grid false)))

;(defn medium-difficulty [grid]
;  (let [move-count (count (remove number? grid))
;        hard-ai? (or (< move-count 5) (zero? (rand-int 2)))]
;    (if hard-ai?
;      (mm/next-move grid)
;      (ec/place-easy-move grid))))

(defn medium-difficulty [next-move grid]
  (let [move-count (count (remove number? grid))
        hard-ai? (or (< move-count 5) (zero? (rand-int 2)))]
    (if hard-ai?
      (next-move grid)
      (ec/place-easy-move grid))))

;(let [game {:board [1 2 3 4 5 6 7 8 9]
;            :mode :pvp ; :pvc :cvc
;            :x    :human
;            :o    :hard
;            :turn :x ; :o
;            }])
;
;(defmulti next-move [] )
;(defmethod next-move :human [{:keys [board x o]}]
;  4)

(defn ai [difficulty]
  (loop [grid [1 2 3 4 5 6 7 8 9]
         x-turn? (ui/start-first?)]
    (let [move (difficulty grid)
          new-grid (grid-after-comp x-turn? grid move)]
      (ui/print-board new-grid)
      (if (not (ui/endgame-result new-grid))
        (recur new-grid (not x-turn?))
        (ui/print-end-computer new-grid)))))

; map, reduce, filter, ...
;(count (filter (complement number?) [0 1 2 3 4 5 6 7 8 9]))

(defn easy-ai []
  (ai ec/place-easy-move))

(defn hard-ai []
  (ai mm/next-move))

(defn medium-ai []
  (ai #(medium-difficulty mm/next-move %)))


(defn comp-move-statement [x-turn? grid move]
  (let [[letter statement] (if x-turn?
                       ["X" ui/comp-1-statement]
                       ["O" ui/comp-2-statement])]
    (do (statement)
        (ui/place-xo grid move letter))))

(defn comp-vs-comp [difficulty-1 difficulty-2]
  (loop [grid [1 2 3 4 5 6 7 8 9]
         x-turn? true]
    (let [move (if x-turn?
                 (difficulty-1 grid)
                 (difficulty-2 grid))
          new-grid (comp-move-statement x-turn? grid move)]
      (ui/print-board new-grid)
      (if (not (ui/endgame-result new-grid))
        (recur new-grid (not x-turn?))
        (ui/print-end-computer new-grid)))))

(defn get-difficulty [next-move]
  (let [user-input (ui/get-user-input-difficulty)]
    (cond
      (= user-input "1") ec/place-easy-move
      (= user-input "2") #(medium-difficulty next-move %) ; medium-difficulty-2
      (= user-input "3") next-move
      :else (recur next-move))))

(defn ai-vs-ai []
    (let [diff-1 (do (ui/welcome-c-vs-c)
                     (get-difficulty mm/next-move))
          diff-2 (do (ui/second-difficulty-message)
                     (get-difficulty mm/next-move-2))]
      (comp-vs-comp diff-1 diff-2)))



;(defn comp-helper [x-turn? grid move]
;  (if x-turn?
;    (do (ui/comp-1-statement)
;        (ui/place-xo grid move "X"))
;    (do (ui/comp-2-statement)
;;        (ui/place-xo grid move "O"))))




#_(defn ai-vs-ai []
    (let [diff-1 (do (ui/welcome-c-vs-c)
                     (let [user-input (ui/get-user-input-difficulty)]
                       (cond
                         (= user-input "1") ec/place-easy-move
                         (= user-input "2") medium-difficulty
                         (= user-input "3") mm/next-move
                         :else (recur))))
          diff-2 (do (ui/second-difficulty-message)
                     (let [user-input (ui/get-user-input-difficulty)]
                       (cond
                         (= user-input "1") ec/place-easy-move
                         (= user-input "2") medium-difficulty
                         (= user-input "3") mm/next-move
                         :else (recur))))]
      (comp-vs-comp diff-1 diff-2)))
