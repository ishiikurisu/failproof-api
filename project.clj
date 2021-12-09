(defproject br.bsb.liberdade.fpcl.api "0.0.1"
  :description "Failproof Checklists API"
  :url "http://www.liberdade.bsb.br/util/fpcl"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]]
  :main ^:skip-aot br.bsb.liberdade.fpcl.api
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
