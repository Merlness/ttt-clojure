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
    "1" (dif/difficulty board) ;fix print to be 4x4
    "2" (tp/two-player board)
    "3" (comp/ai-vs-ai board)
    (recur))))

(defn -main []
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
