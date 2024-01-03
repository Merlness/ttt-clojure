(ns ttt-clojure.computer-spec
  (:require [speclj.core :refer :all]
            [ttt-clojure.computer :as sut] ))



(describe "Comp Testing"

(it "says if you start first"
  (with-redefs [read-line (fn [] "1")]
    (should-not (sut/start-first?))))

  (it "says if you start second"
    (with-redefs [read-line (fn [] "2")]
      (should (sut/start-first?))))
  )
