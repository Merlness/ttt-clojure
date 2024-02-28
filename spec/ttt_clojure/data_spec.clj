(ns ttt-clojure.data-spec
  (:require [clojure.edn :as edn]
            [speclj.core :refer :all]
            [ttt-clojure.game-modes :as game]
            [ttt-clojure.data :as sut]
            [ttt-clojure.board :as board]
            [ttt-clojure.ui :as ui]
            [clojure.data.json :as json]))

(describe "save round"
  (with-stubs)
  (before (reset! sut/log-edn {}))

  ;(it "gets game history by id"
  ;  (reset! sut/log {1 {:game-id  1
  ;                      :player-1 {:kind :human :token "X"}
  ;                      :player-2 {:kind :human :token "X"}
  ;                      :size     :3x3 :moves []}})
  ;  (let [game-id 1
  ;        expected-game [{:game-id 1 :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"}}]
  ;        actual-game (sut/get-game-by-id game-id)]
  ;    (should= expected-game actual-game)))

  (it "gets max game id"
    (let [games {1 {:size     :3x3
                    :player-1 {:kind :ai, :token "X", :difficulty :easy},
                    :player-2 {:kind :ai, :token "O", :difficulty :easy}
                    :moves    [7 9 5 6 8 3]}
                 2 {:size     :3x3
                    :player-1 {:kind :human, :token "X"},
                    :player-2 {:kind :human, :token "O"}
                    :moves    [1 2 3 4 5 6]}}
          expected-id 2
          actual-id (sut/max-game-id-edn games)]
      (should= expected-id actual-id)))

  (it "gets the next game id"
    (with-redefs [sut/last-game-id (stub :last-id {:return 2})]
      (should= 3 (sut/get-next-game-id type))
      (should-have-invoked :last-id)))

  (it "gets the last game"
    (with-redefs [sut/get-last-game (stub :last-game {:return {:game-id 2}})]
      (let [last-game (sut/get-last-game type)
            correct-game {:game-id 2}]
        (should= correct-game last-game)
        (should-have-invoked :last-game))))

  (it "initial save"
    (with-redefs [ui/get-game-board (constantly :3x3)
                  spit (stub :spit)
                  sut/get-next-game-id (stub :game-id {:return 1})]
      (let [game {:game-id 1 :player-1 :human :player-2 :ai :size (board/board-size :3x3)}
            expected-shape-of-data {1 game}]
        (sut/save game :edn)
        (should-have-invoked :spit {:with ["log.edn" (pr-str expected-shape-of-data)]}))))

  (it "saves a second game"
    (with-redefs [ui/get-game-board (constantly :3x3)
                  spit (stub :spit)
                  sut/get-next-game-id (stub :game-id {:return 2})]

      (let [saved {:game-id 2 :player-1 :ai :player-2 :human :size (board/board-size :3x3)}]
        (sut/save saved :json)
        (should-have-invoked :spit))))


  (it "fetches games from edn"
    (with-redefs [slurp (stub :slurp {:return "{1 {:size :3x3 :player-1 {:kind :ai :token \"X\" :difficulty :easy} :player-2 {:kind :ai :token \"O\" :difficulty :easy} :moves [7 9 5 6 8 3]}}"})]
      (let [expected-games {1 {:size :3x3 :player-1 {:kind :ai :token "X" :difficulty :easy} :player-2 {:kind :ai :token "O" :difficulty :easy} :moves [7 9 5 6 8 3]}}]
        (should= expected-games (sut/fetch-the-games :edn)))))


  (it "fetches games from json"
    (with-redefs [slurp (stub :slurp {:return "{\"2\":{\"size\":\"3x3\",\"player-1\":{\"kind\":\"human\",\"token\":\"X\"},\"player-2\":{\"kind\":\"human\",\"token\":\"O\"},\"moves\":[1,2,3,4,5,6]}}"
                                      })]
      (let [expected-games {:2 {:size "3x3" :player-1 {:kind "human" :token "X"} :player-2 {:kind "human" :token "O"} :moves [1 2 3 4 5 6]}}]
        (should= expected-games (sut/fetch-the-games :json)))))

  (it "saves games to edn"
    (with-redefs [spit (stub :spit {:return "{:game-id 1, :size :3x3, :player-1 {:kind :ai, :token \"X\", :difficulty :easy}, :player-2 {:kind :ai, :token \"O\", :difficulty :easy}, :moves [7 9 5 6 8 3], :log {:game-id {:game-id 1, :size :3x3, :player-1 {:kind :ai, :token \"X\", :difficulty :easy}, :player-2 {:kind :ai, :token \"O\", :difficulty :easy}, :moves [7 9 5 6 8 3]}}}"})]
      (let [game {:game-id 1 :size :3x3 :player-1 {:kind :ai :token "X" :difficulty :easy} :player-2 {:kind :ai :token "O" :difficulty :easy} :moves [7 9 5 6 8 3]}
            expected-game (pr-str (assoc game :log {:game-id game}))]
        (should= expected-game (sut/save game :edn))
        (should-have-invoked :spit))))

  (it "saves games to json"
    (with-redefs [spit (stub :spit {:return "{\"game-id\":2,\"size\":\"3x3\",\"player-1\":{\"kind\":\"human\",\"token\":\"X\"},\"player-2\":{\"kind\":\"human\",\"token\":\"O\"},\"moves\":[1,2,3,4,5,6],\"log\":{\"game-id\":{\"game-id\":2,\"size\":\"3x3\",\"player-1\":{\"kind\":\"human\",\"token\":\"X\"},\"player-2\":{\"kind\":\"human\",\"token\":\"O\"},\"moves\":[1,2,3,4,5,6]}}}"})]
      (let [game {:game-id 2 :size :3x3 :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "O"} :moves [1 2 3 4 5 6]}
            expected-game (json/write-str (assoc game :log {:game-id game}))]
        (should= expected-game (sut/save game :json)))))


  #_(it "checks json"
      (let [map {:a 1 :b 2}
            write-str (json/write-str map)
            read-str (json/read-str write-str)
            read-str-key (json/read-str write-str
                                        :key-fn keyword)]
        (prn "(pr-str map):" (pr-str map))
        (prn "map:" map)
        (prn "write-str:" write-str)
        (prn "read-str:" read-str)
        (prn "read-str-key:" read-str-key)
        ))

  )

