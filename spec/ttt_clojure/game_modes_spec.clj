(ns ttt-clojure.game-modes-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.computer :as comp]
            [ttt-clojure.game-modes :as sut]
            [ttt-clojure.board :as board]
            [ttt-clojure.ui :as ui]))


(describe "Checking Game"
  (with-stubs)

  (it "checks board 3x3"
    (with-out-str
      (with-in-str "3\n"
        (should= [1 2 3 4 5 6 7 8 9] (sut/board-size)))))

  (it "checks board size 4x4"
    (with-out-str
      (with-in-str "4\n"
        (should= [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16] (sut/board-size)))))

  (it "checks board size 3x3x3"
    (with-out-str
      (with-in-str "9\n"
        (should= [1 2 3 4 5 6 7 8 9
                  10 11 12 13 14 15 16 17 18
                  19 20 21 22 23 24 25 26 27]
                 (sut/board-size)))))

  (it "checks grid after player-1 X move"
    (let [output (sut/grid-after-move true [1 2 3 4 5 6 7 8 9] 1 "X" "O")]
     (with-out-str (should= ["X" 2 3 4 5 6 7 8 9] output))))

  (it "checks grid after player-2 O move"
    (let [output (sut/grid-after-move false [1 2 3 4 5 6 7 8 9] 5 "X" "O")]
     (with-out-str (should= [1 2 3 4 "O" 6 7 8 9] output))))

  (it "returns player-1 token when position is 1"
    (with-redefs [ui/get-player-1-token (stub :next-token {:return "X"})]
      (should= "X" (sut/token-finder 1 "O"))
      (should-have-invoked :next-token {:with []})))

  (it "returns player-2 token when position is not 1"
    (with-redefs [ui/get-player-2-token (stub :next-token {:return "O"})]
      (should= "O" (sut/token-finder 2 "X"))
      (should-have-invoked :next-token {:with ["X"]})))

  (it "creates a human player"
    (with-redefs [ui/get-player (stub :next-kind {:return :human})
                  sut/token-finder (stub :next-token {:return "X"})]
      (let [player (sut/create-player 1 "O")]
        (should= :human (:kind player))
        (should= "X" (:token player))
        (should-have-invoked :next-kind {:with [1]})
        (should-have-invoked :next-token {:with [1 "O"]}))))

  (it "creates an ai player"
    (with-redefs [ui/get-player (stub :next-kind {:return :ai})
                  sut/token-finder (stub :next-token {:return "O"})
                  ui/get-difficulty (stub :next-difficulty {:return :easy})]
      (let [player (sut/create-player 2 "X")]
        (should= :ai (:kind player))
        (should= "O" (:token player))
        (should= :easy (:difficulty player))
        (should-have-invoked :next-kind {:with [2]})
        (should-have-invoked :next-token {:with [2 "X"]})
        (should-have-invoked :next-difficulty {:with []}))))

  (it "does it plays a human move"
    (with-redefs [ui/get-move (stub :next-move {:return 1})]
      (let [player-1 {:kind :human :token "X"}
            player-2 {:kind :ai :token "O" :difficulty :easy}]
        (should= 1 (sut/get-move player-1 player-2 :board))
        (should-have-invoked :next-move {:with [:board]}))))

  (it "does it plays an ai move"
    (with-redefs [comp/ai-move (stub :next-move {:return 1})]
      (let [player-1 {:kind :ai :token "O" :difficulty :easy}
            player-2 {:kind :human :token "X"}]
        (should= 1 (sut/get-move player-1 player-2 :board))
        (should-have-invoked :next-move {:with [:board "O" "X" :easy]}))))

  (it "checks play round"
    (with-redefs [comp/ai-move (stub :next-move {:return 1})]
      (let [player-1 {:kind :ai :token "O" :difficulty :easy}
            player-2 {:kind :human :token "X"}]
        (should= 1 (sut/get-move player-1 player-2 :board))
        (should-have-invoked :next-move {:with [:board "O" "X" :easy]}))))

  (it "plays a round for player-1"
    (with-redefs [sut/get-move (stub :next-move {:return 1})
                  sut/grid-after-move (stub :next-grid {:return [1 2 3 4 5 6 7 8 9]})
                  ui/print-board (stub :next-print {:return nil})]
      (let [player-1 {:kind :human :token "X"}
            player-2 {:kind :ai :token "O" :difficulty :easy}
            grid [1 2 3 4 5 6 7 8 9]
            new-grid (sut/play-round true player-1 player-2 grid)]
        (should= [1 2 3 4 5 6 7 8 9] new-grid)
        (should-have-invoked :next-move {:with [player-1 player-2 grid]})
        (should-have-invoked :next-grid {:with [true grid 1 "X" "O"]})
        (should-have-invoked :next-print {:with [new-grid board/display]}))))
  )
