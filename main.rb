require 'sinatra'
require 'json'
require './db/db.rb'

# SETUP
File.open('./.config/options.json') do |file|
  $options = JSON.parse file.read
end
$db = Database.new $options, "./db/sql"

# ROUTES
post '/users/create' do
  data = JSON.parse request.body.read
  $db.create_user(data['username'], data['password'], data['notes']).to_json
end

post '/users/auth' do
  data = JSON.parse request.body.read
  $db.auth_user(data['username'], data['password']).to_json
end

get '/notes' do
  $db.get_notes(params['auth_key']).to_json
end

post '/notes' do
  data = JSON.parse request.body.read
  $db.update_notes(data['auth_key'], data['notes']).to_json
end
