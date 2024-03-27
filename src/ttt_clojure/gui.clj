(ns ttt-clojure.gui
  (:require [quil.core :as q]
            [ttt-clojure.board :as board]
            [ttt-clojure.data :as data]
            [ttt-clojure.game-modes :as gm]
            [ttt-clojure.game :as game]))

(def input-id (atom nil))

(defn X [x y w h]
  (q/fill 250 0 0)
  (q/quad (+ (* x w) (/ w 2) (/ w -3)) (+ (* y h) (/ h 2) (/ h 3)) (+ (* x w) (/ w 2) (/ w -6)) (+ (* y h) (/ h 2) (/ h 3))
          (+ (* x w) (/ w 2) (/ w 6)) (+ (* y h) (/ h 2) (/ h -3)) (+ (* x w) (/ w 2) (/ w 3)) (+ (* y h) (/ h 2) (/ h -3)))
  (q/quad (+ (* x w) (/ w 2) (/ w -3)) (+ (* y h) (/ h 2) (/ h -3)) (+ (* x w) (/ w 2) (/ w -6)) (+ (* y h) (/ h 2) (/ h -3))
          (+ (* x w) (/ w 2) (/ w 6)) (+ (* y h) (/ h 2) (/ h 3)) (+ (* x w) (/ w 2) (/ w 3)) (+ (* y h) (/ h 2) (/ h 3))))
(defn O [x y w h]
  (q/fill 0 0 250)
  (q/ellipse (+ (* x w) (/ w 2)) (+ (* y h) (/ h 2)) (/ w 1.33) (/ h 1.33)))

(defn dimensions [] [(q/width) (q/height)])

(defn setup []
  (q/color-mode :rgb)
  {:screen :continue-game
   :game   {:game-id  1
            :player-1 {:kind :human :token "X"}
            :player-2 {:kind :human :token "O"}
            :size     :4x4
            :moves    []}})

(defn size [game]
  (cond
    (= :3x3 (:size game)) 3
    (= :4x4 (:size game)) 4))

(defn draw-button [text x y w h]
  (q/with-translation [200 0])
  (q/fill 237)
  (q/rect (- x (/ w 2)) y w h)
  (q/fill 0)
  (q/text-align :center :center)
  (q/text text x (+ y (/ h 2))))

(defn draw-square [size token x y]
  (let [w (/ (q/width) size)
        h (/ (- (q/height) 50) size)]
    (q/fill (case token
              "X" (X x y w h)
              "O" (O x y w h)
              [255 255 255]))
    (q/rect (* x w) (* y h) w h)))

(defn get-index [size mouse]
  (let [w (/ (q/width) size)
        h (/ (- (q/height) 50) size)
        x (int (/ (:x mouse) w))
        y (int (/ (:y mouse) h))]
    (+ x (* y size))))

(defn available-move? [token size index]
  (and (not
         (or (= token "X") (= token "O")))
       (<= index (* size size))))

(defn header [text x y]
  (q/text-size 38)
  (q/text-align :center :center)
  (q/text text x y))

(defn draw-player-screen [player-number]
  (let [[w h] (dimensions)
        divisor 8]
    (q/background 255)
    (header (str "Player " player-number ":\n Please choose your player type:") (/ w 2) (/ h 6))
    (draw-button "Human" (/ w 2) (* h 0.33) (/ w (- divisor 3)) (/ h divisor))
    (draw-button "Easy AI" (/ w 2) (* h 0.5) (/ w (- divisor 3)) (/ h divisor))
    (draw-button "Medium AI" (/ w 2) (* h 0.67) (/ w (- divisor 3)) (/ h divisor))
    (draw-button "Hard AI" (/ w 2) (* h 0.84) (/ w (- divisor 3)) (/ h divisor))))

(defn winner-message [game]
  (let [player-1 (:player-1 game)
        player-2 (:player-2 game)
        board (game/convert-moves-to-board game)
        token-1 (board/player-token player-1)
        token-2 (board/player-token player-2)
        [w h] (dimensions)]
    (cond
      (board/token-wins board token-1)
      (do (q/text-size 30)
          (q/fill 255 0 0)
          (q/text "Player 1 wins!"
                  (/ w 2) (- h 25)))

      (board/token-wins board token-2)
      (do (q/text-size 30)
          (q/fill 0 0 255)
          (q/text "Player 2 wins!"
                  (/ w 2) (- h 25)))

      (board/tie board)
      (do (q/text-size 30)
          (q/fill 128 0 128)
          (q/text "It's a tie!" (/ w 2) (- h 25))))))

(defn message-game-id [game-id h]
  (q/fill 0 0 0)
  (q/text-size 12)
  (q/text-align :left :center)
  (q/text (str "Game id:" game-id) 0 (- h 25)))

