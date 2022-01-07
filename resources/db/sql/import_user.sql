INSERT INTO users(id,username,password,admin,notes,last_updated) 
SELECT '%{id}','%{username}','%{password}','%{admin}','%{notes}','%{last_updated}'
RETURNING *;
