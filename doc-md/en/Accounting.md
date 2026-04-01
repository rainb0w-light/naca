# Accounting

nacaRT holds accounting informations for statics and billing purpose.
They are defined here in tag stored within tag .
The accounting system holds it's own DB connection that is not shared with the connection use dby application programs.
Thus, many parameters defines this DB connection.

String value. 
Physical machine name.

String value. 
Name of the tomcat instance.

String value. 
Name of the db table that stores accounting infos.

Integer value.
Billing can be set at different levels.
Set 0 for storing an account only when starting a new transaction.
Set 1 for storing an account when starting a new transaction, or starting a top level program.
Set 2 for storing an account when starting a new transaction, or starting a top level program, a sub program.
Set typically the parameter to "0".

String value.
Gives the jdbc url identifying the database holding the accounting table.

String value.
Database user able to insert record in the accounting table.

String value.
Database user password able to insert record in the accounting table.

Optional parameter string appended to dburl. It is must be set to "clientProgramName=$FoundPoolName;"

Database environment. This is the accounting table prefix.

JDBC driver class name used to connect to the db. Set it to "com.ibm.db2.jcc.DB2Driver" for DB2 or UDB support.

Boolean value.
DB Connection option that must be set to "true".

Boolean value.
DB Connection option that must be set to "true".

SQL clause that must always succeed, used to check the connection validity. Used to validate the DB connection after opening it.

### pooling

All nacaRT DB connections are pooled internally. This includes the accounting connections.
The pooling supports multiple pools. In the case of accounting, there is only one pool.

Gives the name of the db connection pool used for accounting.
Must be set to "Accounting".

Gives the name of the program that can use this connection pool.
Must be set to "Accounting".

Integer value. Maximum number of connections that this pool can hold. When an accounting record must be inserted, a connection is obtained from the pool, and is SQL statement is issued. Then, the connection returns to the pool. If at a certain moment all connection are used, then the next program requesting an accounting insertion must wait until one of the db connectio nis released.

Time in milliseconds during which a connection can stay open without being used. When the connection is used, it's last usage time is resetted. When the last usage time is higher than this quantiy of milliseconds, then the connection and it's prepared statements are closed and removed. This is done by the GC thread.
This value must not be longer than the corresponding database parameter. In fact, the db engine can pro-activelly close an obsolete connection, and nacaRT won't be informed of this event. It's best to close the connection before the db engine.

Time in milliseconds during which a prepared statement can stay open without being used. If a statement is unused for too long, the DB engine may close it aggressivelly, and nacaRT has no way to be informed of that fact. Thus, it's best to close an unsued statement before the db engine own timout value. This cleanup is done by the GC Thread and when a connection is returned to it's pool.

Boolean value. Set to false.
