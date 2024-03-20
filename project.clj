(defproject ttt_clojure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  ;:main ttt-clojure.main
  :main ttt-clojure.sketch
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/data.json "2.5.0"]
                 [org.postgresql/postgresql "42.7.2"]
                 [com.github.seancorfield/next.jdbc "1.3.909"]
                 [quil "3.1.0"]
                 ]
  :profiles {:dev {:dependencies [[speclj "3.4.3"]
                                  ]}}
  :plugins [[speclj "3.4.3"]]
  :test-paths ["spec"]
  :java-cmd "/Users/merlmartin/Library/Java/JavaVirtualMachines/corretto-1.8.0_402/Contents/Home/bin/java"

  )
