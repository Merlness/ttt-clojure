(ns ttt-clojure.main
  (:require [ttt-clojure.two-player :as tp])
  (:require [ttt-clojure.ui :as ui]
            [ttt-clojure.computer :as comp]))


(defn boardgame [board]
  (case (ui/get-user-input-main)
    :player-vs-ai (comp/human-vs-ai board (ui/get-user-input-difficulty))
    :player-vs-player (tp/two-humans board)
    :ai-vs-ai (comp/ai-vs-ai board)))

(defn boardgame-3x3 [] (boardgame (range 1 10)))
(defn boardgame-4x4 [] (boardgame (range 1 17)))
(defn boardgame-3D [] (boardgame (range 1 28)))

#_(defn play-game [{:keys [board player-1 player-2] :as game}]
    (if (and (ai? player-1) (ai? player-2))
      (special-code game)
      (normal-code game)))

;(defn ->player [label]
;  (println label)
;  (let [kind (ui/get-player-kind) ; human ai
;        difficulty (when (ai? kind) (ui/get-difficulty)) ; :easy :medium :hard
;        token (ui/get-token) ; "X" "O"
;        ]
;    {:kind kind :difficulty difficulty :token token}
;    )
;  )

(defn play [{:keys [mode size]}]

  )

(defn foo []
  (let [size (ui/get-user-input-3-4)
        mode (ui/get-user-input-main)
        token (ui/get-user-x-o)]
    (play {:mode       mode
           :size       size
           :user-token token})
    ))

(defn -main []
  #_(let [game {:board    (get-requested-board)             ; [1 2 3 4 5 6 7 8 9]
                :player-1 (get-player-1)                    ; {:kind :ai :difficulty :hard :token "X"} {:kind :human :token "O"}
                :player-2 (get-player-2)                    ; {:kind :ai :difficulty :hard :token "X"} {:kind :human :token "O"}
                }]
      (play-game game))

  (case (ui/get-user-input-3-4)
    :3x3 (boardgame-3x3)
    :4x4 (boardgame-4x4)
    :3x3x3 (boardgame-3D)))


#_(comment
    (defmulti some-fn :game-type)

    (defmethod some-fn :ai-vs-ai [game-board-map]
      ;stuff
      )

    (defmethod some-fn :human-vs-human [])

    (some-fn {:game-type :ai-vs-ai :difficulty 1 :current-player "X"})
    (some-fn {:game-type :human-vs-human}))
