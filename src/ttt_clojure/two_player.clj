(ns ttt-clojure.two-player
  (:require [ttt-clojure.ui :as ui]))


(defn two-player [board]
 (loop [grid board
       X? true]
  (ui/print-board grid)
  (let [new-grid (ui/update-board grid X?)]
    (if (ui/endgame-result new-grid)
      (ui/print-end new-grid)
      (recur new-grid (not X?))))))
