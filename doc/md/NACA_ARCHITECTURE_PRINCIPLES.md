# Naca Project Design Goals and Architecture Principles

## Overview
Naca is a comprehensive COBOL-to-Java transpilation software stack designed to enable complete migration of mainframe-based central applications to cost-effective server-based infrastructure. The system preserves COBOL semantics while leveraging modern Java runtime capabilities.

## Core Design Goals

### 1. Mainframe Migration Enablement
- **Primary Objective**: Enable complete migration from IBM mainframe (MVS) environments to standard server infrastructure
- **Target Infrastructure**: Any OS supporting Java JRE 1.5+ (Linux/Windows, 32/64-bit)
- **Cost Reduction**: Replace expensive mainframe hardware with commodity servers
- **Production Continuity**: Maintain business-critical application functionality during migration

### 2. Developer Experience Preservation
- **COBOL Developer Friendly**: Allow experienced COBOL developers to maintain transcoded applications with minimal Java knowledge
- **Syntax Fidelity**: Generated "Cobol-like" Java syntax closely mirrors original COBOL structure
- **Non-OOP Focus**: Deliberately avoids object-oriented programming concepts to match COBOL paradigms
- **Familiar Semantics**: Preserves COBOL variable behavior, memory management, and program execution model

### 3. Technical Compatibility & Fidelity
- **Complete COBOL Feature Support**: Handles complex COBOL constructs including:
  - Variable redefinition (`REDEFINES`)
  - Occurs clauses with multiple dimensions
  - PIC X/9/S9 data types with various storage formats (COMP, COMP-3)
  - Buffer-level memory management
  - Hierarchical variable structures
- **Mainframe Emulation**: Provides utilities for EBCDIC/ASCII conversion, CICS terminal emulation, DB2 compatibility
- **Runtime Consistency**: Ensures identical program behavior between mainframe and Java environments

## Architecture Principles

### 1. Modular Component Architecture
The Naca stack consists of four distinct, licensed components:

#### JLib (LGPL2)
- **Role**: Base utility library providing generic services
- **Services**: Logging, XML handling, database connection pooling, JMX integration, encryption, thread pools, file utilities
- **Reusability**: Designed for use beyond Naca (used in Crawler, AQC projects)

#### NacaRT (LGPL2) 
- **Role**: Runtime framework implementing COBOL semantics in Java
- **Core Abstractions**: 
  - Variable definitions (shared across program instances)
  - Instance-specific buffers (isolated per program execution)
  - Hierarchical variable management
  - Memory-efficient storage representation
- **Execution Model**: Supports concurrent program instances with isolated state

#### NacaTrans (GPL2)
- **Role**: Automatic COBOL-to-Java transcompiler
- **Coverage**: CICS COBOL, Batch COBOL, DB2 stored procedures, Filepac scripts
- **Fidelity**: Generates human-readable, maintainable Java code preserving COBOL logic flow

#### NacaRTTests (GPL2)
- **Role**: Comprehensive test suite validating transpilation correctness
- **Purpose**: Ensure functional equivalence between original COBOL and transcoded Java

### 2. Memory Management Architecture
- **Dual-Layer Storage Model**:
  - **Variable Definitions**: Immutable metadata shared across all program instances (name, type, hierarchy, length)
  - **Buffers**: Mutable storage areas unique to each program instance (actual data values)
- **Efficient Redefinition Handling**: Multiple variables can share the same buffer positions through `REDEFINES` semantics
- **Occurs Optimization**: Intelligent buffer layout for multi-dimensional arrays with occurs clauses
- **Unicode Foundation**: Internal storage uses 16-bit Unicode chars (currently ASCII subset)

### 3. Runtime Isolation Principles
- **Instance Isolation**: Each program instance maintains completely separate variable buffers
- **Shared Metadata**: Variable definitions are shared to reduce memory overhead
- **Connection Pooling**: Database connections managed through configurable pools with automatic cleanup
- **Thread Safety**: Designed for concurrent execution in multi-threaded server environments

### 4. Configuration-Driven Flexibility
- **XML-Based Configuration**: Comprehensive runtime configuration through structured XML
- **Environment Adaptability**: Configurable database connections, accounting systems, performance parameters
- **Monitoring Integration**: Built-in JMX support for production monitoring and management
- **Accounting System**: Optional billing/statistics tracking with separate database connections

### 5. Enterprise Production Readiness
- **Robust Error Handling**: Comprehensive exception management and recovery mechanisms
- **Resource Management**: Automatic garbage collection thread, connection cleanup, statement lifecycle management
- **Performance Optimization**: Prepared statement caching, connection pooling, efficient buffer access patterns
- **Scalability**: Designed for high-concurrency server environments with configurable resource limits

## Performance Architecture

### Caching Strategy
NacaRT implements extensive caching at multiple levels to achieve performance comparable to mainframe execution:
- **Program and Program Instance Caching**: Reuses compiled program structures
- **Variable Caching**: Optimizes variable access patterns
- **SQL Caching**: Deep multi-level caching for database operations (>5 levels including connections, statements, resultset columns)
- **XSL Transform Caching**: Compiles screen I/O transforms once and reuses them

### Concurrency Model
- **Online Programs**: Naturally concurrent execution with each session running in its own private thread
- **Multi-core Utilization**: Leverages modern multi-core CPU architectures for improved throughput
- **Batch Programs**: Sequential execution model preserved for batch processing consistency

## Supported Migration Scenarios

### Application Types
- **CICS Applications**: Online transaction processing with screen handling
- **Batch Applications**: High-volume batch processing jobs  
- **DB2 Stored Procedures**: Database-resident COBOL procedures
- **File Processing**: Complex file I/O operations with sorting and formatting
- **Filepac Scripts**: Specialized scripting language support

### Infrastructure Components
- **Database Connectivity**: Full DB2/UDB compatibility with connection pooling
- **Terminal Emulation**: Modified 3270 terminal support for online applications
- **File Transfer**: Utilities for moving data between mainframe and server environments
- **Encoding Conversion**: Complete EBCDIC/ASCII bidirectional conversion support
- **Operation Management**: NacaTools web server for inventory, testing, and daily operations

## Agent-Understandable Key Concepts

For AI agents working with this codebase, understand these fundamental abstractions:

1. **Variable = Definition + Buffer**: Every variable has immutable metadata (definition) and mutable data (buffer)
2. **Program Instance Isolation**: Variables are never shared between program instances
3. **COBOL Semantics Preservation**: All operations must behave exactly as they would on a mainframe
4. **Memory Layout Matters**: Buffer positioning affects variable access and redefinition behavior  
5. **Configuration Drives Behavior**: Runtime characteristics are controlled by XML configuration, not hard-coded logic
6. **Generated Code ≠ Standard Java**: Transcoded programs use Cobol-like syntax and avoid OOP patterns
7. **NacaRT Provides Runtime Emulation**: The runtime library emulates complete COBOL semantics on JVM

This architecture enables faithful COBOL-to-Java translation while providing modern infrastructure benefits and maintaining developer productivity during migration transitions.