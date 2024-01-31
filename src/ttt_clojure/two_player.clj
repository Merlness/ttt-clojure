(ns ttt-clojure.two-player
  (:require [ttt-clojure.ui :as ui]
            [ttt-clojure.board :as board]))


(defn two-humans [board]
  (let [x-o (ui/get-user-x-o)
        display board/display]

    (loop [grid board
           X? x-o]
      (ui/print-board grid display)
      (let [new-grid (ui/update-board grid X?)]
        (if (ui/endgame-result new-grid)
          (ui/print-end new-grid display)
          (recur new-grid (not X?)))))))

;(defn comp-vs-comp [board difficulty-1 difficulty-2]
;  (loop [grid board
;         x-turn? true]
;    (let [move (if x-turn?
;                 (difficulty-1 grid)
;                 (difficulty-2 grid))
;         new-grid (comp-move-statement x-turn? grid move)]
;      (ui/print-board new-grid board/display)
;      (if (not (ui/endgame-result new-grid))
;        (recur new-grid (not x-turn?))
;        (ui/print-end-computer new-grid)))))
;
;(defn ai [board difficulty]
;  (let [user-token (ui/get-user-vs-ai-x-o)
;        difficulty (hard-ai-x-o difficulty user-token)]
;    (loop [grid board
;           comp-turn? (ui/start-first? board)]
;      (let [move (difficulty grid)
;   new-grid (grid-after-comp comp-turn? grid move user-token)]
;        (ui/print-board new-grid board/display)
;        (if (not (ui/endgame-result new-grid))
;          (recur new-grid (not comp-turn?))
;          (ui/print-end-computer new-grid))))))

;(defn play-game [board player-1 player-2]
;  (loop [grid board
;         turn? true]
;    (let [move (if turn?
;                 (player-1 grid)
;                 (player-2 grid))
;          new-grid (apply-move turn? grid move)]
;
;      (ui/print-board new-grid board/display)
;      (if (not (ui/endgame-result new-grid))
;        (recur new-grid (not turn?))
;        (ui/print-end-computer new-grid)))))
;
;(defn human-move [grid]
;  )
;
;(defn ai-move [grid]
;  )

;(defn grid-after-comp [comp-turn? grid move x-o]
;  (let [[comp-token user-token] (if (= "O" x-o)
;                                  ["X" false] ["O" true])]
;    (if comp-turn?
;      (do (ui/my-turn-statement)
;          (ui/place-xo grid move comp-token))
;      (ui/update-board grid user-token))))

;(defn comp-move-statement [x-turn? grid move]
;  (let [[letter statement] (if x-turn?
;                             ["X" (ui/comp-statement 1)]
;                             ["O" (ui/comp-statement 2)])]
;    statement
;(ui/place-xo grid move letter)))

;(defn apply-move [turn? grid move ]
;  (let [[token1 token2] (if turn?
;                          ["X" false] ["O" true])
;        [letter statement] (if turn?
;                             [token1 (ui/my-turn-statement)]
;                             [token2 (ui/comp-statement)])]
;    (ui/place-xo grid move letter)))
