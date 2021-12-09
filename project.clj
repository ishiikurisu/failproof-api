(defproject br.bsb.liberdade.fpcl.api "0.0.1"
  :description "Failproof Checklists API"
  :url "http://www.liberdade.bsb.br/util/fpcl"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [compojure "1.6.1"]
                 [http-kit "2.3.0"]
                 [ring/ring-defaults "0.3.2"]
                 [org.clojure/data.json "0.2.6"]
                 [selmer "1.12.40"]]
  :main ^:skip-aot br.bsb.liberdade.fpcl.api
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
