UPDATE users SET notes='%{notes}' WHERE id=%{id} RETURNING *;
