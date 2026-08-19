create table carddemo_vsam.file_manifest (
    file_name varchar(8) primary key,
    key_offset integer not null check (key_offset >= 0),
    key_length integer not null check (key_length > 0),
    record_length integer not null check (record_length > 0),
    encoding varchar(16) not null check (encoding in ('ASCII', 'EBCDIC'))
);

create table carddemo_vsam.record_store (
    file_name varchar(8) not null references carddemo_vsam.file_manifest(file_name),
    primary_key bytea not null,
    record_data bytea not null,
    record_length integer not null check (record_length = octet_length(record_data)),
    version bigint not null default 0,
    updated_at timestamptz not null default current_timestamp,
    primary key (file_name, primary_key)
);

create index record_store_browse_idx
    on carddemo_vsam.record_store (file_name, primary_key);
