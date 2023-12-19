(ns ttt-clojure.ui)

(defn X? [bool]
  (if bool
    "X"
    "O"))

(defn place-xo [grid old-num xo]
  (map
    #(if (= % old-num)
       xo
       %)
    grid))

(defn valid-move? [num grid]
  (not-any? #{num} grid))

(defn invalid-message []
  (println "The value you entered is not possible silly. Please try again."))

(defn get-move []
  (println "Choose your position")
  (let [user-input (read-line)
        move (Integer/parseInt user-input)]
    move))

(defn update-board [grid xo]
  (loop []
    (let [move (get-move)]
      (if (valid-move? move grid)
        (do (invalid-message) (recur))
        (place-xo grid move (X? xo))))))

;(defn display-board [board])
;(defn game-over [board])
