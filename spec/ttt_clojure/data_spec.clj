(ns ttt-clojure.data-spec
  (:require [clojure.edn :as edn]
            [speclj.core :refer :all]
            [ttt-clojure.game-modes :as game]
            [ttt-clojure.data :as sut]
            [ttt-clojure.ui :as ui]))

(def initial-game {:game-id 1 :player-1? true :player-1 :human :player-2 :ai :board (list 1 2 3 4 5 6 7 8 9)})

(describe "save round"
  (with-stubs)

  (it "gets game history by id"
    (with-redefs [sut/all-games (stub :all-games
                                      {:return [{:game-id 1 :player-1? true
                                                 :board [1 2 3 4 5 6 7 8 9]
                                                 :player-1 {:kind :human :token "X"}
                                                 :player-2 {:kind :human :token "X"}}]})]
      (let [game-id 1
            expected-game [{:game-id 1 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}}]
            actual-game (sut/game-history-by-id game-id)]
        (should= expected-game actual-game))))

  (it "gets max game id"
    (let [games [{:game-id 1 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}}
                 {:game-id 2 :player-1? true :board [1 2 3 4 5 6 7 8 9] :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}}]
          expected-id 2
          actual-id (sut/max-game-id games)]
      (should= expected-id actual-id)))

  (it "gets the next game id"
    (with-redefs [sut/last-game-id (stub :last-id {:return 2})]
      (should= 3 (sut/get-next-game-id))
      (should-have-invoked :last-id)))

  (it "gets the last game"
    (with-redefs [sut/get-last-game (stub :last-game {:return {:game-id 2}})]
      (let [last-game (sut/get-last-game)
            correct-game {:game-id 2}]
        (should= correct-game last-game)
        (should-have-invoked :last-game))))

  (it "initial save"
    (with-redefs [ui/get-game-board (constantly :3x3)
                  spit (stub :spit)
                  pr-str (stub :pr-str)
                  sut/get-next-game-id (stub :game-id {:return 1})]
      (let [game {:game-id 1 :player-1? true :player-1 :human :player-2 :ai :board (game/board-size)}]
        (sut/save game)
        (should-have-invoked :spit)
        (should-have-invoked :pr-str))))

  (it "saves a second game"
    (with-redefs [ui/get-game-board (constantly :3x3)
                  spit (stub :spit)
                  sut/get-next-game-id (stub :game-id {:return 2})]

      (let [saved {:game-id 2 :player-1? false :player-1 :ai :player-2 :human :board (game/board-size)}]
        (sut/save saved)
        (should-have-invoked :spit))))

  )
