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
    (should (sut/o-wins ["O" 2 3 4 "O" 6 7 8 "O"])))

  (context "4x4"

    (it "makes an empty grid"
      (should= "1 | 2 | 3 | 4\n5 | 6 | 7 | 8\n9 | 10 | 11 | 12\n13 | 14 | 15 | 16"
               (sut/display [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16])))

    (it "makes grid with a few X's"
      (should= "X | X | X | X\n5 | 6 | 7 | 8\n9 | 10 | 11 | 12\n13 | 14 | 15 | 16"
               (sut/display ["X" "X" "X" "X" 5 6 7 8 9 10 11 12 13 14 15 16])))

    (it "makes grid with all X's and O's"
      (should= "X | O | X | O\nO | X | O | X\nX | O | X | O\nO | X | O | X"
               (sut/display ["X" "O" "X" "O" "O" "X" "O" "X" "X" "O" "X" "O" "O" "X" "O" "X"])))

    (it "checks winning combo "
      (should (sut/winning-combo? ["X" "X" "X" "X" 5 6 7 8 9 10 11 12 13 14 15]
                                  [0 1 2 3] "X")))

    (it "checks a non winning combo"
      (should-not (sut/winning-combo? ["X" "X" "X" "O" 5 6 7 8 9 10 11 12 13 14 15]
                                      [0 1 2 3] "X")))

    (it "checks x-wins top row"
      (should (sut/x-wins ["X" "X" "X" "X" 5 6 7 8 9 10 11 12 13 14 15 16])))

    (it "checks x-wins second to top row"
      (should (sut/x-wins [1 2 3 4 "X" "X" "X" "X" 9 10 11 12 13 14 15 16])))

    (it "checks x-wins right column"
      (should (sut/x-wins [1 2 3 "X" 5 6 7 "X" 9 10 11 "X" 13 14 15 "X"])))

    (it "checks x-wins second to right column"
      (should (sut/x-wins [1 2 "X" 4 5 6 "X" 8 9 10 "X" 12 13 14 "X" 16])))

    (it "checks x-wins front diagonal"
      (should (sut/x-wins ["X" 2 3 4 5 "X" 7 8 9 10 "X" 12 13 14 15 "X"])))

    (it "checks o-wins bottom row"
      (should (sut/o-wins [1 2 3 4 5 6 7 8 9 10 11 12 "O" "O" "O" "O"])))

    (it "checks o-wins second to bottom row"
      (should (sut/o-wins [1 2 3 4 5 6 7 8 "O" "O" "O" "O" 13 14 15 16])))

    (it "checks o-wins left column"
      (should (sut/o-wins ["O" 2 3 4 "O" 6 7 8 "O" 10 11 12 "O" 14 15 16])))

    (it "checks o-wins second to left column"
      (should (sut/o-wins [1 "O" 3 4 5 "O" 7 8 9 "O" 11 12 13 "O" 15 16])))

    (it "checks o-wins back diagonal"
      (should (sut/o-wins [1 2 3 "O" 5 6 "O" 8 9 "O" 11 12 "O" 14 15 16])))

    (it "checks a tie"
      (should (sut/tie ["X" "O" "X" "O"
                        "O" "O" "X" "X"
                        "X" "X" "O" "O"
                        "O" "X" "O" "X"])))
    (it "checks if o doesnt win"
      (should-not (sut/o-wins ["X" "O" "X" "O" "O" "O" "X" "X" "X" "X" "O" "O" "O" "X" "O" "X"])))

    (it "checks if x doesnt win"
      (should-not (sut/x-wins ["X" "O" "X" "O" "O" "O" "X" "X" "X" "X" "O" "O" "O" "X" "O" "X"])))

    (it "checks not a tie"
      (should-not (sut/tie ["X" 2 "X" "O" "O" "O" "X" "X" "X" "X" "O" "O" "O" "X" "O" "X"])))

    )
  )
