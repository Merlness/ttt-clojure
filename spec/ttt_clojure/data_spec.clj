(ns ttt-clojure.data-spec
  (:require
            [speclj.core :refer :all]
            [next.jdbc :as j]
            [ttt-clojure.data :as sut]
            [ttt-clojure.board :as board]
            [next.jdbc.connection :as connection]
            [ttt-clojure.ui :as ui]
            [clojure.data.json :as json]))

(def default-map {1 {:size     :3x3
                     :player-1 {:kind :ai, :token "X", :difficulty :easy},
                     :player-2 {:kind :ai, :token "O", :difficulty :easy}
                     :moves    [7 9 5 6 8 3]}
                  2 {:game-id  2
                     :player-1 {:kind :human :token "X"}
                     :player-2 {:kind :human :token "O"}
                     :size     :3x3 :moves []}
                  3 {:game-id  3
                     :player-1 {:kind :ai :difficulty :easy :token "X"}
                     :player-2 {:kind :human :token "O"}
                     :size     :3x3 :moves [1 3 4]}})

(def db {:dbtype "postgres" :dbname "tic_tac_toe" ;:username "merl" :password "clojure"
         })
(def ds (j/get-datasource db))

;(j/execute! ds ["
;CREATE TABLE game_map
;(game_id int, board_size VARCHAR(9), moves VARCHAR(100),
;player_1 VARCHAR(50), player_2 VARCHAR(50))"])
;
;(j/execute! ds ["
;INSERT INTO game_map (game_id, board_size, moves, player_1, player_2)
;VALUES (3, ':3x3', '[1 3 4]',
;'{:kind :ai, :token \"X\", :difficulty :easy}',
;'{:kind :human, :token \"O\"}')"])


;(j/execute! ds ["
; DELETE FROM game_map WHERE game_id='17'
; " ])

;(j/execute! ds ["
; UPDATE game_map
;SET moves = '[1 3 4 9]'
;WHERE game_id = 3;
; " ])

(defn transform-data [data]
  (let [game (into {} data)]
  {:game-id (:game_map/game_id game)
   :player-1 (read-string (:game_map/player_1 game))
   :player-2 (read-string (:game_map/player_2 game))
   :size (read-string (:game_map/board_size game))
   :moves (read-string (:game_map/moves game))}))


(describe "save round"
  (with-stubs)
  (before (reset! sut/log-edn {}))

  (it "gets game history by id"
    (reset! sut/log-edn {1 {:game-id  1
                            :player-1 {:kind :human :token "X"}
                        :player-2 {:kind :human :token "X"}
                        :size     :3x3 :moves []}})
    (let [game-id 1
          expected-game {:game-id 1 :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "X"} :size :3x3 :moves []}
          actual-game (sut/get-game-by-id game-id :edn)]
      (should= expected-game actual-game)))

  (it "gets max game id"
    (let [expected-id 3
          actual-id (sut/max-game-id-edn default-map)]
      (should= expected-id actual-id)))

  (it "gets the last game id"
    (with-redefs [sut/all-games (constantly default-map)]
      (should= 3  (sut/last-game-id :edn))))

  (it "gets the next game id"
    (with-redefs [sut/last-game-id (stub :last-id {:return 2})]
      (should= 3 (sut/get-next-game-id type))
      (should-have-invoked :last-id)))

  (it "gets the last game"
    (with-redefs [sut/all-games (constantly default-map)]
      (let [last-game (sut/get-last-game type)
            correct-game {:game-id 3, :player-1 {:kind :ai, :difficulty :easy, :token "X"},
                          :player-2 {:kind :human, :token "O"}, :size :3x3, :moves [1 3 4]}]
        (should= correct-game last-game))))

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
      (let [expected-games {2 {:size :3x3 :player-1 {:kind :human :token "X"} :player-2 {:kind :human :token "O"} :moves [1 2 3 4 5 6]}}]
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


  (it "checks get game by id "
    (with-redefs [sut/all-games (constantly default-map)]
    (should= {:game-id  3
              :player-1 {:kind :ai :difficulty :easy :token "X"}
              :player-2 {:kind :human :token "O"}
              :size     :3x3 :moves [1 3 4]} (sut/get-game-by-id 3 :edn))))


  (it "checks db from sql"
    (should= 1 (j/execute! ds ["select * from game_map"])))

  ;(it "checks db from sql"
  ;  (should= 1 (j/execute! ds ["SELECT * FROM game_map WHERE game_id = ?" 3])))

  (it "checks transform"
    (should= 1 (transform-data (j/execute! ds ["SELECT * FROM game_map WHERE game_id = ?" 3]))))




  ;(it "c"
  ;  (let [game {:game-id 3, :player-1 {:kind :ai, :difficulty :easy, :token "X"},
  ;              :player-2 {:kind :human, :token "O"}, :size :3x3, :moves [1 3 4]}]
  ;    (should= ":3x3" (vec (:moves game)))))


  ;before all create db tranactions
  ;after all delete the db
  ;start on saving first

  ;(focus-it "fetch games"
  ;  (should= 1 (sut/fetch-the-games :edn)))
  ;
  ;(focus-it "tests update key"
  ;  (should= 1 (sut/all-games :json)))


  )

;use sratch sql to visually see how to play with the command line
; find a way to implement sql DB with

;17 {
; :game-id 17,
; :player-1 {:kind :ai, :token "O", :difficulty :medium},

; :player-2 {:kind :human, :token "X"},
; :size :4x4,
; :moves [1 2 3 4 5 15 6 12 7 8 13 16]}

;game-id   size         moves    kind-1      token-1     kind-2      token-2     difficulty-1  difficulty-2
;int      varchar(6)             varchar(6)  varchar(3)  varchar(6)  varchar(6)  varchar(7)    varchar(7)


;def connection
;:dbtype "sqlite"
;   :dbname "file-path"
