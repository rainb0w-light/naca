# JLib Logger

The logger uses a XML configuration file to set the various supported output and their options.
There is also some java methods to call for generating log events.

## usage:

### operations

	- Create a configuration file as described below

	- Initialize the global log object, by specifying it's configuration file:
Log.open("Test", "D:/Dev/JLogWriterTest/src/net/publigroupe/config/Logging.ini", "RunId", "ProductId");

Comments:
The Field having value "Test" identifies the channel.
The field RunId identifies the current run; Its optional. if not provided, it will be replaced by a unique value generated at run time, BUT ONLY If USING a DBFLAT LogCenter.
The field ProductId identifies the products. It may be null if not required.

These 2 last fields are optionals, but can be used to have results' filtering after process execution.

	- Do all applications operations, including sending log events:

		Create a custom log class. You can use STModuleGen application to generate your custom log object

		- Send the log to all available log writers:
Example: LogEventEndCrawl.log(www.microsoft.com, nNbMilliseconds, "Crawling over");

The event will by dispatched synchronously to all enabled log writer. 
A log writer is enable if all the following conditions are met:

	- Its parameter "Enable" in configuration file is set to true, or if it's status has been dynamically set to true (to be done)

	- It's channel must matches log event channel. In previous sample, the channel is "Test". It is provided in the static log method, at the line Log.log(, event, Message);

	- It's flow must be either "any" or exactly match the flow indicated in the log event constructor.

	- It's level must be lower or equal than the level declared by log event constructor; the valid levels are given in descending order:

		Critical

		- Important

		- Normal

		- Verbose

		- Debug

		- FineDebug

At program end, close logger:

```
Log.close();
```

## Logger configuration file format settings

```

		%Timestamp (%ThreadName): %Message%CR"/>

```

```

		[]*

		[]
 [For Db Mode
				DbUser=String
				DbPassword=String
				DbUrl=String
				DbProvider="MySQL"|"Oracle"|"DB2"
				MasterTable=String
				DetailsTable= String]
 [For DB Flat mode
			 Mode="DbFlat" 
 		DbUser=String
			 DbPassword=String
			 DbUrl=String
			 DbProvider="MySQL"|"Oracle"|"DB2"
			 Table=String
			 TableRunId=String	
 Machine=String
 Process=String
 RunMode=String
 LogEventDefinitionTable=String
			/>
		]*

```

### explanations:

	- Settings/GetCallerLocation: True if tack call must be logged; false otherwise

	- Settings/CallLocation/Exclude: The value give the name of a java package that must be ignored form stack call. Multiple tags may be specified

	- LogCenters/ LogCenter: There may be multiple tags, each with it's own particularities

	- LogCenters/ LogCenter / Enable: Boolean that indicates whether the log channel is enable or not

	- LogCenters/ LogCenter / NbRequestBufferSize: Integer that defines how many requests will be grouped befors being flush on output writers

	- LogCenters/ LogCenter / Asynchronous: Boolean that defines if the writing operation is done asynchronously or not.

	- LogCenters/ LogCenter / Channel: Identifies the channel that can be used to send data to this log center. Only log event belonging to the correct channel can be sent by this log writer.

	- LogCenters/ LogCenter / Mode: Identifies 2 things:

	- The destination (Console, File or Database)

	- The formatting for file output
Value must belong to: "Console" |"FileST6"|"FileRawLine"|"FileSTCheck" |"FileChunk"|"Db"

	- LogCenters/ LogCenter / Level: Identifies the minimum level value acceptable for a log line to be written.
Value must belong to: "Critical" | "Important" | "Normal" | "Verbose" | "Debug" | "FineDebug"

	- LogCenters/ LogCenter / Flow: Identifes which flow can be catch by current LogCenter.
Value must belong to: "System" | "Monitoring" | "Trace" | "Any" | "None"
"Any" indicates that no restriction on flow is done
"None" desactivate the current LogCenter: it will never receive any log.

Specifics items depending on Mode

	- For Console Mode or RawLine Mode:

	- LogCenters/ LogCenter / Format: format is currently used only by mode="Console".
