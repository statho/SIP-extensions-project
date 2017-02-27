CREATE TABLE IF NOT EXISTS users(
	username varchar(64) NOT NULL,
	password varchar(64) NOT NULL,
	policy varchar(64),
	creditCardNo varchar(64),
	charge float
);

CREATE TABLE IF NOT EXISTS blocking(
	blocker varchar(64), 
	blocked varchar(64)
);

CREATE TABLE IF NOT EXISTS billing(
	caller varchar(64),
	start_time BIGINT
);

CREATE TABLE IF NOT EXISTS forwarding(
	source varchar(64),
	target varchar(64)
);