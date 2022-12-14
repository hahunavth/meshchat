
PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;

-- Table: users
CREATE TABLE IF NOT EXISTS users (
	id INTEGER PRIMARY KEY, 
	first_name TEXT NOT NULL, 
	last_name TEXT NOT NULL, 
	user_name TEXT NOT NULL, 
	password TEXT NOT NULL, 
	phone_number TEXT NOT NULL,
	email TEXT NOT NULL
);

-- Table: chat
CREATE TABLE IF NOT EXISTS chats (
	id INTEGER PRIMARY KEY,
	member1 INTEGER NOT NULL REFERENCES users (id)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	member2 INTEGER NOT NULL REFERENCES users (id)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	UNIQUE (member1, member2)
);

-- Table: conversations
CREATE TABLE IF NOT EXISTS conversations (
	id INTEGER PRIMARY KEY,
	admin_id INTEGER NOT NULL REFERENCES users (id)
		ON UPDATE CASCADE
		ON DELETE CASCADE, 
	name TEXT NOT NULL
);

-- Table: members
CREATE TABLE IF NOT EXISTS members (
	user_id INTEGER NOT NULL REFERENCES users (id)
		ON UPDATE CASCADE
		ON DELETE CASCADE, 
	conv_id INTEGER NOT NULL REFERENCES conversations (id)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	PRIMARY KEY (user_id, conv_id)
);

-- Table: messages
CREATE TABLE IF NOT EXISTS messages (
	id INTEGER PRIMARY KEY,
	from_user_id INTEGER REFERENCES users (id) NOT NULL, 
	reply_to INTEGER NOT NULL REFERENCES messages (id)
		ON UPDATE CASCADE
		ON DELETE CASCADE, 
	content TEXT NOT NULL, 
	created_at TIMESTAMP NOT NULL, 
	chat_id INTEGER NOT NULL REFERENCES chats (id)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	conv_id INTEGER NOT NULL REFERENCES conversations (id)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	type INTEGER NOT NULL DEFAULT 0
);

COMMIT TRANSACTION;

PRAGMA foreign_keys = OFF;
