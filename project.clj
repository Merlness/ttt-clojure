(defproject ttt_clojure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main ttt-clojure.main
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :profiles {:dev {:dependencies [[speclj "3.4.3"]
                                  [org.clojure/data.json "2.5.0"]

                                  ]}}
  :plugins [[speclj "3.4.3"]]
  :test-paths ["spec"])
