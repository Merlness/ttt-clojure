(ns ttt-clojure.computer
  (:require [ttt-clojure.minimax :as mm]))

(defn find-actions [board]
  (filter number? board))

(defn find-rand-int [coll]
  (rand-nth coll))

(defn place-easy-move [board]
  (->> board
       (find-actions)
       (find-rand-int)))

; Use board, player, and opponent params only
(defmulti ai-move (fn [_board _ai-token _opponent-token difficulty] difficulty))

(defmethod ai-move :easy [board _ai-token _opponent _difficulty] (place-easy-move board))

(defmethod ai-move :medium [board ai-token opponent-token _difficulty]
  (let [move-count (count (remove number? board))
        hard-ai? (or (< move-count 5) (zero? (rand-int 2)))]
    (if hard-ai?
      (mm/next-move board ai-token opponent-token)
      (place-easy-move board))))

(defmethod ai-move :hard [board ai-token opponent-token _difficulty]
  (mm/next-move board ai-token opponent-token))
