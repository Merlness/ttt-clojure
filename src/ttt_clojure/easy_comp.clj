(ns ttt-clojure.easy-comp)

(defn find-actions [board]
  (filter number? board))

(defn find-rand-int [coll]
  (rand-nth coll))

(defn place-easy-move [board]
  (->> board
       (find-actions)
       (find-rand-int)))
