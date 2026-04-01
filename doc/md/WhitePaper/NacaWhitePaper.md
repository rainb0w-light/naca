# Publicitas SA Naca: Cobol transcoding software stack documentation

## 0. Introduction

### 0.1. Contacts

Please use the following contact information for details:  
Web site: <http://technology.publicitas.com>  
email: <naca-contact@publicitas.com>

### 0.2. Licence

Naca is currently made of 4 different projects:
- JLib, Publicitas Java base library, licenced under LGPL2 terms.
- NacaRT, Naca transcoded program base library, licenced under LGPL2 terms.
- NacaTrans, Cobol to Java transcoder, licenced under GPL2 terms.
- NacaRTTests, Test programs, licenced under GPL2 terms.

### 0.3. Concepts

Naca is a full set of tools / libraries that permit a complete migration for mainframe based central applications, to low costs server based infrastructure.

These servers can use any operating system supporting Java JRE 1.5 or greater. Linux or Windows are fully supported, with 32 or 64 bits versions. 64 bits supports enables a much wider memory space than 32 bits.

The challenges are important, as mainframe applications are critical for owner company daily production.

## 1. Document coverage

This document does not cover all aspects of the various steps required for a complete migration from a host/MVS world to a Java/Server based environment.

However, the following points are detailed here:

- Legacy Cobol application automatic conversion to Java
- Utilities and tools used during migration preparation
- Some utilities and tools used in the new environment, to ensure daily production

Thus, this document is mainly targeted to issues involved by the automatic transcoding, and their solutions, as provided by the Naca software stack.

## 2. Naca software stack

Naca provides various tools, libraries and executable. They are usable in all or some steps of the conversion process.

The list of these software resources includes:

- Automatic CICS Cobol to Java transcoder.
- Automatic Batch Cobol to Java transcoder.
- Automatic DB2 stored procedure Cobol to UDB Java stored procedure transcoder.
- Filepac scripts transcoder.
- Runtime Java library used by all transcoded programs, in supported category.
- External Sort utility.
- Database utilities.
- File transfer from host to server utility.
- File encoding/decoding utilities, including complete support for EBCDIC / ASCII conversions.
- Modified 3270 terminals emulation tools.

The list of required web servers includes:

- Standard source code repository ("CVS" for exemple).
