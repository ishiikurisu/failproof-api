require 'pg'
require 'jwt'
require 'digest'

class Database
  attr_reader :create_tables_sql

  def initialize dboptions, sql_folder
    host = dboptions['host']
    port = dboptions['port']
    dbname = dboptions['dbname']
    user = dboptions['user']
    password = dboptions['password']
    tty = nil
    options = nil

    @sql = {}
    Dir["#{sql_folder}/*.sql"].each do |fn|
      File.open(fn) do |fp|
        @sql[fn.split("/")[-1].chomp(".sql")] = fp.read
      end
    end
      
    @salt = dboptions['salt']

    @conn = PG.connect(host, port, options, tty, dbname, user, password)
    setup
  end

  def create_user username, password, notes
    create_user_sql = @sql['create_user'] % {
      :username => username,
      :password => hide(password),
      :notes => (notes == nil)? "" : notes,
    }
    result = @conn.exec create_user_sql
    user_id = nil
    result.each_row do |row|
      user_id = row[0]
    end
    return {
      "auth_key"  => (user_id == nil)? nil : encode_auth_key({:user_id => user_id}),
    }
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
      notes = row[4]
    end

    return {
      "auth_key" => (user_id == nil)? nil : encode_auth_key({:user_id => user_id}),
      "notes" => notes,
    }
  end

  def get_notes auth_key
    user_data = decode_auth_key auth_key

    get_notes_sql = @sql['get_notes'] % {
      :id => user_data[0]["user_id"],
    }
    notes = nil
    @conn.exec(get_notes_sql).each_row do |row|
      notes = row[4]
    end

    return {
      "notes" => notes,
    }
  end

  def update_notes auth_key, notes
    user_data = decode_auth_key auth_key

    update_notes_sql = @sql['update_notes'] % {
      :id => user_data[0]["user_id"],
      :notes => notes,
    }
    oops = "It wasn't possible to perform this operation"
    @conn.exec(update_notes_sql).each_row do |row|
      oops = nil
    end

    return {
      "error" => oops,
    }
  end

  def drop
    @conn.exec "DROP TABLE users;"
  end

  def setup
    @conn.exec @sql['create_tables']
  end

  def hide password
    outlet = password
    1000.times do
      outlet = Digest::RMD160.hexdigest(outlet + @salt)
    end
    return outlet
  end

  def encode_auth_key user_data
    JWT.encode user_data, @salt, 'HS256'
  end

  def decode_auth_key auth_key
    JWT.decode auth_key, @salt, 'HS256'
  end
end
