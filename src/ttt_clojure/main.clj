(ns ttt-clojure.main
  (:require [ttt-clojure.two-player :as tp])
  (:require [ttt-clojure.ui :as ui]
            [ttt-clojure.comp-difficulty :as dif]
            [ttt-clojure.computer :as comp]))


(defn boardgame-3x3 []
  (let [board [1 2 3 4 5 6 7 8 9]]
    (case (ui/get-user-input-main)
      :playerVSai (dif/difficulty board)
      :playerVSplayer (tp/two-player board)
      :aiVSai (comp/ai-vs-ai board)
      (recur))))

(defn boardgame-4x4 []
  (let [board [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16]]
    (case (ui/get-user-input-main)
      :playerVSai (dif/difficulty board)
      :playerVSplayer (tp/two-player board)
      :aiVSai (comp/ai-vs-ai board)
      (recur))))

(defn boardgame-3D []
  (let [board [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16
               17 18 19 20 21 22 23 24 25 26 27]]
    (tp/two-player board)))

#_(defn play-game [{:keys [board player-1 player-2] :as game}]
  (if (and (ai? player-1) (ai? player-2))
    (special-code game)
    (normal-code game)))

(defn -main []
  #_(let [game {:board    (get-requested-board)             ; [1 2 3 4 5 6 7 8 9]
                :player-1 (get-player-1)                    ; {:kind :ai :difficulty :hard :token "X"} {:kind :human :token "O"}
                :player-2 (get-player-2)                    ; {:kind :ai :difficulty :hard :token "X"} {:kind :human :token "O"}
                }]
      (play-game game))

  (case (ui/get-user-input-3-4)
    :3x3 (boardgame-3x3)
    :4x4 (boardgame-4x4)
    :3x3x3 (boardgame-3D)
    (recur)))

#_(comment
    (defmulti some-fn :game-type)

    (defmethod some-fn :ai-vs-ai [game-board-map]
      ;stuff
      )

    (defmethod some-fn :human-vs-human [])

    (some-fn {:game-type :ai-vs-ai :difficulty 1 :current-player "X"})
    (some-fn {:game-type :human-vs-human}))
