(ns ttt-clojure.main
  (:require [ttt-clojure.two-player :as tp])
  (:require [ttt-clojure.ui :as ui]
            [ttt-clojure.computer :as comp]
            [ttt-clojure.game-modes :as gm]))


(defn boardgame [board]
  (case (ui/get-user-input-main)
    :player-vs-ai (comp/human-vs-ai board (ui/get-difficulty))
    :player-vs-player (tp/two-humans board)
    :ai-vs-ai (comp/ai-vs-ai board)))

(defn boardgame-3x3 [] (boardgame (range 1 10)))
(defn boardgame-4x4 [] (boardgame (range 1 17)))
(defn boardgame-3D [] (boardgame (range 1 28)))

;(defn foo []
;  (let [size (ui/get-user-input-3-4)
;        mode (ui/get-user-input-main)
;        token (ui/get-user-x-o)]
;    (play {:mode       mode
;           :size       size
;           :user-token token}) ))

(defn -main []
  (gm/player-vs-player)
  ;(case (ui/old-new-way)
  ;  :new (gm/player-vs-player)
  ;  :old
  ;  (case (ui/get-user-input-3-4)
  ;         :3x3 (boardgame-3x3)
  ;         :4x4 (boardgame-4x4)
  ;         :3x3x3 (boardgame-3D)))
  )
