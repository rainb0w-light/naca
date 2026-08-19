/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import jlib.log.Log;
import jlib.misc.DataFileRead;
import jlib.misc.DataFileWrite;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;   // IBM JVM



//import org.jdom.JDOMException;
//import org.jdom.input.SAXBuilder;
// SUN JVM import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;

/**
 * @author SLY
 *
 */
public class XMLUtil
{
    /** Loads the xml. */
    public static Document LoadXML(String csFilePath)
    {
        if (csFilePath==null || csFilePath.equals(""))
        {
            return null ;
        }
        File s = new File(csFilePath);
        return LoadXML(s) ;
    }

    /** Loads the xml. */
    public static Document LoadXML(File f)
    {
        Source file = new StreamSource(f) ;
        return LoadXML(file);
    }

    /** Loads the xml. */
    public static Document LoadXML(Source file)
    {
        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactoryImpl.newInstance() ;
            DocumentBuilder db = dbf.newDocumentBuilder() ;
            Document doc = db.newDocument() ;
            Result res = new DOMResult(doc) ;
            TransformerFactory tr = TransformerFactory.newInstance();
            Transformer xformer = tr.newTransformer();
            xformer.transform(file, res);

            return doc ;
        }
        catch (Exception e)
        {
            String csError = e.toString();
            Log.logImportant(csError);
            Log.logImportant("ERROR while loading XML "+file.toString());
        }
        return null;
    }

    /** Loads the xml. */
    public static Document loadXML(ByteArrayInputStream byteArrayInputStream)
    {
        try
        {
            StreamSource streamSource = new StreamSource(byteArrayInputStream);
            DocumentBuilderFactory dbf = DocumentBuilderFactoryImpl.newInstance() ;
            DocumentBuilder db = dbf.newDocumentBuilder() ;
            Document doc = db.newDocument() ;
            Result res = new DOMResult(doc) ;
            TransformerFactory tr = TransformerFactory.newInstance();
            Transformer xformer = tr.newTransformer();
            xformer.transform(streamSource, res);

            return doc ;
        }
        catch (Exception e)
        {
            String csError = e.toString();
            Log.logImportant(csError);
            Log.logImportant("ERROR while loading XML from byteArrayInputStream "+byteArrayInputStream.toString());
        }
        return null;
    }

//  Works but cost 25% more memory than other loadXML methods; seems to use SAX engine ?
//  public static Document loadXMLFromString(String cs)
//  {
//      try
//      {
//          StringReader stringReader = new StringReader(cs);
//          InputSource inputSource = new InputSource(stringReader);
//          Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource);
//          return doc;
//      }
//      catch (Exception e)
//      {
//          String csError = e.toString();
//          Log.logImportant(csError);
//          Log.logImportant("ERROR while loading XML data "+cs);
//      }
//      return null;
//  }



    /** Exports the xml. */
    public static boolean ExportXML(Document xmlOutput, String filename)
    {
        try
        {
            if (xmlOutput != null)
            {
                Source source = new DOMSource(xmlOutput);
                FileOutputStream file = new FileOutputStream(filename);
                StreamResult res = new StreamResult(file) ;
                Transformer xformer = TransformerFactory.newInstance().newTransformer();
                xformer.setOutputProperty(OutputKeys.ENCODING, "ISO8859-1");
                xformer.setOutputProperty(OutputKeys.INDENT, "yes");

                xformer.transform(source, res);
                file.close();

                return true ;
            }
            return false ;
        }
        catch (FileNotFoundException e)
        {
            return false ;
        }
        catch (TransformerConfigurationException e)
        {
            return false ;
        }
        catch (TransformerException e)
        {
            return false ;
        }
        catch (IOException e)
        {
            return false ;
        }
    }

    /** Creates the document. */
    public static Document CreateDocument()
    {
        try
        {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument() ;
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (FactoryConfigurationError e)
        {
            e.printStackTrace();
        }
        return null ;
    }

    /** Executes the get first element child operation. */
    public static Element GetFirstElementChild(Element tag)
    {
        Node n = tag.getFirstChild() ;
        while (n.getNodeType() != Node.ELEMENT_NODE)
        {
            n = n.getNextSibling() ;
        }
        Element e = (Element)n ;
        return e ;
    }

    /** Executes the encode64 operation. */
    public static byte[] encode64(byte[] arrBytes)
    {
        Base64 base64 = new Base64();
        return base64.encode(arrBytes);
    }

    /** Executes the decode64 operation. */
    public static byte[] decode64(byte[] arrBytes)
    {
        Base64 base64 = new Base64();
        return base64.decode(arrBytes);
    }

    /** Executes the encode64 as string operation. */
    public static String encode64AsString(byte[] arrBytes)
    {
        Base64 base64 = new Base64();
        byte[] tOut = base64.encode(arrBytes);
        String cs = new String(tOut);
        return cs;
    }

    /** Executes the decode64 operation. */
    public static byte[] decode64(String cs)
    {
        Base64 base64 = new Base64();
        byte[] bytes = cs.getBytes();
        return base64.decode(bytes);
    }

    /** Executes the decode64 as string operation. */
    public static String decode64AsString(String cs)
    {
        Base64 base64 = new Base64();
        byte[] bytes = cs.getBytes();
        byte[] tOut = base64.decode(bytes);
        String csOut = new String(tOut);
        return csOut;
    }

    /** Executes the encode64 as string operation. */
    public static String encode64AsString(String cs)
    {
        Base64 base64 = new Base64();
        byte[] bytes = cs.getBytes();
        byte[] tOut = base64.encode(bytes);
        String csOut = new String(tOut);
        return csOut;
    }

    /** Executes the encode64 file operation. */
    public static boolean encode64File(String csIn, String csOut)
    {
        DataFileRead in = new DataFileRead(csIn);
        boolean b = in.open(null);
        if(b)
        {
            byte[] tIn = in.readWholeFileAsArray();

            DataFileWrite out = new DataFileWrite(csOut, false);
            boolean isout = out.open();
            if(isout)
            {
                byte[] tOut = XMLUtil.encode64(tIn);
                out.write(tOut);
                out.close();
                return true;
            }
            in.close();
        }
        return false;
    }

    /** Executes the decode64 file operation. */
    public static boolean decode64File(String csIn, String csOut)
    {
        DataFileRead in = new DataFileRead(csIn);
        boolean b = in.open(null);
        if(b)
        {
            byte[] tIn = in.readWholeFileAsArray();

            DataFileWrite out = new DataFileWrite(csOut, false);
            boolean isout = out.open();
            if(isout)
            {
                byte[] tOut = XMLUtil.decode64(tIn);
                out.write(tOut);
                out.close();
                return true;
            }
            in.close();
        }
        return false;
    }
}
