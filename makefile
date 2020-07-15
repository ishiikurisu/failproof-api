default:
	bundle exec rackup -p 9292 config.ru

test:
	ruby test_routes.rb
