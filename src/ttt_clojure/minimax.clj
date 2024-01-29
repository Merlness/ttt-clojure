(ns ttt-clojure.minimax
  (:require [ttt-clojure.ui :as ui])
  (:require [ttt-clojure.board :as board]))

(declare alpha-beta)
(declare minimize)
(declare maximize)
(def value-max -100000)
(def value-min 100000)

(defn set-depth [board]
  (cond
    (> (count (filter number? board)) 20) 2
    (> (count (filter number? board)) 11) 3
    (> (count (filter number? board)) 8) 4
    :else 7))


(defn value [game-board depth]
  (cond
    (board/x-wins game-board) depth
    (board/o-wins game-board) (- depth)
    :else 0))


(defn best-move [maximizing? game-board depth [best-value best-action] action]
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

;(defn best-move [maximizing? game-board depth alpha beta [best-value best-action] action]
;  (let [[evaluate token] (if maximizing?
;                                   [alpha-beta "X"]
;                                   [alpha-beta "O"])
;        new-game-board (ui/place-xo game-board action token)
;        value (if (zero? depth)
;                (value new-game-board depth)
;                (first (evaluate new-game-board (dec depth) alpha beta (not maximizing?))))]
;      (let [new-best-value value
;            ;max/minEval?
;            new-alpha (if maximizing? (max alpha new-best-value) alpha)
;            new-beta (if maximizing? beta (min beta new-best-value))]
;        (if (> new-beta new-alpha)
;          [new-best-value action]
;          [best-value best-action]))
;      [best-value best-action]))

(defn extremity [actions maximizing? game-board depth]
  (let [default-value (if maximizing? value-max value-min)]
    (reduce
      #(best-move maximizing? game-board depth %1 %2)
      [default-value nil]
      actions)))
;
;(defn extremity [actions maximizing? game-board depth alpha beta]
;    (let [default-value (if maximizing? value-max value-min)]
;      (reduce
;        #(let [alpha (max %1 alpha)]
;           (if (<= beta alpha)
;             [%1 %2]
;             (best-move ...)))
;        [default-value nil]
;        actions)))


(defn min-or-max [board depth maximizing?]
  (let [actions (filter number? board)]
    (if (ui/endgame-result board)
      [(value board depth)]
      (extremity actions maximizing? board depth))))

;(defn alpha-beta [board depth alpha beta maximizing?]
;  (let [actions (filter number? board)]
;    (if (ui/endgame-result board)
;      [(value board depth)]
;      (extremity actions maximizing? board depth alpha beta))))

(defn maximize [board depth]
  (min-or-max board depth true))

(defn minimize [board depth]
  (min-or-max board depth false))

;(defn maximize [board depth]
;  (alpha-beta board depth value-min value-max true))
;
;(defn minimize [board depth]
;  (alpha-beta board depth value-max value-min false))

(defn find-next-move [board evaluate]
  (if (every? number? board)
    1
    (second (evaluate board
                      (set-depth board)
                      ;default-depth
                      ))))

(defn next-move [board]
  (find-next-move board maximize))

(defn next-move-2 [board]
  (find-next-move board minimize))



;(defn best-move [maximizing? game-board depth alpha beta [best-value best-action] action]
;  (let [[compare evaluate token] (if maximizing?
;                                   [> alpha-beta "X"]
;                                   [< alpha-beta "O"])
;        new-game-board (ui/place-xo game-board action token)
;        value (if (zero? depth)
;                (value new-game-board depth)
;                (first (evaluate new-game-board (dec depth) alpha beta (not maximizing?))))]
;    ;board depth alpha beta maximizing?
;    (if (compare value best-value)
;      (let [new-best-value value
;            new-alpha (if maximizing? (max alpha new-best-value) alpha)
;            new-beta (if maximizing? beta (min beta new-best-value))]
;        (if (> new-beta new-alpha)
;          [new-best-value action]
;          [best-value best-action]))
;      [best-value best-action])))
;
;(defn extremity [actions maximizing? game-board depth alpha beta]
;  (let [default-value (if maximizing? value-max value-min)]
;    (reduce
;      #(best-move maximizing? game-board depth alpha beta %1 %2)
;      [default-value nil]
;      actions)))
;
;(defn alpha-beta [board depth alpha beta maximizing?]
;  (let [actions (filter number? board)]
;    (if (ui/endgame-result board)
;      [(value board depth)]
;      (extremity actions maximizing? board depth alpha beta))))
;
;(defn maximize [board depth]
;  (alpha-beta board depth value-min value-max true))
;
;(defn minimize [board depth]
;  (alpha-beta board depth value-max value-min false))


;(defn alpha-beta [game-board depth alpha beta maximizingPlayer]
;  (if (zero? depth)
;    (value game-board depth)
;    (if maximizingPlayer
;      (let [value value-max]
;        (for [child (best-move maximizingPlayer game-board depth )]
;          (let [value (max value (alpha-beta child (dec depth) alpha beta false))]
;            (if (>= value beta)
;              value
;              (let [alpha (max alpha value)]))))
;        value)
;      (let [value value-min]
;        (for [child (children node)]
;          (let [value (min value (alpha-beta child (dec depth) alpha beta true))]
;            (if (<= value alpha)
;               value
;              (let [beta (min beta value)]))))
;        value))))