(defn play-message [state w h]
  (let [moves (:moves (:game state))
        game (:game state)
        board (game/convert-moves-to-board game)
        game-id (:game-id game)]
    (q/text-size 20)
    (q/text-align :center :center)
    (cond
      (board/game-over? board game) (winner-message game)

      (even? (count moves)) (do
                              (q/fill 255 0 0)
                              (q/text "Player 1's turn"
                                      (/ w 2) (- h 25)))
      :else (do
              (q/fill 0 0 255)
              (q/text "Player 2's turn" (/ w 2) (- h 25))))
    (message-game-id game-id h)))

(defn player-display [player w1]
  (let [[w h] (dimensions)
        kind (:kind player)
        token (:token player)
        message (if (= :ai kind)
                  (str (name (:difficulty player)) " ai")
                  (str (name kind)))
        number (if (= "X" token) 1 2)]
    (q/fill 0)
    (q/text (str "Player " number " " token) (* w w1) (* h 0.2))
    (q/text message (* w w1) (* h 0.25))))

(defn moves-display [moves]
  (let [[w h] (dimensions)]
    (q/text-size 30)
    (q/text (str "Positions chosen in order") (* w 0.5) (* h 0.5))
    (q/text (str (clojure.string/join "  " moves)) (* w 0.5) (* h 0.6))))


(defmulti draw-state :screen)

(defmethod draw-state :continue-game [state]
  (let [[w h] (dimensions)]
    (q/background 255)
    (header (str "Would you like to continue your old game,\n"
                 "or start a new one?") (/ w 2) (/ h 5))
    (draw-button "Continue" (/ w 2) (* h 0.33) (/ w 5) (/ h 10))
    (draw-button "New Game" (/ w 2) (* h 0.5) (/ w 5) (/ h 10)))
  state)

(defmethod draw-state :recap [state]
  (let [[w h] (dimensions)
        game (:game state)
        player-1 (:player-1 game)
        player-2 (:player-2 game)
        size (:size game)
        moves (:moves game)]
    (q/background 255)
    (q/text (str (name size)) (* w 0.5) (* h 0.06))
    (q/line (* w 0.15) (* h 0.23) (* w 0.35) (* h 0.23))
    (q/line (* w 0.65) (* h 0.23) (* w 0.85) (* h 0.23))
    (player-display player-1 0.25)
    (player-display player-2 0.75)
    (moves-display moves)
    state))

(defmethod draw-state :size [state]
  (let [[w h] (dimensions)]
    (q/background 255)
    (header (str "Welcome to Merl's Tic Tac Toe game!\n"
                 " Please choose your board size:")
            (/ w 2)
            (/ h 5))
    (draw-button "3x3" (/ w 2) (* h 0.33) (/ w 10) (/ h 10))
    (draw-button "4x4" (/ w 2) (* h 0.5) (/ w 10) (/ h 10)))
  state)

(defmethod draw-state :player-1 [_state]
  (draw-player-screen 1))

(defmethod draw-state :player-2 [_state]
  (draw-player-screen 2))

(defmethod draw-state :play [state]
  (q/background 235)
  (let [size (size (:game state))
        game (:game state)
        board (game/convert-moves-to-board game)
        [w h] (dimensions)]
    (doseq [y (range size)
            x (range size)]
      (let [index (+ x (* y size))
            token (get board index)]
        (draw-square size token x y)))
    (play-message state w h))
  state)

(defmethod draw-state :again [state]
  (let [[w h] (dimensions)]
    (q/background 255)
    (header (str "Would you like to play again?")
            (/ w 2)
            (/ h 5))
    (draw-button "Play Again" (/ w 2) (* h 0.33) (/ w 5) (/ h 10))
    (draw-button "No Thanks" (/ w 2) (* h 0.5) (/ w 5) (/ h 10)))
  state)

(defn area-clicked [x-mouse y-mouse x y width height]
  (and (>= x-mouse (- x (/ width 2)))
       (<= x-mouse (+ x (/ width 2)))
       (>= y-mouse y)
       (<= y-mouse (+ y height))))

(defn continue? []
  (let [input-id (some-> @input-id int)
        requested-game-map (when input-id (data/get-game-by-id input-id))
        last-game-map (data/get-last-game)
        requested-board (when requested-game-map (game/convert-moves-to-board requested-game-map))
        last-board (when last-game-map (game/convert-moves-to-board last-game-map))
        requested-game-over? (board/game-over? requested-board requested-game-map)
        last-game-over? (board/game-over? last-board last-game-map)]
    (cond
      (and requested-game-map (not requested-game-over?)) requested-game-map
      (and last-game-map (not last-game-over?)) last-game-map
      :else false)))

(defmulti mouse-clicked (fn [state _mouse] (:screen state)))

