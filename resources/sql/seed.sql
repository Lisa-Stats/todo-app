-- users
truncate table users cascade;
insert into users ("username", "password", "email")
values ('mike', 12345, 'mike@mail.com'),
       ('jim', 78910, 'jim@mail.com');

-- todo
truncate table todo cascade;
insert into todo ("todo_name", "todo_body", "user_id")
values ('groceries', 'milk, eggs, bread', 'c5d6ec73-ffbd-4b44-9e26-3a5e55075893'),
       ('call brother', 'call jon for his birthday', '06512da8-43e0-489f-8ed9-9204ddc0a6b5'),
       ('get gas', 'get a car wash too', '06512da8-43e0-489f-8ed9-9204ddc0a6b5');
