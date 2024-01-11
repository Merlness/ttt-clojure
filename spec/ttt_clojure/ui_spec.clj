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
      (let [output (with-out-str (sut/get-move))]
        (should= "Choose your position\n" output))))

  (it "updates board for X"
    (with-redefs [read-line (fn [] "1")]
      (with-out-str
        (should= ["X" 2 3 4 5 6 7 8 9]
                 (sut/update-board [1 2 3 4 5 6 7 8 9] true)))))

  (it "updates board for O"
    (with-redefs [read-line (fn [] "1")]
      (with-out-str
        (should= ["O" 2 3 4 5 6 7 8 9]
                 (sut/update-board [1 2 3 4 5 6 7 8 9] false)))))

  (it "updates board after two moves"
    (with-in-str "1\n2\n"
      (with-out-str
        (should= ["X" "O" 3 4 5 6 7 8 9]
                 (-> (sut/update-board [1 2 3 4 5 6 7 8 9] true)
                     (sut/update-board false))))))

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
          (should= :easy (sut/get-user-input-difficulty)))))

    (it "medium"
      (with-out-str
        (with-in-str "2\n"
          (should= :medium (sut/get-user-input-difficulty)))))

    (it "hard"
      (with-out-str
        (with-in-str "3\n"
          (should= :hard (sut/get-user-input-difficulty)))))

    (it "invalid"
      (with-out-str
        (with-in-str "a\n1\n"
          (should= :easy (sut/get-user-input-difficulty)))))

    (it "displays a message"
      (with-in-str "1\n"
        (let [output (with-out-str (sut/get-user-input-difficulty))]
          (should= "Please press 1 for an easy AI
             2 for a medium AI
             3 for a an impossible AI\n" output))))
    )

  (it "checks welcome computer vs computer message"
    (let [output (with-out-str (sut/welcome-c-vs-c))]
      (should= "Welcome to Computer vs Computer
          For the first computer, choose your difficulty\n" output)))

  (it "checks second difficulty message"
    (let [output (with-out-str (sut/second-difficulty-message))]
      (should= "For the second computer, choose your difficulty\n" output)))
  )