A formatting string can be specified; it can contains embedded variable placeholders:

	- %Message: Replace by the log message

	- %ThreadName: Replaced the thread name

	- %ThreadId: Relaced by the thread id

	- %Timestamp: Replaced by the time stamp at which log was called; it is different form current time

	- % StartTime: Number of milliseconds that elapsed since process startup. Determined at log call time

	- %CR: Carriage return char

	- %LF: Line feed char

	- For File Mode:

		LogCenters/ LogCenter / FilePath: String that give an absolute or relative path where logs files are stored; the path is created is necessary.

		- LogCenters/ LogCenter / FileName: String that gives the name of the log file; It is created as necessary

		- LogCenters/ LogCenter / FileStrategy: "Append" | "BackupOnstart"; Indicates what to do upon process startup with the current (if existing) log file.

		- LogCenters/ LogCenter / Backup / BackupPath: Path used if "BackupOnstart" Filestrategy is declared for current LogCenter. It is created as necessary

		- LogCenters/ LogCenter / Backup / BackupFileFormat: File name of the backup file within backup path. The name can contains the reserved keywords:

		- BackupDateTime: It is replaced at backup time by the current displayable date time in format YYYYMMDD_HHMMSS

		- LogCenters/ LogCenter / Backup / MaxBackupFileCount: Number of file to keep in backup directory. Only the most recent one are kept, old one are removed definitly.

	- For Database mode (Mode="Db")

		DbUser: String that gives the db connection user

		- DbPassword: String that gives the db connection password

		- DbUrl: String that gives the db connection url

		- DbProvider: Either "MySQL", "Oracle" or "DB2" depending on DB service used

		- MasterTable: Name of the table used as the log master. the table structure is described below

XML parameters:

	- DbUser must give the name of the DB user to use

	- DbPassword must give the password of the DB user to use

	- DbUrl must give the URL of the DB instance to use

	- DbProvider: one of "MySQL"|"Oracle"|"DB2" (currently, only "MySQL" is supported, other are to be done)

	- Table: Gives the name of the table used for log event write

	- TableRunId: Gives the name of the table used to generate a RuntimeId, and a RunId if none are provided at Log.open() time

	- Machine: Gives the name of the machine execution the process to be logged

	- Process: Give the name of the process to be logged

	- RunMode: Gives a string identifying the run mode (TLog / Prod, ...)

	- LogEventDefinitionTable: Gives the name of the table used for event definition registration

### log events and events type

There are various already built standard events. All these events must identify the channel they are using.

	- LogEventStartProcess: Typed LogEventType.Start; Automatically sent when the logger is started. Nothing to do at the application level.

	- LogEventStopProcess: Typed LogEventType.Stop; Must be sent when application is being shutdown.

	- LogEventAbortProcess: Typed LogEventType.Abort; Must be sent when application is being aborted

	- LogEventProgress: Typed LogEventType.Progress; Must be sent when an item processing is being progressing, and we want to get a trace of the advancements. 3 parameters are to be provided: int nNbItemsProcessed, int nNbItemsToProcess, String csMessage.
The message can be null.

	- LogEventReport: Typed LogEventType.Report; Must be sent when an item processing is finished. 3 parameters must be provided: int nNbItemsProcessed, int nNbItemsSucessfullyProcess, String csMessage; the message can be null.

	- LogEventLaunchProcess: Typed LogEventType.Launch; Must be sent before launching another process. 2 other parameters must be provided: , LaunchedRunId, and and optinalMessage. The LaunchedRunId must match the parameter that will be given in the launched process as a RunId in the Log.open() method. This enables log viewers to chain logically the 2 processes

	- LogEventAliveProcess: Typed LogEventType.Process; Must be sent to report that the process is alive

It is important to use these standard events as required, because they are intended in log viewer application for post execution analysis.

All these standards events provides 2 different log() methods, with or without a String csProduct argument. If no csProduct is provided, then the default csProduct value given at log.open() time is used, otherwise it's the specified value that is used..

### custom log object:

package net.publigroupe.logEvent;
import jlib.log.*;

public class LogEventEndCrawl extends LogEvent
{
	public LogEventEndCrawl(String csProduct)

	Unknown macro: {		super(LogEventType.Rem, LogFlow.Any, LogLevel.Normal, csProduct);	} 

public static LogEvent log(String csUrl, int nNbMs, String csMessage)
{
	return LogEventEndCrawl.log(null, csUrl, nNbMs, csMessage);
}

public static LogEvent log(String csProduct, String csUrl, int nNbMs, String csMessage)

		Unknown macro: {			LogEventEndCrawl event = new LogEventEndCrawl(csProduct);			event.fillMember("URL", csUrl);			event.fillMember("NbMs", nNbMs);			Log.log("Test", event, csMessage);			return event;		} 
}

Explanations: The constructor gives the event type, Flow restriction and minimal level that this log applies to.
The first static method log(...) is the log event call using the default product, specified at log.open() time.
The second static method log(...) is the log event call using a specific product, replacing the value specified at log.open() time.

## application

The sample uses the custom event : LogEventEndCrawl

