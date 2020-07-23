require 'rack'
require 'rack/cors'
require './main'

use Rack::Cors do
  allow do
    origins '*'
    resource '/*', headers: :any, methods: '*'
  end
end

run Sinatra::Application
