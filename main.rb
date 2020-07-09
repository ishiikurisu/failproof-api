require 'sinatra'

get '/' do
  return {
    "data" => "Hello World!",
  }.to_json
end
