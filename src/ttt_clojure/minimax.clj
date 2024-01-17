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

(defn select-move [board valid-moves-set]
  (let [available-moves (filter number? board)
        valid-moves (filter valid-moves-set available-moves)]
    (when (not-empty valid-moves)
      ;(seq valid-moves)
      (rand-nth valid-moves))))

(comment
  (if test nil (code))
  (if-not test (code) nil)
  (when-not test (code))

  (if-not test nil (code))
  (if test (code) nil)
  (when test (code))
  )

(defn corners [board]
  (select-move board #{1 4 13 16}))

(defn center [board]
  (select-move board #{6 7 10 11}))

;take board winner out into other function
(defn winning-move [board available-moves token]
  (first (filter #(board/winner?
                    (ui/place-xo board % token) token)
                 available-moves)))

(defn win-or-block [board opponent-token]
  (let [available-moves (filter number? board)
        my-token (if (= opponent-token "X") "O" "X")]
    (or (winning-move board available-moves my-token)
        (winning-move board available-moves opponent-token))))

(defn hard-16-helper [board opponent-token]
  (let [move-count (count (remove number? board))
        evaluate (if (= opponent-token "O") maximize minimize)]
    (if (< move-count 7)
      (or (win-or-block board opponent-token)
          (corners board)
          (center board))
      (second (evaluate board 1)))))

(defn hard-9-helper [board evaluate]
  (if (every? number? board)
    1
    (second (evaluate board 1))))

(comment
  (if condition
    truthy-case
    falsy-case)

  (if test 1 6)

  )

(defn find-next-move [board maximize?]
  (let [[evaluate opponent-token] (if (= maximize? maximize)
                                    [maximize "O"]
                                    [minimize "X"])]
    (if (= 16 (count board))
      (hard-16-helper board opponent-token)
      (hard-9-helper board evaluate))))

(defn next-move [board]
  (find-next-move board maximize))

(defn next-move-2 [board]
  (find-next-move board minimize))
