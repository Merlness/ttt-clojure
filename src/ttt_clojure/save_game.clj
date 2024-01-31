(ns ttt-clojure.save-game
  (:require [clojure.edn :as edn]))

(def save-data {:player-1 nil :player-2 nil :board nil})

;(defn save-round [player-1 player-2 grid diff-1 diff-2]
;  (let [game-state (assoc save-data :player-1 player-1 :player-2 player-2 :board grid :difficulty-1 diff-1 :difficulty-2 diff-2)]
;    (spit "log.edn" game-state )))
;
;(defn save-round [game-id player-1 player-2 grid diff-1 diff-2]
;  (defn save-round [game-id player-1 player-2 grid diff-1 diff-2]
;    (let [game-state (assoc save-data :game-id game-id :player-1 player-1 :player-2 player-2 :board grid :difficulty-1 diff-1 :difficulty-2 diff-2)]
;      (spit "log.edn" (str game-state "\n") :append true))))

(defn save-round [player-1 player-2 grid]
  (let [game-state (assoc save-data :player-1 player-1 :player-2 player-2 :board grid)]
    (spit "log.edn" (str game-state "\n") :append true)))
