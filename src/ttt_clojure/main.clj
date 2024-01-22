(ns ttt-clojure.main
  (:require [ttt-clojure.two-player :as tp])
  (:require [ttt-clojure.ui :as ui]
            [ttt-clojure.comp-difficulty :as dif]
            [ttt-clojure.computer :as comp]))


(defn boardgame-3x3 []
  (let [board [1 2 3 4 5 6 7 8 9]]
    (case (ui/get-user-input-main)
      "1" (dif/difficulty board)
      "2" (tp/two-player board)
      "3" (comp/ai-vs-ai board)
      (recur))))

(defn boardgame-4x4 []
  (let [board [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16]]
    (case (ui/get-user-input-main)
      "1" (dif/difficulty board)
      "2" (tp/two-player board)
      "3" (comp/ai-vs-ai board)
      (recur))))

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
    "3" (boardgame-3x3)
    "4" (boardgame-4x4)
    (recur)))

#_(comment
    (defmulti some-fn :game-type)

    (defmethod some-fn :ai-vs-ai [game-board-map]
      ;stuff
      )

    (defmethod some-fn :human-vs-human [])

    (some-fn {:game-type :ai-vs-ai :difficulty 1 :current-player "X"})
    (some-fn {:game-type :human-vs-human}))
