default: tdd

try:
	lein run https://failproof-checklists.5apps.com/checklists/lists.yml

uberjar:
	lein uberjar

create: uberjar
	cp target\\uberjar\\br.eng.crisjr.failproof-0.2.2-standalone.jar java\\br.eng.crisjr.failproof.jar
	cd java
	make do
	cd ..

tdd:
	lein test
