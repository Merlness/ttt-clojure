(ns ttt-clojure.main
  (:require [ttt-clojure.game :as game]
            [ttt-clojure.game-modes :as gm]
            [ttt-clojure.board :as board]
            [ttt-clojure.data :as data]
            [ttt-clojure.ui :as ui]))

(defn select-db [db]
  (cond
    (= "--jsondb" db) :json
    (= "--psldb" db) :psql
    :else :edn))

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
  (->> game
       game/creates-board-per-move
       ui/print-previous-moves)
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
  (let [new-board (game/convert-moves-to-board requested-game)]
    (and requested-game (board/game-over? new-board requested-game))))

(defn replay [requested-game]
  (ui/print-previous-player-kinds requested-game)
  (->> requested-game
       game/creates-board-per-move
       ui/print-previous-moves)
  (ui/print-end requested-game))

(defn continue-game? [input-id]
  (let [requested-game (data/get-game-by-id input-id)
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

(defn game-loop [game-id db-type]
  (let [[game id] (continue-game? game-id)]
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
        db-type (select-db (last args))
        _load-db (data/load-db db-type)
        requested-game (data/get-game-by-id game-id)]
    (if (replay? requested-game)
      (replay requested-game)
      (game-loop game-id db-type))))
