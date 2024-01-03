(ns ttt-clojure.two-player-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.two-player :as sut]))


;(describe "two player test"
;  (it "updates board after two moves"
;      (with-in-str "1\n2\n"
;        (should= ["X" "O" 3 4 5 6 7 8 9]
;                 (-> (sut/two-player)))))
;          )