Configuration file (D:/Dev/JLogWriterTest/src/net/publigroupe/config/Logging.ini), used to log on the Console and in DB in flat mode:

File Logging.java: Main file
import jlib.log.*;
public class Logging
{
	public static void main(String[] args)

	Unknown macro: {		Log.open("Test", "D} 
}

File SampleCallLog.java:
import net.publigroupe.logEvent.*;
import jlib.log.*;
import jlib.log.stdEvents.*;

public class SampleCallLog
{
	SampleCallLog()
	{	
		int nNbItemToProcess = 15;
		int nNbItemProcessed=0;
		int nNbItemSuccessfullyProcessed = 0;
		for(nNbItemProcessed=0; nNbItemProcessedUnknown macro: {			LogEventProgress.log("Test", nNbItemProcessed, nNbItemToProcess, "Processin in way ...");			if((nNbItemProcessed % 3) == 0)	// To simulate errors				nNbItemSuccessfullyProcessed++;		} 
LogEventReport.log("Test", nNbItemProcessed, nNbItemSuccessfullyProcessed, "Processing done");

// Lauching child process ...
		try

		Unknown macro: {			LogEventLaunchProcess.log("Test", "ChildRunId", "Lauching analyser process");			Process procChild = Runtime.getRuntime().exec("Anaylser");		} 
catch (IOException e)

		Unknown macro: {			// TODO Auto-generated catch block			e.printStackTrace();		} 		

LogEventEndCrawl.log("www.sun.com", 125, "");	// Custom log: No need to provide channel info; it's already specified
// in the custom log event LogEventEndCrawl.

// End
		LogEventStopProcess.log("Test");
	}
}

Initilialisation: In main file: 
Log.open("Test", "D:/Dev/JLogWriterTest/src/net/publigroupe/config/Logging.ini", "Run1", "Crawler");

	- Test is the channel; it must match items in ini configuation file.

	- D:/Dev/JLogWriterTest/src/net/publigroupe/config/Logging.ini: Is the path to the configuration file

	- Run1 identifies the run id; It is used to group chained processes

	- Crawler identifies the product.
This call will result in a LogEventStartProcess event to be sent.

If progress execution is to be logged, the following will be done:
Log.LogEventProgress.log("Test", nNbItemProcessed, nNbItemToProcess, "Process in way ...");

After processing of all items:
Log.LogEventProgress.log("Test", nNbItemProcessed, nNbItemSuccessfullyProcessed, "Process done");

To report the previously defined custom log event LogEventEndCrawl call:
LogEventEndCrawl.log(csDomainUrl, nElpasedTimeInMilliseconds, "Domain crawled");
There is no need to provide the channel id, as it is already specified in the LogEventEndCrawl log method.

Before launching a child process, we identifies the child process name:
LogEventLaunchProcess.log("Test", "ChildProcess", "Lauching analyser process");

In the child process, we will find:
Log.open("Test", "D:/Dev/JLogWriterTest/src/net/publigroupe/config/Logging.ini", "ChildRunId", "Crawler");
The field "RunId" has the value "ChildRunId"; this will enable the logviewer to connect these 2 processes.

## logger tools

Some tools are available to support jlib logger :

	- STModuleGen.exe is a wizard used to generate custom log event object. It's packaged as a MSI installable file on Windows. Its available on \\c930st3\SystoolsDeliveries\STTools\STModuleGen\Vxxx where vxxx identifies version.

	- STStudio.exe is a stand alone log viewer, specifically designed for displaying ST6 format. It is available on \\c930st3\SystoolsDeliveries\STTools\STStudio\vxxx where vxxx identifies version.

## creation SQL statement (for MySQL DB)

MySQL Syntax:

CREATE TABLE 'logheader' (
 'Id' int(10) unsigned NOT NULL auto_increment,
 'Ins_Date' timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
 'Log_Type' int(10) unsigned NOT NULL default '0',
 'File_Name' varchar(255) character set latin1 collate latin1_bin default NULL,
 'Line' int(10) unsigned NOT NULL default '0',
 'Thread' varchar(45) NOT NULL default '',
 'Method' varchar(255) NOT NULL default '',
 'StartTime' int(10) unsigned NOT NULL default '0',
 'Event_Name' varchar(255) NOT NULL default '',
 'Message' text NOT NULL,
 PRIMARY KEY ('Id')
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

?	Oracle syntax
CREATE TABLE LOGHEADER
(
 ID NUMBER NOT NULL,
 INS_DATE DATE DEFAULT sysdate NOT NULL,
 LOG_TYPE VARCHAR2(40 BYTE) NOT NULL,
 FILE_NAME VARCHAR2(255 BYTE) NOT NULL,
 LINE NUMBER NOT NULL,
 THREAD VARCHAR2(255 BYTE) NOT NULL,
 METHOD VARCHAR2(255 BYTE) NOT NULL,
 START_TIME NUMBER NOT NULL,
 EVENT_NAME VARCHAR2(255 BYTE) NOT NULL,
 MESSAGE VARCHAR2(4000 BYTE) NOT NULL
) ;

o	DetailsTable: Name of the table used as the log details
?	MySQL Syntax:
CREATE TABLE 'logdetails' (
 'Id' int(10) unsigned NOT NULL default '0',
 'DetailId' int(10) unsigned NOT NULL auto_increment,
 'Name' varchar(255) NOT NULL default '',
 'Value' text NOT NULL,
 PRIMARY KEY ('DetailId')
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
?	Oracle syntax:
CREATE TABLE LOGDETAILS
(
 ID NUMBER NOT NULL,
 DETAIL_ID NUMBER NOT NULL,
 NAME VARCHAR2(255 BYTE),
 VALUE VARCHAR2(4000 BYTE)
) ;
o	Sequences: Only for Oracle:
CREATE SEQUENCE TSTJAVA.SEQ_LOGDETAIL_ID
 START WITH 2
 MAXVALUE 999999999999999999999999999
 MINVALUE 0
 NOCYCLE
 NOCACHE
 NOORDER;

CREATE SEQUENCE TSTJAVA.SEQ_LOG_ID
 START WITH 10
 MAXVALUE 999999999999999999999999999
 MINVALUE 0
 NOCYCLE
 NOCACHE
 NOORDER;

?	For Flat Database mode (mode="DbFlat")

DROP TABLE IF EXISTS `tcopcwl`.`Log_Definitions`;
CREATE TABLE `Log_Definitions` (
 `Event_Name` varchar(255) NOT NULL default '',
 `Parameter_Name0` varchar(255) default NULL,
 `Parameter_Name1` varchar(255) default NULL,
 `Parameter_Name2` varchar(255) default NULL,
 `Parameter_Name3` varchar(255) default NULL,
 `Parameter_Name4` varchar(255) default NULL,
 `Parameter_Name5` varchar(255) default NULL,
 `Parameter_Name6` varchar(255) default NULL,
 `Parameter_Name7` varchar(255) default NULL,
 `Parameter_Name8` varchar(255) default NULL,
 `Parameter_Name9` varchar(255) default NULL,
 `Event_Id` int(11) default NULL,
 `Short_Event_Name` varchar(255) NOT NULL default ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tcopcwl`.`Log_Events`;
CREATE TABLE `tcopcwl`.`Log_Events` (
 `Machine` varchar(255) NOT NULL default '',
 `Process` varchar(255) NOT NULL default '',
 `Run_Mode` varchar(45) NOT NULL default '',
 `Ins_Date` datetime NOT NULL default '0000-00-00 00:00:00',
 `Event_Message` text NOT NULL,
 `Log_Type` varchar(45) NOT NULL default '',
 `File_Name` varchar(255) NOT NULL default '',
 `Line` int(10) unsigned NOT NULL default '0',
 `Thread` varchar(255) NOT NULL default '',
 `Method` varchar(255) NOT NULL default '',
 `Start_Time` int(10) unsigned NOT NULL default '0',
 `Parameter_Value0` varchar(255) default NULL,
 `Parameter_Value1` varchar(255) default NULL,
 `Parameter_Value2` varchar(255) default NULL,
 `Parameter_Value3` varchar(255) default NULL,
 `Parameter_Value4` varchar(255) default NULL,
 `Parameter_Value5` varchar(255) default NULL,
 `Parameter_Value6` varchar(255) default NULL,
 `Parameter_Value7` varchar(255) default NULL,
 `Parameter_Value8` varchar(255) default NULL,
 `Parameter_Value9` varchar(255) default NULL,
 `Message` text,
 `Event_Id` int(11) NOT NULL default '0',
 `Run_Id` varchar(45) default NULL,
 `Product` varchar(45) default NULL,
 `Runtime` varchar(45) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS 'tcopcwl'.'Log_RunId';
CREATE TABLE 'tcopcwl'.'Log_RunId' (
 'Channel' varchar(255) NOT NULL default '',
 'RunId' int(10) unsigned NOT NULL default '0',
 PRIMARY KEY ('Channel','RunId')
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

The column Event_Id is used to link an Event to it's definition. It's a hash code of the event name + number of arguments + names of parameters. Thus, it is sufficient to manage versioning.
