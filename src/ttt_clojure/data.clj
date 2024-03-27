(ns ttt-clojure.data
  (:require [clojure.edn :as edn]
            [next.jdbc :as j]
            [clojure.data.json :as json]))

(def db {:dbtype "postgres" :dbname "tic_tac_toe"})         ;:username "merl" :password "clojure"
(def ds (j/get-datasource db))

(def string-to-keyword
  {"ai"     :ai
   "human"  :human
   "easy"   :easy
   "medium" :medium
   "hard"   :hard
   "3x3"    :3x3
   "4x4"    :4x4
   "3x3x3"  :3x3x3})

(defn- keywordize-value [value]
  (if (string? value)
    (or (string-to-keyword value) value)
    value))

(defn- keywordize [game]
  (into {} (map (fn [[key value]]
                  [(if (string? key) (keyword key) key)
                   (cond
                     (string? value) (keywordize-value value)
                     (map? value) (keywordize value)
                     :else value)])) game))

(defn str-keys-to-int [map]
  (into {} (for [[key value] map] [(Integer/parseInt key) value])))

(defn psql-to-map [data]
  (let [game (into {} data)]
    {:game-id  (:game_map/game_id game)
     :player-1 (read-string (:game_map/player_1 game))
     :player-2 (read-string (:game_map/player_2 game))
     :size     (read-string (:game_map/board_size game))
     :moves    (read-string (:game_map/moves game))}))

(defmulti fetch-the-games (fn [db-type] db-type))

(defmethod fetch-the-games :memory [_db-type])
(defmethod fetch-the-games :edn [_db-type]
  (let [log-edn (slurp "log.edn")]
    (if (empty? log-edn) {} (edn/read-string log-edn))))

(defmethod fetch-the-games :json [_db-type]
  (let [log-json (slurp "log.json")]
    (if (empty? log-json)
      {}
      (-> log-json
          (json/read-str :key-fn keyword)
          keywordize
          (update-keys name)
          str-keys-to-int))))

(defmethod fetch-the-games :psql [_db-type]
  (let [games-from-db (j/execute! ds ["select * from game_map"])
        games (map psql-to-map games-from-db)]
    (into {} (map (fn [game] [(:game-id game) game]) games))))

(def db-atom (atom nil))
(def log (atom {}))

(defn load-db [db-type]
  (reset! db-atom db-type)
  (reset! log (fetch-the-games db-type)))

(defn max-game-id-edn [games]
  (->> games
       keys
       (apply max 0)))

(defn get-game-by-id [game-id] (get @log game-id))
(defn last-game-id [] (max-game-id-edn @log))
(defn get-next-game-id [] (inc (last-game-id)))
(defn get-last-game [] (get @log (last-game-id)))

(defmulti save (fn [_game db-type] db-type))

(defmethod save :memory [game _db-type]
  )
(defmethod save :edn [game _db-type]
  (->> game
       (swap! log assoc (:game-id game))
       pr-str
       (spit "log.edn")))

(defmethod save :json [game _db-type]
  (->> game
       (swap! log assoc (:game-id game))
       json/write-str
       (spit "log.json")))

(defmethod save :psql [game _db-type]
  (let [game-id (:game-id game)
        board-size (str (:size game))
        moves (str (:moves game))
        player-1 (str (:player-1 game))
        player-2 (str (:player-2 game))
        query (str "INSERT INTO game_map (game_id, board_size, moves, player_1, player_2) VALUES ("
                   game-id ", '"
                   board-size "', '"
                   moves "', '"
                   player-1 "', '"
                   player-2 "')"
                   ;" ON CONFLICT(game_id) DO UPDATE SET moves = EXCLUDED.moves" ;possible error
                   )]
    (j/execute! ds [query])))

(defn save! [game] (save game @db-atom))
