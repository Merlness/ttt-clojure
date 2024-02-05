(ns ttt-clojure.main
  (:require [ttt-clojure.game-modes :as gm]
            [ttt-clojure.board :as board]
            [ttt-clojure.ui :as ui]
            ))

(defn -main []
  (let [board (gm/board-size)
        player-1 (gm/create-player 1 nil)
        player-2 (gm/create-player 2 (:token player-1))
        token-1 (:token player-1)
        token-2 (:token player-2)]
    (ui/print-board board board/display)
    (loop [grid board
           player-1? true]
      (let [new-grid (gm/play-round player-1? player-1 player-2 grid)]
        (if (board/game-over? new-grid token-1 token-2)
          (gm/complete-game new-grid token-1 token-2)
          (recur new-grid (not player-1?)))))))
;(defn -main [] (gm/player-vs-player))
