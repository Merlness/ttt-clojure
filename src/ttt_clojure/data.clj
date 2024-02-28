(ns ttt-clojure.data
  (:require [clojure.edn :as edn]
            [clojure.data.json :as json]))

(defn- fetch-edn-games []
  (let [log-edn (slurp "log.edn")
        ]
    (if (empty? log-edn) {} (edn/read-string log-edn))))

(defn- fetch-json-games []
  (let [log-json (slurp "log.json")]
    (if (empty? log-json) {} (json/read-str log-json :key-fn keyword))))


(defmulti fetch-the-games (fn [db-type] db-type))

(defmethod fetch-the-games :edn [_db-type]
  (let [log-edn (slurp "log.edn")]
    (if (empty? log-edn) {} (edn/read-string log-edn))))

(defmethod fetch-the-games :json [_db-type]
  (let [log-json (slurp "log.json")]
    (if (empty? log-json) {} (json/read-str log-json :key-fn keyword))))




(def log-edn (atom (fetch-edn-games)))
(def log-json (atom (fetch-json-games)))

(defn all-games []
  @log-edn) ;case db

(defn get-game-by-id [game-id]
  (get (all-games) game-id)) ;db

(defn max-game-id [games]
  (->> games
       keys
       (apply max 0)))

(defn last-game-id [] (max-game-id (all-games))) ;db
(defn get-next-game-id [] (inc (last-game-id)))
(defn get-last-game [] (get (all-games) (last-game-id))) ;db here

(defn get-game-data []
  (let [all-games (all-games)
        last-game-id (max-game-id all-games)
        next-game-id (inc last-game-id)
        last-game (get all-games last-game-id)]
    [last-game-id next-game-id last-game]))


(defn save [game]
  ;(let [write-string  (if (= "--json" _DB) json/write-str pr-str)]

  (->> game
       (swap! log-edn assoc (:game-id game))
       ; write-string
       pr-str
       (spit "log.edn")
       ))
;  )

(defn save-json [game]
  (->> game
       (swap! log-edn assoc (:game-id game))
       json/write-str                                       ;maybe json/generate-string
       (spit "log.json")))

(defmulti save1 (fn [game db-type] db-type))

(defmethod save1 :edn [game db-type]
  (->> game
       (swap! log-edn assoc (:game-id game))
       pr-str
       (spit "log.edn")))

(defmethod save1 :json [game db-type]
  (->> game
       (swap! log-edn assoc (:game-id game))
       json/write-str
       (spit "log.json")))


; x = 1 ; (reset! x 1)
; x = 2 ; (reset! x 2)

;(update {:x 1} :x - 5)
;(assoc {:x 1} :x (- 1 5))
;(swap! log conj game)
;(reset! log (conj @log game))

; READ data
; WRITE data
