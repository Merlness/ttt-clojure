(ns ttt-clojure.main
  (:require [ttt-clojure.game-modes :as gm]
            [ttt-clojure.board :as board]
            [ttt-clojure.ui :as ui]))

(defn start-new-game []
  (let [board (gm/board-size)
        player-1 (gm/create-player 1 nil)
        player-2 (gm/create-player 2 (:token player-1))]
    {:player-1? true :board board :player-1 player-1 :player-2 player-2}))

(defn continue-last-game? [last-game]
  (and last-game
       (not (board/game-over? last-game))
       (ui/continue-last-game?)))

(defn -main []
  (let [last-game (gm/get-last-game)
        game (if (continue-last-game? last-game)
               last-game
               (start-new-game))]
    (ui/print-board (:board game))
    (loop [game game]
      (let [game (gm/play-round game)]
        (if (board/game-over? game)
          (gm/complete-game game)
          (recur game))))))
