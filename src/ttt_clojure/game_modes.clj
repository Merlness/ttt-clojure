(ns ttt-clojure.game-modes
  (:require [ttt-clojure.ui :as ui]
            [ttt-clojure.board :as board]
            [ttt-clojure.save-game :as save]
            [ttt-clojure.computer :as comp]))

(defn board-size []
  (case (ui/get-game-board)
    :3x3 (range 1 10)
    :4x4 (range 1 17)
    :3x3x3 (range 1 28)))

(defn grid-after-move [player-1? grid move token-1 token-2]
  (let [[letter player] (if player-1?
                          [token-1 1]
                          [token-2 2])]
    (ui/player-statement player)
    (ui/place-xo grid move letter)))

(defn token-finder [position unavailable-token]
  (if (= position 1)
    (ui/get-player-1-token)
    (ui/get-player-2-token unavailable-token)))

(defn create-player [position unavailable-token]
  (let [kind (ui/get-player position)
        token (token-finder position unavailable-token)
        base-data {:kind  kind
                   :token token}]
    (if (= :ai kind)
      (assoc base-data :difficulty (ui/get-difficulty))
      base-data)))

(defmulti get-move (fn [player _opponent _grid] (:kind player)))

(defmethod get-move :human [_player _opponent grid]
  (ui/get-move grid))

(defmethod get-move :ai [player opponent grid]
  (comp/ai-move grid (:token player) (:token opponent) (:difficulty player)))

(defn play-round [player-1? player-1 player-2 grid]
  (let [[player opponent] (if player-1? [player-1 player-2] [player-2 player-1])
        move (get-move player opponent grid)
        token-1 (:token player-1)
        token-2 (:token player-2)
        new-grid (grid-after-move player-1? grid move token-1 token-2)]
    (save/save-round player-1 player-2 new-grid)
    (ui/print-board new-grid board/display)
    new-grid))

(defn player-vs-player []
  (let [board (board-size)
        player-1 (create-player 1 nil)
        player-2 (create-player 2 (:token player-1))
        token-1 (:token player-1)
        token-2 (:token player-2)]
    (ui/print-board board board/display)
    (loop [grid board
           player-1? true]
      (let [new-grid (play-round player-1? player-1 player-2 grid)]
        (if (board/game-over? new-grid token-1 token-2)
          (ui/print-end new-grid token-1 token-2)
          (recur new-grid (not player-1?)))))))

;notes
;stories are taking longer, think about
; always reformat code
; dont comment out tests
; limit commented out code
;delete unnecessary code
