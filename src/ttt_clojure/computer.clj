(ns ttt-clojure.computer
  (:require [ttt-clojure.board :as board]
            [ttt-clojure.minimax :as mm]
            [ttt-clojure.ui :as ui]))

(defn set-depth [board]
  (condp < (count (board/find-available-moves board))
    20 2
    11 3
    8 4
    7))

(defn find-next-move [board evaluate maximizing-token minimizing-token]
  (if (every? number? board)
    1
    (second (evaluate board (set-depth board) maximizing-token minimizing-token))))

(defn checks-end-move [board available-moves token]
  (first (filter #(board/winner?
                    (ui/place-xo board % token) token)
                 available-moves)))

(defn win-or-block [board maximizing-token minimizing-token]
  (let [available-moves (board/find-available-moves board)
        winning-move (checks-end-move board available-moves maximizing-token)
        blocking-move (checks-end-move board available-moves minimizing-token)]
    (or winning-move blocking-move)))

(defn helper-3d-logic [board]
  (let [available-moves (board/find-available-moves board)
        number-available (count available-moves)]
    (when (> number-available 25) (some #{14} available-moves))))

(defn next-move [board maximizing-token minimizing-token]
  (or
    (helper-3d-logic board)
    (win-or-block board maximizing-token minimizing-token)
    (find-next-move board mm/maximize maximizing-token minimizing-token)))

(defn place-easy-move [board] (-> board board/find-available-moves rand-nth))

(defmulti ai-move (fn [_board _ai-token _opponent-token difficulty] difficulty))

(defmethod ai-move :easy [board _ai-token _opponent _difficulty] (place-easy-move board))

(defmethod ai-move :medium [board ai-token opponent-token _difficulty]
  (let [move-count (board/find-move-count board)
        hard-ai? (or (< move-count 5) (zero? (rand-int 2)))]
    (if hard-ai?
      (next-move board ai-token opponent-token)
      (place-easy-move board))))

(defmethod ai-move :hard [board ai-token opponent-token _difficulty]
  (next-move board ai-token opponent-token))
