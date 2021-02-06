-- users
truncate table users cascade;
insert into users ("username", "password", "email")
values ('mike', 12345, 'mike@mail.com'),
       ('jim', 789, 'jim@mail.com');
