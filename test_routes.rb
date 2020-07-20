require 'sinatra'
require 'rack/test'
require 'test/unit'
require './main'

class MainRoutesTest < Test::Unit::TestCase
  include Rack::Test::Methods
  def app
    Sinatra::Application
  end

  def test_post_create_users
    $db.drop
    $db.setup
    
    data = {
      "username": "user",
      "password": "password",
      "notes": nil,
    }
    post '/users/create', data.to_json, "CONTENT_TYPE" => "application/json"
    assert last_response.ok?
    
    expected_auth_key = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiMSJ9.OiHSk5Cl2k7cxj2yhsNuEHtjBdAc3PbJk3X6rl4ROZ0"
    result_auth_key = JSON.parse(last_response.body)["auth_key"]
    assert expected_auth_key == result_auth_key
  end
  
  def test_post_auth_users
    # inexistent user
    $db.drop
    $db.setup
    
    data = {
      "username": "user",
      "password": "password",
    }
    post '/users/auth', data.to_json, "CONTENT_TYPE" => "application/json"
    assert last_response.ok?
    
    expected_auth_key = nil
    result_auth_key = JSON.parse(last_response.body)["auth_key"]
    assert expected_auth_key == result_auth_key
    
    # existent user
    test_post_create_users
    
    post '/users/auth', data.to_json, "CONTENT_TYPE" => "application/json"
    assert last_response.ok?
    
    expected_auth_key = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiMSJ9.OiHSk5Cl2k7cxj2yhsNuEHtjBdAc3PbJk3X6rl4ROZ0"
    result_auth_key = JSON.parse(last_response.body)["auth_key"]
    assert expected_auth_key == result_auth_key
    
    expected_notes = ""
    result_notes = JSON.parse(last_response.body)["notes"]
    assert expected_notes == result_notes
  end
end
