(ns ttt-clojure.core-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.ui :as sut]))

(describe "Making a grid"
  (with-stubs)




    ;(it "should work"
    ;  (should= [1 2 3 4 5 6 7 8 9]
    ;           (sut/game-loop )))
    )

  #_(it "captures console out"
      (should= "hello merl!\n" (with-out-str (println "hello merl!")))

      ;[1 3.14 "string" :keyword 'symbol [] {} '()]
      ;(should= 1 (map str [1 3.14 "string" :keyword 'symbol]))

      (let [m {:x 1 :y 2}
            v ["a" "b" "c"]]
        ;(should= "foo" (:y m))
        ;(should= "foo" (:x m))
        ;(should= "foo" (m :x))
        ;(should= 1 (nth v 3 "not-found"))
        )

      (with-redefs [println (stub :blah {:invoke str})]
        (should= "hello merl!" (println "hello merl!"))
        (should-have-invoked :blah {:with ["hello merl!"]}))

      )

  #_(it "plays a full game"
      (let [board []]
        (sut/game-loop board)
        (move 1)
        (move 2)
        (move 4)
        (move 3)
        (move 6)
        (winner!)
        )
      )
