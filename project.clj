(defproject br.eng.crisjr.failproof "0.2.2"
    :description "API for Failproof Checklists"
    :url "http://wwww.crisjr.eng.br"
    :license {:name "Beerware 42"
              :url "http://people.freebsd.org/~phk"}
    :dependencies [[org.clojure/clojure "1.7.0"]]
    :main ^:skip-aot br.eng.crisjr.failproof.tools
    :target-path "target/%s"
    :profiles {:uberjar {:aot :all}})
