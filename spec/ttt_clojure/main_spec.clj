(ns ttt-clojure.main-spec
  (:require [speclj.core :refer :all]
            [clojure.edn :as edn]
            [ttt-clojure.game-modes :as gm]
            [ttt-clojure.computer :as comp]
            [ttt-clojure.main :as sut]
            [ttt-clojure.board :as board]
            [ttt-clojure.ui :as ui]))

(describe "Testing Main"
  (with-stubs)

  (it "starts a new game correctly"
    (with-redefs [gm/board-size (stub :board-size {:return [1 2 3 4 5 6 7 8 9]})
                  gm/create-player (stub :create-player {:return {:kind :human :token "X"}})]
      (let [game (sut/start-new-game)]
        (should= {:player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}} game))))

  (it "continues the last game correctly"
    (with-redefs [board/game-over? (stub :game-over? {:return false})
                  ui/continue-last-game? (stub :continue-last-game? {:return true})]
      (let [last-game {:player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "O"}}
            result (sut/continue-last-game? last-game)]
        (should= true result))))


  #_(it "runs the main function correctly"
    (let [game {:player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "O"}}
          modified-game {:player-1? false :board ["X" 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "O"}}]
      (with-redefs [gm/get-last-game (stub :get-last-game {:return nil})
                    gm/play-round (stub :play-round (fn [game] modified-game))
                    board/game-over? (stub :game-over? {:return true})
                    gm/complete-game (stub :complete-game)
                    ui/print-board (stub :print-board)
                    sut/continue-last-game? (stub :continue-last-game? {:return false})
                    sut/start-new-game (stub :start-new-game {:return game})]
        (should-have-invoked :print-board {:with [(:board modified-game)]})
        (should-have-invoked :complete-game {:with [modified-game]}))))

  )
