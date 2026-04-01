# SQL Connections and connection pooling (英文版 / English Version)

SQL connections are pooled for performance reasons.
There can be a connection pool by program.

The DB connection parameters are described in tag , inside tag , itself inside tag .
The connection pools are described within a tag .
These application pools must not be confused with the accounting pool.

# Connection

String value.
Gives the jdbc url identifying the database holding the application tables.

String value.
Database user able to access application tables.

String value.
Password associated with the provided user. It's in encrypted hexadecimal form. A separate crypting utility can generate this password.

String value.
Key used to decrypt the crypted password. It must match the key given to the separate crypting utility that has generated the crypted password.

Optional parameter string appended to dburl. It is must be set to "currentPackageSet=NACA;keepDynamic=yes;clientProgramName=$FoundPoolName;"
The $ placeholders are used to identify the pool within DB2 administration console.

Database environment. This is a table prefix that must exists within database.

JDBC driver class name used to connect to the db. Set it to "com.ibm.db2.jcc.DB2Driver" for DB2 or UDB support.

Boolean value.
Set to true to close cursor when a commit is issued.

Boolean value.
Set to true to open connection in autocommit mode. Should be set to "false".

SQL clause that must always succeed, used to check the connection validity. Used to validate the DB connection after opening it.
Typically set to "select * from SYSIBM.SYSDUMMY1".

# pooling

Connection pooling parameters are described in tag of of , inside tag , itself inside tag .
These pools are defined as accounting connections pools, but are not mixed.

All nacaRT DB connections are pooled internally. This includes the accounting connections.
The pooling supports multiple pools. In the case of accounting, there is only one pool.

Gives the name of the db connection pool.
If the name is Generic, then this pool is used by all programs that are not defining their own pool. There must only one pool with name "Generic".
If the name is not "Generic", then this pool wil be reserved for the program whose is given in value "ProgramId".

Gives the name of the program that can use this connection pool.
If the pool's name is "Generic", then this value to "", as there is not program restriction to use this pool.

Integer value. Maximum number of connections that this pool can hold. 
When a transaction is launched, a connection is taken from the pool associated with the transaction's first program, or (if no specific definition found) from the generic pool.
The connection is returned to the pool when the transaction finishes. There can be no more than MaxConnection taken simultaneously from a pool. If all connections are obtained by running programs, then the next program requesting an unused connection has to wait until one is returned to the pool (that is it becoms free).

Time in milliseconds during which a connection can stay open without being used. Each time a connection is used by a running program, it's last usage time is resetted. When the elapsed time between now and the last usage time is higher than this quantiy of milliseconds, then the connection and all it's prepared statements are closed and removed, as the connection is considered obsolete. This is done by the GC thread.
This value must not be longer than the corresponding database parameter. In fact, the db engine can pro-activelly close an obsolete connection, and nacaRT won't be informed of this event. It's best to close the connection before the db engine.

Time in milliseconds during which a prepared statement can stay open without being used. If a statement is unused for too long, the DB engine may close it aggressivelly, and nacaRT has no way to be informed of that fact. Thus, it's best to pro-activelly close an unsued statement before the db engine. This cleanup is done by the GC Thread and when a connection is returned to it's pool.

Boolean value. Set to false.

# SQL code handling

Some SQL special code may indicate that the db connection is corrupted. This tga defins a list of such code. Getting one of these code generates an Abort excption which termintaes program and transacton execution. An email report is also generated.

Negaive Integer number ranging form -1 to -32767. Set the code very carefully, as the transaction is terminated when this code is received.
