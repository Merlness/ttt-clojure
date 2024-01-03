(ns ttt-clojure.main
  (:require [ttt-clojure.two-player :as tp])
  (:require [ttt-clojure.computer :as ai]))

(defn -main []
  (let [user-input
        (do (println "Please press 1 for Tic Tac Toe vs AI,
          or 2 for Two Player Tic Tac Toe ")
            (read-line))]
    (cond
      (= user-input "1") (ai/computer)
      (= user-input "2") (tp/two-player)
      :else (recur))))
