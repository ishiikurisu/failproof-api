try:
	lein run https://raw.githubusercontent.com/ishiikurisu/checklists/master/lists.yml

create:
	lein uberjar
	copy target\\uberjar\\br.eng.crisjr.failproof-0.2.0-standalone.jar java\\br.eng.crisjr.failproof.jar
	cd java
	make do
	cd ..

unittest:
	lein test
