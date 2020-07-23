require 'sinatra'
require 'json'
require './db/db.rb'

# SETUP
if ENV['DATABASE_URL'] and ENV['SALT']
  $db = Database.new({
    "database_url" => ENV['DATABASE_URL'],
    "salt" => ENV['SALT'],
  }, "./db/sql")
else
  File.open('./.config/options.json') do |file|
    $db = Database.new(JSON.parse(file.read), "./db/sql")
  end
end

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
