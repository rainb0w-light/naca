# Online specific parameters (英文版 / English Version)

These parameters are used only for online programs.

# interface element configuration

String parameter giving the full file name of the xslt file that transforms the XML data into HTML file sent to the browser.

String parameter that gives the full file name of a xslt file used to produce the help screen

String parameter that gives the full directory path where screen resource files are stored. These are the *.res files that describes all screen graphical layout.

String that gives the full file name of the xml file that describes the HTML frame layout

String that gives the full path and file name for EmulWeb mode. This mode is not used in production environment, and is reserved for development process. It must be set as EmulWebRootPath=""

Number of seconds that a http session is holded within tomcat container. It can be setup with a large value, as a session doesn't use too much resources. The typical value is "10800" (3 hours).

Boolean value
Set this flag to "true" to enable caching of .res files. These files are then loaded and cached during server startup.
Resource file cache can be updated by clicking a button in the JMX console.
If it's set to false, then .res files are loaded at every user request. This is discouraged.

### configuration

nacaRT uses an initial LDAP login for users authentication.
There can ba at maximum 3 differents LDAP servers, which are queried simultaneously.

Identifies the DNS name of the first LDAP autentication server. Must be filled.

Identifies the DNS name of the second LDAP autentication server. Can be an empty, if only one authentication serveris used.

Identifies the DNS name of the third and last LDAP autentication server. Can be an empty, if only one or two authentication servers are used.

Root OU; example is DAPRootOU="OU=FUTUR_PUBLIGROUPE,DC=Publigroupe,DC=net"

Generic LDap user

Generic LDap password

# definition and translation support

These resources are defined in the tag of tag .

Gives the full path and file name of an XML file that contains mappings between transaction name and 1st program running this transaction.
The XML file structure is

```

	[] *

```

This file is then used by nacaRT to identify which program must be run when a transaction has been launched.

Gives the full path and file name of an XML file that contains multilingual texts used by the custom help system.

# launched programs

An optionnal list of program to launch asynchronously can be setup. 
These programs will be launched after the application server is started.
They are described by a list of tag within tag within tag , itself with tag .
Each program is run in it's own thread. The thread is created immediatly after server startup, but the program may wait a given number of second before being actually executed.

String value.
Gives the name of a program to launch asynchronously after server startup. A custom thread is allocated for each program. Only 1 instance of the program is created.

Integer value.
Number of seconds to wait after having created and launched the custom thread before actually executing the program's code.

# login and help

When a user first connect to the nacaRT, a login screen is displayed on it's browser. This screen is a custom one, as well as the help screen.
These elements are configured in tag of tag , itself within 

Gives the name of the first program to launch when a new http session is started by a new user.

Identifies how this program is executed. It must be set to "XCTL".

Gives the full class name of the help program.
