(ns ttt-clojure.gui-spec
  (:require [quil.core :as q]
            [speclj.core :refer :all]
            [ttt-clojure.board :as board]
            [ttt-clojure.data :as save]
            [ttt-clojure.data :as data]
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
        (should= :continue-game (:screen state))))
    )

  (context "draw-state"
    (redefs-around [sut/draw-player-screen (fn [& _])
                    q/height (constantly 5)
                    q/width (constantly 5)
                    q/background (fn [& _])
                    q/fill (fn [& _])
                    q/text (fn [& _])
                    q/text-size (fn [& _])
                    q/text-align (fn [& _])
                    sut/draw-button (fn [& _])
                    sut/draw-square (fn [& _])
                    ;data/get-next-game-id (fn [& _])
                    ])

    (it "draws continue game"
      (let [state {:screen :continue-game}]
        (should= state (sut/draw-state state))))

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
                    data/get-next-game-id (stub :next-id)
                    sut/play-message (stub :play-message)
                    game/convert-moves-to-board (fn [_] ["X" 2 3 4 5 6 "O" 8 9])]
        (let [state {:game   {:game-id  1
                              :player-1 {:kind :human :token "X"}
                              :player-2 {:kind :human :token "O"}
                              :size     :3x3
                              :moves    [1 7]}
                     :screen :play}]
          (sut/draw-state state)
          (should-have-invoked :draw-square))))

    (it " recap screen"
      (with-redefs [sut/player-display (stub :player-display)
                    sut/moves-display (stub :moves-display)
                    q/line (stub :line)
                    ]
        (let [state {:game   {:game-id  1
                              :player-1 {:kind :human :token "X"}
                              :player-2 {:kind :human :token "O"}
                              :size     :3x3
                              :moves    [1 7]}
                     :screen :recap}]
          (sut/draw-state state)
          (should-have-invoked :player-display {:with                     [{:kind :human :token "X"} 0.25]
                                                {:kind :human :token "O"} 0.75})
          (should-have-invoked :moves-display {:with [[1 7]]}))))

    )

  (context "mouse clicked"
    (before (reset! sut/input-id 10))

    (redefs-around [q/height (constantly 5)
                    q/width (constantly 5)
                    q/exit (stub :exit)
                    data/get-next-game-id (fn [& _])
                    data/get-last-game (fn [& _])])

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
      (with-redefs [sut/get-index (constantly 2)
                    save/save (stub :save)]
        (let [state {:screen :play
                     :game   {:size  :3x3
                              :moves [1 7 8]}}
              new-state {:screen :play
                         :game   {:size  :3x3
                                  :moves [1 7 8 3]}}]

          (should= new-state (sut/mouse-clicked state {:x 1 :y 1}))
          (sut/mouse-clicked state {:x 1 :y 1})
          (should-have-invoked :save))))

    (it "mouse clicks on already made move"
      (with-redefs [sut/get-index (constantly 0)]
        (let [state {:screen :play
                     :game   {:size     :3x3
                              :player-1 {:kind :human :token "X"}
                              :player-2 {:kind :human :token "O"}
                              :moves    [1 7 8]}}]          ;[X 2 3 4 5 6 O X 9]
          (should= state (sut/mouse-clicked state {:x 1 :y 1})))))

    (it "returns the requested game when it is not over"
      (with-redefs [data/get-game-by-id (constantly {:game-id  10
                                                     :size     :3x3
                                                     :player-1 {:kind :human :token "X"}
                                                     :player-2 {:kind :human :token "O"}
                                                     :moves    [1 7 8]})
                    board/game-over? (constantly false)]
        (let [expected-game {:game-id  10
                             :size     :3x3
                             :player-1 {:kind :human :token "X"}
                             :player-2 {:kind :human :token "O"}
                             :moves    [1 7 8]}
              actual-game (sut/continue?)]
          (should= expected-game actual-game))))

    (it "returns the last game when it is not over"
      (with-redefs [data/get-game-by-id (stub :game-by-id)
                    data/get-last-game (constantly {:game-id  10
                                                    :size     :3x3
                                                    :player-1 {:kind :human :token "X"}
                                                    :player-2 {:kind :human :token "O"}
                                                    :moves    [1 7 8]})
                    board/game-over? (constantly false)]
        (let [expected-game {:game-id  10
                             :size     :3x3
                             :player-1 {:kind :human :token "X"}
                             :player-2 {:kind :human :token "O"}
                             :moves    [1 7 8]}
              actual-game (sut/continue?)]
          (should= expected-game actual-game))))

    (it "returns  nil for the last game when it is  over"
      (with-redefs [data/get-game-by-id (stub :game-by-id)
                    data/get-last-game (constantly {:game-id  10
                                                    :size     :3x3
                                                    :player-1 {:kind :human :token "X"}
                                                    :player-2 {:kind :human :token "O"}
                                                    :moves    [1 2 3 4 5 6 7]})]
        (let [actual-game (sut/continue?)]
          (should= false actual-game))))

    (it "checks if you clicked continue"
      (with-redefs [data/get-last-game (stub :last:game)
                    data/get-game-by-id (constantly {:game-id  10
                                                     :size     :3x3
                                                     :player-1 {:kind :human :token "X"}
                                                     :player-2 {:kind :human :token "O"}
                                                     :moves    [1 7 8]})]
        (let [state {:screen :continue-game}
              new-state {:screen :recap :game {:game-id  10
                                               :size     :3x3
                                               :player-1 {:kind :human :token "X"}
                                               :player-2 {:kind :human :token "O"}
                                               :moves    [1 7 8]}}
              [w h] (sut/dimensions)
              mouse {:x (/ w 2) :y (* h 0.33)}]
          (should= new-state (sut/mouse-clicked state mouse)))))

    (it "checks if you clicked new game"
      (with-redefs [data/get-next-game-id (constantly 1)]
        (let [state {:screen :continue-game}
              new-state {:screen :size :game {:game-id 1}}
              [w h] (sut/dimensions)
              mouse {:x (/ w 2) :y (* h 0.5)}]
          (should= new-state (sut/mouse-clicked state mouse)))))

    (it "checks if you clicked size 3x3"
      (let [state {:screen :size}
            new-state {:screen :player-1, :game {:size :3x3}}
            [w h] (sut/dimensions)
            mouse {:x (/ w 2) :y (* h 0.33)}]
        (should= new-state (sut/mouse-clicked state mouse))))

    (it "checks if you clicked size 4x4"
      (let [state {:screen :size}
            new-state {:screen :player-1, :game {:size :4x4}}
            [w h] (sut/dimensions)
            mouse {:x (/ w 2) :y (* h 0.5)}]
        (should= new-state (sut/mouse-clicked state mouse))))

    (it "checks player 1 clicked human"
      (let [state {:screen :player-1}
            new-state {:screen :player-2, :game {:player-1 {:kind :human, :token "X"}}}
            [w h] (sut/dimensions)
            mouse {:x (/ w 2) :y (* h 0.33)}]
        (should= new-state (sut/mouse-clicked state mouse))))

    (it "checks player 1 clicked ai easy"
      (let [state {:screen :player-1}
            new-state {:screen :player-2 :game {:player-1 {:kind :ai :token "X" :difficulty :easy}}}
            [w h] (sut/dimensions)
            mouse {:x (/ w 2) :y (* h 0.5)}]
        (should= new-state (sut/mouse-clicked state mouse))))

    (it "checks player 1 clicked ai medium"
      (let [state {:screen :player-1}
            new-state {:screen :player-2 :game {:player-1 {:kind :ai :token "X" :difficulty :medium}}}
            [w h] (sut/dimensions)
            mouse {:x (/ w 2) :y (* h 0.67)}]
        (should= new-state (sut/mouse-clicked state mouse))))

    (it "checks player 1 clicked ai medium"
      (let [state {:screen :player-1}
            new-state {:screen :player-2 :game {:player-1 {:kind :ai :token "X" :difficulty :hard}}}
            [w h] (sut/dimensions)
            mouse {:x (/ w 2) :y (* h 0.84)}]
        (should= new-state (sut/mouse-clicked state mouse))))

    (it "checks player 2 clicked human"
      (let [state {:screen :player-2}
            new-state {:screen :play, :game {:player-2 {:kind :human, :token "O"}}}
            [w h] (sut/dimensions)
            mouse {:x (/ w 2) :y (* h 0.33)}]
        (should= new-state (sut/mouse-clicked state mouse))))

    (it "checks player 2 clicked ai easy"
      (let [state {:screen :player-2}
            new-state {:screen :play :game {:player-2 {:kind :ai :token "O" :difficulty :easy}}}
            [w h] (sut/dimensions)
            mouse {:x (/ w 2) :y (* h 0.5)}]
        (should= new-state (sut/mouse-clicked state mouse))))

    (it "checks player 2 clicked ai medium"
      (let [state {:screen :player-2}
            new-state {:screen :play :game {:player-2 {:kind :ai :token "O" :difficulty :medium}}}
            [w h] (sut/dimensions)
            mouse {:x (/ w 2) :y (* h 0.67)}]
        (should= new-state (sut/mouse-clicked state mouse))))

    (it "checks player 2 clicked ai medium"
      (let [state {:screen :player-2}
            new-state {:screen :play :game {:player-2 {:kind :ai :token "O" :difficulty :hard}}}
            [w h] (sut/dimensions)
            mouse {:x (/ w 2) :y (* h 0.84)}]
        (should= new-state (sut/mouse-clicked state mouse))))

    (it "End screen comes up for player 1"
      (with-redefs [sut/get-index (constantly 0)]
        (let [state {:screen :play
                     :game   {:size     :3x3
                              :player-1 {:kind :human :token "X"}
                              :player-2 {:kind :human :token "O"}
                              :moves    [1 2 3 4 5 6 7]}}
              new-state {:screen :again
                         :game   {:size     :3x3
                                  :player-1 {:kind :human :token "X"}
                                  :player-2 {:kind :human :token "O"}
                                  :moves    [1 2 3 4 5 6 7]}}]
          (should= new-state (sut/mouse-clicked state {:x 1 :y 1})))))

    (it "checks if you clicked again"
      (let [state {:screen :again :game {:game-id 1 :moves []}}
            new-state {:screen :size, :game {:game-id 2
                                             :moves   []}}
            [w h] (sut/dimensions)
            mouse {:x (/ w 2) :y (* h 0.33)}]
        (should= new-state (sut/mouse-clicked state mouse))))

    (it "checks if you clicked no"
      (let [state {:screen :again}
            [w h] (sut/dimensions)
            mouse {:x (/ w 2) :y (* h 0.5)}]
        (sut/mouse-clicked state mouse)
        (should-have-invoked :exit)))

    (it "checks if you clicked anywhere on recap"
      (let [state {:screen :recap
                   :game   {:size     :3x3
                            :player-1 {:kind :human :token "X"}
                            :player-2 {:kind :human :token "O"}
                            :moves    [1 2 3 4 5 6 7]}}
            [w h] (sut/dimensions)
            mouse {:x (/ w 2) :y (* h 0.5)}
            new-state {:screen :play
                       :game   {:size     :3x3
                                :player-1 {:kind :human :token "X"}
                                :player-2 {:kind :human :token "O"}
                                :moves    [1 2 3 4 5 6 7]}}]

        (should= new-state (sut/mouse-clicked state mouse))))
    )
  )
