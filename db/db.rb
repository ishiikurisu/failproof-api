require 'pg'
require 'jwt'

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

    @conn = PG.connect(host, port, options, tty, dbname, user, password)
    setup
  end

  def create_user username, password, notes
    create_user_sql = @sql['create_user'] % {
      :username => username,
      :password => password,
      :notes => (notes == nil)? "" : notes,
    }
    result = @conn.exec create_user_sql
    user_id = nil
    result.each_row do |row|
      user_id = row[0]
    end
    auth_key = {:user_id => user_id}
    JWT.encode auth_key, 'random_secret', 'HS256'
  end
  
  def auth_user username, password
    auth_user_sql = @sql['auth_user'] % {
      :username => username,
      :password => password,
    }
    
    user_id = nil
    notes = nil
    @conn.exec(auth_user_sql).each_row do |row|
      user_id = row[0]
      notes = row[4]
    end
    
    auth_key = (user_id == nil)? nil : JWT.encode({:user_id => user_id}, 'random_secret', 'HS256')
      
    return {
      "auth_key" => auth_key,
      "notes" => notes,
    }
  end
  
  def drop
    @conn.exec "DROP TABLE users;"
  end
  
  def setup
    @conn.exec @sql['create_tables']
  end
end
