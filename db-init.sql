-- Als Postgres User:
CREATE USER mctguser PASSWORD 'mctgpw';
CREATE DATABASE mctgdb with owner mctguser;
ALTER DATABASE mctgdb SET timezone to 'Europe/Vienna';
GRANT ALL ON DATABASE mctgdb TO mctguser;

create schema mctg;
alter schema mctg owner to mctguser;

------------------------------------------------------------
-- Als mctguser user:

SET search_path TO mctg;

CREATE TYPE cardTypes AS ENUM (
    'MONSTER',
    'SPELL'
    );

CREATE TYPE elementTypes AS ENUM (
    'FIRE',
    'WATER',
    'NORMAL'
    );


CREATE TABLE "user"
(
    pk_user_id BIGSERIAL,
    username   TEXT    NOT NULL,
    password   TEXT    NOT NULL,
    bio        TEXT,
    wallet     INTEGER NOT NULL DEFAULT 20,
    elo        INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (pk_user_id)
);

CREATE TABLE "token"
(
    pk_token_id   BIGSERIAL,
    token         TEXT        NOT NULL,
    expires       TIMESTAMPTZ NOT NULL,
    created       TIMESTAMPTZ NOT NULL,
    fk_pk_user_id BIGSERIAL REFERENCES "user" (pk_user_id) ON DELETE CASCADE,
    PRIMARY KEY (pk_token_id)
);

CREATE TABLE "package"
(
    pk_package_id BIGSERIAL,
    name          TEXT     NOT NULL,
    price         SMALLINT NOT NULL,
    PRIMARY KEY (pk_package_id)
);

CREATE TABLE "card"
(
    pk_card_id       BIGSERIAL,
    name             TEXT     NOT NULL,
    damage           SMALLINT NOT NULL,
    card_type        cardTypes,
    element_type     elementTypes,
    is_active        BOOLEAN  NOT NULL DEFAULT FALSE,
    fk_pk_user_id    BIGSERIAL REFERENCES "user" (pk_user_id) ON DELETE CASCADE,
    fk_pk_package_id BIGSERIAL REFERENCES "package" (pk_package_id) ON DELETE CASCADE,
    PRIMARY KEY (pk_card_id)
);

CREATE TABLE "battlelog"
(
    pk_battlelog_id BIGSERIAL,
    row_nr       BIGINT UNIQUE,
    log_text        TEXT NOT NULL,
    PRIMARY KEY (pk_battlelog_id, row_nr),
    UNIQUE (pk_battlelog_id, row_nr)
);

CREATE TABLE "battle"
(
    pk_battle_id       BIGSERIAL,
    time_start         TIMESTAMPTZ NOT NULL,
    time_end           TIMESTAMPTZ NOT NULL,
    rounds_nr          INTEGER,
    fk_pk_battlelog_id BIGSERIAL REFERENCES "battlelog" (pk_battlelog_id) ON DELETE CASCADE,
    log_text           TEXT        NOT NULL,
    PRIMARY KEY (pk_battle_id)
);

CREATE TYPE battleStatus AS ENUM (
    'WIN',
    'LOSS',
    'TIE'
    );

CREATE TABLE "user_battle"
(
    status          battleStatus NOT NULL,
    fk_pk_user_id   BIGSERIAL REFERENCES "user" (pk_user_id) ON DELETE CASCADE,
    fk_pk_battle_id BIGSERIAL REFERENCES "battle" (pk_battle_id) ON DELETE CASCADE,
    PRIMARY KEY (fk_pk_user_id, fk_pk_battle_id)
);

CREATE TYPE tradeStatus AS ENUM (
    'PENDING',
    'ACCEPTED'
    );

CREATE TABLE "trade"
(
    pk_trade_id        BIGSERIAL   NOT NULL,
    fk_pk_initiator_id BIGSERIAL REFERENCES "user" (pk_user_id) ON DELETE CASCADE,
    fk_pk_tradepartner_id BIGSERIAL REFERENCES "user" (pk_user_id) ON DELETE CASCADE,
    fk_pk_sendercard_id BIGSERIAL REFERENCES "card" (pk_card_id) ON DELETE CASCADE,
    fk_pk_receivercard_id BIGSERIAL REFERENCES "card" (pk_card_id) ON DELETE CASCADE,
    status tradeStatus NOT NULL,
    time_start         TIMESTAMPTZ NOT NULL,
    time_completed     TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (pk_trade_id)
);
