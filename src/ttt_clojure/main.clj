(ns ttt-clojure.main
  (:require [ttt-clojure.game-modes :as gm]
            [ttt-clojure.board :as board]
            [ttt-clojure.data :as data]
            [ttt-clojure.ui :as ui]))

(defn start-new-game [game-id]
  (let [board (gm/board-size)
        player-1 (gm/create-player 1 nil)
        player-2 (gm/create-player 2 (:token player-1))]
    {:game-id game-id :player-1? true :board board :player-1 player-1 :player-2 player-2}))

(defn continue-previous-game [game input-id]
 (let [game (last game)]
   (ui/print-previous-moves input-id)
  (ui/print-resume-game game)
   game))

;(defn continue-game? [input-id]
;  (let [game (data/get-game-by-id input-id)
;        possible? (and game (not (board/game-over? (last game))))
;        last-game (data/get-last-game)
;        continue? (and (not (board/game-over? last-game)) (ui/continue-last-game?))
;        ;input-game (println "input game over?" (not (board/game-over? (last game))))
;        ;ig (println "input game" (last game))
;        ;
;        ;last-game? (println "last game? " last-game)
;        ;
;        ;con?  (println "last game over?" (board/game-over? (last last-game)))
;        new-game-id (data/get-next-game-id)
;        last-game-id (data/last-game-id)]
;    (if continue?
;      [(continue-previous-game game input-id) input-id]
;      [(start-new-game new-game-id) new-game-id]
;      ))
;  #_(cond
;      possible? [(continue-previous-game game input-id) input-id]
;      continue? [(continue-previous-game last-game last-game-id) last-game-id]
;      :else [(start-new-game new-game-id) new-game-id]
;      )
;  ))

(defn continue-game? [input-id]
  (let [game (data/get-game-by-id input-id)
        possible? (and game (not (board/game-over? (last game))))
        continue? (or possible? (ui/continue-last-game?))
        new-game-id (data/get-next-game-id)]
    (if continue?
      [(continue-previous-game game input-id) input-id]
      [(start-new-game new-game-id) new-game-id])))

(defn -main [& args]
  (let [game-id (if args (read-string (first args)) 100000)
        [game id] (continue-game? game-id)]
    (ui/print-id-and-empty-board id (:board game))
    (loop [game game]
      (let [game (gm/play-round game)]
        (if (board/game-over? game)
          (gm/complete-game game)
          (recur game))))))
