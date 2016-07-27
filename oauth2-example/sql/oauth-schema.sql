drop table if exists oauth_client;
drop table if exists oauth_user;

create table oauth_user (
  id bigint auto_increment,
  username varchar(100),
  password varchar(100),
  salt varchar(100),
  constraint pk_oauth_user primary key(id)
) charset=utf8 ENGINE=InnoDB;
create unique index idx_oauth_user_username on oauth_user(username);

create table oauth_client (
  id bigint auto_increment,
  client_name varchar(100),
  client_id varchar(100),
  client_secret varchar(100),
  constraint pk_oauth_client primary key(id)
) charset=utf8 ENGINE=InnoDB;
create index idx_oauth_client_client_id on oauth_client(client_id);