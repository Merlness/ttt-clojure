(ns ttt-clojure.minimax
  (:require [ttt-clojure.ui :as ui])
  (:require [ttt-clojure.board :as board]))

(declare minimize)
(declare maximize)
(def value-max -100000)
(def value-min 100000)

(defn remaining-moves [board] (count (filter number? board)))

(defn set-depth [board]
  (condp < (remaining-moves board)
    20 2
    11 3
    8 4
    7))

(defn value [game-board depth maximizing-token minimizing-token]
  (cond
    (board/token-wins game-board maximizing-token) depth
    (board/token-wins game-board minimizing-token) (- depth)
    :else 0))

(defn moves-exhausted? [board depth maximizing-token minimizing-token]
  (or (zero? depth)
      (board/game-over? board maximizing-token minimizing-token)))

(defn find-value [board evaluate depth maximizing-token minimizing-token]
  (if (moves-exhausted? board depth maximizing-token minimizing-token)
    (value board depth maximizing-token minimizing-token)
    (first (evaluate board (dec depth) maximizing-token minimizing-token))))

(defn best-move [maximizing? game-board depth [best-value best-action] action maximizing-token minimizing-token]
  (let [[compare evaluate token] (if maximizing?
                                   [> minimize maximizing-token]
                                   [< maximize minimizing-token])
        new-game-board (ui/place-xo game-board action token)
        value (find-value new-game-board evaluate depth maximizing-token minimizing-token)]
    (if (compare value best-value)
      [value action]
      [best-value best-action])))

(defn extremity [actions maximizing? game-board depth maximizing-token minimizing-token]
  (let [default-value (if maximizing? value-max value-min)]
    (reduce
      #(best-move maximizing? game-board depth %1 %2 maximizing-token minimizing-token)
      [default-value nil]
      actions)))

(defn min-or-max [board depth maximizing? maximizing-token minimizing-token]
  (let [actions (filter number? board)]
    (if (board/game-over? board maximizing-token minimizing-token)
      [(value board depth maximizing-token minimizing-token)]
      (extremity actions maximizing? board depth maximizing-token minimizing-token))))

(defn maximize [board depth maximizing-token minimizing-token]
  (min-or-max board depth true maximizing-token minimizing-token))

(defn minimize [board depth maximizing-token minimizing-token]
  (min-or-max board depth false maximizing-token minimizing-token))

(defn find-next-move-2 [board evaluate maximizing-token minimizing-token]
  (if (every? number? board)
    1
    (second (evaluate board (set-depth board) maximizing-token minimizing-token))))

(defn next-move-real [board maximizing-token minimizing-token]
  (find-next-move-2 board maximize maximizing-token minimizing-token))
