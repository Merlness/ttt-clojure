(ns ttt-clojure.gui-spec
  (:require [quil.core :as q]
            [speclj.core :refer :all]
            [speclj.stub :as stub]
            [ttt-clojure.gui :as sut]
            ))

(describe "GUI tests"
  (with-stubs)

  (context "setup"
    (redefs-around [q/color-mode (stub :color-mode)])

    (it "sets color mode to RGB"
      (sut/setup)
      (should-have-invoked :color-mode {:with [:rgb]}))

    (it "starts by asking size"
      (let [state (sut/setup)]
        (should= :size (:screen state))))

    )

  (context "draw-state"

    (redefs-around [sut/draw-player-screen (fn [& _])
                    q/height (constantly 5)
                    q/width (constantly 5)
                    q/background (fn [& _])
                    q/text (fn [& _])
                    q/text-size (fn [& _])
                    q/text-align (fn [& _])
                    sut/draw-button (fn [& _])])

    (it "draws size screen"
      (let [state {:screen :size}]
        (should= state (sut/draw-state state))))

    (it "draws play screen"
      (let [state {:screen :play}]
        (should= state (sut/draw-state state))))

    (it "draws player-1 screen"
      (with-redefs [sut/draw-player-screen (stub :draw-player-screen)]
        (sut/draw-state {:screen :player-1})
        (should-have-invoked :draw-player-screen {:with [1]})))

    (it "draws player-2 screen"
      (with-redefs [sut/draw-player-screen (stub :draw-player-screen)]
        (sut/draw-state {:screen :player-2})
        (should-have-invoked :draw-player-screen {:with [2]})))


    ;that draw player screen gets invoked, stub everything else out.
    ;that q text is invoked with "player 1 please choose ..."

    ;tests for
    ;draw player screem
    ;draw state all parts
    ;mouse clicked

    )
  )

;





;
;(it "speclj stub examples"
;  (with-redefs [sut/blah (stub :blah)]
;    (should-have-invoked :blah {:times 1})
;    (should-have-invoked :blah {:with [1 2 3]})
;    (let [[inv1 inv2] (stub/invocations-of :blah)]
;      (should= [1 2 3] inv1)
;      (should= [4 5 6] inv2)))
;  )
;
;(it "another stub example"
;  (with-redefs [q/rect (stub :rect)]
;    (let [invocations (stub/invocations-of :rect)]
;      (should= [0 0 10 10] (nth invocations 0))
;      (should= [0 10 10 10] (nth invocations 1))
;      (should= [0 20 10 10] (nth invocations 2)))))
;
;(it "destructuring example"
;  (let [vec2 [[1 2 3] [4 5 6] [7 8 9]]
;        [v1 v2 v3] vec2]
;    v1 ; => [1 2 3]
;    v2 ; => [4 5 6]
;    ))
;
;(it "associative destructuring"
;  (let [my-map {:key1 :val1 :key2 :val2 :key3 :val3}
;        {:keys [key1 key2]} my-map]
;    ))
