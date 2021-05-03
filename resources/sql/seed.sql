-- users
truncate table users cascade;
insert into users ("username", "password", "email")
values ('lisa', 12345, 'lisa@mail.com'),
       ('jim', 78910, 'jim@mail.com');

-- todo
truncate table todo cascade;
insert into todo ("todo_name", "todo_body", "user_id")
values ('groceries', 'milk, eggs, bread', '655aefdc-2b7b-4254-b288-41a1c3a9a4b8'),
       ('call brother', 'call jon for his birthday', '655aefdc-2b7b-4254-b288-41a1c3a9a4b8'),
       ('get gas', 'get a car wash too', '655aefdc-2b7b-4254-b288-41a1c3a9a4b8');
