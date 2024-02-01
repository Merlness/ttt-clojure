(ns ttt-clojure.ui
  (:require [ttt-clojure.board :as board]))

(defn place-xo [grid old-num xo]
  (map
    #(if (= % old-num)
       xo
       %)
    grid))

(defn invalid-move? [num grid]
  (not-any? #{num} grid))
;tests
(defn valid-input? [input grid]
  (let [move (try
               input
               (catch NumberFormatException e
                 nil))
        size (count grid)]
    (and move (<= 1 move size))))

(defn get-move [grid]
  (println "Choose your position")
  (loop []
    (let [user-input (Integer/parseInt (read-line))]
      (if (and (not (invalid-move? user-input grid)) (valid-input? user-input grid))
        user-input
        (do
          (println "Invalid input. Please place a number that is available.")
          (recur))))))

;here
(defn endgame-result [grid]
  (cond
    (board/x-wins grid) "X is the winner!"
    (board/o-wins grid) "O is the winner!"
    (board/tie grid) "Womp, its a tie"))
;to here

(defn endgame-result-2 [grid token-1 token-2]
  (cond
    (board/token-wins grid token-1) (str token-1 " is the winner!")
    (board/token-wins grid token-2) (str token-2 " is the winner!")
    (board/tie grid) "Womp, its a tie"))

;tests for these V
(defn print-board [grid display] (println (display grid)))
(defn print-end [grid token-1 token-2] (println (endgame-result-2 grid token-1 token-2)))
(defn player-statement [num] (println (str "Player " num "'s turn")))
;tests
(defn get-user-input-3-4 []
  (println "Welcome to Merl's Tic Tac Toe
  Please press 3 if you would like to play on a 3x3 board,
    4 if you would like to play on a 4x4 board,
    or 9 if you would like to play 3D Tic Tac Toe")
  (case (read-line)
  "3" :3x3
  "4" :4x4
  "9" :3x3x3
  (recur)))
;good
(defn get-difficulty []
  (println "Please press 1 for an easy AI
             2 for a medium AI
             3 for a hard AI")
  (case (read-line)
    "1" :easy
    "2" :medium
    "3" :hard
    (recur)))

;tests
(defn get-player [player-number]
  (println (str "If you would like Player " player-number
                " to be human press 1, or press 2 for AI?"))
  (case (read-line)
    "1" :human
    "2" :ai
    (recur player-number)))
;more tests
(defn get-player-1-token []
  (println "Please press 1 if you want Player 1 to be X and Player 2 to be O,
or anything else for Player 1 to be O and Player 2 to be X")
  (if (= "1" (read-line)) "X" "O"))
;more tests
(defn get-player-2-token [player-1-token]
  (if (= player-1-token "X") "O" "X"))
