insert into carddemo_runtime.deployment_state(component, version)
values
    ('schema', '1'),
    ('carddemo-corpus', '59cc6c2fd7ebd7ef7925cad552a01a4b8b6e4d5e')
on conflict (component) do update
set version = excluded.version,
    installed_at = current_timestamp;
