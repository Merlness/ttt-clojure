(ns ttt-clojure.comp-difficulty
      (:require [ttt-clojure.computer :as ai]
                [ttt-clojure.ui :as ui]))


(defn difficulty []
  (let [user-input (ui/get-user-input-difficulty)]
    (cond
      (= user-input "1") (ai/easy-ai)
      (= user-input "2") (ai/medium-ai)
      (= user-input "3") (ai/hard-ai)
      :else (recur))))
