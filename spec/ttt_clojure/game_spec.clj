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

          ;(it "prints convert map" (println (convert-map-to-board example-map)))
          )
