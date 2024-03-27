(ns ttt-clojure.game-modes-spec
  (:require [clojure.edn :as edn]
            [speclj.core :refer :all]
            [ttt-clojure.computer :as comp]
            [ttt-clojure.game :as game]
            [ttt-clojure.game-modes :as sut]
            [ttt-clojure.data :as data]
            [ttt-clojure.ui :as ui]))

(describe "Checking Game"
  (with-stubs)

  (it "checks grid after player-1 X move"
    (let [output (sut/grid-after-move 1 "X" "O" :3x3 [])]
      (with-out-str (should= ["X" 2 3 4 5 6 7 8 9] output))))


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
            player-2 {:kind :ai :token "O" :difficulty :easy}
            game {:player-1 player-1 :player-2 player-2 :size :3x3 :moves []}
            new-board (game/convert-moves-to-board game)]
        (should= 1 (sut/get-move player-1 player-2 new-board))
        (should-have-invoked :next-move {:with [new-board]}))))

  (it "does it plays an ai move"
    (with-redefs [comp/ai-move (stub :next-move {:return 1})]
      (let [player-1 {:kind :ai :token "O" :difficulty :easy}
            player-2 {:kind :human :token "X"}
            game {:player-1 player-1 :player-2 player-2 :size :3x3 :moves []}
            new-board (game/convert-moves-to-board game)]
        (should= 1 (sut/get-move player-1 player-2 new-board))
        (should-have-invoked :next-move {:with [new-board "O" "X" :easy]}))))

  (it "checks play round"
    (with-redefs [comp/ai-move (stub :next-move {:return 1})]
      (let [player-1 {:kind :ai :token "O" :difficulty :easy}
            player-2 {:kind :human :token "X"}
            game {:player-1 player-1 :player-2 player-2 :size :3x3 :moves []}
            new-board (game/convert-moves-to-board game)]
        (should= 1 (sut/get-move player-1 player-2 new-board))
        (should-have-invoked :next-move {:with [new-board "O" "X" :easy]}))))

  (it "plays a round for player-1"
    (with-redefs [sut/get-move (stub :next-move {:return 1})
                  ui/print-board (stub :next-print {:return nil})
                  spit (stub :spit)
                  ui/player-statement (stub :player-statement)
                  sut/grid-after-move (stub :grid-after-move {:return ["X" 2 3 4 5 6 7 8 9]})
                  data/save (stub :save1)
                  ]
      (let [player-1 {:kind :human :token "X"}
            player-2 {:kind :ai :token "O" :difficulty :easy}
            grid [1 2 3 4 5 6 7 8 9]
            game {:game-id 1 :player-1 player-1 :player-2 player-2
                  :size    :3x3 :moves []}
            new-game (sut/play-round :memory game)
            correct-game {:game-id  1, :player-1 {:kind :human, :token "X"},
                          :player-2 {:kind :ai, :token "O", :difficulty :easy},
                          :size     :3x3, :moves [1]}
            new-board (game/convert-moves-to-board new-game)]
        (should= correct-game new-game)
        (should-have-invoked :next-move {:with [player-1 player-2 grid]})
        (should-not-have-invoked :player-statement {:with [1]})
        (should-have-invoked :next-print {:with [new-board]}))))

  (it "plays a round for player-2"
    (with-redefs [sut/get-move (stub :next-move {:return 2})
                  ui/print-board (stub :next-print)
                  ui/player-statement (stub :player-statement)
                  spit (stub :spit)
                  sut/grid-after-move (stub :grid-after-move {:return ["X" "O" 3 4 5 6 7 8 9]})
                  ]
      (let [player-1 {:kind :human :token "X"}
            player-2 {:kind :ai :token "O" :difficulty :easy}
            grid ["X" 2 3 4 5 6 7 8 9]
            game {:game-id 1 :player-1 player-1 :player-2 player-2 :size :3x3 :moves [1]}
            new-game (sut/play-round :memory game)
            correct-game {:game-id  1,
                          :player-1 {:kind :human, :token "X"},
                          :player-2 {:kind :ai, :token "O", :difficulty :easy}, :size :3x3, :moves [1 2]}
            new-board (game/convert-moves-to-board new-game)]
        (should-have-invoked :next-move {:with [player-2 player-1 grid]})
        (should-not-have-invoked :player-statement {:with [2]})
        (should= correct-game new-game)
        (should-have-invoked :next-print {:with [new-board]}))))


  )
