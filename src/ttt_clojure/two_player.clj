(ns ttt-clojure.two-player
  (:require [ttt-clojure.ui :as ui]
            [ttt-clojure.board :as board]))


(defn two-player [board]
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
