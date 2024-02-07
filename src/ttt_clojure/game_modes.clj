(ns ttt-clojure.game-modes
  (:require [ttt-clojure.ui :as ui]
            [ttt-clojure.save-game :as save]
            [ttt-clojure.computer :as comp]
            [clojure.edn :as edn]))

(defn get-next-game-id []
  (let [log-data (slurp "log.edn")
        data (edn/read-string log-data)
        game-ids (map :game-id data)
        max-game-id (if (empty? game-ids) 0 (apply max game-ids))]
    (inc max-game-id)))

(def game-id (atom (get-next-game-id)))

(defn get-last-game []
  (let [log-data (slurp "log.edn")
        data (edn/read-string log-data)]
    (last data)))

(defn board-size []
  (case (ui/get-game-board)
    :3x3 (range 1 10)
    :4x4 (range 1 17)
    :3x3x3 (range 1 28)))

(defn grid-after-move
  ([move {:keys [player-1? board player-1 player-2]}]
   (grid-after-move move player-1? board (:token player-1) (:token player-2)))
  ([move player-1? grid token-1 token-2]
   (let [[letter player] (if player-1?
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

(defn save [game]
  (let [player-1? (:player-1? game)
        player-1 (:player-1 game)
        player-2 (:player-2 game)
        grid (:board game)]
    (save/save-round @game-id player-1? player-1 player-2 grid)))

(defn play-round [{:keys [player-1? player-1 player-2 board] :as game}]
  (let [[player opponent] (if player-1? [player-1 player-2] [player-2 player-1])
        move (get-move player opponent board)
        new-grid (grid-after-move move game)
        game (assoc game :board new-grid :player-1? (not (:player-1? game)))]
    (save game)
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
; dont comment out tests
; limit commented out code
;delete unnecessary code
