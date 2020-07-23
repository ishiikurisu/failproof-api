require 'sinatra'
require 'rack/test'
require 'test/unit'
require 'pry'
require './main'

class MainRoutesTest < Test::Unit::TestCase
  include Rack::Test::Methods
  def app
    Sinatra::Application
  end

  def test_post_create_users
    # inexistent user
    $db.drop
    $db.setup

    data = {
      "username": "user",
      "password": "password",
      "notes": nil,
    }
    post '/users/create', data.to_json, "CONTENT_TYPE" => "application/json"
    assert last_response.ok?

    expected_auth_key = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiMSJ9.Ivr5KYifo8B-ecdNqaOJZOmHX2eG1h7akHViZzoSRBk"
    result_auth_key = JSON.parse(last_response.body)["auth_key"]
    assert expected_auth_key == result_auth_key

    # existing user
    post '/users/create', data.to_json, "CONTENT_TYPE" => "application/json"
    assert last_response.ok?

    expected_auth_key = nil
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

    expected_auth_key = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiMSJ9.Ivr5KYifo8B-ecdNqaOJZOmHX2eG1h7akHViZzoSRBk"
    result_auth_key = JSON.parse(last_response.body)["auth_key"]
    assert expected_auth_key == result_auth_key

    expected_notes = ""
    result_notes = JSON.parse(last_response.body)["notes"]
    assert expected_notes == result_notes
  end

  def test_create_user_with_notes
    # binding.pry for debugging purposes
    $db.drop
    $db.setup

    expected_notes = %Q(# My first checklist
This is what I need to do
- [x] This is a done item
- [ ] This item is still pending
)

    data = {
      "username": "user",
      "password": "password",
      "notes": expected_notes,
    }
    post '/users/create', data.to_json, "CONTENT_TYPE" => "application/json"
    assert last_response.ok?

    expected_auth_key = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiMSJ9.Ivr5KYifo8B-ecdNqaOJZOmHX2eG1h7akHViZzoSRBk"
    result_auth_key = JSON.parse(last_response.body)["auth_key"]
    assert expected_auth_key == result_auth_key

    post '/users/auth', data.to_json, "CONTENT_TYPE" => "application/json"
    assert last_response.ok?

    result_notes = JSON.parse(last_response.body)["notes"]
    assert expected_notes == result_notes
  end

  def test_hide_password
    password = "some random string"
    encrypted = $db.hide password
    assert password != encrypted
  end

  def test_get_checklists
    # user does not exist
    $db.drop
    $db.setup
    auth_key = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiMSJ9.Ivr5KYifo8B-ecdNqaOJZOmHX2eG1h7akHViZzoSRBk"
    get "/notes?auth_key=#{auth_key}"
    assert last_response.ok?
    result_notes = JSON.parse(last_response.body)["notes"]
    assert result_notes == nil

    # existing user
    test_create_user_with_notes
    expected_notes = %Q(# My first checklist
This is what I need to do
- [x] This is a done item
- [ ] This item is still pending
)
    get "/notes?auth_key=#{auth_key}"
    assert last_response.ok?
    result_notes = JSON.parse(last_response.body)["notes"]
    assert expected_notes == result_notes
  end

  def test_update_checklists
    # user does not exist
    $db.drop
    $db.setup
    # TODO include a test which this token's secret does not match ours
    auth_key = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiMSJ9.Ivr5KYifo8B-ecdNqaOJZOmHX2eG1h7akHViZzoSRBk"
    new_notes = %Q(# My first checklist
This is what I need to do
- [x] This is a done item
- [x] This item is still pending
)
    data = {
      "auth_key" => auth_key,
      "notes" => new_notes,
    }
    post '/notes', data.to_json, "CONTENT_TYPE" => "application/json"
    assert last_response.ok?
    result = JSON.parse(last_response.body)["error"]
    assert result != nil

    # existing user
    test_create_user_with_notes
    get "/notes?auth_key=#{auth_key}"
    assert last_response.ok?
    result_notes = JSON.parse(last_response.body)["notes"]
    old_notes = %Q(# My first checklist
This is what I need to do
- [x] This is a done item
- [ ] This item is still pending
)
    assert old_notes == result_notes
    
    post '/notes', data.to_json, "CONTENT_TYPE" => "application/json"
    result_notes = JSON.parse(last_response.body)["error"]
    result = JSON.parse(last_response.body)["error"]
    assert result == nil

    get "/notes?auth_key=#{auth_key}"
    assert last_response.ok?
    result_notes = JSON.parse(last_response.body)["notes"]
    assert new_notes == result_notes
  end
end
