(ns ttt-clojure.computer
  (:require [ttt-clojure.ui :as ui])
  (:require [ttt-clojure.minimax :as mm])
  (:require [ttt-clojure.easy-comp :as ec]
            [ttt-clojure.board :as board]))

(defn medium-difficulty [next-move grid]
  (let [move-count (count (remove number? grid))
        hard-ai? (or (< move-count 5) (zero? (rand-int 2)))]
    (if hard-ai?
      (next-move grid)
      (ec/place-easy-move grid))))

(defn grid-after-comp [comp-turn? grid move x-o]
  (let [[comp-token user-token] (if (= "O" x-o)
                                  ["X" false] ["O" true])]
    (if comp-turn?
      (do (ui/my-turn-statement)
          (ui/place-xo grid move comp-token))
      (ui/update-board grid user-token))))

(defn hard-ai-x-o [difficulty user-token]
  (if (and (= mm/next-move difficulty)
           (= "X" user-token))
    mm/next-move-2
    difficulty))

;(defmulti display board-type)
;
;(defmethod display :2d [board]
;  (->> board
;       rows
;       (map separate)
;       (str/join "\n")))

(defn next-hard-move [token]
  (if (= token "X") mm/next-move-2 mm/next-move))

(defmulti move (fn [_board _opponent difficulty] difficulty))  ; :easy :medium :hard

(defmethod move :easy [board _opponent _difficulty] (ec/place-easy-move board))

(defmethod move :medium [board opponent _difficulty]
  (let [next-move (next-hard-move opponent)]
    (medium-difficulty next-move board)))

(defmethod move :hard [board opponent _difficulty]
  (let [next-move (next-hard-move opponent)]
    (next-move board)))

(defn human-vs-ai [board difficulty]
  (let [user-token (ui/get-user-vs-ai-x-o)
        ;difficulty (hard-ai-x-o difficulty user-token)
        ]
    (loop [grid board
           comp-turn? (ui/start-first? board)]
      (let [;move (difficulty grid)
            new-move (move grid user-token difficulty)
            new-grid (grid-after-comp comp-turn? grid new-move user-token)]
        (ui/print-board new-grid board/display)
        (if (not (ui/endgame-result new-grid))
          (recur new-grid (not comp-turn?))
          (ui/print-end-computer new-grid))))))
;combine with comp vs comp and hvh

(defn comp-move-statement [x-turn? grid move]
  (let [[letter statement] (if x-turn?
                             ["X" (ui/comp-statement 1)]
                             ["O" (ui/comp-statement 2)])]
    statement
    (ui/place-xo grid move letter)))

(defn comp-vs-comp [board difficulty-1 difficulty-2]
  #_{:board         board
     :active-player :player-1
     :player-1      {:kind :ai :difficulty :hard :token "X"}
     :player-2      {:kind :ai :difficulty :hard :token "O"}} ; game
  (loop [grid board
         x-turn? true
         ; game
         ]
    (let [move (if x-turn?
                 (difficulty-1 grid)
                 (difficulty-2 grid))
          ;move (ai/move :medium board "X")
          ;move (player/move game) ;player/move is a fn or multimethod
          new-grid (comp-move-statement x-turn? grid move)]
      (ui/print-board new-grid board/display)
      (if (not (ui/endgame-result new-grid))
        (recur new-grid (not x-turn?))
        (ui/print-end-computer new-grid)))))
;should be same as human vs human

(defn get-difficulty [next-move]
  (let [user-input (ui/get-user-input-difficulty)]
    (case user-input
      :easy ec/place-easy-move
      :medium #(medium-difficulty next-move %)
      :hard next-move)))

(defn ai-vs-ai [board]
  (let [diff-1 (do (ui/welcome-c-vs-c)
                   (get-difficulty mm/next-move))
        diff-2 (do (ui/second-difficulty-message)
                   (get-difficulty mm/next-move-2))]
    (comp-vs-comp board diff-1 diff-2)))


;4x4 medium difficulty

;multimethods
;single play game function
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



