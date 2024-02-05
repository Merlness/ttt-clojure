(ns ttt-clojure.game-modes
  (:require [ttt-clojure.ui :as ui]
            [ttt-clojure.board :as board]
            [ttt-clojure.save-game :as save]
            [ttt-clojure.computer :as comp]
            [clojure.edn :as edn]))

clojure.core/read-string
clojure.edn/read-string

(defn get-next-game-id []
  (let [log-data (slurp "log.edn")
        data (edn/read-string log-data)
        ;game-ids (find data :game-id)
        game-ids (map :game-id data)

        max-game-id (if (empty? game-ids) 0 (apply max game-ids))
        ]

    ;max-game-id
    (println "game-ids" game-ids)
    ;(println "log-data" log-data)
    (inc max-game-id)
    ;(println log-data)
    ;(prn data)
    ;pr-str
    ))

(def game-id (atom (get-next-game-id)))
;(def game-id (atom 0))

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

(defn save [player-1 player-2 grid];test
  ;(save/save-round @game-id player-1 player-2 grid)
  (save/save-round-2 @game-id player-1 player-2 grid)
  (save/save-game-id @game-id))

(defn play-round [player-1? player-1 player-2 grid]
  (let [[player opponent] (if player-1? [player-1 player-2] [player-2 player-1])
        move (get-move player opponent grid)
        token-1 (:token player-1)
        token-2 (:token player-2)
        new-grid (grid-after-move player-1? grid move token-1 token-2)]
    (save player-1 player-2 new-grid)
    (ui/print-board new-grid board/display)
    new-grid))

(defn complete-game [grid token-1 token-2]                  ;test this
  ;(swap! game-id inc)
  (ui/print-end grid token-1 token-2))


;notes
;stories are taking longer, think about
; always reformat code
; dont comment out tests
; limit commented out code
;delete unnecessary code
