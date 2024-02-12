(ns ttt-clojure.minimax-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.minimax :as sut]))

(defn maximize [board maximizing-token minimizing-token]
  (second (sut/maximize board (sut/set-depth board) maximizing-token minimizing-token)))
;command r to replace
;the tests that fail should move to computer (probably)
(describe "Minimax-tokens tests"
  ;pass player instead of tokens and a board instead of an array


  (it "select the last and only move"
    (should= 9 (maximize ["X" "O" "X"
                          "O" "X" "0"
                          "O" "X" 9] "X" "O")))

  (it "select the second to last move"
    (should= 2 (maximize [1 2 "X"
                          "X" "X" "O"
                          "O" "X" "O"] "X" "O")))

  (it "tests end move"
    (should= 9 (maximize ["X" 2 3
                          "O" "X" 6
                          "O" 8 9] "X" "O")))

  (it "tests block end move O"
    (should= 9 (maximize ["X" 2 3
                          "O" "X" 6
                          "O" 8 9] "O" "X")))

  (it "tests block end move O"
    (should= 9 (maximize ["O" 2 3
                          "X" "O" 6
                          "X" 8 9] "O" "X")))

  (it "tests block end move A"
    (should= 9 (maximize ["A" 2 3
                          "B" "A" 6
                          "B" 8 9] "A" "B")))

  (it "tests end move"
    (let [board ["X" "O" "X"
                 "O" "X" 6
                 7 8 9]]
      (should-contain (maximize board "X" "O") [7 9])))


  (it "tests block"
    (should= 1 (maximize [1 2 3
                          "O" "X" "X"
                          "O" 8 9] "X" "O")))

  (it "tests win O"
    (should= 1 (maximize [1 2 3
                          "O" "X" "X"
                          "O" 8 9] "O" "X")))

  (it "tests optimal move"
    (should= 2 (maximize ["X" 2 3
                          "O" 5 6
                          7 8 9] "X" "O")))

  (it "tests first move"
    (let [board [1 2 3
                 4 5 6
                 7 8 9]]
      (should-contain (maximize board "X" "O") [1 3 7 9])))

  (it "tests first move O"
    (let [board [1 2 3
                 4 5 6
                 7 8 9]]
      (should-contain (maximize board "O" "X") [1 3 7 9])))

  (context "4x4"
    (it "select the last and only move"
      (should= 16 (maximize ["X" "O" "X" "X"
                             "O" "X" "0" "X"
                             "O" "X" "O" "X"
                             "O" "O" "O" 16] "X" "O")))

    (it "selects the only winning move"
      (should= 16 (maximize ["X" "O" "X" "X"
                             "O" "X" "0" "X"
                             "O" "X" "O" "X"
                             "O" "O" 15 16] "X" "O")))

    (it "selects the winning move with 3 options"
      (should= 16 (maximize ["X" "O" "X" "X"
                             "O" "X" "0" "X"
                             "O" 10 "O" "X"
                             "O" "O" 15 16] "X" "O")))

    (it "selects X winning move over block"
      (should= 16 (maximize [1 2 "X" "X"
                             "O" "X" "O" "X"
                             "O" 10 "O" "X"
                             "O" "O" 15 16] "X" "O")))


    (it "selects O winning move over block"
      (should= 1 (maximize [1 2 "X" "X"
                            "O" "X" "O" "X"
                            "O" 10 "O" "X"
                            "O" "O" 15 16] "O" "X")))

    ;(it "sut/minimize check"
    ;  (should= 1 (sut/find-next-move [1 2 "X" "X"
    ;                                  "O" "X" "O" "X"
    ;                                  "O" 10 "O" "X"
    ;                                  "O" "O" 15 16] sut/minimize "X" "O")))

    (it "checks block for X"
      (should= 1 (maximize [1 2 "X" "X"
                            "O" "X" "O" "X"
                            "O" 10 "O" 12
                            "O" "O" 15 16] "X" "O")))

    (it "can win for X"
      (should= 16 (maximize [1 2 3 "X"
                             5 6 7 "X"
                             "O" 10 11 "X"
                             "O" 14 15 16] "X" "O")))

    (it "can win for O"
      (should= 1 (maximize [1 2 3 "X"
                            "O" 6 7 "X"
                            "O" 10 11 12
                            "O" 14 15 16] "O" "X")))

    (it "selects winning move over blockX"
      (should= 16 (maximize [1 2 3 "X"
                             "O" 6 7 "X"
                             "O" 10 11 "X"
                             "O" 14 15 16] "X" "O")))

    (it "selects winning move over blockO"
      (should= 1 (maximize [1 2 3 "X"
                            "O" 6 7 "X"
                            "O" 10 11 "X"
                            "O" 14 15 16] "O" "X")))

    (it "selects 2nd move O"
      (should= 2 (maximize ["X" 2 3 4
                            5 6 7 8
                            9 10 11 12
                            13 14 15 16] "O" "X")))

    (it "checks block max"
      (should= 1 (maximize [1 2 "X" "X"
                            "O" "X" "O" "X"
                            "O" 10 "O" 12
                            "O" "O" 15 16] "X" "O")))


    ;(it "can't block"
    ;  (should= nil (sut/win-or-block [1 2 "X" "X"
    ;                                  5 "X" "O" "X"
    ;                                  "O" 10 "O" 12
    ;                                  "O" "O" 15 16] "X" "O")))
    ;
    ;(it "can win for X"
    ;  (should= 16 (sut/win-or-block [1 2 3 "X"
    ;                                 5 6 7 "X"
    ;                                 "O" 10 11 "X"
    ;                                 "O" 14 15 16] "O" "X")))

    ;(it "can win for X"
    ;  (should= 16 (maximize [1 2 3 "X"
    ;                         5 6 7 "X"
    ;                         "O" 10 11 "X"
    ;                         "O" 14 15 16] "O" "X")))

    ;(it "can win for O"
    ;  (should= 1 (sut/win-or-block [1 2 3 "X"
    ;                                "O" 6 7 "X"
    ;                                "O" 10 11 12
    ;                                "O" 14 15 16] "X" "O")))

    ;(it "can win for O"
    ;  (should= 1 (maximize [1 2 3 "X"
    ;                                "O" 6 7 "X"
    ;                                "O" 10 11 12
    ;                                "O" 14 15 16] "X" "O")))


    )

  (context "3D grid"
    (it "selects winning move"
      (should= 3 (maximize ["X" "X" 3
                            "O" "O" 6
                            7 8 9

                            10 11 12
                            13 14 15
                            16 17 18

                            19 20 21
                            22 23 24
                            25 26 27] "X" "O")))

    (it "selects blocking move"
      (should= 3 (maximize ["X" "X" 3
                            "O" 5 6
                            7 "O" 9

                            10 11 12
                            13 14 15
                            16 17 18

                            19 20 21
                            22 23 24
                            25 26 27] "O" "X")))

    ;(it "selects optimal 2nd move"
    ;  (should= 14 (sut/helper-3d ["X" 2 3
    ;                              4 5 6
    ;                              7 8 9
    ;
    ;                              10 11 12
    ;                              13 14 15
    ;                              16 17 18
    ;
    ;                              19 20 21
    ;                              22 23 24
    ;                              25 26 27])))

    ;(it "helper provides first move"
    ;  (should= 14 (sut/helper-3d [1 2 3
    ;                              4 5 6
    ;                              7 8 9
    ;
    ;                              10 11 12
    ;                              13 14 15
    ;                              16 17 18
    ;
    ;                              19 20 21
    ;                              22 23 24
    ;                              25 26 27])))

    ;(it "helper doesn't work again"
    ;  (should-not (sut/helper-3d ["X" "X" 3
    ;                              4 5 6
    ;                              7 8 9
    ;
    ;                              10 11 12
    ;                              13 14 15
    ;                              16 17 18
    ;
    ;                              19 20 21
    ;                              22 23 24
    ;                              25 26 27])))

    (it "selects 3rd move"
      (should= 3 (maximize ["X" "O" 3
                            4 5 6
                            7 8 9

                            10 11 12
                            13 14 15
                            16 17 18

                            19 20 21
                            22 23 24
                            25 26 27] "O" "X")))

    ;(it "selects blocking move"
    ;  (should= 7 (sut/win-or-block ["X" "O" "X"
    ;                                "O" "X" 6
    ;                                7 8 9
    ;
    ;                                10 11 12
    ;                                13 14 15
    ;                                16 17 18
    ;
    ;                                19 20 21
    ;                                22 23 24
    ;                                25 26 27] "O" "X")))
    ;create helper function to print values and moves
    ;value to number of losing/winning opps
    (it "selects winning move"
      (should= 20 (maximize ["X" "O" "X"
                             "O" "X" 6
                             7 8 9

                             10 "O" 12
                             13 14 15
                             16 17 18

                             19 20 21
                             22 23 24
                             25 26 27] "O" "X")))

    )
  )

;; Player 1: human
;; Player 2: easy AI
