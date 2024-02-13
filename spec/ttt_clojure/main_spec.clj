(ns ttt-clojure.main-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.data :as data]
            [ttt-clojure.game-modes :as gm]
            [ttt-clojure.main :as sut]
            [ttt-clojure.board :as board]
            [ttt-clojure.ui :as ui]))

(describe "Testing Main"
  (with-stubs)

  (it "starts a new game correctly"
    (with-redefs [gm/board-size (stub :board-size {:return [1 2 3 4 5 6 7 8 9]})
                  gm/create-player (stub :create-player {:return {:kind :human :token "X"}})]
      (let [game (sut/start-new-game 2)]
        (should= [{:game-id 2 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}} 2]
                 game))))

  (it "continue a previous game"
    (with-redefs [ui/print-previous-moves-game (stub :print-previous-moves-game)]
      (let [input-id "123"
            game {:game-id 2 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}}
            [actual-game actual-input-id] (sut/continue-previous-game game input-id)]
        (should= game actual-game)
        (should= input-id actual-input-id))))

  (it "possible to continue a game"
    (with-redefs [board/game-over? (stub :game-over? {:return false})]
      (let [game {:game-id 2 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}}]
        (should= true (sut/possible-to-continue? game)))))

  (it "continue a last game"
    (with-redefs [ui/continue-last-game? (stub :continue-last-game? {:return true})
                  board/game-over? (stub :game-over? {:return false})]
      (let [last-game {:game-id 2 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}}]
        (should= true (sut/continue-last-game? last-game)))))

  (it "gets a game by id"
    (with-redefs [data/game-history-by-id (stub :game-history-by-id
                                                {:return [{:game-id  2 :player-1? true
                                                           :board    [1 2 3 4 5 6 7 8 9]
                                                           :player-1 {:kind :human :token "X"}
                                                           :player-2 {:kind :human :token "X"}}]})]
      (let [id 2
            expected-game {:game-id 2 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}}
            actual-game (sut/game-by-id id)]
        (should= expected-game actual-game))))


  (it "starts a new game if no game can be continued"
    (with-redefs [data/get-next-game-id (stub :get-next-game-id {:return 4})
                  sut/possible-to-continue? (stub :possible-to-continue? {:return false})
                  sut/continue-last-game? (stub :continue-last-game? {:return false})
                  sut/start-new-game (stub :start-new-game {:return [{:game-id 4 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}} 4]})]
      (let [input-id 2
            expected-game {:game-id 4 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}}
            actual-game (sut/continue-game? input-id)]
        (should= expected-game (first actual-game)))))

  (it "continues a requested game if possible"
    (with-redefs [sut/game-by-id (stub :game-by-id {:return {:game-id 2 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}}})
                  sut/possible-to-continue? (stub :possible-to-continue? {:return true})
                  sut/continue-previous-game (stub :continue-previous-game {:return [{:game-id 2 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}} 2]})]
      (let [input-id 2
            expected-game {:game-id 2 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}}
            actual-game (sut/continue-game? input-id)]
        (should= expected-game (first actual-game)))))

  (it "continues the last game if possible"
    (with-redefs [data/get-last-game (stub :get-last-game {:return {:game-id 3 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}}})
                  data/last-game-id (stub :last-game-id {:return 3})
                  sut/possible-to-continue? (stub :possible-to-continue? {:return false})
                  sut/continue-last-game? (stub :continue-last-game? {:return true})
                  sut/continue-previous-game (stub :continue-previous-game {:return [{:game-id 3 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}} 3]})]
      (let [input-id 2
            expected-game {:game-id 3 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}}
            actual-game (sut/continue-game? input-id)]
        (should= expected-game (first actual-game)))))

  (it "tests the -main function"
    (with-redefs [sut/continue-game? (stub :continue-game? {:return [{:game-id 1 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}} 1]})
                  ui/print-id-and-empty-board (stub :print-id-and-empty-board)
                  gm/play-round (stub :play-round {:return {:game-id 1 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}}})
                  board/game-over? (stub :game-over? {:return true})
                  gm/complete-game (stub :complete-game)]
      (let [game-id "1"]
        (sut/-main game-id)
        (should-have-invoked :continue-game? {:times 1})
        (should-have-invoked :print-id-and-empty-board {:times 1})
        (should-have-invoked :play-round {:times 1})
        (should-have-invoked :game-over? {:times 1})
        (should-have-invoked :complete-game {:times 1}))))


  ;test starting at the end and work your way backwards
  ;begin with an almost complete board
  ; make a complete game see if it invokes complete game
  ; make 1 move available, so it does not have to recur


  )
