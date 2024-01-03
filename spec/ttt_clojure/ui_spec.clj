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
    (should= "Congrats O is the winner!"
             (sut/endgame-result ["O" "O" "O" 4 5 6 7 8 9])))

  (it "knows X winner"
    (should= "Congrats X is the winner!"
             (sut/endgame-result [1 2 3 "X" "X" "X" 7 8 9])))

  (it "knows its a tie"
    (should= "Womp, its a tie"
             (sut/endgame-result ["X" "O" "X"
                                  "O" "X" "X"
                                  "O" "X" "O"])))

  (it "knows game is still going"
    (should= nil
             (sut/endgame-result [1 2 3 4 5 6 7 8 9])))
  )
