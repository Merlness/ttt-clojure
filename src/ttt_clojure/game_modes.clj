(ns ttt-clojure.game-modes
  (:require [ttt-clojure.ui :as ui]
            [ttt-clojure.data :as save]
            [ttt-clojure.board :as board]
            [ttt-clojure.computer :as comp]))


(defn board-size [size]
  (case size
    :3x3 (range 1 10)
    :4x4 (range 1 17)
    :3x3x3 (range 1 28)))

(defn grid-after-move
  ([move {:keys [board player-1 player-2 moves]}]
   (grid-after-move move board (:token player-1) (:token player-2) moves))
  ([move grid token-1 token-2 moves]
   (let [[letter player] (if (board/player1? moves)
                           [token-1 1]
                           [token-2 2])]
     (ui/player-statement player)
     (ui/place-xo grid move letter))))

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
(defmethod get-move :human [_player _opponent grid] (ui/get-move grid))
(defmethod get-move :ai [player opponent grid]
  (comp/ai-move grid (:token player) (:token opponent) (:difficulty player)))

(defn play-round [{:keys [player-1 player-2 board moves] :as game}]
  (let [[player opponent] (if (board/player1? moves) [player-1 player-2] [player-2 player-1])
        move (get-move player opponent board)
        new-grid (grid-after-move move game)
        game (assoc game :board new-grid
                         :moves (conj moves move))]         ; :moves (conj moves move))
    (save/save-edn game)
    (ui/print-board new-grid)
    game))

(defn complete-game
  ([{:keys [board player-1 player-2]}]
   (complete-game board (:token player-1) (:token player-2)))
  ([grid token-1 token-2]
   (ui/print-end grid token-1 token-2)))


;notes
;stories are taking longer, think about
; always reformat code
; don't comment out tests
; limit commented out code
;delete unnecessary code
