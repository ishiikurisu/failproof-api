require 'pg'
require 'jwt'

class Database
  attr_reader :create_tables_sql

  def initialize dboptions
    host = dboptions['host']
    port = dboptions['port']
    dbname = dboptions['dbname']
    user = dboptions['user']
    password = dboptions['password']
    tty = nil
    options = nil

    create_tables_sql = File.open("./db/sql/create_tables.sql", "r") do |file|
      @create_tables_sql = file.read
    end
    create_user_sql = File.open("./db/sql/create_user.sql", "r") do |file|
      @create_user_sql = file.read
    end

    @conn = PG.connect(host, port, options, tty, dbname, user, password)
    setup
  end

  def create_user username, password, notes
    create_user_sql = @create_user_sql % {
      :username => username,
      :password => password,
      :notes => (notes == nil)? "" : notes,
    }
    result = @conn.exec create_user_sql
    user_id = nil
    result.each_row do |row|
      user_id = row[0]
    end
    payload = {:user_id => user_id}
    JWT.encode payload, 'random_secret', 'HS256'
  end
  
  def drop
    @conn.exec "DROP TABLE users;"
  end
  
  def setup
    @conn.exec @create_tables_sql
  end
end
