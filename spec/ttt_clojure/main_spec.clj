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
        (should= {:game-id 2 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}} game))))

  ;(it "continues the last game correctly with input id"
  ;  (let [last-game {:player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "O"}}]
  ;    (with-redefs [board/game-over? (stub :game-over? {:return false})
  ;                  ui/continue-last-game? (stub :continue-last-game? {:return false})
  ;                  data/last-game-id (stub :last-game-id {:return "123"})
  ;                  data/get-next-game-id (stub :get-next-game-id {:return "124"})
  ;                  sut/continue-last-game (stub :continue-last-game {:return last-game})
  ;                  sut/start-new-game (stub :start-new-game {:return {:new-game "new game"}})]
  ;      (let [input-id "123"
  ;            [result id] (sut/continue-last-game? last-game input-id)]
  ;        (should= "123" id)
  ;        (should= last-game result)
  ;        (should-have-invoked :continue-last-game {:with [last-game]})
  ;        (should-not-have-invoked :start-new-game)))))
  ;
  ;
  ;(it "continues the last game correctly with wrong input id"
  ;  (let [last-game {:player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "O"}}]
  ;    (with-redefs [board/game-over? (stub :game-over? {:return false})
  ;                  ui/continue-last-game? (stub :continue-last-game? {:return true})
  ;                  data/last-game-id (stub :last-game-id {:return "1"})
  ;                  data/get-next-game-id (stub :get-next-game-id {:return "2"})
  ;                  sut/continue-last-game (stub :continue-last-game {:return last-game})
  ;                  sut/start-new-game (stub :start-new-game)]
  ;      (let [input-id "123"
  ;            [result new-id] (sut/continue-last-game? last-game input-id)]
  ;        (should= "1" new-id)
  ;        (should= last-game result)
  ;        (should-have-invoked :continue-last-game {:with [last-game]})
  ;        (should-not-have-invoked :start-new-game)))))
  ;
  ;(it "start a new game"
  ;  (let [new-game {:player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "O"}}]
  ;    (with-redefs [board/game-over? (stub :game-over? {:return false})
  ;                  ui/continue-last-game? (stub :continue-last-game? {:return false})
  ;                  data/last-game-id (stub :last-game-id {:return "1"})
  ;                  data/get-next-game-id (stub :get-next-game-id {:return "2"})
  ;                  sut/continue-last-game (stub :continue-last-game)
  ;                  sut/start-new-game (stub :start-new-game {:return new-game})]
  ;      (let [last-game {:player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "O"}}
  ;            input-id "123"
  ;            [result new-id] (sut/continue-last-game? last-game input-id)]
  ;        (should= "2" new-id)
  ;        (should= new-game result)
  ;        (should-have-invoked :start-new-game)
  ;        (should-not-have-invoked :continue-last-game)))))

;test starting at the end and work your way backwards
  ;begin with an almost complete board
  ; make a complete game see if it invokes complete game
  ; make 1 move available so it does not have to recur

  ;(it "runs the main function correctly"
  ;  (let [game {:player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "O"}}
  ;        modified-game {:player-1? false :board ["X" 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "O"}}]
  ;    (with-redefs [save/get-last-game (stub :get-last-game {:return nil})
  ;                  gm/play-round (stub :play-round (fn [game] modified-game))
  ;                  board/game-over? (stub :game-over? {:return true})
  ;                  gm/complete-game (stub :complete-game)
  ;                  ui/print-board (stub :print-board)
  ;                  sut/continue-last-game? (stub :continue-last-game? {:return false})
  ;                  sut/start-new-game (stub :start-new-game {:return game})]
  ;      (should-have-invoked :print-board {:with [(:board modified-game)]})
  ;      (should-have-invoked :complete-game {:with [modified-game]}))))

  )
