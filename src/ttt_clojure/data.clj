(ns ttt-clojure.data
  (:require [clojure.edn :as edn]))

(defn- fetch-all-games []
  (let [log-edn (slurp "log.edn")]
    (if (empty? log-edn) [] (edn/read-string log-edn))))

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

; x = 1 ; (reset! x 1)
; x = 2 ; (reset! x 2)

;(update {:x 1} :x - 5)
;(assoc {:x 1} :x (- 1 5))
;(swap! log conj game)
;(reset! log (conj @log game))

(defn save [game]
  (->> game
       (swap! log conj)
       pr-str
       (spit "log.edn")))
; READ data
; WRITE data
