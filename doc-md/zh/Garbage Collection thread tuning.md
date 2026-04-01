# Garbage Collection thread tuning

These parameters are given in a tag within main tag.
NacaRT uses an internal thread that periodically do internal cache cleanup.

Must be set to "true" to activate this cleanup thread. If set to false, then the other paraneters are ignored.

Number of milliseconds, defining a period of wait time. When this period has elapsed, the thread is activated.
This value is typically set to 5 minutes (300000 milliseconds).

Defines a threshold in mega-bytes for the JVM permanent heap size. When the permanent heap size if higher than this value, then the cleanup processing is activated.
It size must be consistent with the JVM memory space allocated.

Defines the quantity of SQL prepared statements to force remove when the permanent heap size is too large. The prepared statements that were executed the longest time ago (and then are currently unused) are removed first. The sum of prepared statements removed if kept and is compared to the value of conf/GCThread/NbStatementsToRemoveBeforeGC. Only this quantity of prepared statements can be removed in one run of the GC thread
Removing this prepared statements happens only when the GC threads is activated. It's purpose it to lower the permanent heap size where prepared statements are holded, thus preventing a memory overflow. Ideally, prepared statement should never be removed in such a way. Thus, when memory is correctly tuned, this parameter must be set to "0".

The number of statements removed is cumulated. When it reaches NbStatementsToRemoveBeforeGC's value, then a garbage collector is forced.
This helps avoiding memory overflow.

Number of System.GC() call to do when garbage collector must be forced.
