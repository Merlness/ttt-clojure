(ns ttt-clojure.easy-comp-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.easy-comp :as sut]))

(describe "easy computer"
  (it "finds available actions"
    (let [board [1 2 3
                 4 5 6
                 7 8 9]]
      (should= (sut/find-actions board)
                      '(1 2 3 4 5 6 7 8 9))))

  (it "finds less available actions"
    (let [board ["X" "O" "X"
                 4 5 6
                 7 8 9]]
      (should= (sut/find-actions board)
                      '(4 5 6 7 8 9))))

  (it "finds no actions"
    (let [board ["X" "O" "X"
                 "X" "X" "X"
                 "X" "O" "O"]]
      (should= (sut/find-actions board)
               '())))

  (it "generates a random number"
      (should-contain (sut/find-rand-int [1 2 3 4 5 6 7 8 9])
               [1 2 3 4 5 6 7 8 9]))

  (it "generates a random number form all possibilities"
    (should-contain (sut/find-rand-int '(4 5 6 7 8 9))
                    [4 5 6 7 8 9]))

  (it "generates a random number from less possibilities"
    (should-contain (sut/find-rand-int '(4 5 6 7 8 9))
                    [4 5 6 7 8 9]))

  (it "generates a random number from even less possibilities"
    (should-contain (sut/find-rand-int '(4 7 9)) [4 7 9]))

  (it "generates a random number from two possibilities"
    (should-contain (sut/find-rand-int '(4 9)) [4 9]))

  (it "generates a random number from 1 possibility"
    (should= 9 (sut/find-rand-int '(9))))

  (it "chooses a random move from an empty board"
    (should-contain (sut/place-easy-move [1 2 3 4 5 6 7 8 9])
                    [1 2 3 4 5 6 7 8 9]))

  (it "chooses a random move  after 3 moves"
    (should-contain (sut/place-easy-move [4 5 6 7 8 9]) [4 5 6 7 8 9]))

  (it "chooses a random move after 6 moves"
    (should-contain (sut/place-easy-move [4 7 9]) [4 7 9]))

  (it "chooses a random move after 7 moves"
    (should-contain (sut/place-easy-move [4 9]) [4 9]))

  (it "chooses a random move after 7 moves"
    (should= 4 (sut/place-easy-move [4])))

  ;(it "generates a random number from nothing"
  ;  (should= nil  (sut/find-rand-int '())))

  #_(it "finds less available actions"
    (let [board ["X" "O" "X"
                 4 5 6
                 7 8 9]]
      (should-contain (sut/find-actions board)
                      (4 5 6 7 8 9))))
  )
