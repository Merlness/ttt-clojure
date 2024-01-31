(ns ttt-clojure.computer-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.computer :as sut]
            [ttt-clojure.easy-comp :as ec]
            [ttt-clojure.minimax :as mm]
            [ttt-clojure.ui :as ui]
            [ttt-clojure.board :as board]))

(defn game-result [grid]
  (cond
    (board/x-wins grid) :win
    (board/o-wins grid) :loss
    (board/tie grid) :tie))

(defn place-ai-move [board]
  (let [move (mm/next-move board)]
    (ui/place-xo board move "X")))

(declare play-game-computer-second)

(defn- collect-ai-results [results board]
  (let [board (place-ai-move board)
        result (game-result board)]
    (if result
      (do
        (conj results result))
      (concat results (play-game-computer-second board)))))

(defn collect-move-results [results move board]
  (let [board (ui/place-xo board move "O")
        result (game-result board)]
    (if result
      (do
        (conj results result))
      (collect-ai-results results board))))

(defn- play-game-computer-second [board]
  (reduce #(collect-move-results %1 %2 board) [] (filter number? board)))

(defn- play-game-computer-first [board]
  (play-game-computer-second (place-ai-move board)))

(describe "Comp Testing"
  (with-stubs)

  #_(context "minimax"
      (it "tests minimax going first"
        (let [results (play-game-computer-first [1 2 3 4 5 6 7 8 9])]
          ;(println "results " (count results))
          ;(println "results " (count results) results)
          (should-not-contain :loss results)))


      (it "tests minimax going second"
        (let [results (play-game-computer-second [1 2 3 4 5 6 7 8 9])]
          ;(println "results " (count results))
          ;(println "results " (count results) results)
          (should (not-any? #(= % :loss) results))))

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

    (it "fifth-random move"
      (with-redefs [rand-int (fn [_] 1)
                    ec/place-easy-move (stub :place-easy-move {:return 9})
                    mm/next-move (stub :next-move)]
        (should= 9 (sut/medium-difficulty mm/next-move ["O" "O" "X" 4 "X" 6 "O" 8 9]))
        (should-have-invoked :place-easy-move {:with [["O" "O" "X" 4 "X" 6 "O" 8 9]]})))

    (it "fifth move-hard move"
      (with-redefs [rand-int (fn [_] 0)
                    mm/next-move (stub :next-move {:return 6})
                    ec/place-easy-move (stub :place-easy-move)]
        (should= 6 (sut/medium-difficulty mm/next-move ["O" "O" "X" 4 "X" 6 "O" 8 9]))
        (should-have-invoked :next-move {:with [["O" "O" "X" 4 "X" 6 "O" 8 9]]})
        (should-not-have-invoked :place-easy-move)))

    (it "seventh-random move"
      (with-redefs [rand-int (fn [_] 1)
                    ec/place-easy-move (stub :place-easy-move {:return 8})
                    mm/next-move (stub :next-move)]
        (should= 8 (sut/medium-difficulty mm/next-move ["O" "O" "X" 4 "X" "X" 7 8 "O"]))
        (should-have-invoked :place-easy-move {:with [["O" "O" "X" 4 "X" "X" 7 8 "O"]]})))

    (it "seventh move-hard move"
      (with-redefs [rand-int (fn [_] 0)
                    mm/next-move (stub :next-move {:return 4})
                    ec/place-easy-move (stub :place-easy-move)]
        (should= 4 (sut/medium-difficulty mm/next-move ["O" "O" "X" 4 "X" "X" "O" 8 9]))
        (should-have-invoked :next-move {:with [["O" "O" "X" 4 "X" "X" "O" 8 9]]})
        (should-not-have-invoked :place-easy-move)))

    )

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
        (should-have-invoked :next-move {:with [[1 2 3 4 5 6 7 8 9]]})))
           )



  ;(it "says if you start first"
  ;  (with-redefs [read-line (fn [] "1")]
  ;    (with-out-str (should-not (sut/start-first?)))))
  ;
  ;(it "says if you start second"
  ;  (with-redefs [read-line (fn [] "2")]
  ;    (with-out-str (should (sut/start-first?)))))
  (context "non stud testing"

    (it "tests grid-after-comp-O"
      (with-in-str "1\n"
        (should= ["O" 2 3 4 5 6 7 8 9]
                 (sut/grid-after-comp false [1 2 3 4 5 6 7 8 9] 1 "O"))))

    (it "tests grid-after-comp X"
      (with-in-str "1\n"
        (should= ["X" 2 3 4 5 6 7 8 9]
                 (sut/grid-after-comp false [1 2 3 4 5 6 7 8 9] 1 "X"))))

    (it "tests comp-move-statement-x"
      (with-out-str
        (should= ["X" 2 3 4 5 6 7 8 9]
                 (sut/comp-move-statement true [1 2 3 4 5 6 7 8 9] 1))))

    (it "tests comp-move-statement-o"
      (with-out-str
        (should= [1 2 3 4 5 6 7 8 "O"]
                 (sut/comp-move-statement false [1 2 3 4 5 6 7 8 9] 9)))))

  (context "comp vs comp"
    #_(it "comp-vs-comp"
      (with-redefs [ui/start-first? (constantly true)
                    ui/print-board (stub :print-board)
                    ui/print-end-computer (stub :print-end-computer)
                    ;mm/next-move (stub :next-move {:return 1})
                    ;mm/next-move-2 (stub :next-move-2 {:return 2})
                    ]

        (let [output (with-out-str (sut/comp-vs-comp [1 2 3 4 5 6 7 8 9] mm/next-move mm/next-move-2))]
          ;(should= ["X" "O" 3 4 5 6 7 8 9] output)
          (should-have-invoked :next-move {:with [[1 2 3 4 5 6 7 8 9]]})
          (should-have-invoked :next-move-2 {:with [["X" 2 3 4 5 6 7 8 9]]})
          (should-have-invoked :print-board {:with [["X" "O" 3 4 5 6 7 8 9]]})
          (should-not-have-invoked :print-end-computer {:with [["X" "O" 3 4 5 6 7 8 9]]})
          )))

    (context "get difficulty"

      (it "easy"
        (with-redefs [ui/get-difficulty (constantly :easy)]
          (should= ec/place-easy-move (sut/get-difficulty "foo"))))

      #_(it "medium"
          (with-redefs [ui/get-difficulty (constantly :medium)]
            (should= "bar" (sut/get-difficulty "bar"))))

      (it "hard"
        (with-redefs [ui/get-difficulty (constantly :hard)]
          (should= "blah" (sut/get-difficulty "blah")))))

    )
  #_(context "AI vs AI"
      (it "ai-vs-ai"
        (with-redefs [ui/welcome-c-vs-c (stub :welcome)
                      ui/second-difficulty-message (stub :second-message)
                      ui/get-difficulty (constantly "1")
                      sut/comp-vs-comp (stub :comp-vs-comp {:return
                                                            (ec/place-easy-move ec/place-easy-move)})]
          (let [output (with-out-str (sut/ai-vs-ai [1 2 3 4 5 6 7 8 9] ))]
            (should-have-invoked :comp-vs-comp {:with (ec/place-easy-move ec/place-easy-move)})))))

  )
