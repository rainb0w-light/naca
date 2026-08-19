/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import jlib.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import jlib.log.AssertException;
import nacaLib.mapSupport.MapFieldAttrColor;
import nacaLib.mapSupport.MapFieldAttrFill;
import nacaLib.mapSupport.MapFieldAttrHighlighting;
import nacaLib.mapSupport.MapFieldAttrIntensity;
import nacaLib.mapSupport.MapFieldAttrJustify;
import nacaLib.mapSupport.MapFieldAttrModified;
import nacaLib.mapSupport.MapFieldAttrProtection;
import nacaLib.mapSupport.MapFieldAttribute;
import nacaLib.mathSupport.MathBase;
import nacaLib.misc.StringAsciiEbcdicUtil;
import nacaLib.tempCache.CStr;



/**
 * @author U930DI
 *
 */
public abstract class Edit extends VarAndEdit
{
    Edit(DeclareTypeBase declareTypeBase)
    {
        super(declareTypeBase);
    }

    protected Edit()
    {
        super();
    }

    boolean isEdit()
    {
        return true;
    }

    public int getAbsolutePosition()
    {
        return bufferPos.nAbsolutePosition; // varDef.nAbsolutePosition;
    }

    /** Executes the debugget absolute position operation. */
    public int DEBUGgetAbsolutePosition()
    {
        return bufferPos.nAbsolutePosition; // varDef.nAbsolutePosition;
    }

    /** Executes the assign buffer ext operation. */
    public void assignBufferExt(VarBuffer bufferSource)
    {
        bufferPos = new VarBufferPos(bufferSource, varDef.nDefaultAbsolutePosition);
        getEditAttributManager();   // Must be called for EditInMapRedefine
    }


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
        if (e == VarTypeEnum.TypeFieldEdit) {
            return true;
        }
        return false;
    }

    /** Returns the string. */
    public String getString()
    {
        CStr cstr = varDef.getAsDecodedString(bufferPos);
        String cs = cstr.getAsString();
        //cstr.resetManagerCache();
        return cs;
    }

    /** Sets the and fill. */
    public void setAndFill(String csValue)
    {
        if (csValue.length() > 0) {
            varDef.writeAndFill(bufferPos, csValue.charAt(0));
        }
    }

    /** Returns the dotted signed string. */
    public String getDottedSignedString()
    {
        String cs = varDef.getDottedSignedString(bufferPos).getAsString();
        return cs;
    }

    /** Returns the dotted signed string as sqlcol. */
    public String getDottedSignedStringAsSQLCol()
    {
        String cs = varDef.getDottedSignedStringAsSQLCol(bufferPos).getAsString();
        return cs;
    }

    public double getDouble()
    {
        return varDef.getDouble(bufferPos);
    }

    public int getInt()
    {
        return varDef.getAsDecodedInt(bufferPos);
    }

    public Dec getDec()
    {
        return varDef.getAsDecodedDec(bufferPos);
    }

    /** Returns the edit at. */
    public Edit getEditAt(Var x)
    {
        return getAt(x);
    };
    /** Returns the edit at. */
    public Edit getEditAt(MathBase x)
    {
        return getAt(x);
    };
    /** Returns the edit at. */
    public Edit getEditAt(Var x, Var y)
    {
        return getAt(x, y);
    };
    /** Returns the edit at. */
    public Edit getEditAt(Var x, int y)
    {
        return getAt(x, y);
    };
    /** Returns the edit at. */
    public Edit getEditAt(int x, Var y)
    {
        return getAt(x, y);
    };
    /** Returns the edit at. */
    public Edit getEditAt(int x, int y)
    {
        return getAt(x, y);
    };
    /** Returns the at. */
    public abstract Edit getAt(Var x);
    /** Returns the edit at. */
    public Edit getEditAt(int x)
    {
        return getAt(x);
    };
    /** Returns the at. */
    public abstract Edit getAt(int x);
    /** Returns the at. */
    public Edit getAt(MathBase index)
    {
        int nIndex = index.d.intValue() ;
        return getAt(nIndex) ;
    }
    /** Returns the at. */
    public abstract Edit getAt(int y, int x);
    /** Returns the at. */
    public Edit getAt(Var vy, Var vx)
    {
        int y = vy.getInt() ;
        int x = vx.getInt() ;
        return getAt(y, x) ;
    }
    /** Returns the at. */
    public Edit getAt(Var vy, Var vx, Var vz)
    {
        int z = vz.getInt() ;
        int y = vy.getInt() ;
        int x = vx.getInt() ;
        return getAt(y, x, z) ;
    }
    /** Returns the at. */
    public Edit getAt(Var vy, int x)
    {
        int y = vy.getInt() ;
        return getAt(y, x) ;
    }
    /** Returns the at. */
    public Edit getAt(int x, Var vy)
    {
        int y = vy.getInt() ;
        return getAt(x, y) ;
    }
    /** Returns the at. */
    public abstract Edit getAt(int z, int y, int x);

    /** Executes the transfer to operation. */
    public abstract void transferTo(Var varDest);

    /** Executes the set operation. */
    public abstract void set(Var varSource);
    /** Executes the set operation. */
    public abstract void set(Edit varSource);

    /** Returns whether edit in map. */
    public abstract boolean isEditInMap();

    /** Executes the transfer to operation. */
    public abstract void transferTo(Edit varDest);
