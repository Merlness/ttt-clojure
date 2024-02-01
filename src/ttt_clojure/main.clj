(ns ttt-clojure.main
  (:require [ttt-clojure.game-modes :as gm]))

(defn -main [] (gm/player-vs-player))
