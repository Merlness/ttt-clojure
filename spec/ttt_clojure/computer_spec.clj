(ns ttt-clojure.computer-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.minimax :as mm]
            [ttt-clojure.ui :as ui]
            [ttt-clojure.board :as board]
            [ttt-clojure.computer :as sut]))

(defn game-result [grid maximizing-token minimizing-token]
  (cond
    (board/token-wins grid maximizing-token) :win
    (board/token-wins grid minimizing-token) :loss
    (board/tie grid) :tie))

(defn place-ai-move [board maximizing-token minimizing-token]
  (let [move (sut/next-move board maximizing-token minimizing-token)]
    (ui/place-xo board move maximizing-token)))

(declare play-game-computer-second)

(defn- collect-ai-results [results board maximizing-token minimizing-token]
  (let [board (place-ai-move board maximizing-token minimizing-token)
        result (game-result board maximizing-token minimizing-token)]
    (if result (do (conj results result)) (concat results (play-game-computer-second board maximizing-token minimizing-token)))))

(defn collect-move-results [results move board maximizing-token minimizing-token]
  (let [board (ui/place-xo board move minimizing-token)
        result (game-result board maximizing-token minimizing-token)]
    (if result (do (conj results result)) (collect-ai-results results board maximizing-token minimizing-token))))

(defn- play-game-computer-second [board maximizing-token minimizing-token]
  (reduce #(collect-move-results %1 %2 board maximizing-token minimizing-token) [] (board/find-available-moves board)))

(defn- play-game-computer-first [board maximizing-token minimizing-token]
  (play-game-computer-second (place-ai-move board maximizing-token minimizing-token) maximizing-token minimizing-token))


(describe "Comp Testing"
  (with-stubs)

  ;(it "tests minimax going first"
  ;  (let [results (play-game-computer-first [1 2 3 4 5 6 7 8 9] "X" "O")]
  ;    ;(println "results " (count results))
  ;    ;(println "results " (count results) results)
  ;    (should-not-contain :loss results)))
  ;
  ;(it "tests minimax going second"
  ;  (let [results (play-game-computer-second [1 2 3 4 5 6 7 8 9] "X" "O")]
  ;    ;(println "results " (count results))
  ;    ;(println "results " (count results) results)
  ;    (should (not-any? #(= % :loss) results))))

  (it "makes an easy move"
    (with-redefs [sut/place-easy-move (stub :next-move {:return 1})]
      (should= 1 (sut/ai-move [1 2 3 4 5 6 7 8 9] "O" "X" :easy))
      (should-have-invoked :next-move {:with [[1 2 3 4 5 6 7 8 9]]})))

  ;(it "makes a medium move"
  ;  (with-redefs [sut/place-easy-move (stub :next-easy-move {:return 1})
  ;                mm/next-move (stub :next-hard-move {:return 2})]
  ;    (should= 2 (sut/ai-move [1 2 3 4 5 6 7 8 9] "O" "X" :medium))
  ;    (should-have-invoked :next-hard-move {:with [[1 2 3 4 5 6 7 8 9] "O" "X"]})))
  ;
  ;(it "makes a hard move"
  ;  (with-redefs [mm/next-move (stub :next-move {:return 1})]
  ;    (should= 1 (sut/ai-move [1 2 3 4 5 6 7 8 9] "O" "X" :hard))
  ;    (should-have-invoked :next-move {:with [[1 2 3 4 5 6 7 8 9] "O" "X"]})))


  (context "easy computer"
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
      (should= 4 (sut/place-easy-move [4]))))


  (context "logic to help minimax"


    (it "can't block"
      (should= nil (sut/win-or-block [1 2 "X" "X"
                                      5 "X" "O" "X"
                                      "O" 10 "O" 12
                                      "O" "O" 15 16] "X" "O")))

    (it "can win for X"
      (should= 16 (sut/win-or-block [1 2 3 "X"
                                     5 6 7 "X"
                                     "O" 10 11 "X"
                                     "O" 14 15 16] "O" "X")))


    (it "can win for O"
      (should= 1 (sut/win-or-block [1 2 3 "X"
                                    "O" 6 7 "X"
                                    "O" 10 11 12
                                    "O" 14 15 16] "X" "O")))


    (context "3D grid"


      (it "selects optimal 2nd move"
        (should= 14 (sut/helper-3d-logic ["X" 2 3
                                          4 5 6
                                          7 8 9

                                          10 11 12
                                          13 14 15
                                          16 17 18

                                          19 20 21
                                          22 23 24
                                          25 26 27])))

      (it "helper provides first move"
        (should= 14 (sut/helper-3d-logic [1 2 3
                                          4 5 6
                                          7 8 9

                                          10 11 12
                                          13 14 15
                                          16 17 18

                                          19 20 21
                                          22 23 24
                                          25 26 27])))

      (it "helper doesn't work again"
        (should-not (sut/helper-3d-logic ["X" "X" 3
                                          4 5 6
                                          7 8 9

                                          10 11 12
                                          13 14 15
                                          16 17 18

                                          19 20 21
                                          22 23 24
                                          25 26 27])))


      (it "selects blocking move"
        (should= 7 (sut/win-or-block ["X" "O" "X"
                                      "O" "X" 6
                                      7 8 9

                                      10 11 12
                                      13 14 15
                                      16 17 18

                                      19 20 21
                                      22 23 24
                                      25 26 27] "O" "X")))
      ;create helper function to print values and moves
      ;value to number of losing/winning opps
      (it "selects winning move"
        (should= 20 (sut/next-move ["X" "O" "X"
                                    "O" "X" 6
                                    7 8 9

                                    10 "O" 12
                                    13 14 15
                                    16 17 18

                                    19 20 21
                                    22 23 24
                                    25 26 27] "O" "X")))

      (it "checks block for X"
        (should= 1 (sut/next-move [1 2 "X" "X"
                                   "O" "X" "O" "X"
                                   "O" 10 "O" 12
                                   "O" "O" 15 16] "X" "O")))

      (it "can win for X"
        (should= 16 (sut/next-move [1 2 3 "X"
                                    5 6 7 "X"
                                    "O" 10 11 "X"
                                    "O" 14 15 16] "X" "O")))

      (it "can win for X"
        (should= 16 (sut/next-move [1 2 3 "X"
                                    5 6 7 "X"
                                    "O" 10 11 "X"
                                    "O" 14 15 16] "O" "X")))


      (it "selects blocking move"
        (should= 7 (sut/next-move ["X" "O" "X"
                                   "O" "X" 6
                                   7 8 9

                                   10 11 12
                                   13 14 15
                                   16 17 18

                                   19 20 21
                                   22 23 24
                                   25 26 27] "O" "X")))

      )
    )

  )
