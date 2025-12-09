-- Таблица для управления состоянием диалога (stateful conversation)
create table users
(
    id                  int8 primary key not null,
    step_code           varchar(100), -- текущее состояние диалога
    text                varchar(100), -- временные данные ввода пользователя
    accept              varchar(3), -- data from buttons (yes/no etc)
    transaction_type    varchar(7)
);