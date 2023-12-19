(ns ttt-clojure.core
  (:require [clojure.string :as str]
            [ttt-clojure.board :as board]
            [ttt-clojure.ui :as ui]))


(defn game-over? [grid] ; result, outcome, ...
  (cond
    (board/x-wins grid) "Congrats X is the winner!"
    (board/o-wins grid) "Congrats O is the winner!"
    (board/tie grid) "Womp, its a tie"))




;(defn game-loop
;loop [grid [1-9]
;       X?   true]
;
;ask for player to go
;update board
;print board
;see if game over
;change xo
;recur new grid

;)

(defn -main []
  (loop [grid [1 2 3 4 5 6 7 8 9]
         X? true]
    (println (board/display grid))
    (let [new-grid (ui/update-board grid X?)]
      ;(println (board/display new-grid))
      (if (game-over? new-grid)
        ;(ui/game-over new-grid)
        (do (println (board/display new-grid))
            (println (game-over? new-grid)))
        (recur new-grid (not X?)))))
  )

(comment
  change name of game-over? to result
  (println (board/display grid)) new function
new function into game-over
  printlns -> game-over
  )
