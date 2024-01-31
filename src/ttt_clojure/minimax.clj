(ns ttt-clojure.minimax
  (:require [ttt-clojure.ui :as ui])
  (:require [ttt-clojure.board :as board]))

(declare minimize)
(declare maximize)
(declare minimize-2)
(declare maximize-2)
(def value-max -100000)
(def value-min 100000)

(defn remaining-moves [board] (count (filter number? board)))

(defn set-depth [board]
  (condp < (remaining-moves board)
    20 2
    11 3
    8 4
    7))

(defn value [game-board depth]
  (cond
    (board/x-wins game-board) depth
    (board/o-wins game-board) (- depth)
    :else 0))

(defn best-move [maximizing? game-board depth [best-value best-action] action]
  ;get rid of x or o
  ;add  parameters to minimizing and maximizing token
  ;maximizing-token minimizing-token
  (let [[compare evaluate token] (if maximizing?
                                   [> minimize "X"]
                                   [< maximize "O"])
        new-game-board (ui/place-xo game-board action token)
        value (if (zero? depth)
                (value new-game-board depth)
                (first (evaluate new-game-board (dec depth))))]
    (if (compare value best-value)
      [value action]
      [best-value best-action])))

(defn extremity [actions maximizing? game-board depth]
  (let [default-value (if maximizing? value-max value-min)]
    (reduce
      #(best-move maximizing? game-board depth %1 %2)
      [default-value nil]
      actions)))

(defn min-or-max [board depth maximizing?]
  (let [actions (filter number? board)]
    (if (ui/endgame-result board)
      [(value board depth)]
      (extremity actions maximizing? board depth))))

(defn maximize [board depth]
  (min-or-max board depth true))

(defn minimize [board depth]
  (min-or-max board depth false))

(defn find-next-move [board evaluate]
  (if (every? number? board)
    1
    (second (evaluate board (set-depth board)))))

(defn next-move [board]
  (find-next-move board maximize))

(defn next-move-2 [board]
  (find-next-move board minimize))

(defn value-2 [game-board depth maximizing-token minimizing-token]
  (cond
    (board/token-wins game-board maximizing-token) depth
    (board/token-wins game-board minimizing-token) (- depth)
    :else 0))

(defn moves-exhausted? [board depth maximizing-token minimizing-token]
  (or (zero? depth)
      (board/game-over? board maximizing-token minimizing-token)))

(defn find-value [board evaluate depth maximizing-token minimizing-token]
  (if (moves-exhausted? board depth maximizing-token minimizing-token)
    (value-2 board depth maximizing-token minimizing-token)
    (first (evaluate board (dec depth) maximizing-token minimizing-token))))

(defn best-move-2 [maximizing? game-board depth [best-value best-action] action maximizing-token minimizing-token]
  (let [[compare evaluate token] (if maximizing?
                                   [> minimize-2 maximizing-token]
                                   [< maximize-2 minimizing-token])
        new-game-board (ui/place-xo game-board action token)
        value (find-value new-game-board evaluate depth maximizing-token minimizing-token)]
    (if (compare value best-value)
      [value action]
      [best-value best-action])))

(defn extremity-2 [actions maximizing? game-board depth maximizing-token minimizing-token]
  (let [default-value (if maximizing? value-max value-min)]
    (reduce
      #(best-move-2 maximizing? game-board depth %1 %2 maximizing-token minimizing-token)
      [default-value nil]
      actions)))

(defn min-or-max-2 [board depth maximizing? maximizing-token minimizing-token]
  (let [actions (filter number? board)]
    (if (board/game-over? board maximizing-token minimizing-token)
      [(value board depth)]
      (extremity-2 actions maximizing? board depth maximizing-token minimizing-token))))

(defn maximize-2 [board depth maximizing-token minimizing-token]
  (min-or-max-2 board depth true maximizing-token minimizing-token))

(defn minimize-2 [board depth maximizing-token minimizing-token]
  (min-or-max-2 board depth false maximizing-token minimizing-token))

(defn find-next-move-2 [board evaluate maximizing-token minimizing-token]
  (if (every? number? board)
    1
    (second (evaluate board (set-depth board) maximizing-token minimizing-token))))

(defn next-move-real [board maximizing-token minimizing-token]
  (find-next-move-2 board maximize-2 maximizing-token minimizing-token))

;asking player
;need a map to pass around the rest of the app
; easy to pass and retrieve
