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
   
CREATE TYPE battleStatus AS ENUM (
    'WIN',
    'LOSS',
    'TIE'
    );
   
CREATE TYPE tradeStatus AS ENUM (
    'PENDING',
    'ACCEPTED'
    );


CREATE TABLE "user"
(
    pk_user_id SERIAL,
    username   TEXT    NOT NULL,
    password   TEXT    NOT NULL,
    name       TEXT,
    bio        TEXT,
    image      TEXT,
    wallet     INTEGER NOT NULL DEFAULT 20,
    elo        INTEGER NOT NULL DEFAULT 100,
    PRIMARY KEY (pk_user_id),
    UNIQUE (username)
);

CREATE TABLE "token"
(
    pk_token_id   SERIAL,
    token         TEXT        NOT NULL,
    expires       TIMESTAMPTZ NOT NULL,
    created       TIMESTAMPTZ NOT NULL,
    fk_pk_user_id INTEGER REFERENCES "user" (pk_user_id) ON DELETE CASCADE,
    PRIMARY KEY (pk_token_id)
);

CREATE TABLE "package"
(
    pk_package_id SERIAL,
    name          TEXT,
    price         SMALLINT NOT NULL DEFAULT 5,
    PRIMARY KEY (pk_package_id)
);

CREATE TABLE "card"
(
    pk_card_id       TEXT,
    name             TEXT     NOT NULL,
    damage           SMALLINT default 0,
    card_type        cardTypes,
    element_type     elementTypes,
    is_active        BOOLEAN  NOT NULL DEFAULT FALSE,
    fk_pk_user_id    INTEGER REFERENCES "user" (pk_user_id) ON DELETE CASCADE,
    fk_pk_package_id INTEGER REFERENCES "package" (pk_package_id) ON DELETE CASCADE,
    PRIMARY KEY (pk_card_id)
);

CREATE TABLE "battlelog"
(
    pk_battlelog_id SERIAL,
    row_nr          BIGINT,
    log_text        TEXT NOT NULL,
    PRIMARY KEY (pk_battlelog_id, row_nr)
);

CREATE TABLE "battle"
(
    pk_battle_id       SERIAL,
    time_start         TIMESTAMPTZ,
    time_end           TIMESTAMPTZ,
    rounds_nr          INTEGER,
    fk_pk_battlelog_id INTEGER,
    PRIMARY KEY (pk_battle_id)
);

CREATE TABLE "user_battle"
(
    status          battleStatus,
    fk_pk_user_id   INTEGER REFERENCES "user" (pk_user_id) ON DELETE CASCADE,
    fk_pk_battle_id INTEGER REFERENCES "battle" (pk_battle_id) ON DELETE CASCADE,
    PRIMARY KEY (fk_pk_user_id, fk_pk_battle_id)
);

CREATE TABLE "trade"
(
    pk_trade_id           TEXT NOT NULL,
    fk_pk_initiator_id    INTEGER REFERENCES "user" (pk_user_id) ON DELETE CASCADE,
    fk_pk_tradepartner_id INTEGER REFERENCES "user" (pk_user_id) ON DELETE CASCADE,
    fk_pk_sendercard_id   TEXT REFERENCES "card" (pk_card_id) ON DELETE CASCADE,
    fk_pk_receivercard_id TEXT REFERENCES "card" (pk_card_id) ON DELETE CASCADE,
    status                tradeStatus NOT NULL,
    time_start            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    time_completed        TIMESTAMPTZ,
    requested_type         cardTypes not null,
    requested_damage       INTEGER not null,
    PRIMARY KEY (pk_trade_id)
);
