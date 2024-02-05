(ns ttt-clojure.save-game
  (:require [clojure.edn :as edn]))

(def save-data {:game-id nil :player-1 nil :player-2 nil :board nil })

;(defn save-round-2 [game-id player-1 player-2 grid]
;  (let [log-edn (slurp "log.edn")
;        log (edn/read-string log-edn)
;        game-state (assoc save-data :game-id game-id :player-1 player-1 :player-2 player-2 :board grid)
;        new-log (conj log game-state)
;        new-log-edn (pr-str new-log)
;        ]
;    (spit "log.edn" (pr-str new-log-edn) :append false)
;    ;(spit "log.edn" (pr-str new-log-edn "\n") :append false)
;    ))

(defn save-round-2 [game-id player-1 player-2 grid]
  (let [log-edn (slurp "log.edn")
        log (if (empty? log-edn) [] (edn/read-string log-edn))
        game-state (assoc save-data :game-id game-id :player-1 player-1 :player-2 player-2 :board grid)
        new-log (conj log game-state)
        ;new-log-edn (apply str (interpose "\n" (map pr-str new-log)))]
    new-log-edn (prn-str new-log)]
    (spit "log.edn" new-log-edn :append false)))


(defn save-game-id [game-id]
  (spit "game-id.edn" (str game-id) :append false))

;read log.edn, turn to vector, append, spit/:append false
