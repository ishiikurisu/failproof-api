UPDATE users SET password='%{newpassword}' WHERE id='%{id}' AND password='%{oldpassword}' RETURNING *;
