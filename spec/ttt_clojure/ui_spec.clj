(ns ttt-clojure.ui-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.ui :as sut]))


(describe "Updating board"

  (it "updates board for X"
    (with-redefs [read-line (fn [] "1")]
      (should= ["X" 2 3 4 5 6 7 8 9] (sut/update-board [1 2 3 4 5 6 7 8 9] true))))

  (it "updates board for O"
    (with-redefs [read-line (fn [] "1")]
      (should= ["O" 2 3 4 5 6 7 8 9] (sut/update-board [1 2 3 4 5 6 7 8 9] false))))
  )
