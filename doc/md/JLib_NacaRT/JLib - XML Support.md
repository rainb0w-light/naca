# JLib - XML Support

*This page last changed on 16 May 2007 by u930di.*

## XML Tag manager

JLib supports easy XML parsing, by wrapping Xerces parser.

## XML Data read

XML Data can be easily read form an XML source.

Example :

```java
Tag tagRoot = Tag.createFromFile(m_csIniFilePath);
	if(tagRoot != null)
	{
		String csLogCfg = tagRoot.getVal("LogSettingsPathFile");
		Tag tagLogSettings = Log.open("NacaRT", csLogCfg);
		if (tagLogSettings != null)
		{
			Tag tagSettings = tagLogSettings.getChild("Settings");
			if(tagSettings != null)
			{
				isLogCESM = tagSettings.getValAsBoolean("CESM"); 
				isLogFlow = tagSettings.getValAsBoolean("Flow");
			}
		}
	}
```

Exemplanations:

```java
Tag tagRoot = Tag.createFromFile(m_csIniFilePath);
```
Open a XML file and fill a document.

```java
String csLogCfg = tagRoot.getVal("LogSettingsPathFile");
```
Read a parameter inside the current tag. The parameter name is provided.

```java
Tag tagSettings = tagLogSettings.getChild("Settings");
```
Get the first named child of a tag ; tagSettings will be null if no child of this name exists.

```java
isLogCESM = tagSettings.getValAsBoolean("CESM");
```
Get a named parameter's value as boolean.

You can enumerate all child of a tag :

```java
Tag tagLogCenter = tagLogCenters.getEnumChild("LogCenter");
while(tagLogCenter != null)
{
	String csChannel = tagLogCenter.getVal("Channel");
	// Do something with csChannel
	tagLogCenter = tagLogCenters.getEnumChild();
}
```

```java
Tag tagLogCenter = tagLogCenters.getEnumChild("LogCenter");
```
Returns the first child tag named LogCenters. An enumeration is read to be processed on next call.
It's value will be null when all identically named children tags will have been read.

```java
tagLogCenter = tagLogCenters.getEnumChild();
```
Get the next child tag with the same name.

A textual parameter value can be read by calling

```java
Tag.getNodeVal()
```

Example :

```xml
<Parent>
    <Node>
	SomeData
    </Node>
</Parent>
```

```java
Tag tagNode = tagParent.getChild(« Node ») ;
if(tagNode != null)
{
    String csData = tagNode.getNodeVal() ;	// csData will be filled with « SomeData »
    ...
}
```

Other APIs are available through Tag class. 

## XML Data write

A root tag is created by callling :

```java
Tag tagRoot = new Tag (« root ») ;
```

A child tag is appended by calling

```java
Tag tagLogCenters = tagOrganisation.addTag("LogCenters");
```
In this sample, tagLogCenters is a child of tagOrganisation. It is named « LogCenters ».

A value parameter is added to the current  tag by calling

```java
Tag.addVal(« Name », nValue) ;
```
The parameter types can be : String, int, long, boolean.

A value parameter can be removed by calling 

```java
tag.removeVal(« Parameter Name »);
```

A parameter existence can be checked by calling 

```java
isValExisting(Name) ;
```

A Tag can by serialized to a file by calling :

```java
tag.exportToFile(String filename) ;
```

It can also be set in a string by calling :

```java
String cs = tag.exportToString() ;
```