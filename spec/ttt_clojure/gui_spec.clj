(ns ttt-clojure.gui-spec
  (:require [quil.core :as q]
            [speclj.core :refer :all]
            [speclj.stub :as stub]
            [ttt-clojure.game :as game]
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
                    ;sut/get-index (fn [& _])
                    sut/draw-button (fn [& _])
                    sut/draw-square (fn [& _])])

    (it "draws size screen"
      (let [state {:screen :size}]
        (should= state (sut/draw-state state))))

    (it "draws player-1 screen"
      (with-redefs [sut/draw-player-screen (stub :draw-player-screen)]
        (sut/draw-state {:screen :player-1})
        (should-have-invoked :draw-player-screen {:with [1]})))

    (it "draws player-2 screen"
      (with-redefs [sut/draw-player-screen (stub :draw-player-screen)]
        (sut/draw-state {:screen :player-2})
        (should-have-invoked :draw-player-screen {:with [2]})))

    (it "draws play screen"
      (with-redefs [sut/draw-square (stub :draw-square)
                    game/convert-moves-to-board (fn [_] ["X" 2 3 4 5 6 "O"])]
        (let [state {:game   {:game-id  1
                              :player-1 {:kind :human :token "X"}
                              :player-2 {:kind :human :token "O"}
                              :size     :4x4
                              :moves    [1 9 10]}
                     :screen :play
                     }]
          (sut/draw-state state)
          (should-have-invoked :draw-square))))

    (it "senses that mouse clicked with size is invoked"
      (let [state {:screen :size}]
        (should= state (sut/mouse-clicked state {:x 1 :y 1}))))


    (it "senses that mouse clicked with player 1 is invoked"
      (let [state {:screen :player-1}]
        (should= state (sut/mouse-clicked state {:x 1 :y 1}))))


    (it "senses that mouse clicked with player 2 is invoked"
      (let [state {:screen :player-2}]
        (should= state (sut/mouse-clicked state {:x 1 :y 1}))))


    (it "senses that mouse clicked with play is invoked"
     (with-redefs [sut/get-index (constantly 2)]
       (let [state {:screen :play
                   :game {:size :3x3
                          :moves [1 9 10]}}]

        (should= state (sut/mouse-clicked state {:x 1 :y 1})))))


    ;that draw player screen gets invoked, stub everything else out.
    ;that q text is invoked with "player 1 please choose ..."

    ;tests for
    ;draw player screem
    ;draw state all parts
    ;mouse clicked

    )
  )
