(ns ttt-clojure.ui
  (:require [ttt-clojure.board :as board]))

(declare start-first-question)
(declare luck-greeting)

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
    (board/x-wins grid) "X is the winner!"
    (board/o-wins grid) "O is the winner!"
    (board/tie grid) "Womp, its a tie"))

(defn print-board [grid] (println (board/display grid)))
(defn print-end [grid] (println (board/display grid)) (println (endgame-result grid)))
(defn print-end-computer [grid] (println (endgame-result grid)))

(defn start-first-question [] (println "Would you like to go first or second?"))
(defn luck-greeting [] (println "Ok, best of luck ... you're gonna need it"))
(defn my-turn-statement [] (println "My turn..."))
(defn comp-1-statement [] (println "Computer 1's turn"))
(defn comp-2-statement [] (println "Computer 2's turn"))


(defn start-first? []
  (start-first-question)
  (let [user-input (read-line)]
    (cond
      (= "1" user-input) (do (luck-greeting)
                             (print-board [1 2 3 4 5 6 7 8 9]) false)
      (= "2" user-input) true
      :else (recur)
      )))

(defn get-user-input-main []
  (do (println "Please press 1 for Tic Tac Toe vs AI
             2 for Two Player Tic Tac Toe
             3 for Computer vs Computer Tic Tac Toe"
               )
      (read-line)))

(defn get-user-input-difficulty []
  (do (println "Please press 1 for an easy AI
             2 for a medium AI
             3 for a an impossible AI")
      (read-line)))

(defn welcome-c-vs-c []
  (println "Welcome to Computer vs Computer
          For the first computer, choose your difficulty"))

(defn second-difficulty-message []
  (println "For the second computer, choose your difficulty"))

;(defn comp-difficulty []
;  (let [user-input (get-user-input-difficulty)]
;    (cond
;      (= user-input "1") (ai/easy-ai)
;      (= user-input "2") (ai/medium-ai)
;      (= user-input "3") (ai/hard-ai)
;      :else (recur))))
