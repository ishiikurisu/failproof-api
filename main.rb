require 'sinatra'
require 'json'
require './db/db.rb'

# SETUP
File.open('./.config/options.json') do |file|
  $options = JSON.parse(file.read())
end
$db = Database.new $options

# ROUTES
get '/' do
  $options.to_json
end

# TODO add `POST /users/create` route
