cmd /C "lein uberjar"
copy target\uberjar\br.eng.crisjr.failproof-0.1.0-standalone.jar java\br.eng.crisjr.failproof.jar
cd java
make do
cd ..
