(ns ttt-clojure.minimax
  (:require [ttt-clojure.ui :as ui])
  (:require [ttt-clojure.board :as board]))

(declare minimize)
(declare maximize)
(def value-max -100000)
(def value-min 100000)

(defn value [game-board depth]
  (cond
    (board/x-wins game-board) (/ 12 depth)
    (board/o-wins game-board) (/ -12 depth)
    (board/tie game-board) 0))

(defn best-move [maximizing? game-board depth [best-value best-action] action]
  (let [[compare evaluate token] (if maximizing?
                                   [> minimize "X"]
                                   [< maximize "O"])
        new-game-board (ui/place-xo game-board action token)
        [value] (evaluate new-game-board (inc depth))]
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
      [(value board depth) nil]
      (extremity actions maximizing? board depth))))

(defn maximize [board depth]
  (min-or-max board depth true))

(defn minimize [board depth]
  (min-or-max board depth false))

(defn next-move [board] (second (maximize board 1)))
(defn next-move-2 [board] (second (minimize board 1)))
