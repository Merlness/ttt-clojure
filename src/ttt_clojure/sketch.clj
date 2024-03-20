(ns ttt-clojure.sketch
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [ttt-clojure.gui :as gui]))

(q/defsketch ttt_gui
  :title "Merl's tic Tac Toe"
  :size [1000 1000]
  :setup gui/setup
  :draw gui/draw-state
  :mouse-clicked gui/mouse-clicked
  :features [:keep-on-top]
  :middleware [m/fun-mode])

(defn -main [])
