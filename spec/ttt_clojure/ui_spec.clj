(ns ttt-clojure.ui-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.data :as data]
            [ttt-clojure.game :as game]
            [ttt-clojure.ui :as sut]))

(describe "Updating board"
  (it "is an invalid move"
    (should= true (sut/invalid-move? 1 ["X" 2 3 4 5 6 7 8 9])))

  (it "is a valid move"
    (should= false (sut/invalid-move? 1 [1 2 3 4 5 6 7 8 9])))

  (it "is a valid input"
    (should= true (sut/valid-input? 9 [1 2 3 4 5 6 7 8 9])))

  (it "is not a valid input"
    (should= false (sut/valid-input? 90 [1 2 3 4 5 6 7 8 9])))

  (it "get-move"
    (with-in-str "1\n"
      (let [output (with-out-str (sut/get-move [1 2 3 4 5 6 7 8 9]))]
        (should= "Choose your position\n" output))))

  (it "should print out x winner"
    (should= "X is the winner!"
             (sut/endgame-result [1 2 3 "X" "X" "X" 7 8 9] "X" "O")))
  (it "knows O winner"
    (should= "O is the winner!"
             (sut/endgame-result ["O" "O" "O" 4 5 6 7 8 9] "X" "O")))

  (it "knows it's a tie"
    (should= "Womp, its a tie"
             (sut/endgame-result ["X" "O" "X"
                                  "O" "X" "X"
                                  "O" "X" "O"] "X" "O")))

  (it "knows game is still going"
    (should= nil
             (sut/endgame-result [1 2 3 4 5 6 7 8 9] "X" "O")))

  (it "tests print-board on 3x3"
    (should= "1 | 2 | 3\n4 | 5 | 6\n7 | 8 | 9\n"
             (with-out-str
               (sut/print-board [1 2 3 4 5 6 7 8 9]))))

  (it "tests print-board on 4x4"
    (should= "1 | 2 | 3 | 4\n5 | 6 | 7 | 8\n9 | 10 | 11 | 12\n13 | 14 | 15 | 16\n"
             (with-out-str
               (sut/print-board [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16]))))

  (it "tests print-board on 3x3x3"
    (should= "1 | 2 | 3\n4 | 5 | 6\n7 | 8 | 9\n\n10 | 11 | 12\n13 | 14 | 15\n16 | 17 | 18\n\n19 | 20 | 21\n22 | 23 | 24\n25 | 26 | 27\n"
             (with-out-str
               (sut/print-board [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
                                 16 17 18 19 20 21 22 23 24 25 26 27]))))

  (it "tests print end"
    (should= "O is the winner!\n"
             (with-out-str
               (sut/print-end ["O" 2 3
                               "O" "X" "X"
                               "O" 8 9] "O" "X"))))

  (it "tests print end"
    (should= "nil\n"
             (with-out-str
               (sut/print-end [1 2 3
                               "O" "X" "X"
                               "O" 8 9] "O" "X"))))

  (it "tests player 1 statement"
    (should= "Player 1's turn\n"
             (with-out-str
               (sut/player-statement 1))))

  (it "tests player 2 statement"
    (should= "Player 2's turn\n"
             (with-out-str
               (sut/player-statement 2))))

  (it "getting a 3x3 board"
    (with-out-str
      (with-in-str "3\n"
        (should= :3x3 (sut/get-game-board)))))

  (it "getting a 4x4 board"
    (with-out-str
      (with-in-str "4\n"
        (should= :4x4 (sut/get-game-board)))))

  (it "getting a 3x3x3 board"
    (with-out-str
      (with-in-str "9\n"
        (should= :3x3x3 (sut/get-game-board)))))

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

  (it "checks get player human"
    (with-out-str
      (with-in-str "1\n"
        (should= :human (sut/get-player 1)))))

  (it "checks get player ai"
    (with-out-str
      (with-in-str "2\n"
        (should= :ai (sut/get-player 1)))))

  (it "displays a message for player 1"
    (with-in-str "1\n"
      (let [output (with-out-str (sut/get-player 1))]
        (should= "If you would like Player 1 to be human press 1, or press 2 for AI?\n"
                 output))))

  (it "displays a message for player 2"
    (with-in-str "2\n"
      (let [output (with-out-str (sut/get-player 2))]
        (should= "If you would like Player 2 to be human press 1, or press 2 for AI?\n"
                 output))))

  (it "gets player-1-token X"
    (with-out-str
      (with-in-str "1\n"
        (should= "X" (sut/get-player-1-token)))))

  (it "gets player-1-token O"
    (with-out-str
      (with-in-str "\n"
        (should= "O" (sut/get-player-1-token)))))

  (it "displays a message for player 2"
    (with-in-str "2\n"
      (let [output (with-out-str (sut/get-player-1-token))]
        (should= "Please press 1 if you want Player 1 to be X and Player 2 to be O,
or anything else for Player 1 to be O and Player 2 to be X\n"
                 output))))

  (it "gets player-2-token O"
    (let [output (sut/get-player-2-token "X")]
      (should= "O" output)))

  (it "gets player-2-token X"
    (let [output (sut/get-player-2-token "O")]
      (should= "X" output)))

  (it "displays a message for continue last game"
    (with-in-str "1\n"
      (let [output (with-out-str (sut/continue-last-game?))]
        (should= "Would you like to finish your previous game?
  1 for Yes, anything else for No\n"
                 output))))

  (it "returns true for last game"
    (with-out-str
      (with-in-str "1\n"
        (let [output (sut/continue-last-game?)]
          (should output)))))

  (it "returns nil for last game"
    (with-out-str
      (with-in-str "\n"
        (let [output (sut/continue-last-game?)]
          (should-not output)))))

  (it "returns correct strings"
    (should= "Easy AI" (sut/difficulty-to-string :easy))
    (should= "Medium AI" (sut/difficulty-to-string :medium))
    (should= "Hard AI" (sut/difficulty-to-string :hard))
    (should= "Unknown" (sut/difficulty-to-string :merlo)))

  (it "prints player kind for non-AI player player-2"
    (let [player {:kind :human}]
      (should= "Player-2: Human \n"
               (with-out-str
                 (sut/print-player-kind "2" player)))))

  (it "prints player kind and difficulty for AI player"
    (let [player {:kind :ai :difficulty :hard}]
      (should= "Player-1: Hard AI \n"
               (with-out-str (sut/print-player-kind "1" player)))))

  (it "prints resumed game with two AI players"
    (let [player-1 {:kind :ai :token "O" :difficulty :hard}
          player-2 {:kind :ai :token "X" :difficulty :easy}
          game {:game-id 1 :player-1 player-1 :player-2 player-2}]
      (should= "\nResuming game:\nPlayer-1: Hard AI O\nPlayer-2: Easy AI X\n"
               (with-out-str (sut/print-resume-game game)))))

  (it "prints resumed game with two AI players"
    (let [player-1 {:kind :human :token "O"}
          player-2 {:kind :human :token "X"}
          game {:game-id 1 :player-1 player-1 :player-2 player-2}]
      (should= "\nResuming game:\nPlayer-1: Human O\nPlayer-2: Human X\n"
               (with-out-str (sut/print-resume-game game)))))

  (context "new print"
    (with-stubs)


    (it "returns game id statement"
      (should= "Game-ID: 3\n" (with-out-str (sut/print-id 3))))


    (it "returns game id and board"
      (should= "Game-ID: 3\n1 | 2 | 3\n4 | 5 | 6\n7 | 8 | 9\n"
               (with-out-str (sut/print-id-and-board 3 {:player-1       {:kind :human :token "O"}
                                                              :player-2 {:kind :human :token "X"}
                                                              :size     :3x3 :moves []}))))



    )

  )
