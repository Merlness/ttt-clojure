(ns ttt-clojure.sketch
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [ttt-clojure.data :as data]
            [ttt-clojure.main :as main]
            [ttt-clojure.gui :as gui]))

(q/defsketch ttt_gui
  :title "Merl's tic Tac Toe"
  :size [1000 1000]
  :setup gui/setup
  :draw gui/draw-state
  :mouse-clicked gui/mouse-clicked
  :features [:keep-on-top]
  :middleware [m/fun-mode])

(defn -main [& args]
  (let [[game-id DB] args
        game-id (when game-id (read-string game-id))
        db-type (main/select-db (last args))
        _load-db (data/load-db db-type)
        _input-id (when (and game-id (integer? game-id))
                    (reset! gui/input-id game-id))
        _requested-game (data/get-game-by-id game-id)])
  )
