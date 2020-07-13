require 'pg'

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
    
    File.open("./db/sql/create_tables.sql", "r") do |file|
      @create_tables_sql = file.read
    end
    
    @conn = PG.connect(host, port, options, tty, dbname, user, password)
    @conn.exec @create_tables_sql
  end 
end