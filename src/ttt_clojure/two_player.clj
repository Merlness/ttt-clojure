(ns ttt-clojure.two-player
  (:require [ttt-clojure.ui :as ui]
            [ttt-clojure.board :as board]))


(defn two-humans [board]
  (let [x-o (ui/get-user-x-o)
        display (if (board/two-dimensional? board)
                  board/display
                  board/display-3-3)]
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
;          new-grid (comp-move-statement x-turn? grid move)]
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
;            new-grid (grid-after-comp comp-turn? grid move user-token)]
;        (ui/print-board new-grid board/display)
;        (if (not (ui/endgame-result new-grid))
;          (recur new-grid (not comp-turn?))
;          (ui/print-end-computer new-grid))))))
