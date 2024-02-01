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

  (it "should print out x winner"
    (should= "X is the winner!"
             (sut/endgame-result-2 [1 2 3 "X" "X" "X" 7 8 9] "X" "O")))
  (it "knows O winner"
    (should= "O is the winner!"
             (sut/endgame-result-2 ["O" "O" "O" 4 5 6 7 8 9] "X" "O")))

  (it "knows it's a tie"
    (should= "Womp, its a tie"
             (sut/endgame-result-2 ["X" "O" "X"
                                    "O" "X" "X"
                                    "O" "X" "O"] "X" "O")))

  (it "knows game is still going"
    (should= nil
             (sut/endgame-result-2 [1 2 3 4 5 6 7 8 9] "X" "O")))

  (it "\"gets player-1-token\""
    (with-out-str
      (with-in-str "1\n"
        (should= "X" (sut/get-player-1-token)))))

  (it "gets player-2-token"
    (let [output (sut/get-player-2-token "X")]
      (should= "O" output)))

  )
