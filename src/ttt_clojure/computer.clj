(ns ttt-clojure.computer
  (:require [ttt-clojure.minimax :as mm])
  (:require [ttt-clojure.easy-comp :as ec]))


; Use board, player, and opponent params only
(defmulti ai-move (fn [_board _ai-token _opponent-token difficulty] difficulty))

(defmethod ai-move :easy [board _ai-token _opponent _difficulty] (ec/place-easy-move board))

(defmethod ai-move :medium [board ai-token opponent-token _difficulty]
  (let [move-count (count (remove number? board))
        hard-ai? (or (< move-count 5) (zero? (rand-int 2)))]
    (if hard-ai?
      (mm/next-move-real board ai-token opponent-token)
      (ec/place-easy-move board))))

(defmethod ai-move :hard [board ai-token opponent-token _difficulty]
  (mm/next-move-real board ai-token opponent-token))
