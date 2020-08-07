require 'pg'
require 'jwt'
require 'digest'
require 'uri'

class Database
  attr_reader :create_tables_sql

  def initialize dboptions, sql_folder
    if dboptions.has_key? 'database_url'
      uri = URI.parse(ENV['DATABASE_URL'])
      @conn = PG.connect(uri.hostname, uri.port, nil, nil, uri.path[1..-1], uri.user, uri.password)
    else
      host = dboptions['host']
      port = dboptions['port']
      dbname = dboptions['dbname']
      user = dboptions['user']
      password = dboptions['password']
      tty = nil
      options = nil
      @conn = PG.connect(host, port, options, tty, dbname, user, password)
    end

    @sql = {}
    Dir["#{sql_folder}/*.sql"].each do |fn|
      File.open(fn) do |fp|
        @sql[fn.split("/")[-1].chomp(".sql")] = fp.read
      end
    end

    @salt = dboptions['salt']

    setup
  end

  def new_user username, password, admin, notes
    create_user_sql = @sql['create_user'] % {
      :username => username,
      :password => password,
      :notes => encode_secret((notes == nil)? "" : notes),
      :admin => (admin)? 'on' : 'off',
    }
    result = @conn.exec create_user_sql
    user_id = nil
    result.each_row do |row|
      user_id = row[0]
    end
    return {
      "auth_key"  => (user_id == nil)? nil : encode_secret({:user_id => user_id}),
    }
  end

  def create_user username, password, admin, notes
    new_user username, hide(password), admin, notes
  end

  def auth_user username, password
    auth_user_sql = @sql['auth_user'] % {
      :username => username,
      :password => hide(password),
    }

    user_id = nil
    notes = nil
    @conn.exec(auth_user_sql).each_row do |row|
      user_id = row[0]
      notes = decode_secret row[4]
    end

    return {
      "auth_key" => (user_id == nil)? nil : encode_secret({:user_id => user_id}),
      "notes" => notes,
    }
  end

  def get_notes auth_key
    user_data = decode_secret auth_key

    get_notes_sql = @sql['get_notes'] % {
      :id => user_data["user_id"],
    }
    notes = nil
    @conn.exec(get_notes_sql).each_row do |row|
      notes = decode_secret(row[4])
    end

    return {
      "notes" => notes,
    }
  end

  def update_notes auth_key, notes
    user_data = decode_secret auth_key

    update_notes_sql = @sql['update_notes'] % {
      :id => user_data["user_id"],
      :notes => encode_secret(notes),
    }
    oops = "It wasn't possible to perform this operation"
    @conn.exec(update_notes_sql).each_row do |row|
      oops = nil
    end

    return {
      "error" => oops,
    }
  end

  def sync auth_key, proposed_notes, last_updated_unix
    user_data = decode_secret auth_key
    get_notes_sql = @sql['get_notes'] % {
      :id => user_data["user_id"],
    }

    last_updated_on_db = nil
    stored_notes = nil
    @conn.exec(get_notes_sql).each_row do |row|
      stored_notes = decode_secret(row[4])
      last_updated_on_db = Time.parse(row[-1]).to_i
    end

    notes = stored_notes
    last_updated = last_updated_on_db
    last_updated_on_client = Time.at(last_updated_unix).to_i

    if last_updated_on_client > last_updated_on_db
      update_notes auth_key, proposed_notes
      notes = proposed_notes
      last_updated = Time.now.to_i
    end

    return {
      "notes" => notes,
      "last_updated" => last_updated,
    }
  end

  def drop
    @conn.exec "DROP TABLE users;"
  end

  def setup
    @conn.exec @sql['create_tables']
  end

  def is_user_admin user_id
    is_admin = false

    @conn.exec(@sql['get_notes'] % {
      :id => user_id,
    }).each_row do |row|
      is_admin = row[3] == 't'
    end

    return is_admin
  end

  def reset auth_key
    user_data = decode_secret(auth_key)
    oops = "user not authorized"

    if is_user_admin user_data['user_id']
      drop
      setup
      # XXX create admin user if possible
      oops = nil
    end

    return {
      "error" => oops,
    }
  end

  def export auth_key
    user_data = decode_secret auth_key
    outlet = nil

    if is_user_admin user_data['user_id']
       outlet = [["id", "username", "password", "admin", "notes"]]
       @conn.exec(@sql['export_users']).each_row do |row|
         outlet << [
           row[0].to_i,  # id
           row[1],  # username
           row[2],  # password
           row[3] == 't',  # admin
           decode_secret(row[4]),  # notes
          ]
      end
    end

    return {
      "database" => outlet
    }
  end

  def import auth_key, database
    user_data = decode_secret(auth_key)
    oops = nil

    if is_user_admin user_data['user_id']
       fields = database[0]
       rows = database[1...]
       rows.each do |row|
         new_user(*row[1...])
       end
    end

    return {
      "error" => oops
    }
  end

  def hide password
    outlet = password
    1000.times do
      outlet = Digest::RMD160.hexdigest(outlet + @salt)
    end
    return outlet
  end

  def encode_secret data
    JWT.encode(data, @salt, 'HS256')
  end

  def decode_secret secret
    JWT.decode(secret, @salt, 'HS256')[0]
  end
end
