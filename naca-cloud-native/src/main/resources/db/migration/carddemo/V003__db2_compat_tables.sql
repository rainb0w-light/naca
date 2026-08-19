create table carddemo.transaction_type (
    transaction_type_code varchar(4) primary key,
    description varchar(128) not null,
    active boolean not null default true
);

create view carddemo_compat.sysdummy1 as
select 'Y'::char(1) as ibmreqd;
