-- Таблица для управления состоянием диалога (stateful conversation)
CREATE TABLE chat_context
(
    id           BIGINT PRIMARY KEY NOT NULL, -- tg chat id
    step_code    VARCHAR(100),                -- current dialog state
    text         VARCHAR(100),                -- temporary text
    accept       BOOLEAN DEFAULT FALSE,       -- data from boolean buttons (yes/no)
    flow_context VARCHAR(50),                 -- transaction flow to choose next step after common step
    error_msg    VARCHAR(255),                -- error message
    error_step   VARCHAR(100)                 -- error step
);

-- add transaction
CREATE TABLE transaction_draft
(
    chat_id  BIGINT PRIMARY KEY REFERENCES chat_context (id) NOT NULL,
    type     VARCHAR(7),
    category VARCHAR(100), -- INCOME/EXPENSE category
    amount   DECIMAL(19, 2),
    date     TIMESTAMP,
    comment  VARCHAR(255)
);

-- search transaction
CREATE TABLE search_context
(
    chat_id  BIGINT PRIMARY KEY REFERENCES chat_context (id) NOT NULL, -- tg chat id
    trx_id   BIGINT,                                                   -- transaction display_id
    type     VARCHAR(7),                                               -- INCOME/EXPENSE
    category VARCHAR(100)                                               -- INCOME/EXPENSE category
);