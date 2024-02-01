(ns ttt-clojure.computer-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.minimax :as mm]
            [ttt-clojure.ui :as ui]
            [ttt-clojure.board :as board]))

(defn game-result [grid maximizing-token minimizing-token]
  (cond
    (board/token-wins grid maximizing-token) :win
    (board/token-wins grid minimizing-token) :loss
    (board/tie grid) :tie))

(defn place-ai-move [board maximizing-token minimizing-token]
  (let [move (mm/next-move-real board maximizing-token minimizing-token)]
    (ui/place-xo board move maximizing-token)))

(declare play-game-computer-second)

(defn- collect-ai-results [results board maximizing-token minimizing-token]
  (let [board (place-ai-move board maximizing-token minimizing-token)
        result (game-result board maximizing-token minimizing-token)]
    (if result (do (conj results result)) (concat results (play-game-computer-second board maximizing-token minimizing-token)))))

(defn collect-move-results [results move board maximizing-token minimizing-token]
  (let [board (ui/place-xo board move minimizing-token)
        result (game-result board maximizing-token minimizing-token)]
    (if result (do (conj results result)) (collect-ai-results results board maximizing-token minimizing-token))))

(defn- play-game-computer-second [board maximizing-token minimizing-token]
  (reduce #(collect-move-results %1 %2 board maximizing-token minimizing-token) [] (filter number? board)))

(defn- play-game-computer-first [board maximizing-token minimizing-token]
  (play-game-computer-second (place-ai-move board maximizing-token minimizing-token) maximizing-token minimizing-token))


(describe "Comp Testing"
  (with-stubs)

  ;(it "tests minimax going first"
  ;  (let [results (play-game-computer-first [1 2 3 4 5 6 7 8 9] "X" "O")]
  ;    ;(println "results " (count results))
  ;    ;(println "results " (count results) results)
  ;    (should-not-contain :loss results)))
  ;
  ;(it "tests minimax going second"
  ;  (let [results (play-game-computer-second [1 2 3 4 5 6 7 8 9] "X" "O")]
  ;    ;(println "results " (count results))
  ;    ;(println "results " (count results) results)
  ;    (should (not-any? #(= % :loss) results))))

  )


;(it "seventh move-hard move"
;  (with-redefs [rand-int (fn [_] 0)
;                mm/next-move (stub :next-move {:return 4})
;                ec/place-easy-move (stub :place-easy-move)]
;    (should= 4 (sut/medium-difficulty mm/next-move ["O" "O" "X" 4 "X" "X" "O" 8 9]))
;    (should-have-invoked :next-move {:with [["O" "O" "X" 4 "X" "X" "O" 8 9]]})
;    (should-not-have-invoked :place-easy-move)))
