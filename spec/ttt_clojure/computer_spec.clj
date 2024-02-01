(ns ttt-clojure.computer-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.minimax :as mm]
            [ttt-clojure.ui :as ui]
            [ttt-clojure.board :as board]
            [ttt-clojure.computer :as sut]))

(defn game-result [grid maximizing-token minimizing-token]
  (cond
    (board/token-wins grid maximizing-token) :win
    (board/token-wins grid minimizing-token) :loss
    (board/tie grid) :tie))

(defn place-ai-move [board maximizing-token minimizing-token]
  (let [move (mm/next-move board maximizing-token minimizing-token)]
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

  (it "makes an easy move"
    (with-redefs [sut/place-easy-move (stub :next-move {:return 1})]
      (should= 1 (sut/ai-move [1 2 3 4 5 6 7 8 9] "O" "X" :easy))
      (should-have-invoked :next-move {:with [[1 2 3 4 5 6 7 8 9]]})))

  (it "makes a medium move"
    (with-redefs [sut/place-easy-move (stub :next-easy-move {:return 1})
                  mm/next-move (stub :next-hard-move {:return 2})]
      (should= 2 (sut/ai-move [1 2 3 4 5 6 7 8 9] "O" "X" :medium))
      (should-have-invoked :next-hard-move {:with [[1 2 3 4 5 6 7 8 9] "O" "X"]})))

  (it "makes a hard move"
    (with-redefs [mm/next-move (stub :next-move {:return 1})]
      (should= 1 (sut/ai-move [1 2 3 4 5 6 7 8 9] "O" "X" :hard))
      (should-have-invoked :next-move {:with [[1 2 3 4 5 6 7 8 9] "O" "X"]})))


  (context "easy computer"
    (it "finds available actions"
      (let [board [1 2 3
                   4 5 6
                   7 8 9]]
        (should= (sut/find-actions board)
                 '(1 2 3 4 5 6 7 8 9))))

    (it "finds less available actions"
      (let [board ["X" "O" "X"
                   4 5 6
                   7 8 9]]
        (should= (sut/find-actions board)
                 '(4 5 6 7 8 9))))

    (it "finds no actions"
      (let [board ["X" "O" "X"
                   "X" "X" "X"
                   "X" "O" "O"]]
        (should= (sut/find-actions board)
                 '())))

    (it "generates a random number"
      (should-contain (sut/find-rand-int [1 2 3 4 5 6 7 8 9])
                      [1 2 3 4 5 6 7 8 9]))

    (it "generates a random number form all possibilities"
      (should-contain (sut/find-rand-int '(4 5 6 7 8 9))
                      [4 5 6 7 8 9]))

    (it "generates a random number from less possibilities"
      (should-contain (sut/find-rand-int '(4 5 6 7 8 9))
                      [4 5 6 7 8 9]))

    (it "generates a random number from even less possibilities"
      (should-contain (sut/find-rand-int '(4 7 9)) [4 7 9]))

    (it "generates a random number from two possibilities"
      (should-contain (sut/find-rand-int '(4 9)) [4 9]))

    (it "generates a random number from 1 possibility"
      (should= 9 (sut/find-rand-int '(9))))

    (it "chooses a random move from an empty board"
      (should-contain (sut/place-easy-move [1 2 3 4 5 6 7 8 9])
                      [1 2 3 4 5 6 7 8 9]))

    (it "chooses a random move  after 3 moves"
      (should-contain (sut/place-easy-move [4 5 6 7 8 9]) [4 5 6 7 8 9]))

    (it "chooses a random move after 6 moves"
      (should-contain (sut/place-easy-move [4 7 9]) [4 7 9]))

    (it "chooses a random move after 7 moves"
      (should-contain (sut/place-easy-move [4 9]) [4 9]))

    (it "chooses a random move after 7 moves"
      (should= 4 (sut/place-easy-move [4]))))
  )


;(it "seventh move-hard move"
;  (with-redefs [rand-int (fn [_] 0)
;                mm/next-move (stub :next-move {:return 4})
;                ec/place-easy-move (stub :place-easy-move)]
;    (should= 4 (sut/medium-difficulty mm/next-move ["O" "O" "X" 4 "X" "X" "O" 8 9]))
;    (should-have-invoked :next-move {:with [["O" "O" "X" 4 "X" "X" "O" 8 9]]})
;    (should-not-have-invoked :place-easy-move)))
