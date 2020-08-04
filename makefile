default:
	bundle exec rackup -p 9292 config.ru

test:
	ruby test_routes.rb

import-import:
	ruby tasks.rb i db.jsonl

export-database:
	ruby tasks.rb e db.jsonl
