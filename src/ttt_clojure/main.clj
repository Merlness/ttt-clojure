(ns ttt-clojure.main
  (:require [ttt-clojure.two-player :as tp])
  (:require [ttt-clojure.ui :as ui]
            [ttt-clojure.comp-difficulty :as dif]
            [ttt-clojure.computer :as comp]))

(defn -main []
  (let [user-input (ui/get-user-input-main)]
    (cond
      (= user-input "1") (dif/difficulty)
      (= user-input "2") (tp/two-player)
      (= user-input "3") (comp/ai-vs-ai)
      :else (recur))))

(comment
 (defmulti some-fn :game-type)

(defmethod some-fn :ai-vs-ai [game-board-map]
  ;stuff
  )

(defmethod some-fn :human-vs-human [])

(some-fn {:game-type :ai-vs-ai :difficulty 1 :current-player "X"})
(some-fn {:game-type :human-vs-human}))
