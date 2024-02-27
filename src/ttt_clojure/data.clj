(ns ttt-clojure.data
  (:require [clojure.edn :as edn]
            [clojure.data.json :as json]))

(defn- fetch-all-games []
  (let [log-edn (slurp "log.edn")
        log-json (slurp "log.json")]
    (if (empty? log-edn) [] (edn/read-string log-edn))
    (if (empty? log-json) [] (json/read-str log-edn :key-fn keyword)) ;maybe (json/parse-string log-json true)
    )) ;maybe conj? concat

(def log (atom (fetch-all-games)))

(defn all-games [] @log)

(defn game-history-by-id [game-id]
    (filter #(= (:game-id %) game-id) (all-games)))

(defn max-game-id [games]
  (->> games
       (map :game-id)
       (apply max 0)))

(defn last-game-id [] (max-game-id @log))
(defn get-next-game-id [] (inc (last-game-id)))
(defn get-last-game [] (last @log))


(defn save-edn [game]
  (->> game
       (swap! log conj)
       pr-str
       (spit "log.edn")))

(defn save-json [game]
  (->> game
       (swap! log conj)
       json/write-str ;maybe json/generate-string
       (spit "log.json")))


; x = 1 ; (reset! x 1)
; x = 2 ; (reset! x 2)

;(update {:x 1} :x - 5)
;(assoc {:x 1} :x (- 1 5))
;(swap! log conj game)
;(reset! log (conj @log game))

; READ data
; WRITE data
