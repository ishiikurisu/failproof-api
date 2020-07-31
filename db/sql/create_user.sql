INSERT INTO users(username,password,admin,notes)
SELECT '%{username}','%{password}','%{admin}','%{notes}'
WHERE NOT EXISTS (
  SELECT * FROM users WHERE username='%{username}')
RETURNING *;
