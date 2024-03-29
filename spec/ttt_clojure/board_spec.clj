(ns ttt-clojure.board-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.board :as sut]))

(describe "Making a grid"
  (with-stubs)

  (it "checks size 3x3"
    (should= 3 (sut/size [1 2 3 4 5 6 7 8 9])))

  (it "checks size 4x4"
    (should= 4 (sut/size [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16])))

  (it "checks size 3x3x3"
    (should= 3 (sut/size-3d [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16
                             17 18 19 20 21 22 23 24 25 26 27])))

  (it "checks 2d"
    (should (sut/two-dimensional? [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16])))

  (it "checks 3d"
    (should-not (sut/two-dimensional? [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16
                                       17 18 19 20 21 22 23 24 25 26 27])))

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
    (should (sut/winner? ["X" "X" "X" 4 5 6 7 8 9] "X")))

  (it "checks for false winning combos"
    (should-not (sut/winner? ["X" "X" 3 4 5 6 7 8 9] "X")))

  (it "checks X-wins top row"
    (should (sut/token-wins ["X" "X" "X" 4 5 6 7 8 9] "X")))

  (it "checks X-wins right column"
    (should (sut/token-wins [1 2 "X" 4 5 "X" 7 8 "X"] "X")))

  (it "checks X-wins back diagonal"
    (should (sut/token-wins [1 2 "X" 4 "X" 6 "X" 8 9] "X")))

  (it "checks O-wins bottom row"
    (should (sut/token-wins [1 2 3 4 5 6 "O" "O" "O"] "O")))

  (it "checks O-wins isn't returning something incorrectly"
    (should-not (sut/token-wins [1 2 3 4 5 6 "O" "X" "O"] "O")))

  (it "checks O-wins middle column"
    (should (sut/token-wins [1 "O" 3 4 "O" 6 7 "O" 9] "O")))

  (it "checks O-wins front diagonal"
    (should (sut/token-wins ["O" 2 3 4 "O" 6 7 8 "O"] "O")))

  (context "game-over?"
    (it "game over with player X"
      (let [x {:token "X"}
            o {:token "O"}]
        (should (sut/game-over? ["X" "X" "X" 4 5 6 7 8 9] x o))))

    (it "game over with player O"
      (let [x {:token "X"}
            o {:token "O"}]
        (should (sut/game-over? ["O" "O" "O" 4 5 6 7 8 9] x o))))
    )

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
      (should (sut/winner? ["X" "X" "X" "X" 5 6 7 8 9 10 11 12 13 14 15 16] "X")))

    (it "checks a non winning combo"
      (should-not (sut/winner? ["X" "X" 3 "O" 5 6 7 8 9 10 11 12 13 14 15 16] "X")))

    (it "checks x-wins top row"
      (should (sut/token-wins ["X" "X" "X" "X" 5 6 7 8 9 10 11 12 13 14 15 16] "X")))

    (it "checks x-wins second to top row"
      (should (sut/token-wins [1 2 3 4 "X" "X" "X" "X" 9 10 11 12 13 14 15 16] "X")))

    (it "checks x-wins right column"
      (should (sut/token-wins [1 2 3 "X" 5 6 7 "X" 9 10 11 "X" 13 14 15 "X"] "X")))

    (it "checks x-wins second to right column"
      (should (sut/token-wins [1 2 "X" 4 5 6 "X" 8 9 10 "X" 12 13 14 "X" 16] "X")))

    (it "checks x-wins front diagonal"
      (should (sut/token-wins ["X" 2 3 4 5 "X" 7 8 9 10 "X" 12 13 14 15 "X"] "X")))

    (it "checks o-wins bottom row"
      (should (sut/token-wins [1 2 3 4 5 6 7 8 9 10 11 12 "O" "O" "O" "O"] "O")))

    (it "checks o-wins second to bottom row"
      (should (sut/token-wins [1 2 3 4 5 6 7 8 "O" "O" "O" "O" 13 14 15 16] "O")))

    (it "checks o-wins left column"
      (should (sut/token-wins ["O" 2 3 4 "O" 6 7 8 "O" 10 11 12 "O" 14 15 16] "O")))

    (it "checks o-wins second to left column"
      (should (sut/token-wins [1 "O" 3 4 5 "O" 7 8 9 "O" 11 12 13 "O" 15 16] "O")))

    (it "checks o-wins back diagonal"
      (should (sut/token-wins [1 2 3 "O" 5 6 "O" 8 9 "O" 11 12 "O" 14 15 16] "O")))

    (it "checks a tie"
      (should (sut/tie ["X" "O" "X" "O"
                        "O" "O" "X" "X"
                        "X" "X" "O" "O"
                        "O" "X" "O" "X"])))
    (it "checks if o doesnt win"
      (should-not (sut/token-wins ["X" "O" "X" "O" "O" "O" "X" "X" "X" "X" "O" "O" "O" "X" "O" "X"] "O")))

    (it "checks if x doesnt win"
      (should-not (sut/token-wins ["X" "O" "X" "O" "O" "O" "X" "X" "X" "X" "O" "O" "O" "X" "O" "X"] "X")))

    (it "checks not a tie"
      (should-not (sut/tie ["X" 2 "X" "O" "O" "O" "X" "X" "X" "X" "O" "O" "O" "X" "O" "X"])))

    (it "checks board 3x3"
      (with-out-str
        (with-in-str "3\n"
          (should= [1 2 3 4 5 6 7 8 9] (sut/board-size :3x3) ))))

    (it "checks board size 4x4"
      (with-out-str
        (with-in-str "4\n"
          (should= [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16] (sut/board-size :4x4)))))

    (it "checks board size 3x3x3"
      (with-out-str
        (with-in-str "9\n"
          (should= [1 2 3 4 5 6 7 8 9
                    10 11 12 13 14 15 16 17 18
                    19 20 21 22 23 24 25 26 27]
                   (sut/board-size :3x3x3)))))
    )

  (context "3x3x3"
    (it "checks the 3x3x3 display"
      (should= "1 | 2 | 3\n4 | 5 | 6\n7 | 8 | 9\n\n10 | 11 | 12
13 | 14 | 15\n16 | 17 | 18
\n19 | 20 | 21\n22 | 23 | 24\n25 | 26 | 27"
               (sut/display [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18
                             19 20 21 22 23 24 25 26 27])))

    (it "checks  size 3d 3x3"
      (should= 3 (sut/size-3d [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18
                               19 20 21 22 23 24 25 26 27])))

    (it "checks  size 3d 4x4"
      (should= 4 (sut/size-3d [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18
                               19 20 21 22 23 24 25 26 27 28 29 30 31 32 33
                               34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49
                               50 51 52 53 54 55 56 57 58 59 60 61 62 63 64])))

    (it "checks back diagonal through"
      (should= [[3 11 19] [6 14 22] [9 17 25]]
               (sut/back-diagonal-through
                 [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18
                  19 20 21 22 23 24 25 26 27])))

    (it "checks front diagonal through"
      (should= [[1 11 21] [4 14 24] [7 17 27]]
               (sut/front-diagonal-through [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18
                                            19 20 21 22 23 24 25 26 27])))

    (it "checks same space all faces"
      (should= [[1 10 19] [2 11 20] [3 12 21] [4 13 22] [5 14 23]
                [6 15 24] [7 16 25] [8 17 26] [9 18 27]]
               (sut/same-space-all-faces [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18
                                          19 20 21 22 23 24 25 26 27])))
    (it "checks front diagonal across"
      (should= [[1 13 25] [2 14 26] [3 15 27]]
               (sut/front-diagonal-across [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18
                                           19 20 21 22 23 24 25 26 27])))
    (it "checks back diagonal across"
      (should= [[7 13 19] [8 14 20] [9 15 21]]
               (sut/back-diagonal-across [1 2 3 4 5 6 7 8 9
                                          10 11 12 13 14 15 16 17 18
                                          19 20 21 22 23 24 25 26 27])))

    (it "checks back diagonal across X wins"
      (should (sut/winner? [1 2 3 4 5 6 7 "X" 9
                            10 11 12 13 "X" 15 16 17 18
                            19 "X" 21 22 23 24 25 26 27]
                           "X")))

    (it "checks front diagonal across X wins"
      (should (sut/winner? [1 2 "X" 4 5 6 7 8 9
                            10 11 12 13 14 "X" 16 17 18
                            19 20 21 22 23 24 25 26 "X"]
                           "X")))

    (it "checks basic row for x wins"
      (should (sut/winner? ["X" "X" "X" 4 5 6 7 8 9
                            10 11 12 13 14 15 16 17 18
                            19 20 21 22 23 24 25 26 27] "X")))

    (it "checks basic column for O wins"
      (should (sut/winner? [1 2 3 4 5 6 7 8 9
                            10 11 "O" 13 14 "O" 16 17 "O"
                            19 20 21 22 23 24 25 26 27] "O")))

    (it "checks same space O wins"
      (should (sut/winner? [1 2 3 4 "O" 6 7 8 9
                            10 11 12 13 "O" 15 16 17 18
                            19 20 21 22 "O" 24 25 26 27] "O")))

    (it "checks basic column for O wins"
      (should (sut/winner? [1 2 3 4 5 6 7 8 9
                            10 11 "O" 13 14 "O" 16 17 "O"
                            19 20 21 22 23 24 25 26 27] "O")))

    (it "checks edge case O wins"
      (should (sut/winner? ["O" 2 3 4 5 6 7 8 9
                            10 11 12 13 "O" 15 16 17 18
                            19 20 21 22 23 24 25 26 "O"] "O")))

    (it "checks false edge case"
      (should-not (sut/winner? [1 "O" 3 4 5 6 7 8 9
                                10 11 12 13 "O" 15 16 17 18
                                19 20 21 22 23 24 25 26 "O"] "O")))
    )

  )
