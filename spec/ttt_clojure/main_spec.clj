(ns ttt-clojure.main-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.data :as data]
            [ttt-clojure.game :as game]
            [ttt-clojure.game-modes :as gm]
            [ttt-clojure.main :as sut]
            [ttt-clojure.board :as board]
            [ttt-clojure.ui :as ui]))

(describe "Testing Main"
  (with-stubs)
  (it "starts a new game correctly"
    (with-redefs [ui/get-game-board (constantly :3x3)
                  board/board-size (stub :board-size {:return [1 2 3 4 5 6 7 8 9]})
                  data/get-next-game-id (constantly 2)
                  gm/create-player (stub :create-player {:return {:kind :human :token "X"}})]
      (let [game (sut/start-new-game)]
        (should= [{:game-id  2
                   :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}, :size :3x3, :moves []} 2]
                 game))))

  (it "continue a previous game"
    (with-redefs [ui/print-resume-game (stub :print-resume-game)]
      (let [input-id "123"
            game {:game-id  2
                  :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "O"}}
            [actual-game actual-input-id] (sut/continue-previous-game game input-id)]
        (should= game actual-game)
        (should= input-id actual-input-id))))

  (it "possible to continue a game"
    (with-redefs [board/game-over? (stub :game-over? {:return false})]
      (let [game {:game-id 2 :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "O"} :size :3x3 :moves []}]
        (should= true (sut/possible-to-continue? game)))))

  (it "continue a last game"
    (with-redefs [ui/continue-last-game? (stub :continue-last-game? {:return true})
                  board/game-over? (stub :game-over? {:return false})]
      (let [last-game {:game-id 2 :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "O"} :size :3x3 :moves []}]
        (should= true (sut/continue-last-game? last-game)))))



  (it "starts a new game if no game can be continued"
    (with-redefs [data/get-next-game-id (stub :get-next-game-id {:return 4})
                  sut/possible-to-continue? (stub :possible-to-continue? {:return false})
                  sut/continue-last-game? (stub :continue-last-game? {:return false})
                  data/last-game-id (stub :last-id)
                  data/get-last-game (stub :last-game)
                  sut/start-new-game (stub :start-new-game {:return [{:game-id  4
                                                                      :player-1 {:kind :human :token "X"}
                                                                      :player-2 {:kind :human :token "O"}
                                                                      :size     :3x3 :moves []} 4]})]
      (let [input-id 2
            expected-game {:game-id 4 :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "O"} :size :3x3 :moves []}
            actual-game (sut/continue-game? input-id)]
        (should= expected-game (first actual-game)))))

  (it "continues a requested game if possible"
    (with-redefs [data/get-game-by-id (constantly {:game-id  2
                                                   :player-1 {:kind :human :token "X"}
                                                   :player-2 {:kind :human :token "O"}
                                                   :size     :3x3 :moves []})
                  data/last-game-id (stub :last-id)
                  data/get-last-game (stub :last-game)
                  sut/possible-to-continue? (constantly true)
                  ui/print-resume-game (stub :print-resume-game)]
      (let [input-id 2
            expected-game {:game-id  2
                           :player-1 {:kind :human :token "X"}
                           :player-2 {:kind :human :token "O"}
                           :size     :3x3 :moves []}
            actual-game (sut/continue-game? input-id)]
        (should= expected-game (first actual-game)))))

  (it "continues the last game if possible"
    (with-redefs [data/get-last-game (stub :get-last-game {:return {:game-id  3
                                                                    :player-1 {:kind :human :token "X"}
                                                                    :player-2 {:kind :human :token "O"}
                                                                    :size     :3x3 :moves []}})
                  data/last-game-id (stub :last-game-id {:return 3})
                  ui/play-again-message (stub :play-again {:return false})
                  sut/possible-to-continue? (stub :possible-to-continue? {:return false})
                  sut/continue-last-game? (stub :continue-last-game? {:return true})
                  sut/continue-previous-game (stub :continue-previous-game {:return [{:game-id  3
                                                                                      :player-1 {:kind :human :token "X"}
                                                                                      :player-2 {:kind :human :token "O"}
                                                                                      :size     :3x3 :moves []} 3]})]
      (let [input-id 2
            expected-game {:game-id  3
                           :player-1 {:kind :human :token "X"}
                           :player-2 {:kind :human :token "O"}
                           :size     :3x3 :moves []}
            actual-game (sut/continue-game? input-id)]

        ;(with-in-str "/n"
        (should= expected-game (first actual-game)))))

  (it "tests the -main function"
    (with-redefs [sut/continue-game? (stub :continue-game? {:return [{:game-id  1 :player-1 {:kind :human :token "X"}
                                                                      :player-2 {:kind :human :token "O"}
                                                                      :size     :3x3 :moves []} 1]})
                  ui/print-id-and-board (stub :print-id-and-empty-board)
                  gm/play-round (stub :play-round {:return {:game-id  1
                                                            :player-1 {:kind :human :token "X"}
                                                            :player-2 {:kind :human :token "O"}
                                                            :size     :3x3 :moves []}})
                  sut/replay? (constantly false)
                  data/get-next-game-id (stub :next-id)
                  ui/play-again-message (constantly false)
                  board/game-over? (stub :game-over? {:return true})
                  ui/print-end (stub :complete-game)]
      (let [game-id "1"]                                    ;stub out game by id to satisfy replay and one to not satisfy replay
        (sut/-main game-id)
        (should-have-invoked :continue-game? {:times 1})
        (should-have-invoked :print-id-and-empty-board {:times 1})
        (should-have-invoked :play-round {:times 1})
        (should-have-invoked :game-over? {:times 1})
        (should-have-invoked :complete-game {:times 1}))))

  (it "requested game is not over"
    (with-redefs [game/convert-moves-to-board (constantly [1 2 3 4 5 6 7 8 9])]
      (should= false (sut/replay? {:player-1 {:kind :human :token "X"}
                                   :player-2 {:kind :human :token "O"}}))))

  (it "requested game is over X wins"
    (with-redefs [game/convert-moves-to-board (constantly ["X" "O" 3
                                                           "X" "O" 6
                                                           "X" 8 9])]
      (should= true (sut/replay? {:player-1 {:kind :human :token "X"}
                                  :player-2 {:kind :human :token "O"}}))))

  (it "requested game is over O wins"
    (with-redefs [game/convert-moves-to-board (constantly ["X" "O" 3
                                                           "X" "O" 6
                                                           7 "O" 9])]
      (should= true (sut/replay? {:player-1 {:kind :human :token "X"}
                                  :player-2 {:kind :human :token "O"}}))))

  (it "requested game is over tie"
    (with-redefs [game/convert-moves-to-board (constantly ["X" "O" "O"
                                                           "X" "O" "X"
                                                           "O" "X" "O"])]
      (should= true (sut/replay? {:player-1 {:kind :human :token "X"}
                                  :player-2 {:kind :human :token "O"}}))))

  (it "checks end game"
    (with-redefs [data/get-next-game-id (stub :next-id)]
      (let [game {:game-id  1
                  :player-1 {:kind :human :token "X"}
                  :player-2 {:kind :human :token "O"}
                  :size     :3x3 :moves [1 2 3 4 5 6 7]}]
        (with-in-str "blah\n"
          (should= (str "X is the winner!\n"
                        "Would you like to play a new game?\n"
                        "  Please press 1 for a new game or anything else to exit.\n")
                   (with-out-str (sut/end-game game :json)))))))

  (it "checks if it's not possible to continue"
    (let [game {:game-id  1
                :player-1 {:kind :human :token "X"}
                :player-2 {:kind :human :token "O"}
                :size     :3x3 :moves [1 2 3 4 5 6 7]}]
      (should= false (sut/possible-to-continue? game))))

  (it "checks if possible to continue"
    (let [game {:game-id  1
                :player-1 {:kind :human :token "X"}
                :player-2 {:kind :human :token "O"}
                :size     :3x3 :moves [1 2 3 4 5 6]}]
      (should= true (sut/possible-to-continue? game))))


  ;test starting at the end
  ; and work your way backwards
  ;begin with an almost complete board
  ; make a complete game see if it invokes complete game
  ; make 1 move available, so it does not have to recur

  )
