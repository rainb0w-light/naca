# JLib

*This page last changed on 16 May 2007 by u930di.*

This library has primarily been used as a base lib for NacaRT framework, but has been reused by other projects:

- Crawler
- AQC

## Content

Jlib contains various services:

- Logger. See package "log
- XML access helping classes. See package "xml"
- Database connection pooling, prepared statement pooling, ... See package "sql"
- JMX mbean registration. See package "jmxMBean".
- blowfish encryption. See package "blowfish".
- dynamic class loading. See package "classLoader.
- Garbage collection thread, helping to limit "out of memory exception"
- thread pools. See package "threads".
- timer. See package "threads".
- persitant queue. See package "persistantQueue".
- various helper classes (String, File access, LDap, NumberParser, ...): pachage "misc".

## Usage

To use jlib, your project must either have `jlib.jar` in it's classpath, or depends on jlib in eclispe project settings. 

The project can be obtains through Consultas's CVS repository , by checking out the jlib project.