/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Creat/d on 15 oct. 2004
 *
 */

/**
 * @author sly
 *
 */
package nacaLib.varEx;

import jlib.misc.AsciiEbcdicConverter;
import nacaLib.tempCache.CStr;

/** Provides map redefine behavior. */
public class MapRedefine extends Var
{
    /** Creates a new map redefine instance. */
    public MapRedefine(DeclareTypeMapRedefine declareTypeMapRedefine)
    {
        super(declareTypeMapRedefine);
        formRedefineOrigin = declareTypeMapRedefine.formRedefineOrigin;
    }

    protected MapRedefine()
    {
        super();
    }

    protected VarBase allocCopy()
    {
        MapRedefine v = new MapRedefine();
        return v;
    }

    /* (non-Javadoc)
     * @see nacaLib.varEx.VarBase#getAsLoggableString()
     */
    protected String getAsLoggableString()
    {
        //return varDef.getRawStringIncludingHeader(bufferPos);
        CStr cstr = bufferPos.getOwnCStr(varDef.getLength());
        String cs = cstr.getAsString();
        //cstr.resetManagerCache();
        return cs;
    }

    /** Returns whether s type. */
    public boolean hasType(VarTypeEnum e)
    {
        return false;
    }

    /** Executes the encode to var operation. */
    public void encodeToVar(Var varDest)
    {
        varDef.varDefFormRedefineOrigin.encodeToVar(bufferPos, varDest);
    }

    /** Executes the decode from var operation. */
    public void decodeFromVar(Var varSource)
    {
        varDef.varDefFormRedefineOrigin.decodeFromVar(bufferPos, varSource);
    }

    /** Executes the encode to char buffer operation. */
    public InternalCharBuffer encodeToCharBuffer()
    {
        int nDestLength = varDef.getBodyLength() + varDef.getHeaderLength();
        VarDefForm varDefFormOrigin = varDef.varDefFormRedefineOrigin;
        return varDefFormOrigin.encodeToCharBuffer(nDestLength);
    }

    /** Executes the decode from char buffer operation. */
    public void decodeFromCharBuffer(InternalCharBuffer charBufferSource)
    {
        VarDefForm varDefFormOrigin = varDef.varDefFormRedefineOrigin;
        varDefFormOrigin.decodeFromCharBuffer(bufferPos, charBufferSource);
    }

    /** Returns the string including header. */
    public String getStringIncludingHeader()
    {
        CStr cstr = bufferPos.getOwnCStr(varDef.getLength());
        String cs = cstr.getAsString();
        //cstr.resetManagerCache();
        return cs;
        //return varDef.getRawStringIncludingHeader(bufferPos);
    }

    /** Executes the initialize operation. */
    public void initialize()
    {
        if(formRedefineOrigin != null)
        {
            InitializeCache initializeCache = getProgramManager().getOrCreateInitializeCache(getVarDef());
            formRedefineOrigin.initialize(initializeCache);
            // Was before optimizations: formRedefineOrigin.initialize();
        }
    }

    /** Executes the compare to operation. */
    public int compareTo(int nValue)
    {
        int nVarValue = getInt();
        return nVarValue - nValue;
    }


    /** Executes the compare to operation. */
    public int compareTo(double dValue)
    {
        double varValue = getDouble();
        double d = varValue - dValue;
        if (d < -0.00001) {    //Consider epsilon precision at 10 e-5
            return -1;
        } else if (d > 0.00001) {    //Consider epsilon precision at 10 e-5
            return 1;
        }
        return 0;
    }


    protected byte[] convertUnicodeToEbcdic(char[] tChars)
    {
        return AsciiEbcdicConverter.noConvertUnicodeToEbcdic(tChars);
    }

    protected char[] convertEbcdicToUnicode(byte[] tBytes)
    {
        return AsciiEbcdicConverter.noConvertEbcdicToUnicode(tBytes);
    }

    public VarType getVarType()
    {
        return VarType.VarMapRedefine;
    }

    Form formRedefineOrigin = null;
}
