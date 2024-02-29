(ns ttt-clojure.game
  )

(defn board-size [size]
  (case size
    :3x3 (range 1 10)
    :4x4 (range 1 17)
    :3x3x3 (range 1 28)))

(defn place-xo [grid old-num xo]
  (map
    #(if (= % old-num)
       xo
       %)
    grid))

(defn place-token [board [token move]] (place-xo board move token))

(defn place-moves-into-board [player-1-token player-2-token board-size moves]
  (let [players (cycle [player-1-token player-2-token])
        _coll (map vector players moves)]
    (vec (reduce place-token board-size _coll))))

(defn convert-moves-to-board
  ([game-map]
   (convert-moves-to-board (:token (:player-1 game-map))
                           (:token (:player-2 game-map))
                           (:size game-map) (:moves game-map)))
  ([player-1-token player-2-token size moves]
   (if moves
     (place-moves-into-board player-1-token player-2-token (board-size size) moves)
     size)))


(defn creates-board-per-move
  ([game-map]
   (creates-board-per-move (:token (:player-1 game-map))
                           (:token (:player-2 game-map))
                           (:size game-map) (:moves game-map)))
  ([player-1-token player-2-token size moves]
   (map
     #(convert-moves-to-board player-1-token player-2-token size (subvec moves 0 %))
     (range 1 (inc (count moves))))))
