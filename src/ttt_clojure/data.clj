(ns ttt-clojure.data
  (:require [clojure.edn :as edn]
            [next.jdbc :as j]
            [clojure.data.json :as json]))

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

;(do-something {:1 {:game-id 1 :blah :foo}}) => {1 {:game-id 1 :blah :foo}}
;(update-keys all data key to number )
(defn str-keys-to-int [m]
  (into {} (for [[k v] m] [(Integer/parseInt k) v])))

(defmulti fetch-the-games (fn [db-type] db-type))

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

;load
;so def log can be an atom and we can call it and not need to
;pass it as an arguement everywhere
;reset log to fetch games



(def log (atom {}))
(def log-edn (atom (fetch-the-games :edn)))
(def log-json (atom (fetch-the-games :json)))

(defn load-db [db-type]
  (reset! log (fetch-the-games db-type)))

(defmulti all-games (fn [db-type] db-type))
(defmethod all-games :json [_db-type] @log-json)
(defmethod all-games :edn [_db-type] @log-edn)

(defn get-game-by-id [game-id db-type]
  (get (all-games db-type) game-id))

(defn max-game-id-edn [games]
  (->> games
       keys
       (apply max 0)))

(defn last-game-id [db-type]
  (max-game-id-edn (all-games db-type)))

(defn get-next-game-id [db-type] (inc (last-game-id db-type)))


(defn get-last-game [db-type]
  (get (all-games db-type) (last-game-id db-type)))

(defmulti save (fn [_game db-type] db-type))

(defmethod save :edn [game _db-type]
  (->> game
       (swap! log-edn assoc (:game-id game))
       pr-str
       (spit "log.edn")))

(defmethod save :json [game _db-type]
  (->> game
       (swap! log-json assoc (:game-id game))
       json/write-str
       (spit "log.json")))

(def db {:dbtype "postgres" :dbname "tic_tac_toe"})         ;:username "merl" :password "clojure"
(def ds (j/get-datasource db))

(defmethod save :sql [game _db-type]
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
                   " ON CONFLICT (game_id) DO UPDATE SET moves = EXCLUDED.moves")]
    (j/execute! ds [query])))

;INSERT INTO inventory (id, name, price, quantity)
;VALUES (1, 'A', 16.99, 120)
;ON CONFLICT(id)
;DO UPDATE SET
;price = EXCLUDED.price,
;quantity = EXCLUDED.quantity;

;(defn update-postsql-moves [game]
;  (let [moves (:moves game)
;        id (:game-id game)
;        query (str "UPDATE game_map SET moves = '"
;                   moves "' WHERE game_id = "
;                   id)]
;    (j/execute! ds [query])))

(def foo-impl (atom :bar))
(defmulti -foo (fn [impl _arg1 _arg2] impl))
(defmethod -foo :bar [_impl arg1 arg2] [arg1 arg2])
(defn foo [arg1 arg2] (-foo @foo-impl arg1 arg2))

;(update {:x 1} :x - 5)
;(assoc {:x 1} :x (- 1 5))
;(swap! log conj game)
;(reset! log (conj @log game))

; READ data
; WRITE data
