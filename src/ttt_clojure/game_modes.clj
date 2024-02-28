(ns ttt-clojure.game-modes
  (:require [ttt-clojure.ui :as ui]
            [ttt-clojure.data :as save]
            [ttt-clojure.game :as game]
            [ttt-clojure.board :as board]
            [ttt-clojure.computer :as comp]))


(defn grid-after-move
  ([move {:keys [player-1 player-2 size moves]}]
   (grid-after-move move (:token player-1) (:token player-2) size moves))
  ([move token-1 token-2 size moves]
   (let [[letter player] (if (board/player1? moves)
                           [token-1 1]
                           [token-2 2])
         new-board (game/convert-moves-to-board token-1 token-2 size moves)
         ]
     (ui/player-statement player)
     (ui/place-xo new-board move letter))))

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

(defmulti get-move (fn [player _opponent _grid] (:kind player))) ;use multi for json or dn
(defmethod get-move :human [_player _opponent grid] (ui/get-move grid))
(defmethod get-move :ai [player opponent grid]
  (comp/ai-move grid (:token player) (:token opponent) (:difficulty player)))

(defn play-round [db-type {:keys [player-1 player-2 moves] :as game}]
  (let [[player opponent] (if (board/player1? moves) [player-1 player-2] [player-2 player-1])
        new-board (game/convert-moves-to-board game)
        move (get-move player opponent new-board)
        new-grid (grid-after-move move game)
        game (assoc game :moves (conj moves move))]         ; :moves (conj moves move))
    (save/save game db-type) ;db
    (ui/print-board new-grid)
    game))

(defn complete-game
  ([{:keys [player-1 player-2 size moves]}]
   (complete-game (:token player-1) (:token player-2) size moves))
  ([token-1 token-2 size moves]
   (let [new-board (game/convert-moves-to-board token-1 token-2 size moves)]
     (ui/print-end new-board token-1 token-2))))


;notes
;stories are taking longer, think about
; always reformat code
; don't comment out tests
; limit commented out code
;delete unnecessary code
