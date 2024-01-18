(ns ttt-clojure.comp-difficulty
  (:require [ttt-clojure.computer :as ai]
            [ttt-clojure.ui :as ui]))


;(defn difficulty [board]
;    (case (ui/get-user-input-difficulty)
;      "1" (ai/easy-ai board)
;      "2" (ai/medium-ai board)
;      "3" (ai/hard-ai board)
;      (recur (ui/get-user-input-difficulty))))

(defn difficulty [board]
    (case (ui/get-user-input-difficulty)
      :easy (ai/easy-ai board)
      :medium (ai/medium-ai board)
      :hard (ai/hard-ai board)
      :else (recur (ui/get-user-input-difficulty))))