# CICS runtime capability matrix

This matrix records the M8 decision for every former TODO/fake family in
`BaseCESMManager`. “Fail closed” means new COBOL is rejected by
`DiagnosticSink` during semantic lowering; the retained runtime signature also
throws for previously generated Java.

| Capability | Decision | Verification |
| --- | --- | --- |
| `ASSIGN APPLID/TCTUALENG` | Implemented; values come from environment configuration and the TCTUA buffer size | runtime contract + full build |
| `ENQ` / `DEQ` | Implemented with JVM-wide fair, reentrant resource locks; unmatched `DEQ` is an error | `BaseCESMManagerRuntimeTest` |
| `HANDLE AID` / `UNHANDLE AID` | Implemented as normalized AID-to-target registrations | `BaseCESMManagerRuntimeTest` |
| TS and TD queue storage | Implemented as independent stores | `CESMQueueManagerTest`, legacy runtime suite |
| `SET TDQUEUE OPEN/CLOSED` | Implemented; closed TD queues reject access | `CESMQueueManagerTest` |
| local `DELETEQ TS/TD` | Implemented; missing queues set `QIDERR` | `CESMQueueManagerTest`, legacy runtime suite |
| `DELETEQ ... SYSID` | Fail closed; remote queue transport is not configured | `CICSRuntimeBackendDiagnosticTest` |
| `INQUIRE TRANSACTION + PROGRAM` | Implemented through local transaction-to-program resolution | `CICSInquireRenderTest` |
| partial/bare `INQUIRE` | Fail closed; the parsed form has no observable local result | `CICSRuntimeBackendDiagnosticTest` |
| `GETMAIN` | Fail closed; the parser does not capture a storage target | `CICSRuntimeBackendDiagnosticTest`, `BaseCESMManagerRuntimeTest` |
| `STARTBR`, `READ`, `READNEXT`, `READPREV` | Fail closed until an indexed-file backend is configured | `CICSRuntimeBackendDiagnosticTest` |
| `WRITE`, `REWRITE` for FILE/DATASET | Fail closed until an indexed-file backend is configured | `CICSRuntimeBackendDiagnosticTest`, `BaseCESMManagerRuntimeTest` |

`CCESMFakeMethodContainer` remains as a compatibility type name because it is
part of the checked codegen/runtime signature catalog. Its environment-backed
ASSIGN operations are no longer fake, and unreachable builder shapes are kept
only so historical generated Java can be compiled while migrating.
