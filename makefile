PORT=3000

.PHONY: default
default: run

.PHONY: migrate
migrate:
	lein run m

.PHONY: run
run: migrate
	lein run

.PHONY: unit_test
unit_test:
	lein test

.PHONY: integration_test
integration_test:
	lein run &
	bb integration/network_test.clj
	fuser -k $(PORT)/tcp

.PHONY: test
test: unit_test integration_test

