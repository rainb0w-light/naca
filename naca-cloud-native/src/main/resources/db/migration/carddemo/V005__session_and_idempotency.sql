create table carddemo_runtime.conversation (
    conversation_id uuid primary key,
    transaction_id varchar(4) not null,
    program_name varchar(8) not null,
    commarea bytea not null default ''::bytea,
    terminal_state jsonb not null default '{}'::jsonb,
    version bigint not null default 0,
    expires_at timestamptz not null,
    updated_at timestamptz not null default current_timestamp
);

create index conversation_expiry_idx
    on carddemo_runtime.conversation (expires_at);

create table carddemo_runtime.idempotency_request (
    request_id uuid primary key,
    conversation_id uuid references carddemo_runtime.conversation(conversation_id),
    request_hash varchar(64) not null,
    response_status integer,
    response_body jsonb,
    created_at timestamptz not null default current_timestamp,
    unique (conversation_id, request_hash)
);
