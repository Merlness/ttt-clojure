(ns ttt-clojure.gui
  (:require [quil.core :as q]
            [quil.middleware :as m]
            ))

(defn update-state []
  ;(let [grid (:grid state)
  ;      new-grid (kata/evolve grid)]
  ;  {:grid (mapv vec new-grid)})
  )
;;
;;(defn default-grid [size]
;;  (mapv (fn [_] (mapv (fn [_] (rand-nth [0 1])) (range size))) (range size)))
;;
(defn setup []
  (q/frame-rate 1)
  (q/color-mode :hsb)
  {:grid [1 2 3];(default-grid 50)
   })

(defn draw-state-data []
  (let [cell-size (/ (q/width) 3)
        grid [1 2 3]
        ]
    (for [row (range (count grid))
          column (range (count grid))]
      {:cell-state (get-in grid [row column])
       :x          (* column cell-size)
       :y          (* row cell-size)
       :size       cell-size})))

(defn draw-state []
  (doseq [cell  (draw-state-data )]
    (q/fill (if (= (:cell-state cell) 1) 0 255))
    (q/rect (:x cell) (:y cell) (:size cell) (:size cell))))

(defn gui []
(q/defsketch tic-tac-toe
             :title "Tic Tac Toe"
             :size [1200 1200]
             :setup setup
             :update update-state
             :draw draw-state
             :features [:keep-on-top]
             :middleware [m/fun-mode]))


;;
;;
;
;
;;(defn update-state [state]
;;  (let [grid (:grid state)
;;        new-grid (kata/evolve grid)]
;;    {:grid (mapv vec new-grid)}))
;;
;;(defn default-grid [size]
;;  (mapv (fn [_] (mapv (fn [_] (rand-nth [0 1])) (range size))) (range size)))
;;
;;(defn setup []
;;  (q/frame-rate 10)
;;  (q/color-mode :hsb)
;;  {:grid (default-grid 50)})
;;
;;(defn draw-state-data [state]
;;  (let [cell-size (/ (q/width) (count (:grid state)))
;;        grid (:grid state)]
;;    (for [row (range (count grid))
;;          column (range (count grid))]
;;      {:cell-state (get-in grid [row column])
;;       :x          (* column cell-size)
;;       :y          (* row cell-size)
;;       :size       cell-size})))
;;
;;(defn draw-state [state]
;;  (doseq [cell (draw-state-data state)]
;;    (q/fill (if (= (:cell-state cell) 1) 0 255))
;;    (q/rect (:x cell) (:y cell) (:size cell) (:size cell))))
;;(q/defsketch game-of-life-2
;;             :title "Game of Life"
;;             :size [1000 1000]
;;              setup function called only once, during sketch initialization.
;             ;:setup ui/setup
;             ; update-state is called on each iteration before draw-state.
;             ;:update ui/update-state
;             ;:draw ui/draw-state
;             ;:features [:keep-on-top]
;             ; This sketch uses functional-mode middleware.
;             ; Check quil wiki for more info about middlewares and particularly
;             ; fun-mode.
;             ;:middleware [m/fun-mode])
