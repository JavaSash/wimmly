-- Таблица для управления состоянием диалога (stateful conversation)
create table users
(
    id                int8 primary key not null,
    step_code         varchar(100), -- текущее состояние диалога
    text              varchar(100), -- временные данные ввода пользователя
    accept            boolean default false,   -- data from boolean buttons (yes/no)
    transaction_type  varchar(7),
    category          varchar(255),
    amount            numeric(19, 2),
    transaction_date  date,
    comment           varchar(255)
);