(ns ttt-clojure.data
  (:require [clojure.edn :as edn]
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

(defn- fetch-edn-games []
  (let [log-edn (slurp "log.edn")]
    (if (empty? log-edn) {} (edn/read-string log-edn))))

(defn- fetch-json-games []
  (let [log-json (slurp "log.json")]
    (if (empty? log-json) {} (keywordize (json/read-str log-json :key-fn keyword)))))

;(do-something {:1 {:game-id 1 :blah :foo}}) => {1 {:game-id 1 :blah :foo}}
;(update-keys all data key to number )

(defn int-to-keyword [id]
  (->> id
       str
       keyword))

(defmulti fetch-the-games (fn [db-type] db-type))

(defmethod fetch-the-games :edn [_db-type]
  (let [log-edn (slurp "log.edn")]
    (if (empty? log-edn) {} (edn/read-string log-edn))))

(defmethod fetch-the-games :json [_db-type]
  (let [log-json (slurp "log.json")]
    (if (empty? log-json) {} (json/read-str log-json :key-fn keyword))))

(def log-edn (atom (fetch-edn-games)))
(def log-json (atom (fetch-json-games)))


(defmulti all-games (fn [db-type] db-type))
(defmethod all-games :json [_db-type] @log-json)
(defmethod all-games :edn [_db-type] @log-edn)

(defmulti get-game-by-id (fn [_game-id db-type] db-type))

(defmethod get-game-by-id :json [game-id db-type]
  (get (all-games db-type) (int-to-keyword game-id)))

(defmethod get-game-by-id :edn [game-id db-type]
  (get (all-games db-type) game-id))

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

(defmulti last-game-id (fn [db-type] db-type))

(defmethod last-game-id :json [db-type]
  (max-game-id-json (all-games db-type)))

(defmethod last-game-id :edn [db-type]
  (max-game-id-edn (all-games db-type)))

(defn get-next-game-id [db-type] (inc (last-game-id db-type)))

(defmulti get-last-game (fn [db-type] db-type))

(defmethod get-last-game :json [db-type]
  (get (all-games db-type) (int-to-keyword (last-game-id db-type))))

(defmethod get-last-game :edn [db-type]
  (get (all-games db-type) (last-game-id db-type)))

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
