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

  def test_problematic_notes
    $db.drop
    $db.setup
    user_payload = $db.create_user "joe", "password", false, nil
    auth_key = user_payload['auth_key']
    expected_notes = %Q(# Inbox

- [ ] Clean Reddit's saved posts
)
    data = {
      "auth_key" => auth_key,
      "notes" => expected_notes,
    }
    post '/notes', data.to_json, "CONTENT_TYPE" => "application/json"
    assert last_response.ok?

    get "/notes?auth_key=#{auth_key}"
    assert last_response.ok?
    result_notes = JSON.parse(last_response.body)["notes"]
    assert expected_notes == result_notes
  end

  def test_export_database
    $db.drop
    $db.setup

    # populating database
    admin_user_payload = $db.create_user "joe", "password", true, nil
    admin_auth_key = admin_user_payload['auth_key']

    users = [
      {
        "username" => "mira",
        "password" => "123456",
        "notes" => %Q(# My first checklist
This is what I need to do
- [x] This is a done item
- [ ] This item is still pending
),
      }, {
        "username" => "pietro",
        "password" => "789101",
        "notes" => '# Famous people',
      }, {
        "username" => "flora",
        "password" => "andes song",
        "notes" => nil,
      },
    ]

    users.each do |u|
      $db.create_user u['username'], u['password'], false, u['notes']
    end

    # exporting database
    data = {
      "auth_key" => admin_auth_key,
    }
    post '/export', data.to_json, "CONTENT_TYPE" => "application/json"
    assert last_response.ok?

    # checking if exported data is correct
    expected = [
        ["id", "username", "password", "admin", "notes"],
        [1, "joe", $db.hide("password"), true, ""],
    ]
    users.each_index do |i|
      u = users[i]
      expected << [i + 2, u['username'], $db.hide(u['password']), false, u['notes'] || ""]
    end
    result = JSON.parse(last_response.body)["database"]
    assert expected == result
  end

  def test_import_database
    # preparing database
    $db.drop
    $db.setup
    admin_user_payload = $db.create_user "joe", "password", true, nil
    admin_auth_key = admin_user_payload['auth_key']

    # checking is database is correct before import
    database = [
        ["id", "username", "password", "admin", "notes"],
        [1, "joe", "981a32f2bd2b8d65b4f23c90be8a7bd99cfe8d61", true, ""],
    ]
    data = {
      "auth_key" => admin_auth_key,
    }
    post '/export', data.to_json, "CONTENT_TYPE" => "application/json"
    assert last_response.ok?
    assert database == JSON.parse(last_response.body)["database"]

    # importing database
    database = [
      ["id", "username", "password", "admin", "notes"],
      [1, "joe", "981a32f2bd2b8d65b4f23c90be8a7bd99cfe8d61", true, ""],
      [2, "mira", "4bc477c6ae1695066e3f26d8454af9a882839d9c", false, "# My first checklist\nThis is what I need to do\n- [x] This is a done item\n- [ ] This item is still pending\n"],
      [3, "pietro", "450624ea8c301011394c325a92652e888d0abfc6", false, "# Famous people"],
      [4, "flora", "e592ea6f709ab906042f1323d61920a928948803", false, ""],
    ]
    data = {
      "auth_key" => admin_auth_key,
      "database" => database,
    }
    post '/import', data.to_json, "CONTENT_TYPE" => "application/json"
    assert last_response.ok?
    assert JSON.parse(last_response.body)["error"] == nil

    # checking if imported database is correct
    data = {
      "auth_key" => admin_auth_key,
    }
    post '/export', data.to_json, "CONTENT_TYPE" => "application/json"
    assert last_response.ok?
    result = JSON.parse(last_response.body)["database"]
    assert database == result
  end

  def test_sync_notes
    $db.drop
    $db.setup

    old_notes = %Q(# My first checklist
This is what I need to do
- [x] This is a done item
- [ ] This item is still pending
)
    new_notes = %Q(# My first checklist
This is what I need to do
- [x] This is a done item
- [x] This item is still pending
)

    user_payload = $db.create_user "joe", "password", false, old_notes
    auth_key = user_payload["auth_key"]

    data = {
      "auth_key" => auth_key,
      "notes" => new_notes,
      "last_updated" => (Time.now + (5 * 60)).to_i,  # now + 5 minutes
    }
    post "/sync", data.to_json, "CONTENT_TYPE" => "application/json"
    assert last_response.ok?
    payload = JSON.parse(last_response.body)
    result_notes = payload["notes"]
    assert result_notes == new_notes
    expected_last_updated = payload["last_updated"]

    data = {
      "auth_key" => auth_key,
      "notes" => old_notes,
      "last_updated" => (Time.now - (5 * 60)).to_i,  # now - 5 minutes
    }
    post "/sync", data.to_json, "CONTENT_TYPE" => "application/json"
    assert last_response.ok?
    payload = JSON.parse(last_response.body)
    result_notes = payload["notes"]
    result_last_updated = payload["last_updated"]
    assert result_notes == new_notes
    assert expected_last_updated == result_last_updated
  end

  def test_encoding_decoding_notes
    notes = %Q(# My first checklist
This is what I need to do
- [x] This is a done item
- [x] This item is still pending
)
    encoded_notes = $db.encode_secret notes
    decoded_notes = $db.decode_secret encoded_notes
    assert notes == decoded_notes
  end
end