(defmethod mouse-clicked :continue-game [state mouse]
  (let [x (:x mouse)
        y (:y mouse)
        [w h] (dimensions)
        game-id (data/get-next-game-id)
        new-state (assoc-in state [:game :game-id] game-id)
        game-to-continue (continue?)]
    (cond
      (area-clicked x y (/ w 2) (* h 0.5) (/ w 5) (/ h 10))
      (assoc new-state :screen :size)

      (and
        (area-clicked x y (/ w 2) (* h 0.33) (/ w 5) (/ h 10))
        (continue?))
      (-> new-state
          (assoc :game game-to-continue)
          (assoc :screen :recap))                           ;possible to continue? yes :screen play                                                ;no? screen :size                      ;continue the old game

      :else state)))

(defn update-state [state screen game-key value]
  (-> state
      (assoc :screen screen)
      (assoc-in [:game game-key] value)))

(defmethod mouse-clicked :recap [state _mouse]
  (assoc state :screen :play))

(defmethod mouse-clicked :size [state mouse]
  (let [x (:x mouse)
        y (:y mouse)
        [w h] (dimensions)]
    (cond
      (area-clicked x y (/ w 2) (* h 0.33) (/ w 10) (/ h 10))
      (update-state state :player-1 :size :3x3)

      (area-clicked x y (/ w 2) (* h 0.5) (/ w 10) (/ h 10))
      (update-state state :player-1 :size :4x4)

      :else state)))


(defmethod mouse-clicked :player-1 [state mouse]
  (let [x (:x mouse)
        y (:y mouse)
        [w h] (dimensions)
        divisor 8]
    (cond
      (area-clicked x y (/ w 2) (* h 0.33) (/ w (- divisor 3)) (/ h divisor))
      (update-state state :player-2 :player-1 {:kind :human :token "X"})

      (area-clicked x y (/ w 2) (* h 0.5) (/ w (- divisor 3)) (/ h divisor))
      (update-state state :player-2 :player-1 {:kind :ai :token "X" :difficulty :easy})

      (area-clicked x y (/ w 2) (* h 0.67) (/ w (- divisor 3)) (/ h divisor))
      (update-state state :player-2 :player-1 {:kind :ai :token "X" :difficulty :medium})

      (area-clicked x y (/ w 2) (* h 0.84) (/ w (- divisor 3)) (/ h divisor))
      (update-state state :player-2 :player-1 {:kind :ai :token "X" :difficulty :hard})

      :else state)))

(defmethod mouse-clicked :player-2 [state mouse]
  (let [x (:x mouse)
        y (:y mouse)
        [w h] (dimensions)
        divisor 8]
    (cond
      (area-clicked x y (/ w 2) (* h 0.33) (/ w (- divisor 3)) (/ h divisor))
      (update-state state :play :player-2 {:kind :human :token "O"})

      (area-clicked x y (/ w 2) (* h 0.5) (/ w (- divisor 3)) (/ h divisor))
      (update-state state :play :player-2 {:kind :ai :token "O" :difficulty :easy})

      (area-clicked x y (/ w 2) (* h 0.67) (/ w (- divisor 3)) (/ h divisor))
      (update-state state :play :player-2 {:kind :ai :token "O" :difficulty :medium})

      (area-clicked x y (/ w 2) (* h 0.84) (/ w (- divisor 3)) (/ h divisor))
      (update-state state :play :player-2 {:kind :ai :token "O" :difficulty :hard})

      :else state)))

(defmethod mouse-clicked :play [state mouse]
  (let [{:keys [moves player-1 player-2]} (:game state)
        [player opponent] (if (board/player1? moves)
                            [player-1 player-2]
                            [player-2 player-1])
        size (size (:game state))
        index (get-index size mouse)
        game (:game state)
        board (game/convert-moves-to-board game)
        move (if (= :ai (:kind player))
               (gm/get-move player opponent board)
               (inc index))
        token (get board index)
        new-moves (conj (:moves game) move)]
    (cond
      (board/game-over? board game)
      (assoc state :screen :again)

      (= :ai (:kind player))
      (do
        (data/save! (assoc game :moves new-moves))
        (assoc-in state [:game :moves] new-moves))

      (available-move? token size move)
      (do
        (data/save! (assoc game :moves new-moves))
        (assoc-in state [:game :moves] new-moves))

      :else state)))

(defmethod mouse-clicked :again [state mouse]
  (let [x (:x mouse)
        y (:y mouse)
        [w h] (dimensions)
        game-id (:game-id (:game state))
        _ (prn "game-id:" game-id)]
    (cond
      (area-clicked x y (/ w 2) (* h 0.33) (/ w 5) (/ h 10))
      (-> state
          (assoc-in [:game :game-id] (inc game-id))
          (update-state :size :moves []))

      (area-clicked x y (/ w 2) (* h 0.5) (/ w 5) (/ h 10))
      (q/exit)

      :else state)))
