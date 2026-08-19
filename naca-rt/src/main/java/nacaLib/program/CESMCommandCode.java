/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */

/**
 * @author sly
 *
 */

package nacaLib.program;

/** Provides cesmcommand code behavior. */
public class CESMCommandCode
{
    public static String LINK = "\u000E\u0002" ;
    public static String XCTL = "\u000E\u0004" ;
    public static String START = "" ;
    public static String RETURN = "\u000E\u0008" ;
    public static String INQUIRE = "\u0020\u0020" ; // ????
    public static String GET_ADDRESS ="\u0002\u0002" ;
    public static String HANDLE = "\u0002\u0004" ;
    public static String IGNORE = "\u0002\r" ;
    public static String READ_DATASET = "\u0006\u0002" ;
    public static String WRITE_DATASET = "\u0006\u0004" ;
    public static String ABEND = "\u000E\u000C" ;
    public static String ASKTIME = "\u0010\u0002" ;
    public static String RETRIEVE = "\u0010\r" ;
    public static String RECEIVE_MAP = "\u0018\u0002" ;
    public static String SEND_MAP = "\u0018\u0004" ;
    public static String SEND_TEXT = "\u0018\u0006" ;

    public static String READ_TEMPQUEUE = "" ;
    public static String WRITE_TEMPQUEUE = "" ;
    public static String DELETE_TEMPQUEUE = "" ;
//  public static String RETRIEVE =
//  public static String RETRIEVE =
}
