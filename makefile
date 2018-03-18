default: tdd

try:
	lein run https://github.com/ishiikurisu/checklists/blob/master/lists.yml

uberjar:
	lein uberjar

create: uberjar
	cp target\\uberjar\\br.eng.crisjr.failproof-0.2.3-standalone.jar java\\br.eng.crisjr.failproof.jar
	cd java
	make do
	cd ..

tdd:
	lein test
