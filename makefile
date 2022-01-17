default: run

migrate:
	lein run m

run: migrate
	lein run

test:
	lein test

