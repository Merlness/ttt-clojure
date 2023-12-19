(ns ttt-clojure.board-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.board :as sut]))

(describe "Making a grid"
  (with-stubs)

  (it "makes a grid full of numbers"
    (should= "1 | 2 | 3\n4 | 5 | 6\n7 | 8 | 9"
             (sut/display [1 2 3 4 5 6 7 8 9])))

  (it "makes a grid with mostly numbers and a few X's"
    (should= "X | X | X\n4 | 5 | 6\n7 | 8 | 9"
             (sut/display ["X" "X" "X" 4 5 6 7 8 9])))

  (it "makes a grid with all Xs and Os"
    (should= "X | X | X\nO | O | O\nX | O | X"
             (sut/display ["X" "X" "X" "O" "O" "O" "X" "O" "X"])))

  (it "checks winning combo"
    (should (sut/winning-combo? ["X" "X" "X" 4 5 6 7 8 9] [0 1 2] "X")))

  (it "checks for false winning combos"
    (should-not (sut/winning-combo? ["X" "X" 3 4 5 6 7 8 9] [0 1 2] "X")))

  (it "checks X-wins top row"
    (should (sut/x-wins ["X" "X" "X" 4 5 6 7 8 9])))

  (it "checks X-wins right column"
    (should (sut/x-wins [1 2 "X" 4 5 "X" 7 8 "X"])))

  (it "checks X-wins back diagonal"
    (should (sut/x-wins [1 2 "X" 4 "X" 6 "X" 8 9])))

  (it "checks O-wins bottom row"
    (should (sut/o-wins [1 2 3 4 5 6 "O" "O" "O"])))

  (it "checks O-wins isn't returning something incorrectly"
    (should-not (sut/o-wins [1 2 3 4 5 6 "O" "X" "O"])))

  (it "checks O-wins middle column"
    (should (sut/o-wins [1 "O" 3 4 "O" 6 7 "O" 9])))

  (it "checks O-wins front diagonal"
    (should (sut/o-wins ["O" 2 3 4 "O" 6 7 8 "O"]))
    ))
