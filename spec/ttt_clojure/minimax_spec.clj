(ns ttt-clojure.minimax-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.minimax :as sut]))

(describe "Minimax tests"

  (it "select the last and only move"
    (should= 9 (sut/next-move ["X" "O" "X"
                               "O" "X" "0"
                               "O" "X" 9])))

  (it "select the second to last move"
    (should= 2 (sut/next-move [1 2 "X"
                               "X" "X" "O"
                               "O" "X" "O"])))

  (it "tests end move"
    (should= 9 (sut/next-move ["X" 2 3
                               "O" "X" 6
                               "O" 8 9])))

  (it "tests end move"
    (let [board ["X" "O" "X"
                 "O" "X" 6
                 7 8 9]]
      (should-contain (sut/next-move board) [7 9])))

  (it "tests block"
    (should= 1 (sut/next-move [1 2 3
                               "O" "X" "X"
                               "O" 8 9])))

  (it "tests optimal move"
    (should= 2 (sut/next-move ["X" 2 3
                               "O" 5 6
                               7 8 9])))

  (it "tests first move"
    (let [board [1 2 3
                 4 5 6
                 7 8 9]]
      (should-contain (sut/next-move board) [1 3 7 9])))

  (context "4x4"
    (it "select the last and only move"
      (should= 16 (sut/next-move ["X" "O" "X" "X"
                                  "O" "X" "0" "X"
                                  "O" "X" "O" "X"
                                  "O" "O" "O" 16])))

    (it "selects the only winning move"
      (should= 16 (sut/next-move ["X" "O" "X" "X"
                                  "O" "X" "0" "X"
                                  "O" "X" "O" "X"
                                  "O" "O" 15 16])))

    (it "selects the winning move with 3 options"
      (should= 16 (sut/next-move ["X" "O" "X" "X"
                                  "O" "X" "0" "X"
                                  "O" 10 "O" "X"
                                  "O" "O" 15 16])))

    (it "selects X winning move over block"
      (should= 16 (sut/next-move [1 2 "X" "X"
                                  "O" "X" "O" "X"
                                  "O" 10 "O" "X"
                                  "O" "O" 15 16])))

    (it "selects O winning move over block"
      (should= 1 (sut/next-move-2 [1 2 "X" "X"
                                   "O" "X" "O" "X"
                                   "O" 10 "O" "X"
                                   "O" "O" 15 16])))

    (it "checks block"
      (should= 1 (sut/next-move-2 [1 2 "X" "X"
                                      "O" "X" "O" "X"
                                      "O" 10 "O" 12
                                      "O" "O" 15 16] )))

    (it "can win for X"
      (should= 16 (sut/next-move [1 2 3 "X"
                                       5 6 7 "X"
                                       "O" 10 11 "X"
                                       "O" 14 15 16])))

    (it "can win for O"
      (should= 1 (sut/next-move-2 [1 2 3 "X"
                                      "O" 6 7 "X"
                                      "O" 10 11 12
                                      "O" 14 15 16])))

    ;(it "selects winning move over blockX"
    ;  (should= 16 (sut/next-move [1 2 3 "X"
    ;                              "O" 6 7 "X"
    ;                              "O" 10 11 "X"
    ;                              "O" 14 15 16])))
    ;
    ;(it "selects winning move over blockO"
    ;  (should= 1 (sut/next-move-2 [1 2 3 "X"
    ;                               "O" 6 7 "X"
    ;                               "O" 10 11 "X"
    ;                               "O" 14 15 16])))
    ;
    ;(it "selects 2nd move O"
    ;  (should= 2 (sut/next-move-2 ["X" 2 3 4
    ;                               5 6 7 8
    ;                               9 10 11 12
    ;                               13 14 15 16])))

    ;custom hard-medium: random first few moves then minimax
    ; 1. decreasing depth instead of increasing it,
    ; putting a cap on depth
    ; 2. alpha beta pruning
    ; 3. another simpler version of minimax for the first few moves

    )
  #_(context "3D grid"
    (it "selects winning move"
      (should= 3 (sut/next-move ["X" "X" 3
                                   4 5 6
                                   7 8 9

                                   10 11 12
                                   13 14 15
                                   16 17 18

                                   19 20 21
                                   22 23 24
                                   25 26 27])))
    )
  )