;[
; {:game-id 1, :grid (1 2 3 4 5 6 "X" 8 9), :player-1 {:kind :ai, :token "X", :difficulty :easy}, :player-2 {:kind :ai, :token "O", :difficulty :easy}}
; {:game-id 1, :grid (1 2 3 4 5 6 "X" 8 "O"), :player-1 {:kind :ai, :token "X", :difficulty :easy}, :player-2 {:kind :ai, :token "O", :difficulty :easy}}
; {:game-id 1, :grid (1 2 3 4 "X" 6 "X" 8 "O"), :player-1 {:kind :ai, :token "X", :difficulty :easy}, :player-2 {:kind :ai, :token "O", :difficulty :easy}}
; {:game-id 1, :grid (1 2 3 4 "X" "O" "X" 8 "O"), :player-1 {:kind :ai, :token "X", :difficulty :easy}, :player-2 {:kind :ai, :token "O", :difficulty :easy}}
; {:game-id 1, :grid (1 2 3 4 "X" "O" "X" "X" "O"), :player-1 {:kind :ai, :token "X", :difficulty :easy}, :player-2 {:kind :ai, :token "O", :difficulty :easy}}
; {:game-id 1, :grid (1 2 "O" 4 "X" "O" "X" "X" "O"), :player-1 {:kind :ai, :token "X", :difficulty :easy}, :player-2 {:kind :ai, :token "O", :difficulty :easy}}]
;
;save in this format
;{1 { :size :3x3  :player-1 {:kind :ai, :token "X", :difficulty :easy}, :player-2 {:kind :ai, :token "O", :difficulty :easy}
; :moves [7 9 5 6 8 3] }}
