(ns ttt-clojure.game-modes
  (:require [ttt-clojure.ui :as ui]
            [ttt-clojure.board :as board]
            [ttt-clojure.save-game :as save]
            [ttt-clojure.minimax :as mm]
            [ttt-clojure.easy-comp :as ec]
            [ttt-clojure.computer :as comp]))

;(defn -main []
;  #_(let [game {:board    (board-size)
;                :player-1 (ui/get-player 1)                    ; {:kind :ai :difficulty :hard :token "X"} {:kind :human :token "O"}
;                :player-2 (ui/get-player 2)                    ; {:kind :ai :difficulty :hard :token "X"} {:kind :human :token "O"}
;                 [:token-1 :token-2] (ui/get-tokens)
; }]
;      (play-game game))
;  )
(defn board-size []
  (case (ui/get-user-input-3-4)
    :3x3 (range 1 10)
    :4x4 (range 1 17)
    :3x3x3 (range 1 28)))

(defn grid-after-move [player-1? grid move token-1 token-2]
  (let [[letter statement] (if player-1?
                             [token-1 (ui/player-statement 1)]
                             [token-2 (ui/player-statement 2)])]
    statement
    (ui/place-xo grid move letter)))

(defn create-player [position tokens-in-use]
  (let [player-kind (ui/get-player position)
        ;difficulty (when (= :ai (:kind player-kind)) (ui/get-difficulty))
        token (if (= position 1)
                (ui/get-player-1-token)
                (ui/get-player-2-token tokens-in-use))]
    {:kind  player-kind
     ;:difficulty difficulty
     :token token}))

;(defmulti get-move (fn [player _grid _token-2] (:kind player)))
(defmulti get-move (fn [player _grid _token-2 _difficulty] (:kind player)))

;(defmethod get-move :human [_player grid _token-2]
;  (ui/get-move grid))
;(defmethod get-move :human [_player grid _token-2 _difficulty]
;  (ui/get-move grid))

(defmethod get-move :human [_player grid _token-2 _difficulty]
  (ui/get-move-2 grid))
;;
;(defmethod get-move :ai [_player grid opponent]
;  (comp/ai-move grid (:token _player) opponent (:difficulty _player)))
(defmethod get-move :ai [_player grid opponent difficulty]
  (comp/ai-move grid (:token _player) opponent difficulty))

(defn player-vs-player []
  (let [board (board-size)
        player-1 (create-player 1 nil)
        player-2 (create-player 2 (:token player-1))
        diff-1 (when (= :ai (:kind player-1)) (ui/get-difficulty))
        diff-2 (when (= :ai (:kind player-2)) (ui/get-difficulty))
        token-1 (name (:token player-1))
        token-2 (name (:token player-2))]
    (ui/print-board board board/display)
    (loop [grid board
           player-1? true]
      (let [move (if player-1?
                   ;(get-move player-1 grid token-2 )
                   ;(get-move player-2 grid token-1 ))
                   (get-move player-1 grid token-2 diff-1)
                   (get-move player-2 grid token-1 diff-2))
            new-grid (grid-after-move player-1? grid move token-1 token-2)]
        (save/save-round player-1 player-2 new-grid diff-1 diff-2)
        (ui/print-board new-grid board/display)
        (if (board/game-over? new-grid token-1 token-2)
          (ui/print-end-2 new-grid token-1 token-2)
          (recur new-grid (not player-1?)))))))


;{:board board (ui/get-user-input-3-4)
;   :active-player :player-1
;   :player-1? true
;   :player-1 {:kind :ai :difficulty :hard :token "X"}
;   ;ex  :player-1      {:kind :human :difficulty :hard :token "X"}
;   :player-2 {:kind :ai :difficulty :hard :token "O"}}      ; game
