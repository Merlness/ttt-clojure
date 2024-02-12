(ns ttt-clojure.ui
  (:require [ttt-clojure.board :as board]
            [ttt-clojure.data :as data]))

(defn place-xo [grid old-num xo]
  (map
    #(if (= % old-num)
       xo
       %)
    grid))

(defn invalid-move? [num grid] (not-any? #{num} grid))

(defn valid-input? [input grid]
  (let [move (try
               input
               (catch NumberFormatException _
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

(defn endgame-result [grid token-1 token-2]
  (cond
    (board/token-wins grid token-1) (str token-1 " is the winner!")
    (board/token-wins grid token-2) (str token-2 " is the winner!")
    (board/tie grid) "Womp, its a tie"))

(defn print-board [grid] (println (board/display grid)))

(defn print-end
  ([{:keys [board player-1 player-2]}] (print-end board player-1 player-2))
  ([board player-1 player-2]
   (let [player-1 (board/player-token player-1)
         player-2 (board/player-token player-2)]
     (println (endgame-result board player-1 player-2)))))

(defn player-statement [num] (println (str "Player " num "'s turn")))

(defn get-game-board []
  (println "Welcome to Merl's Tic Tac Toe
  Please press 3 if you would like to play on a 3x3 board,
    4 if you would like to play on a 4x4 board,
    or 9 if you would like to play 3D Tic Tac Toe")
  (case (read-line)
    "3" :3x3
    "4" :4x4
    "9" :3x3x3
    (recur)))

(defn get-difficulty []
  (println "Please press 1 for an easy AI
             2 for a medium AI
             3 for a hard AI")
  (case (read-line)
    "1" :easy
    "2" :medium
    "3" :hard
    (recur)))

(defn get-player [player-number]
  (println (str "If you would like Player " player-number
                " to be human press 1, or press 2 for AI?"))
  (case (read-line)
    "1" :human
    "2" :ai
    (recur player-number)))

(defn get-player-1-token []
  (println "Please press 1 if you want Player 1 to be X and Player 2 to be O,
or anything else for Player 1 to be O and Player 2 to be X")
  (if (= "1" (read-line)) "X" "O"))

(defn get-player-2-token [player-1-token]
  (if (= player-1-token "X") "O" "X"))

(defn continue-last-game? []
  (println "Would you like to finish your previous game?
  1 for Yes, anything else for No")
  (= "1" (read-line)))

(defn difficulty-to-string [difficulty]
  (case difficulty
    :easy "Easy AI"
    :medium "Medium AI"
    :hard "Hard AI"
    "Unknown"))

(defn print-player-kind [player-number player]
  (if (= :ai (:kind player))
    (println (str "Player-" player-number ": " (difficulty-to-string (:difficulty player))))
    (println (str "Player-" player-number ": Human"))))

(defn print-resume-game [game]
  (println "\nResuming game:")

  (print-player-kind "1" (:player-1 game))
  (print-player-kind "2" (:player-2 game)))

(defn print-previous-moves [input-id] ;test ;[game]
  (let [;data (data/all-games) ; select game(game-id) in main
        ;max-game-id (data/max-game-id data)
        ; data/history
        ;game-data (filter #(= (:game-id %) max-game-id) data)

        game-data (data/get-game-by-id input-id)
        ] ;move to own fn
    (run! (fn [game]
            (println "Player" (if (:player-1? game) "2" "1") "made a move:")
            (print-board (:board game)))
          game-data)))

(defn print-id [id] (println (str "Game-ID: " id)))

(defn print-id-and-empty-board [game-id grid]
  (print-id game-id)
  (print-board grid))
