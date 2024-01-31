(ns ttt-clojure.game-modes-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.computer :as comp]
            [ttt-clojure.game-modes :as sut]
            [ttt-clojure.ui :as ui]))


(describe "Checking Game"
  (with-stubs)

  (it "does it plays a human move"
    (with-redefs [ui/get-move-2 (stub :next-move {:return 1})]
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
    ;stub out ui or comp to return a constant
    (with-redefs [comp/ai-move (stub :next-move {:return 1})]
      (let [player-1 {:kind :ai :token "O" :difficulty :easy}
            player-2 {:kind :human :token "X"}]
        (should= 1 (sut/get-move player-1 player-2 :board))
        (should-have-invoked :next-move {:with [:board "O" "X" :easy]}))))

  ;test grid after move

  )
