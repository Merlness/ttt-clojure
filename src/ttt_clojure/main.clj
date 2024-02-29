(ns ttt-clojure.main
  (:require [ttt-clojure.game :as game]
            [ttt-clojure.game-modes :as gm]
            [ttt-clojure.board :as board]
            [ttt-clojure.data :as data]
            [ttt-clojure.ui :as ui]))

(defn start-new-game [game-id]
  (let [size (ui/get-game-board)
        player-1 (gm/create-player 1 nil)
        player-2 (gm/create-player 2 (:token player-1))
        game {:game-id  game-id
              :player-1 player-1 :player-2 player-2
              :size     size :moves []}]
    [game game-id]))

(defn continue-previous-game [game input-id]
  ; TODO: Test me
  (ui/print-resume-game game)
  [game input-id])

(defn possible-to-continue? [game]
  (let [new-board (game/convert-moves-to-board game)]
    ; TODO: Test me
    (and game (not (board/game-over? new-board game)))
    ;(boolean game) ; this shouldn't pass
    ))

(defn continue-last-game? [last-game]
  (and
    (possible-to-continue? last-game)
    (ui/continue-last-game?)))

(defn replay? [requested-game]
  (let [
        ;requested-game (data/get-game-by-id input-id db-type)
        new-board (game/convert-moves-to-board requested-game)]
    (and requested-game (board/game-over? new-board requested-game))))

(defn replay [requested-game]
  (do
    (ui/print-previous-player-kinds requested-game)
    (ui/print-previous-moves (game/creates-board-per-move requested-game))
    (ui/print-end requested-game)))

(defn continue-game? [input-id db-type]
  (let [requested-game (data/get-game-by-id input-id db-type)
        last-game (data/get-last-game db-type)
        new-game-id (data/get-next-game-id db-type)
        last-id (data/last-game-id db-type)
        ]
    (cond
      (possible-to-continue? requested-game)
      (continue-previous-game requested-game input-id)

      (continue-last-game? last-game)
      (continue-previous-game last-game last-id)

      :else
      (start-new-game new-game-id))))

(defn game-loop [game-id db-type]
  (let [[game id] (continue-game? game-id db-type)]
    (ui/print-id-and-board id game)
    (loop [game game]
      (let [game (gm/play-round db-type game)
            new-board (game/convert-moves-to-board game)]
        (if (board/game-over? new-board game)
          (ui/print-end game)
          (recur game))))))

(defn -main [& args]
  (let [[game-id DB] args
        game-id (when game-id (read-string game-id))
        db-type (if (= "--jsondb" (last args)) :json :edn)
        ; configure db-type here send only to data! keep as atom in data
        requested-game (data/get-game-by-id game-id db-type)]
    (if (replay? requested-game)
      (replay requested-game)
      (game-loop game-id db-type))))
