require 'sinatra'
require 'json'
require './db/db.rb'

# SETUP
File.open('./.config/options.json') do |file|
  $options = JSON.parse file.read
end
$db = Database.new $options, "./db/sql"

# ROUTES
get '/' do
  $options.to_json
end

post '/users/create' do
  data = JSON.parse request.body.read
  payload = $db.create_user data['username'], data['password'], data['notes']
  return payload.to_json
end

post '/users/auth' do
  data = JSON.parse request.body.read
  payload = $db.auth_user data['username'], data['password']
  return payload.to_json
end

get '/notes' do
  auth_key = params['auth_key']
  payload = $db.get_notes auth_key
  return payload.to_json
end

# TODO implement route to get notes from a user
# TODO implement route to update notes from a user
