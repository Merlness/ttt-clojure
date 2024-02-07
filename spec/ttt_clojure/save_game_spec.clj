(ns ttt-clojure.save-game-spec
  (:require [clojure.edn :as edn]
            [speclj.core :refer :all]
            [ttt-clojure.computer :as comp]
            [ttt-clojure.game-modes :as game]
            [ttt-clojure.board :as board]
            [ttt-clojure.save-game :as sut]
            [ttt-clojure.ui :as ui]))

(def initial-game {:game-id 1 :player-1? true :player-1 :human :player-2 :ai :board (list 1 2 3 4 5 6 7 8 9)})

(describe "save round"
  (with-stubs)

  (it "initial save"
    (with-redefs [slurp (stub :slurp {:return []})
                  ui/get-game-board (constantly :3x3)
                  edn/read-string (stub :read-string)
                  spit (stub :spit)]
      (sut/save-round 1 true :human :ai (game/board-size))
      (should-have-invoked :slurp {:with ["log.edn"]})
      (should-not-have-invoked :read-string {:with ["log.edn"]})
      (should-have-invoked :spit {:with ["log.edn" (str [initial-game])]})))

  (it "saves a second game"
    (with-redefs [slurp (stub :slurp {:return [initial-game]})
                  ui/get-game-board (constantly :3x3)
                  edn/read-string (stub :read-string)
                  spit (stub :spit)]
      (let [saved {:game-id 2 :player-1? false :player-1 :ai :player-2 :human :board (game/board-size)}]
        (sut/save-round 2 false :ai :human (game/board-size))
        (should-have-invoked :slurp {:with ["log.edn"]})
        (should-have-invoked :read-string {:with [[initial-game]]})
        (should-have-invoked :spit {:with ["log.edn" (pr-str (list saved))]})))
    )
  )
