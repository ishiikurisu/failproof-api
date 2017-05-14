try:
	lein run https://failproof-checklists.5apps.com/checklists/lists.yml

create:
	lein uberjar
	copy target\\uberjar\\br.eng.crisjr.failproof-0.2.1-standalone.jar java\\br.eng.crisjr.failproof.jar
	cd java
	make do
	cd ..

unittest:
	lein test
