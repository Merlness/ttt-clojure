(ns ttt-clojure.main
  (:require [ttt-clojure.game-modes :as gm]
            [ttt-clojure.board :as board]
            [ttt-clojure.data :as data]
            [ttt-clojure.ui :as ui]))

(defn start-new-game [game-id]
  (let [board (gm/board-size)
        player-1 (gm/create-player 1 nil)
        player-2 (gm/create-player 2 (:token player-1))
        game {:game-id game-id :player-1? true :board board :player-1 player-1 :player-2 player-2}]
    [game game-id]))

(defn continue-previous-game [game input-id]
  (ui/print-previous-moves-game game input-id)
  [game input-id])

(defn possible-to-continue? [game] (and game (not (board/game-over? game))))
(defn continue-last-game? [last-game] (and (possible-to-continue? last-game) (ui/continue-last-game?)))

(defn game-by-id [id]
  (->> id
       data/game-history-by-id
       last))

(defn continue-game? [input-id]
  (let [requested-game (game-by-id input-id)
        last-game (data/get-last-game)
        new-game-id (data/get-next-game-id)
        last-id (data/last-game-id)]
    (cond
      (possible-to-continue? requested-game)
      (continue-previous-game requested-game input-id)

      (continue-last-game? last-game)
      (continue-previous-game last-game last-id)

      :else
      (start-new-game new-game-id))))

(defn -main [& [game-id]]
  (let [game-id (when game-id (read-string game-id))
        [game id] (continue-game? game-id)]
    (ui/print-id-and-empty-board id (:board game))
    (loop [game game]
      (let [game (gm/play-round game)]
        (if (board/game-over? game)
          (gm/complete-game game)
          (recur game))))))
