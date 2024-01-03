(ns ttt-clojure.ui
  (:require [ttt-clojure.board :as board]))

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

(defn invalid-move? [num grid]
  (not-any? #{num} grid))

(defn invalid-message []
  (println "The value you entered is not possible silly. Please try again."))

(defn valid-input? [input]
  (let [move (try
               (Integer/parseInt input)
               (catch NumberFormatException e
                 nil))]
    (and move (<= 1 move 9))))

(defn get-move []
  (println "Choose your position")
  (loop []
    (let [user-input (read-line)]
      (if (valid-input? user-input)
        (Integer/parseInt user-input)
        (do
          (println "Invalid input. Please enter a number between 1 and 9.")
          (recur))))))

(defn update-board [grid xo]
  (loop []
    (let [move (get-move)]
      (if (invalid-move? move grid)
        (do (invalid-message) (recur))
        (place-xo grid move (X? xo))))))

(defn endgame-result [grid]
  (cond
    (board/x-wins grid) "Congrats X is the winner!"
    (board/o-wins grid) "Congrats O is the winner!"
    (board/tie grid)    "Womp, its a tie"))

(defn print-board [grid]
  (println (board/display grid)))

(defn print-end [grid]
  (println (board/display grid))
  (println (endgame-result grid)))

(defn print-end-computer [grid]
  (println (endgame-result grid)))
