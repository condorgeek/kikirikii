create table user (
  id varchar(255) not null,
  name varchar(50) not null,
  password varchar(50) not null,
  primary key (id)
);

create table space (
  id bigserial not null,
  user_id varchar(255) not null references user(id),
  title varchar(50) not null,
  description varchar(512) not null,
  primary key (id)
);

create table post (
  id bigserial not null,
  space_id bigint not null references space(id),
  description text not null,
  date timestamp without time zone not null,
  primary key (id)
);

create table comment (
  id bigserial not null,
  post_id bigint not null references post(id),
  description text not null,
  date timestamp without time zone not null,
  primary key (id)
);

create table like (
  id bigserial not null,
  user_id varchar(255) not null references user(id),
  post_id bigint not null references post(id)
  date timestamp without time zone not null,
  primary key (id)
);