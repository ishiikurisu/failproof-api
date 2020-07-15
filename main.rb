require 'sinatra'
require 'json'
require './db/db.rb'

# SETUP
File.open('./.config/options.json') do |file|
  $options = JSON.parse file.read
end
$db = Database.new $options

# ROUTES
get '/' do
  $options.to_json
end

post '/users/create' do
  data = JSON.parse request.body.read
  auth_key = $db.create_user data['username'], data['password'], data['notes']
  return {"auth_key" => auth_key}.to_json
end
