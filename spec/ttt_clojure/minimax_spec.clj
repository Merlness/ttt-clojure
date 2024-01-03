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

  (it "tests end move"
    (let [board [1 2 3
                 4 5 6
                 7 8 9]]
      (should-contain (sut/next-move board) [1 3 7 9])))

  )
