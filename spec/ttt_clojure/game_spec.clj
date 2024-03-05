(ns ttt-clojure.game-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.game :as sut]))




(def initial-game {1 {:size     :3x3
                      :player-1 {:kind :ai, :token "X", :difficulty :easy},
                      :player-2 {:kind :ai, :token "O", :difficulty :easy}
                      :moves    [7 9 5 6 8 3]}})

(def game-map {1 {:size     :3x3
                  :player-1 {:kind :ai, :token "X", :difficulty :easy},
                  :player-2 {:kind :ai, :token "O", :difficulty :easy}
                  :moves    [7 9 5 6 8 3]}
               2 {:size     :3x3
                  :player-1 {:kind :human, :token "X"},
                  :player-2 {:kind :human, :token "O"}
                  :moves    [1 2 3 4 5 6]}})



(def example-map {;add size and moves first, then player-uno? and board, finally game-id
                  ; Remove
                  :game-id  1
                  :uno      true
                  :grid     [1 2 3 4 5 6 7 8 9]

                  ; Keep
                  :player-1 {:kind :human, :token "X"}
                  :player-2 {:kind :human, :token "O"}

                  ; Add
                  :size     :3x3
                  :moves    [4 2 9 8]})
(def example-map-2 {
                    ; Keep
                    :player-1 {:kind :human, :token "X"}
                    :player-2 {:kind :human, :token "O"}

                    ; Add
                    :size     :3x3
                    :moves    []})

(defn get-highest-id [game]
  (->> (keys game)
       (apply max)))


(describe "reads new game map well"
  (it "gets the initial  game id"
    (should= 1 (get-highest-id initial-game)))

  (it "gets the highest game id"
    (should= 2 (get-highest-id game-map)))

  (it "checks convert example map moves to board"
    (should= [1 "O" 3 "X" 5 6 7 "O" "X"] (sut/convert-moves-to-board example-map)))

  (it "checks convert example2 with and empty map moves to board"
    (should= [1 2 3 4 5 6 7 8 9] (sut/convert-moves-to-board example-map-2)))

  (it "checks empty moves to board"
    (should= [1 2 3 4 5 6 7 8 9]
             (sut/convert-moves-to-board "O" "X" :3x3 [])))

  (it "checks convert 4x4 moves to board"
    (should= ["O" "X" "O" "X" 5 6 7 8 9 10 11 12 13 14 15 16]
             (sut/convert-moves-to-board "O" "X" :4x4 [1 2 3 4])))

  (it "checks convert 3x3x3 moves to board"
    (should= ["O" 2 3 4 5 6 7 8 9 "X" 11 12 13 14 15 16 17 18 19 20 21 22 "O" "X" 25 26 27]
             (sut/convert-moves-to-board "O" "X" :3x3x3 [1 10 23 24])))

  (it "makes 1  board per 1 move"
    (should= [["X" 2 3 4 5 6 7 8 9]]
             (sut/creates-board-per-move "X" "O" :3x3 [1])))

  (it "makes 2 boards for 2 moves in a 3x3"
    (should= [["X" 2 3 4 5 6 7 8 9]
              ["X" "O" 3 4 5 6 7 8 9]]
             (sut/creates-board-per-move "X" "O" :3x3 [1 2])))

  (it "makes 3 boards for 3 moves in a 3x3"
    (should= [["X" 2 3 4 5 6 7 8 9]
              ["X" "O" 3 4 5 6 7 8 9]
              ["X" "O" 3 4 5 6 "X" 8 9]]
             (sut/creates-board-per-move "X" "O" :3x3 [1 2 7])))

  (it "makes 2 boards for 2 moves in a 4x4"
    (should= [[1 2 3 4 5 6 7 8 9 "O" 11 12 13 14 15 16]
              [1 2 3 4 5 6 7 8 "X" "O" 11 12 13 14 15 16]]
             (sut/creates-board-per-move "O" "X" :4x4 [10 9])))

  (it "makes 3 boards for 3 moves in a 4x4"
    (should= [["O" 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16]
              ["O" 2 3 4 "X" 6 7 8 9 10 11 12 13 14 15 16]
              ["O" 2 3 4 "X" 6 7 8 9 10 11 12 13 14 15 "O"]]
             (sut/creates-board-per-move "O" "X" :4x4 [1 5 16])))

  (it "makes 2 boards for 2 moves in a 3x3x3"
    (should= [[1 2 3 "O" 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27]
              [1 2 3 "O" 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 "X" 26 27]]
             (sut/creates-board-per-move "O" "X" :3x3x3 [4 25])))

  (it "makes 9 boards for 9 moves in a 3x3"
    (should= [["X" 2 3 4 5 6 7 8 9]
              ["X" "O" 3 4 5 6 7 8 9] ["X" "O" "X" 4 5 6 7 8 9]
              ["X" "O" "X" "O" 5 6 7 8 9] ["X" "O" "X" "O" "X" 6 7 8 9]
              ["X" "O" "X" "O" "X" "O" 7 8 9] ["X" "O" "X" "O" "X" "O" 7 "X" 9]
              ["X" "O" "X" "O" "X" "O" "O" "X" 9] ["X" "O" "X" "O" "X" "O" "O" "X" "X"]]
             (sut/creates-board-per-move "X" "O" :3x3 [1 2 3 4 5 6 8 7 9])))


  )
