-- drop tables
drop table if exists users;
drop table if exists todo;

-- create schema

create extension if not exists "pgcrypto";

create table users (
   user_id uuid primary key not null default gen_random_uuid(),
   username text unique not null,
   password text not null,
   email text unique not null,
   created_on timestamp not null default current_timestamp,
   last_login timestamp
);

create table todo (
   todo_id uuid primary key not null default gen_random_uuid(),
   todo_name text not null,
   todo_body text not null,
   user_id uuid not null references users(user_id) on delete cascade
);
