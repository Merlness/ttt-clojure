(ns ttt-clojure.two-player-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.two-player :as sut]))


;(describe "two player test"
;  (it "updates board after two moves"
;      (with-in-str "1\n2\n"
;        (should= ["X" "O" 3 4 5 6 7 8 9]
;                 (-> (sut/two-player)))))
;
; (it "hard-ai"
;  (with-redefs [ui/start-first? (constantly true)
;                ui/print-board (stub :print-board)
;                ui/print-end-computer (stub :print-end-computer)
;                ui/my-turn-statement (stub :my-turn-statement)
;                ui/update-board (stub :update-board)
;                ;mm/next-move (stub :next-move {:return 1})
;                ]
;    (with-in-str "2\n5\n"
;      (let [output (with-out-str (sut/hard-ai))]
;        (should= "" output)
;        ;(should-have-invoked :next-move {:with [[1 2 3 4 5 6 7 8 9]]})
;        (should-have-invoked :print-board {:with [["X" 2 3 4 5 6 7 8 9]]})
;        (should-not-have-invoked :print-end-computer {:with [[1 2 3 4 5 6 7 8 9]]})
;        )))))
