(ns ttt-clojure.computer-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.computer :as sut]
            [ttt-clojure.easy-comp :as ec]
            [ttt-clojure.minimax :as mm]
            [ttt-clojure.ui :as ui]
            [ttt-clojure.board :as board]))

;(defn play-every-game-starts [board]
;  (if (game-over? board)
;    (if (win? board) :win :loss)
;    (let [available-moves (filter number? board)
;          x-or-o "x"]
;      (map (fn [move] (play-every-game (assoc board (dec move) x-or-o))) available-moves))))

;; board = [1 2 3 4 5 6 7 8 9]
;; for every empty space:
;;   place X on 1
;;     board = [X 2 3 4 5 6 7 8 9]
;;     place O on Minimax of board => 5
;;       board = [X 2 3 4 O 6 7 8 9]
;;       for every empty space:
;;         place X on 2
;;           board = [X X 3 4 O 6 7 8 9]
;;           ... minimax ... player ... minimax ...

(defn game-result [grid]
  (cond
    (board/x-wins grid) :win
    (board/o-wins grid) :loss
    (board/tie grid) :tie))

(defn place-ai-move [board]
  (let [move (mm/next-move board)]
    (ui/place-xo board move "X")))

(defn- place-move-and-check-result [board move player]
  (let [board (ui/place-xo board move player)]
    (if-let [result (game-result board)]
      [result board]
      [nil board])))

(defn play-game-logic [board first-second]
  (if-let [result (game-result board)]
    [result]
    (loop [[move & moves] (filter number? board)
           results []]
      (if move
        (let [[result board] (place-move-and-check-result board move "O")]
          (if result
            (recur moves (conj results result))
            (recur moves (concat results (first-second board)))))
        results))))

(defn- play-game-computer-second [board]
  (let [board (place-ai-move board)]
    (play-game-logic board play-game-computer-second)))

(defn- play-game-computer-first [board]
    (let [board (place-ai-move board)]
      (play-game-logic board play-game-computer-first)))


(describe "Comp Testing"
  (with-stubs)
  ;
  ;(it "tests minimax going first"
  ;  (should (not-any? #(= % :loss) (play-game-computer-first [1 2 3 4 5 6 7 8 9]))))
  ;
  ;(it "tests minimax going second"
  ;  (should (not-any? #(= % :loss) (play-game-computer-second [1 2 3 4 5 6 7 8 9]))))

  ;(it "hard-ai"
  ;  (with-redefs [ui/start-first? (constantly true)
  ;                ui/print-board (stub :print-board)
  ;                ui/print-end-computer (stub :print-end-computer)]
  ;    (with-in-str "1\n2\n3\n4\n5\n"
  ;      (let [output (with-out-str (sut/hard-ai))]
  ;        (should= "blah" output)
  ;        )
  ;      ))
  ;  )



  #_(context "foo"

      (it "always wins as x"
        ;blank board
        ;x first in corner
        ;o has 8 options
        ;return wins losses ties
        ;loop[grid [results] moves]
        (loop [grid [1 2 3 4 5 6 7 8 9]
               x-turn? true]
          (let [move (if x-turn?
                       (mm/next-move grid)
                       (testing-all-moves grid))
                new-grid (sut/comp-move-statement x-turn? grid move)]
            (ui/print-board new-grid)
            (if (not (ui/endgame-result new-grid))
              (recur new-grid (not x-turn?))
              (ui/print-end-computer new-grid))))
        )

      (it "plays every game"

        (let [game-results (flatten (play-every-game [1 2 3 4 5 6 7 8 9]))]
          (should (every? #(= :win %) (flatten (play-every-game empty-board)))))
        )
      )

  (context "medium AI"

    (it "empty board"
      (with-redefs [rand-int (stub :rand-int {:invoke (fn [_] 0)})
                    mm/next-move (stub :next-move {:return 1})
                    ec/place-easy-move (stub :place-easy-move)]
        (should= 1 (sut/medium-difficulty mm/next-move [1 2 3 4 5 6 7 8 9]))
        (should-have-invoked :next-move {:with [[1 2 3 4 5 6 7 8 9]]})
        (should-not-have-invoked :place-easy-move)
        ;(should= 50 (rand-int "blah"))
        ;(should= 5 (mm/next-move "blah"))
        ;(should-have-invoked :next-move {:with ["blah"]})
        ;(should= 1 (ec/place-easy-move "blah"))
        ;(should-have-invoked :place-easy-move {:with ["blah"]})
        ))

    (it "second move"
      (with-redefs [mm/next-move (stub :next-move {:return 5})
                    ec/place-easy-move (stub :place-easy-move)]
        (should= 5 (sut/medium-difficulty mm/next-move ["O" 2 3 4 5 6 7 8 9]))
        (should-have-invoked :next-move {:with [["O" 2 3 4 5 6 7 8 9]]})
        (should-not-have-invoked :place-easy-move)))

    (it "seventh-random move"
      (with-redefs [rand-int (fn [_] 1)
                    ec/place-easy-move (stub :place-easy-move {:return 8})
                    mm/next-move (stub :next-move)]
        (should= 8 (sut/medium-difficulty mm/next-move ["O" "O" "X" 4 "X" "X" 7 8 "O"]))
        (should-have-invoked :place-easy-move {:with [["O" "O" "X" 4 "X" "X" 7 8 "O"]]})
        ;(should-not-have-invoked :next-move)
        ))

    (it "seventh move-hard move"
      (with-redefs [rand-int (fn [_] 0)
                    mm/next-move (stub :next-move {:return 4})
                    ec/place-easy-move (stub :place-easy-move)]
        (should= 4 (sut/medium-difficulty mm/next-move ["O" "O" "X" 4 "X" "X" "O" 8 9]))
        (should-have-invoked :next-move {:with [["O" "O" "X" 4 "X" "X" "O" 8 9]]})
        (should-not-have-invoked :place-easy-move))))

  (context "easy AI"

    (it "empty board"
      (with-redefs [ec/find-rand-int (fn [_] 4)
                    ec/place-easy-move (stub :place-easy-move {:return 4})]
        (should= 4 (ec/place-easy-move [1 2 3 4 5 6 7 8 9]))
        (should-have-invoked :place-easy-move {:with [[1 2 3 4 5 6 7 8 9]]}))))

  (context "hard AI"

    (it "empty board"
      (with-redefs [mm/next-move (stub :next-move {:return 1})]
        (should= 1 (mm/next-move [1 2 3 4 5 6 7 8 9]))
        (should-have-invoked :next-move {:with [[1 2 3 4 5 6 7 8 9]]}))))



  ;(it "says if you start first"
  ;  (with-redefs [read-line (fn [] "1")]
  ;    (with-out-str (should-not (sut/start-first?)))))
  ;
  ;(it "says if you start second"
  ;  (with-redefs [read-line (fn [] "2")]
  ;    (with-out-str (should (sut/start-first?)))))
  )

(describe "non stud testing"

  (it "tests grid-after-comp-O"
    (with-in-str "1\n"
      (should= ["O" 2 3 4 5 6 7 8 9]
               (sut/grid-after-comp false [1 2 3 4 5 6 7 8 9] 1))))

  (it "tests grid-after-comp X"
    (with-out-str
      (should= ["X" 2 3 4 5 6 7 8 9]
               (sut/grid-after-comp true [1 2 3 4 5 6 7 8 9] 1))))

  (it "tests comp-move-statement"
    (with-out-str
      (should= ["X" 2 3 4 5 6 7 8 9]
               (sut/comp-move-statement true [1 2 3 4 5 6 7 8 9] 1))))
  )
