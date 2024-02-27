(ns ttt-clojure.data-spec
  (:require [clojure.edn :as edn]
            [speclj.core :refer :all]
            [ttt-clojure.game-modes :as game]
            [ttt-clojure.data :as sut]
            [ttt-clojure.ui :as ui]
            [clojure.data.json :as json]))

(def initial-game {1 {:board    :3x3
                      :player-1 {:kind :ai, :token "X", :difficulty :easy},
                      :player-2 {:kind :ai, :token "O", :difficulty :easy}
                      :moves    [7 9 5 6 8 3]}})

(def game-map {1 {:board    :3x3
                  :player-1 {:kind :ai, :token "X", :difficulty :easy},
                  :player-2 {:kind :ai, :token "O", :difficulty :easy}
                  :moves    [7 9 5 6 8 3]}
               2 {:board    :3x3
                  :player-1 {:kind :human, :token "X"},
                  :player-2 {:kind :human, :token "O"}
                  :moves    [1 2 3 4 5 6]}})

(defn place-token [board [token move]]
  (ui/place-xo board move token))

(defn place-moves-into-board [player-1-token player-2-token board-size moves]
  (let [players (cycle [player-1-token player-2-token])
        _coll (map vector players moves)]
    (vec (reduce place-token board-size _coll))))

(defn convert-map-to-board [game-map]
  (let [player-1-token (:token (:player-1 game-map))
        player-2-token (:token (:player-2 game-map))
        board-size [1 2 3 4 5 6 7 8 9]
        moves (:moves game-map)]
    (if moves
      (place-moves-into-board player-1-token player-2-token board-size moves)
      (cons player-1-token board-size))))

(def example-map { ;add size and moves first, then player-1? and board, finally game-id
                  ; Remove
                  :game-id   1
                  :player-1? true
                  :board     [1 2 3 4 5 6 7 8 9]

                  ; Keep
                  :player-1  {:kind :human, :token "X"}
                  :player-2  {:kind :human, :token "O"}

                  ; Add
                  :size      :3x3
                  :moves     [4 2 9 8]})

(defn get-highest-id [game]
  (->> (keys game)
       (apply max)))


(describe "reads new game map well"
  (it "gets the initial  game id"
    (should= 1 (get-highest-id initial-game)))

  (it "gets the highest game id"
    (should= 2 (get-highest-id game-map)))

  ;(it "prints convert map" (println (convert-map-to-board example-map)))
  )

(describe "save round"
  (with-stubs)

  (it "gets game history by id"
    (with-redefs [sut/all-games (stub :all-games
                                      {:return [{:game-id  1 :player-1? true
                                                 :board    [1 2 3 4 5 6 7 8 9]
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
      (let [game {:game-id 1 :player-1? true :player-1 :human :player-2 :ai :board (game/board-size :3x3)}]
        (sut/save-edn game)
        (should-have-invoked :spit)
        (should-have-invoked :pr-str))))

  (it "saves a second game"
    (with-redefs [ui/get-game-board (constantly :3x3)
                  spit (stub :spit)
                  sut/get-next-game-id (stub :game-id {:return 2})]

      (let [saved {:game-id 2 :player-1? false :player-1 :ai :player-2 :human :board (game/board-size :3x3)}]
        (sut/save-edn saved)
        (should-have-invoked :spit))))

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
;
;
;[{:game-id 1, :player-1? false, :board (1 2 3 4 5 6 "X" 8 9), :player-1 {:kind :ai, :token "X", :difficulty :easy}, :player-2 {:kind :ai, :token "O", :difficulty :easy}}
; {:game-id 1, :player-1? true, :board (1 2 3 4 5 6 "X" 8 "O"), :player-1 {:kind :ai, :token "X", :difficulty :easy}, :player-2 {:kind :ai, :token "O", :difficulty :easy}}
; {:game-id 1, :player-1? false, :board (1 2 3 4 "X" 6 "X" 8 "O"), :player-1 {:kind :ai, :token "X", :difficulty :easy}, :player-2 {:kind :ai, :token "O", :difficulty :easy}}
; {:game-id 1, :player-1? true, :board (1 2 3 4 "X" "O" "X" 8 "O"), :player-1 {:kind :ai, :token "X", :difficulty :easy}, :player-2 {:kind :ai, :token "O", :difficulty :easy}}
; {:game-id 1, :player-1? false, :board (1 2 3 4 "X" "O" "X" "X" "O"), :player-1 {:kind :ai, :token "X", :difficulty :easy}, :player-2 {:kind :ai, :token "O", :difficulty :easy}}
; {:game-id 1, :player-1? true, :board (1 2 "O" 4 "X" "O" "X" "X" "O"), :player-1 {:kind :ai, :token "X", :difficulty :easy}, :player-2 {:kind :ai, :token "O", :difficulty :easy}}]
;
;save in this format
;{1 {:board :3x3  :player-1 {:kind :ai, :token "X", :difficulty :easy}, :player-2 {:kind :ai, :token "O", :difficulty :easy}
; :moves [7 9 5 6 8 3] }}
