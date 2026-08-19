create schema if not exists carddemo_runtime;
create schema if not exists carddemo_vsam;
create schema if not exists carddemo;
create schema if not exists carddemo_compat;

create table carddemo_runtime.deployment_state (
    component varchar(64) primary key,
    version varchar(128) not null,
    installed_at timestamptz not null default current_timestamp
);
