(ns ttt-clojure.gui
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [ttt-clojure.game :as game]))

(defn X [x y w h]
  (q/fill 250 0 0)
  (q/quad (+ (* x w) (/ w 2) (/ w -3)) (+ (* y h) (/ h 2) (/ h 3)) (+ (* x w) (/ w 2) (/ w -6)) (+ (* y h) (/ h 2) (/ h 3))
          (+ (* x w) (/ w 2) (/ w 6)) (+ (* y h) (/ h 2) (/ h -3)) (+ (* x w) (/ w 2) (/ w 3)) (+ (* y h) (/ h 2) (/ h -3)))
  (q/quad (+ (* x w) (/ w 2) (/ w -3)) (+ (* y h) (/ h 2) (/ h -3)) (+ (* x w) (/ w 2) (/ w -6)) (+ (* y h) (/ h 2) (/ h -3))
          (+ (* x w) (/ w 2) (/ w 6)) (+ (* y h) (/ h 2) (/ h 3)) (+ (* x w) (/ w 2) (/ w 3)) (+ (* y h) (/ h 2) (/ h 3))))
(defn O [x y w h]
  (q/fill 0 0 250)
  (q/ellipse (+ (* x w) (/ w 2)) (+ (* y h) (/ h 2)) (/ w 1.33) (/ h 1.33)))

;---------
; | new game |
; | continue |
;-------

;---------
; New Game
; Size
; |3x3| |4x4|
; |next|
; ------

; ------
; Player 1
; |Human| |Easy AI| |Medium AI| |Hard AI|
; |next|
; ------

; ------
; Player 2
; |Human| |Easy AI| |Medium AI| |Hard AI|
; | Play |
;-------

;---
; You win! AI is thinking... Your turn!
; |ttt board|
;---


(defn setup []
  (q/color-mode :rgb)
  {:screen :size})

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
        h (/ (q/height) size)]
    (q/fill (case token
              "X" (X x y w h)
              "O" (O x y w h)
              [255 255 255]))
    (q/rect (* x w) (* y h) w h)))

{:game   {:game-id  1
          :player-1 {:kind :human :token "X"}
          :player-2 {:kind :human :token "O"}
          :size     :4x4
          :moves    [1 9 10]}
 :screen :size
 }

(defn get-index [size mouse]
  (let [w (/ (q/width) size)
        h (/ (q/height) size)
        x (int (/ (:x mouse) w))
        y (int (/ (:y mouse) h))]
    (+ x (* y size))))

(defn available-move? [token]
  (not (or (= token "X") (= token "O"))))

(defn header [text x y]
  (q/text-size 38)
  (q/text-align :center :center)
  (q/text text x y))

(defn draw-player-screen [player-number]
  (let [w (q/width)
        h (q/height)
        divisor 8]
    (q/background 255)
    (header (str "Player " player-number ":\n Please choose your player type:") (/ w 2) (/ h 6))
    (draw-button "Human" (/ w 2) (* h 0.33) (/ w (- divisor 3)) (/ h divisor))
    (draw-button "Easy AI" (/ w 2) (* h 0.5) (/ w (- divisor 3)) (/ h divisor))
    (draw-button "Medium AI" (/ w 2) (* h 0.67) (/ w (- divisor 3)) (/ h divisor))
    (draw-button "Hard AI" (/ w 2) (* h 0.84) (/ w (- divisor 3)) (/ h divisor))))

(defmulti draw-state :screen)

(defmethod draw-state :size [state]
  (let [w (q/width)
        h (q/height)]
    (q/background 255)
    (q/text-size 38)
    (q/text-align :center :center)
    (q/text (str "Welcome to Merl's Tic Tac Toe game!\n"
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
  state)


(defn area-clicked [x-mouse y-mouse x y width height]
  (and (>= x-mouse (- x (/ width 2)))
       (<= x-mouse (+ x width))
       (>= y-mouse y)
       (<= y-mouse (+ y height))))

(defmulti mouse-clicked (fn [state _mouse] (:screen state))) ;multi

(defn dimensions [] [(q/width) (q/height)])
(defmethod mouse-clicked :size [state mouse]
  (let [x (:x mouse)
        y (:y mouse)
        [w h] (dimensions)]
    (cond
      (area-clicked x y (/ w 2) (* h 0.33) (/ w 10) (/ h 10)) (do (println "3x3") (assoc state :screen :player-1))
      (area-clicked x y (/ w 2) (* h 0.5) (/ w 10) (/ h 10)) (do (println "4x4") (assoc state :screen :player-1))))
  state
  )

(defmethod mouse-clicked :player-1 [state mouse]
  (let [x (:x mouse)
        y (:y mouse)
        w (q/width)
        h (q/height)
        divisor 8]
    (cond                                                   ;test this logic make sure it fails if commented out
      (area-clicked x y (/ w 2) (* h 0.33) (/ w (- divisor 3)) (/ h divisor))
      (do (println "Human") (assoc state :screen :player-2))
      (area-clicked x y (/ w 2) (* h 0.5) (/ w (- divisor 3)) (/ h divisor))
      (do (println "AI Easy") (assoc state :screen :player-2))
      (area-clicked x y (/ w 2) (* h 0.67) (/ w (- divisor 3)) (/ h divisor))
      (do (println "AI Medium") (assoc state :screen :player-2))
      (area-clicked x y (/ w 2) (* h 0.84) (/ w (- divisor 3)) (/ h divisor))
      (do (println "AI Hard") (assoc state :screen :player-2))))
  state
  )

(defmethod mouse-clicked :player-2 [state mouse]
  (let [x (:x mouse)
        y (:y mouse)
        w (q/width)
        h (q/height)
        divisor 8]
    (cond
      (area-clicked x y (/ w 2) (* h 0.33) (/ w (- divisor 3)) (/ h divisor))
      (do
        (println "Human")
        (assoc state :screen :play))
      (area-clicked x y (/ w 2) (* h 0.5) (/ w (- divisor 3)) (/ h divisor))
      (do
        (println "AI Easy")
        (assoc state :screen :play))
      (area-clicked x y (/ w 2) (* h 0.67) (/ w (- divisor 3)) (/ h divisor))
      (do
        (println "AI Medium")
        (assoc state :screen :play))
      (area-clicked x y (/ w 2) (* h 0.84) (/ w (- divisor 3)) (/ h divisor))
      (do
        (println "AI Hard")
        (assoc state :screen :play))))
  state
  )

(defmethod mouse-clicked :play [state mouse]
  (let [size (size (:game state))
        index (get-index size mouse)
        game (:game state)
        board (game/convert-moves-to-board game)
        token (get board index)
        new-moves (conj (:moves game) (inc index))]
    (if (available-move? token)
      (assoc state :game (assoc game :moves new-moves))
      state))
  state)


;get rid of update state done
;Any vs Any - Click for every move
;AI move "AI is thinking"
;possible new play round function, or 2 play round functions
;all features present
;I do need Xs and Os


;3-18
; save
; check for wins
; allow player to start previous game
;ai to make moves


;(defn blah [f s t]
;  (prn f)
;  (prn s)
;  (prn t))
;
;; when we define an API, we get a map that looks like this:
;; {:headers [...] :body {...} :something :else}
;; where that map is passed directly into a funciton
;; I don't care about :something key, so I do the following
;(defn my-func [{:keys [body]}]
;  ;...
;  )
;(defn my-func [response]
;  (let [body (:body response)]
;    ))
