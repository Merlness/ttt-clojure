(ns ttt-clojure.computer
  (:require [ttt-clojure.ui :as ui])
  (:require [ttt-clojure.minimax :as mm]))

(defn start-first? []
  (loop []
    (println "Would you like to go first or second?")
    (let [user-input (read-line)]
      (cond
        (= "1" user-input) (do (println "Ok, best of luck ... you're gonna need it")
                               (ui/print-board [1 2 3 4 5 6 7 8 9]) false)
        (= "2" user-input) true
        :else (recur)
        ))))

(defn computer []
   (loop [grid [1 2 3 4 5 6 7 8 9]
         x-turn? (start-first?)]
    (let [move (mm/next-move grid)
          new-grid (if x-turn?
                     (do (println "My turn...")
                         (ui/place-xo grid move "X"))
                     (ui/update-board grid false))]
      (ui/print-board new-grid)
      (if (not (ui/endgame-result new-grid))
        (recur new-grid (not x-turn?))
        (ui/print-end-computer new-grid)))))
