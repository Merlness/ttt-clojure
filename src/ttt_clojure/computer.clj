(ns ttt-clojure.AI
  (:require [ttt-clojure.ui :as ui]))


(defn Unbeatable-AI []
  (loop [grid [1 2 3 4 5 6 7 8 9]
         X? true]
    (ui/print-board grid)
    (let [new-grid (ui/update-board grid X?)]
      (if (ui/endgame-result new-grid)
        (ui/print-end new-grid)
        (recur new-grid (not X?))))))
