(ns ttt-clojure.save-game
  (:require [clojure.edn :as edn]
            [clojure.string :as str]))

(def save-data {:game-id nil :player-1? nil :player-1 nil :player-2 nil :board nil})

(defn save-round [game-id player-1? player-1 player-2 grid]
  (let [log-edn (slurp "log.edn")
        log (if (empty? log-edn) [] (edn/read-string log-edn))
        game-state (assoc save-data :game-id game-id :player-1? player-1?  :player-1 player-1 :player-2 player-2 :board grid)
        new-log (conj log game-state)
        new-log-edn (pr-str new-log)
        ]
    (spit "log.edn" new-log-edn)))
