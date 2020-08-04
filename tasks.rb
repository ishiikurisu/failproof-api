require 'json'
require 'net/http'

def post url, data
  uri = URI.parse url
  http = Net::HTTP.new uri.host, uri.port
  http.use_ssl = true
  http.verify_mode = OpenSSL::SSL::VERIFY_NONE
  request = Net::HTTP::Post.new url
  request.add_field 'Content-Type', 'application/json'
  request.body = data.to_json
  response = http.request request
  return response
end

def export_database admin_token, database_file
  response = post "https://fpcl.herokuapp.com/export", {'auth_key' =>  admin_token}
  payload = JSON.parse response.body
  if payload.has_key? 'database'
    database = payload['database']
    File.open(database_file, "w") do |file|
      database.each do |row|
        file.puts row.to_json
      end
    end
  end
end

def import_database admin_token, database_file
  database = []
  File.readlines(database_file).each do |line|
    database << JSON.parse(line.chomp)
  end
  response = post "https://fpcl.herokuapp.com/import", {
    'auth_key' =>  admin_token,
    'database' => database,
  }
  payload = JSON.parse response.body
  if payload.has_key? 'error' and payload['error'] != nil
    puts payload['error']
  end
end

if $0 == __FILE__
  admin_token = nil
  File.open('./.config/options.json') do |file|
    admin_token = JSON.parse(file.read)['admin_token']
  end
  case ARGV[0]
  when "e"  # export
    export_database admin_token, ARGV[1]
  when "i"  # import
    import_database admin_token, ARGV[1]
  end
end
