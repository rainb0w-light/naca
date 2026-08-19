/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/**
 * @author U930CV
 *
 */


import utils.NacaTransLauncher;

/** Provides naca trans behavior. */
public class NacaTrans
{
//  protected class bmsfilter implements FilenameFilter
//  {
//      public boolean accept(File arg0, String arg1)
//      {
//          return arg1.endsWith(".bms");
//      }
//  }
//  protected class cblfilter implements FilenameFilter
//  {
//      public boolean accept(File arg0, String arg1)
//      {
//          return arg1.endsWith(".cbl");
//      }
//  }

    /** Executes the main operation. */
    public static void main(String[] args)
    {
        NacaTransLauncher.launchMain(args);
    }
}
