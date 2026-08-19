/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 20 ao�t 04
 *
 */
package parser.Cobol.elements.SQL;

/**
 * @author U930DI
 *
 */
public class CSQLTableColDescriptor
{
    /** Creates a new csqltable col descriptor instance. */
    public CSQLTableColDescriptor()
    {
    }

    /** Sets the name. */
    public void SetName(String csName)
    {
        this.csName = csName;
    }

    /** Executes the get name operation. */
    public String GetName()
    {
        return csName;
    }

    void SetLength(int n)
    {
        nLength = n;
        islengthSet = true;
    }

    void SetDecimal(int n)
    {
        nDecimal = n;
        isdecimalSet = true;
    }

    /** Executes the has size operation. */
    public boolean HasSize()
    {
        return islengthSet;
    }

    /** Executes the get sizes operation. */
    public String GetSizes()
    {
        if(islengthSet)
        {
            if (isdecimalSet) {
                return String.valueOf(nLength) + ", " + String.valueOf(nDecimal);
            }
            return String.valueOf(nLength);
        }
        return "";
    }

    void SetType(String csType)
    {
        this.csType = csType;
    }

    /** Executes the get type operation. */
    public String GetType()
    {
        return csType;
    }

    void SetNull(boolean b)
    {
        isnull = b;
    }

    /** Executes the is null operation. */
    public boolean IsNull()
    {
        return isnull;
    }

    private int nLength = 0;
    private int nDecimal = 0;
    private boolean isdecimalSet = false;
    private boolean islengthSet = false;
    private String csType = "";
    private String csName = "";
    private boolean isnull = false;

}
