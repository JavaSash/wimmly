create table users
(
    id        int8 primary key not null,
    step_code varchar(100),
    text      varchar(100), -- any text
    accept    varchar(3) -- data from buttons
);