//  public abstract void transferTo(EditInMap varDest);
//  public abstract void transferTo(EditInMapRedefine varDest);



/////////////////////



/*
    public String toString()
    {
        String cs = csFullName + "[" + varManager.getValueAbsoluteStartPosition() + "-" + getStorageSize() + "]:" +
            "\"" + getString() + "\";" +
            fieldAttributes.toString();

        return cs;
    }
*/

    protected void fillWithValue(Element eField)
    {
        if (eField != null)
        {
            String val = eField.getAttribute("value");
            String upd = eField.getAttribute("updated") ;
            if (upd.equalsIgnoreCase("true"))
            {
                if (val.trim().equals(""))
                {
                    set("") ;
                    attrManager.setCleared() ;
                }
                else
                {
                    set(val, true);
                    attrManager.setModified() ;
                }
            }
            else
            {
                attrManager.setUnmodified() ;
            }
//          else if (!val.equals(""))
//          {
//              set(val) ;
//          }
        }
    }



    /** Executes the attrib operation. */
    public Edit attrib(MapFieldAttrModified modified)
    {
        attrManager.attrib(modified);
        return this;
    }

    /** Sets the modified. */
    public Edit setModified(MapFieldAttrModified modified)
    {
        attrManager.setModified(modified);
        return this;
    }

    /** Executes the color operation. */
    public Edit color(MapFieldAttrColor color)
    {
        attrManager.color(color);
        return this;
    }

    /** Returns whether colored. */
    public boolean isColored(MapFieldAttrColor color)
    {
        return attrManager.isColored(color);
    }


    public MapFieldAttrHighlighting getHighlighting()
    {
        return attrManager.getHighlighting();
    }

    /** Executes the high lighting operation. */
    public Edit highLighting(MapFieldAttrHighlighting hl)
    {
        attrManager.highLighting(hl);
        return this;
    }

    /** Executes the intensity operation. */
    public Edit intensity(MapFieldAttrIntensity intensity)
    {
        attrManager.intensity(intensity);
        return this;
    }


    /** Executes the protection operation. */
    public Edit protection(MapFieldAttrProtection protection)
    {
        attrManager.protection(protection);
        return this;
    }

    /** Sets the modified. */
    public Edit setModified()
    {
        attrManager.setModified();
        return this;
    }

    /** Sets the unmodified. */
    public void setUnmodified()
    {
        attrManager.setUnmodified();
    }

    /** Sets the cleared. */
    public void setCleared()
    {
        attrManager.setCleared();
    }

    public boolean isModified()
    {
        return attrManager.isModified();
    }

    public boolean isUnmodified()
    {
        return attrManager.isUnmodified();
    }

    public boolean isCleared()
    {
        return attrManager.isCleared();
    }

    /** Executes the justify operation. */
    public Edit justify(MapFieldAttrJustify justify)
    {
        attrManager.justify(justify);
        return this;
    }

    /** Executes the justify fill operation. */
    public Edit justifyFill(MapFieldAttrFill fill)
    {
        attrManager.justifyFill(fill);
        return this;
    }

    /** Executes the set operation. */
    public void set(String csValue)
    {
        varDef.write(bufferPos, csValue);
    }

    protected void set(String csValue, boolean bWithPadding)
    {
        if(csValue != null && csValue.trim().length() != 0) // Not an empty value
        {
            if (bWithPadding)
            {
                String csChar = "";
                if (attrManager.isFillBlank()) {
                    csChar = " ";
                }
                if (attrManager.isFillZero()) {
                    csChar = "0";
                }

                String csFilling = new String();

                int nLength = getLength();
                int nNbChars = nLength - csValue.length();
                while(nNbChars > 0)
                {
                    csFilling += csChar;
                    nNbChars--;
                }

                if(attrManager.isJustifyRight())
                {
                    csValue = csFilling + csValue;
                }

                if(attrManager.isJustifyLeft())
                {
                    csValue += csFilling;
                }
            }
            varDef.write(bufferPos, csValue);
        }
//
//
//          varDef.write(bufferPos, csValue);
//          if(varTypeFormat != null)
//          {
//              varTypeFormat.set(this, sValue);
//          }
//          else
//          {
//              if(sValue.length() > nMaxStringLength)
//                  sValue = sValue.substring(0, nMaxStringLength); // Keep only leftmost chars
//              int nPosition = varManager.getValueAbsoluteStartPosition()+7;
//
//              if (bWithPadding)
//              {
//                  if(fieldAttributes.isJustifyRight())
//                  {
//                      int nNbLeftPadChar = nMaxStringLength - sValue.length();
//                      if(nNbLeftPadChar > 0)
//                      {
//                          if(fieldAttributes.isFillBlank())
//                              nPosition = varManager.writeRepeatingCharAt(nPosition, ' ', nNbLeftPadChar);
//                          else if(fieldAttributes.isFillZero())
//                              nPosition = varManager.writeRepeatingCharAt(nPosition, '0', nNbLeftPadChar);
//                      }
//                  }
//              }
//
//              nPosition = varManager.setStringAt(nPosition, sValue);
//
//              if (bWithPadding)
//              {
//                  if(fieldAttributes.isJustifyLeft())
//                  {
//                      int nNbRightPadChar = nMaxStringLength - sValue.length();
//                      if(nNbRightPadChar > 0)
//                      {
//                          if(fieldAttributes.isFillBlank())
//                              varManager.writeRepeatingCharAt(nPosition, ' ', nNbRightPadChar);
//                          else if(fieldAttributes.isFillZero())
//                              varManager.writeRepeatingCharAt(nPosition, '0', nNbRightPadChar);
//                      }
//                  }
//              }
//          }
//      }
    }

    /** Sets the cursor. */
    public Edit setCursor(boolean b)
    {
        attrManager.setCursor(b);
        return this;
    }


    /** Sets the flag. */
    public void setFlag(String cs)
    {
        attrManager.setFlag(cs);
    }


    /** Resets the flag. */
    public void resetFlag()
    {
        attrManager.resetFlag();
    }
    /** Returns whether flag. */
    public boolean isFlag(String cs)
    {
        return attrManager.isFlag(cs);
    }

    public boolean isAutoSkip()
    {
        return attrManager.isAutoSkip();
    }

    public boolean isDark()
    {
        return attrManager.isDark();
    }

    public boolean isProtected()
    {
        return attrManager.isProtected();
    }

    public boolean isNumericProtected()
    {
        return attrManager.isNumericProtected();
    }

    public boolean isUnprotected()
    {
        return attrManager.isUnmodified();
    }

    /** Executes the is colored operation. */
    public boolean IsColored(MapFieldAttrColor col)
    {
        return attrManager.isColored(col);
    }

    public boolean isUnderlined()
    {
        return attrManager.isUnderlined();
    }

    public boolean isReverse()
    {
        return attrManager.isReverse();
    }

    /** Executes the is attribute operation. */
    public boolean IsAttribute(MapFieldAttrIntensity intensity)
    {
        return attrManager.IsAttribute(intensity);
    }

    /** Executes the is attribute operation. */
    public boolean IsAttribute(MapFieldAttrProtection protection)
    {
        return attrManager.IsAttribute(protection);
    }

    /** Executes the is highlighting operation. */
    public boolean IsHighlighting(MapFieldAttrHighlighting highlighting)
    {
        return attrManager.IsHighlighting(highlighting);
    }


    public MapFieldAttribute getAttribute()
    {
        return attrManager.getAttribute();
    }

    /** Sets the attribute. */
    public void setAttribute(MapFieldAttribute att)
    {
        attrManager.setAttribute(att);
    }
    /*
    public int encodeAndAppend(Var varDest, int nVarDestAbsStartposition, int nTextLength)
    {
        //Custon serialization process uncompatible with direct modification of header bytes form Cobol
        int nAttrEncoded = fieldAttributes.getEncodedValue();   // Will use 4 char position
        char cProgrammedSymbolSet = m_Flag.getEncodedValue();   // Will use 1 char
        //int setEncodedEdit(int nAttributes, char cProgrammedSymbolSet, String csText, int nVarDestAbsStartposition)
        int nSize = varDest.varManager.setEncodedEdit(
            nAttrEncoded,
            cProgrammedSymbolSet,
            getString(),
            nVarDestAbsStartposition,
            nTextLength);

        return nSize;
    }
    */

    // debug
    public int getEncodedAttr()
    {
        return attrManager.getEncodedAttr();
    }

    /** Sets the encoded attr. */
    public void setEncodedAttr(int n)
    {
        attrManager.setEncodedAttr(n);
    }

    /** Returns whether s cursor. */
    public boolean hasCursor()
    {
        return attrManager.hasCursor();
    }

    /** Exports the xml. */
    public abstract Element exportXML(Document doc, String csLangId);

    public boolean isFlagSet()
    {
        return attrManager.isFlagSet();
    }
    public MapFieldAttrColor getColor()
    {
        return attrManager.getColor();
    }

    public boolean isHighlightNormal()
    {
        return attrManager.isHighlightNormal();
    }

    /** Sets the attributes. */
    public void setAttributes(int n)
    {
        attrManager.setAttributes(n);
    }

    public String getFlag()
    {
        return attrManager.getFlag();
    }

    /** Sets the string at position. */
    public void setStringAtPosition(String csValue, int nOffsetPosition, int nNbChar)
    {
        varDef.write(bufferPos, csValue, nOffsetPosition+getVarDef().getHeaderLength(), nNbChar);
    }

    /** Executes the set operation. */
    public void set(CobolConstantZero cst)
    {
        varDef.write(bufferPos, cst);
    }

    /** Executes the set operation. */
    public void set(CobolConstantSpace cst)
    {
        varDef.write(bufferPos, cst);
    }

    /** Executes the set operation. */
    public void set(CobolConstantHighValue cst)
    {
        varDef.write(bufferPos, cst);
    }

    /** Executes the set operation. */
    public void set(CobolConstantLowValue cst)
    {
        varDef.write(bufferPos, cst);
    }

    /** Executes the digits operation. */
    public String digits()
    {
        return "";  // varDef.digits();
    }

    public String getValue()
    {
        return getString();
    }

    /** Executes the encode into char buffer operation. */
    public int encodeIntoCharBuffer(InternalCharBuffer charBuffer, String csText, int nTextLength, int nPos)
    {
        int nAttrEncoded = attrManager.getAttributeEncodedValue();  // Will use 4 char position
        nPos = charBuffer.writeInt(nAttrEncoded, nPos);
        if(nPos != -1)
        {
            char programmedSymbolSet = attrManager.getEncodedFlag();    // Will use 1 char
            nPos = charBuffer.writeChar(programmedSymbolSet, nPos);
            if(nPos != -1)
            {
                nPos = charBuffer.writeShort((short)nTextLength, nPos);     // Write string length on a shor
                if(nPos != -1)
                {
                    String cs = csText.substring(0, nTextLength);
                    nPos = charBuffer.writeString(cs, nPos);
                    if(nPos != -1)
                    {
                        if (isLogCESM) {
                            Log.logDebug("edit encodeIntoCharBuffer cs=" + cs + " to edit=" + getLoggableValue());
                        }
                        return nPos;
                    }
                }
            }
        }
        return -1;
    }

    /** Executes the decode from var operation. */
    public int decodeFromVar(VarBase varSource, int nPos, int nDestLength)
    {
        int nPositionSource = varSource.getBodyAbsolutePosition() + nPos;

        int nAttrEncoded = VarDefBuffer.getDecodedEditAttributes(varSource.bufferPos, nPositionSource);
        attrManager.setAttributeEncodedValue(nAttrEncoded); // Will use 4 char position

        char programmedSymbolSet = VarDefBuffer.getDecodedEditFlag(varSource.bufferPos, nPositionSource);
        attrManager.setEncodedFlag(programmedSymbolSet);    // Will use 4 char position

        int nPositionDest = getBodyAbsolutePosition();
        nPositionSource += 7;
        bufferPos.copyBytesFromSource(nPositionDest, varSource.bufferPos, nPositionSource, nDestLength);

        if (isLogCESM) {
            Log.logDebug("edit decodeFromVar source=" + varSource.getLoggableValue() + " to edit=" + getLoggableValue());
        }
        return nPos + 7 + nDestLength;
    }

    /** Executes the decode from char buffer operation. */
    public int decodeFromCharBuffer(InternalCharBuffer charBuffer, int nPos, int nDestLength)
    {
        int nPositionSource = nPos;

        int nAttrEncoded = VarDefBuffer.getDecodedEditAttributes(charBuffer, nPositionSource);
        attrManager.setAttributeEncodedValue(nAttrEncoded); // Will use 4 char position

        char programmedSymbolSet = VarDefBuffer.getDecodedEditFlag(charBuffer, nPositionSource);
        attrManager.setEncodedFlag(programmedSymbolSet);    // Will use 4 char position

        int nPositionDest = getBodyAbsolutePosition();
        nPositionSource += 7;
        bufferPos.copyBytesFromSource(nPositionDest, charBuffer, nPositionSource, nDestLength);

//      logCesm("edit decodeFromVar source="+varSource.getLoggableValue()+" to edit="+getLoggableValue());
        return nPos + 7 + nDestLength;
    }

    public int getLength()
    {
        return varDef.getBodyLength();
    }

    /** Sets the length. */
    public void setLength(int n)
    {
        // TODO(quality-governance): fake method
        throw new AssertException("unsupported action : Edit.setLength()");
    }

    /** Executes the equals operation. */
    public boolean equals(String csValue)
    {
        if (compareTo(ComparisonMode.Unicode, csValue) == 0) {
            return true;
        }
        return false;
    }

    /** Executes the compare to operation. */
    public int compareTo(ComparisonMode mode, VarAndEdit var2)
    {
        String cs1 = getString();
        int n = var2.compareTo(mode, cs1);
        if (n < 0) {
            return 1;
        }
        if (n > 0) {
            return -1;
        }
        return 0;
    }

    /** Executes the compare to operation. */
    public int compareTo(ComparisonMode mode, String sValue)
    {
        String s = getString();
        //s = StringUtil.trimLeftRight(s);
        //sValue = StringUtil.trimLeftRight(sValue);

        return StringAsciiEbcdicUtil.compare(mode, s, sValue);
    }

    /** Executes the compare to operation. */
    public int compareTo(int n2)
    {
        int n1;
        if (getString().trim().equals("")) {
            n1 = -1;
        } else {
            n1 = getInt();
        }
        if (n1 < n2) {
            return -1;
        }
        if (n1 == n2) {
            return 0;
        }
        return 1;
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

    /** Executes the initialize operation. */
    public void initialize(InitializeCache initializeCache)
    {
        varDef.write(bufferPos, CobolConstant.Space) ;

//      //if(attrManager != null)
//      //  attrManager.initialize();
    }


    /** Sets the developable mark. */
    public Edit setDevelopableMark(String string)
    {
        attrManager.setDevelopableMark(string);
        return this ;
    }

    /** Sets the format. */
    public Edit setFormat(String string)
    {
        attrManager.setFormat(string);
        return this ;
    }

    /** Executes the initialize attributes operation. */
    public void initializeAttributes()
    {
        attrManager.initialize() ;
    }

    protected EditAttributManager attrManager = null;
}
