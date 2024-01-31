(ns ttt-clojure.ui-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.ui :as sut]))


(describe "Updating board"

  (it "is an invalid move"
    (should= true (sut/invalid-move? 1 ["X" 2 3 4 5 6 7 8 9])))

  (it "is a valid move"
    (should= false (sut/invalid-move? 1 [1 2 3 4 5 6 7 8 9])))

  (it "get-move"
    (with-in-str "1\n"
      (let [output (with-out-str (sut/get-move [1 2 3 4 5 6 7 8 9]))]
        (should= "Choose your position\n" output))))

  (it "knows O winner"
    (should= "O is the winner!"
             (sut/endgame-result ["O" "O" "O" 4 5 6 7 8 9])))

  (it "knows X winner"
    (should= "X is the winner!"
             (sut/endgame-result [1 2 3 "X" "X" "X" 7 8 9])))

  (it "knows its a tie"
    (should= "Womp, its a tie"
             (sut/endgame-result ["X" "O" "X"
                                  "O" "X" "X"
                                  "O" "X" "O"])))

  (it "checks my turn statement"
    (let [output (with-out-str (sut/my-turn-statement))]
      (should= "My turn...\n" output)))

  (it "checks comp 1 statement"
    (let [output (with-out-str (sut/comp-statement 1))]
      (should= "Computer 1's turn\n" output)))

  (it "checks comp 2 statement"
    (let [output (with-out-str (sut/comp-statement 2))]
      (should= "Computer 2's turn\n" output)))

  (it "knows game is still going"
    (should= nil
             (sut/endgame-result [1 2 3 4 5 6 7 8 9])))

  (it "checks start first question"
    (let [output (with-out-str (sut/start-first-question))]
      (should= "Would you like to go first or second?\n" output)))

  (it "checks luck greeting"
    (let [output (with-out-str (sut/luck-greeting))]
      (should= "Ok, best of luck ... you're gonna need it\n" output)))

  (it "checks my turn"
    (let [output (with-out-str (sut/my-turn-statement))]
      (should= "My turn...\n" output)))

  (it "gets user input"
    (with-in-str "1\n"
      (let [output (with-out-str (sut/get-user-input-main))]
        (should= "Please press 1 for Tic Tac Toe vs AI
             2 for Two Player Tic Tac Toe
             3 for Computer vs Computer Tic Tac Toe\n" output))))

  (context "getting difficulty"
    (it "easy"
      (with-out-str
        (with-in-str "1\n"
          (should= :easy (sut/get-difficulty)))))

    (it "medium"
      (with-out-str
        (with-in-str "2\n"
          (should= :medium (sut/get-difficulty)))))

    (it "hard"
      (with-out-str
        (with-in-str "3\n"
          (should= :hard (sut/get-difficulty)))))

    (it "invalid"
      (with-out-str
        (with-in-str "a\n1\n"
          (should= :easy (sut/get-difficulty)))))

    (it "displays a message"
      (with-in-str "1\n"
        (let [output (with-out-str (sut/get-difficulty))]
          (should= "Please press 1 for an easy AI
             2 for a medium AI
             3 for a hard AI\n" output)))))

  (it "checks welcome computer vs computer message"
    (let [output (with-out-str (sut/welcome-c-vs-c))]
      (should= "Welcome to Computer vs Computer
          For the first computer, choose your difficulty\n" output)))

  (it "checks second difficulty message"
    (let [output (with-out-str (sut/second-difficulty-message))]
      (should= "For the second computer, choose your difficulty\n" output)))

  (it "checks get-user xo 1"
    (with-in-str "1\n"
     (let [output (with-out-str (sut/get-user-x-o))]
      (should= "Please press 1 if Player 1 wants to be X and Player 2 wants to be O,
or anything else if Player 1 wants to be O and Player 2 wants to be X\n"
               output))))

  (it "checks get-user xo return"
    (with-in-str "\n"
      (let [output (with-out-str (sut/get-user-x-o))]
        (should= "Please press 1 if Player 1 wants to be X and Player 2 wants to be O,
or anything else if Player 1 wants to be O and Player 2 wants to be X\n"
                 output))))

  (it "checks get-user vs ai xo"
    (with-in-str "1\n"
      (let [output (with-out-str (sut/get-user-vs-ai-x-o))]
        (should=  "Please press 1 you want to be X or
  anything else if you want to be O\n"
                 output))))

  (it "checks get-user vs ai xo return"
    (with-in-str "\n"
      (let [output (with-out-str (sut/get-user-vs-ai-x-o))]
        (should=  "Please press 1 you want to be X or
  anything else if you want to be O\n"
                  output))))

  (it "should choose O"
    (with-out-str
      (with-in-str "3\n"
        (should= "O" (sut/get-user-vs-ai-x-o)))))

  (it "should print out x winner"
    (should= "X is the winner!"
             (sut/endgame-result-2 [1 2 3 "X" "X" "X" 7 8 9] "X" "O")))
  (it "knows O winner"
    (should= "O is the winner!"
             (sut/endgame-result-2 ["O" "O" "O" 4 5 6 7 8 9] "X" "O")))

  ;(it "gets player-1-token"
  ;
  ;    (let [output (with-out-str (with-in-str "1\n"(sut/get-player-1-token )))]
  ;      (should= :X output))))

  (it "\"gets player-1-token\""
    (with-out-str
      (with-in-str "1\n"
        (should= "X" (sut/get-player-1-token)))))

  (it "gets player-2-token"
    (let [output (sut/get-player-2-token "X")]
      (should= "O" output)))

  )
