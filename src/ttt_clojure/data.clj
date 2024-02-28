(ns ttt-clojure.data
  (:require [clojure.edn :as edn]
            [clojure.data.json :as json]))

(defn- fetch-edn-games []
  (let [log-edn (slurp "log.edn")]
    (if (empty? log-edn) {} (edn/read-string log-edn))))

;(defn- fetch-json-games []
;    (let [log-json (slurp "log.json")]
;      (if (empty? log-json) {}
;                            (->> (json/read-str log-json :key-fn keyword)
;                                 (into {} (map (fn [[k v]]
;                                                 [(Integer/parseInt (name k)) v])))))))

(def string-to-keyword
  {"ai"     :ai
   "human"  :human
   "easy"   :easy
   "medium" :medium
   "hard"   :hard
   "3x3"    :3x3
   "4x4"    :4x4
   "3x3x3"  :3x3x3})

(defn- keywordize-value [v]
  (if (string? v)
    (or (string-to-keyword v) v)
    v))

(defn- keywordize [game]
  (into {} (map (fn [[key value]]
                  [(if (string? key) (keyword key) key)
                   (cond
                     (string? value) (keywordize-value value)
                     (map? value) (keywordize value)
                     :else value)])) game))
;convert keys to strings, remove colon turn to int
(defn- fetch-json-games []
  (let [log-json (slurp "log.json")]
    (if (empty? log-json) {} (keywordize (json/read-str log-json :key-fn keyword)))))

(defmulti fetch-the-games (fn [db-type] db-type))

(defmethod fetch-the-games :edn [_db-type]
  (let [log-edn (slurp "log.edn")]
    (if (empty? log-edn) {} (edn/read-string log-edn))))

(defmethod fetch-the-games :json [_db-type]
  (let [log-json (slurp "log.json")]
    (if (empty? log-json) {} (json/read-str log-json :key-fn keyword))))

(def log-edn (atom (fetch-edn-games)))
(def log-json (atom (fetch-json-games)))

(defn all-games [db-type]
  (cond
    (= db-type :json) @log-json
    :else @log-edn))

(defn get-game-by-id [game-id db-type]
  (if (= db-type :json)
    (get (all-games db-type) (keyword (str game-id)))
   (get (all-games db-type) game-id)))

(defn convert-key-to-number [keys]
  (map #(Integer/parseInt (name %)) keys))

(defn max-game-id-edn [games]
  (->> games
       keys
       (apply max 0)))

(defn max-game-id-json [games]
  (->> games
       keys
       convert-key-to-number
       (apply max 0)))

(defn last-game-id [db-type]
  (if (= db-type :json)
    (max-game-id-json (all-games db-type))
    (max-game-id-edn (all-games db-type))))
(defn get-next-game-id [db-type] (inc (last-game-id db-type)))
(defn get-last-game [db-type]
  (if (= db-type :json)
   (get (all-games db-type) (keyword (str (last-game-id db-type))))
   (get (all-games db-type) (last-game-id db-type))))

(defmulti save (fn [_game db-type] db-type))

(defmethod save :edn [game _db-type]
  (->> game
       (swap! log-edn assoc (:game-id game))
       pr-str
       (spit "log.edn")))

(defmethod save :json [game _db-type]
  (->> game
       (swap! log-edn assoc (:game-id game))
       json/write-str
       (spit "log.json")))


;(update {:x 1} :x - 5)
;(assoc {:x 1} :x (- 1 5))
;(swap! log conj game)
;(reset! log (conj @log game))

; READ data
; WRITE data
