-- Als Postgres User:
CREATE USER mctguser PASSWORD 'mctgpw';
CREATE DATABASE mctgdb with owner mctguser;
ALTER DATABASE mctgdb SET timezone to 'Europe/Vienna';
GRANT ALL ON DATABASE mctgdb TO mctguser;

create schema mctg;
alter schema mctg owner to mctguser;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA mctg TO mctguser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA mctg TO mctguser;
ALTER DEFAULT PRIVILEGES IN SCHEMA mctg GRANT ALL PRIVILEGES ON TABLES TO mctguser;
ALTER DEFAULT PRIVILEGES IN SCHEMA mctg GRANT ALL PRIVILEGES ON SEQUENCES TO mctguser;

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
    pk_user_id SERIAL,
    username   TEXT    NOT NULL,
    password   TEXT    NOT NULL,
    name        TEXT,
    bio        TEXT,
    image      TEXT,
    wallet     INTEGER NOT NULL DEFAULT 20,
    elo        INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (pk_user_id),
    UNIQUE (username)
);

CREATE TABLE "token"
(
    pk_token_id   SERIAL,
    token         TEXT        NOT NULL,
    expires       TIMESTAMPTZ NOT NULL,
    created       TIMESTAMPTZ NOT NULL,
    fk_pk_user_id SERIAL REFERENCES "user" (pk_user_id) ON DELETE CASCADE,
    PRIMARY KEY (pk_token_id)
);

CREATE TABLE "package"
(
    pk_package_id SERIAL,
    name          TEXT     NOT NULL,
    price         SMALLINT NOT NULL,
    PRIMARY KEY (pk_package_id)
);

CREATE TABLE "card"
(
    pk_card_id       SERIAL,
    name             TEXT     NOT NULL,
    damage           SMALLINT NOT NULL,
    card_type        cardTypes,
    element_type     elementTypes,
    is_active        BOOLEAN  NOT NULL DEFAULT FALSE,
    fk_pk_user_id    SERIAL REFERENCES "user" (pk_user_id) ON DELETE CASCADE,
    fk_pk_package_id SERIAL REFERENCES "package" (pk_package_id) ON DELETE CASCADE,
    PRIMARY KEY (pk_card_id)
);

CREATE TABLE "battlelog"
(
    pk_battlelog_id SERIAL,
    row_nr          BIGINT UNIQUE,
    log_text        TEXT NOT NULL,
    PRIMARY KEY (pk_battlelog_id),
    UNIQUE (pk_battlelog_id, row_nr)
);

CREATE TABLE "battle"
(
    pk_battle_id       SERIAL,
    time_start         TIMESTAMPTZ NOT NULL,
    time_end           TIMESTAMPTZ NOT NULL,
    rounds_nr          INTEGER,
    fk_pk_battlelog_id SERIAL REFERENCES "battlelog" (pk_battlelog_id) ON DELETE CASCADE,
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
    fk_pk_user_id   SERIAL REFERENCES "user" (pk_user_id) ON DELETE CASCADE,
    fk_pk_battle_id SERIAL REFERENCES "battle" (pk_battle_id) ON DELETE CASCADE,
    PRIMARY KEY (fk_pk_user_id, fk_pk_battle_id)
);

CREATE TYPE tradeStatus AS ENUM (
    'PENDING',
    'ACCEPTED'
    );

CREATE TABLE "trade"
(
    pk_trade_id           SERIAL      NOT NULL,
    fk_pk_initiator_id    SERIAL REFERENCES "user" (pk_user_id) ON DELETE CASCADE,
    fk_pk_tradepartner_id SERIAL REFERENCES "user" (pk_user_id) ON DELETE CASCADE,
    fk_pk_sendercard_id   SERIAL REFERENCES "card" (pk_card_id) ON DELETE CASCADE,
    fk_pk_receivercard_id SERIAL REFERENCES "card" (pk_card_id) ON DELETE CASCADE,
    status                tradeStatus NOT NULL,
    time_start            TIMESTAMPTZ NOT NULL,
    time_completed        TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (pk_trade_id)
);
