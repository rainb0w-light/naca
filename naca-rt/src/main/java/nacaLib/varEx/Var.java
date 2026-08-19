/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import java.math.BigDecimal;

import nacaLib.mathSupport.MathBase;
import nacaLib.misc.StringAsciiEbcdicUtil;
import nacaLib.tempCache.CStr;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;

/**
 * @author U930DI
 *
 */
public abstract class Var extends VarAndEdit
{
    /** Creates a new var instance. */
    public Var(DeclareTypeBase declareTypeBase)
    {
        super(declareTypeBase);
    }

    protected Var()
    {
        super();
    }

    boolean isEdit()
    {
        return false;
    }

    /** Executes the assign buffer ext operation. */
    public void assignBufferExt(VarBuffer bufferSource)
    {
        if (bufferPos == null) {
            bufferPos = new VarBufferPos(bufferSource, varDef.nDefaultAbsolutePosition);
        } else {    // reuse
            bufferPos.reuse(bufferSource, varDef.nDefaultAbsolutePosition);
        }
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

    /** Sets the string at position. */
    public void setStringAtPosition(String csValue, int nOffsetPosition, int nNbChar)
    {
        varDef.write(bufferPos, csValue, nOffsetPosition, nNbChar);
    }


    /** Sets the and fill. */
    public void setAndFill(String csValue)
    {
        if (csValue.length() > 0) {
            varDef.writeAndFill(bufferPos, csValue.charAt(0));
        }
    }

//  public String digits()
//  {
//      return varDef.digits();
//  }


//  public void set(GenericValue gv)
//  {
//      varDef.write(buffer, gv);
//  }

    /** Returns the at. */
    public Var getAt(VarAndEdit varX)
    {
        int x = varX.getInt();
        return getAt(x);
    }

    /** Returns the at. */
    public Var getAt(MathBase math)
    {
        int x = math.d.intValue() ;
        return getAt(x);
    }

    /** Returns the at. */
    public Var getAt(int x)
    {
        TempCache cache = TempCacheLocator.getTLSTempCache();
        if(cache != null)
        {
            int nTypeId = varDef.getTypeId();
            CoupleVar coupleVarGetAt = cache.getTempVar(nTypeId);
            if(coupleVarGetAt != null)
            {
                if(varDef.occursItemSettings == null) // Not an occursed item, but get the nth char
                {
                    int nAbsStart = varDef.getBodyAbsolutePosition(bufferPos);
                    nAbsStart += x-1;
                    varDef.adjustSettingForCharGetAt(coupleVarGetAt.varDefBuffer, nAbsStart);

                    if (coupleVarGetAt.variable == null) {
                        coupleVarGetAt.variable = allocCopy(coupleVarGetAt.varDefBuffer);
                    }

                    adjust(coupleVarGetAt.varDefBuffer, (Var)coupleVarGetAt.variable);

                    return (Var)coupleVarGetAt.variable;
                }
                else    // Real getAt the reached an occursed item
                {
                    // Adjust varDefGetAt to varDef.getAt(x); It is already created in the correct type
                    int nXBase0 = x-1;
                    varDef.checkIndexes(nXBase0);       // PJD Check added even if the var is retrieved form caches
                    int nAbsStart = varDef.getAbsStart(x-1);
                    int nDebugIndex = VarDefBase.makeDebugIndex(x);
                    varDef.adjustSetting(coupleVarGetAt.varDefBuffer, nAbsStart, nDebugIndex, 1, varDef.varDefParent);

                    if (coupleVarGetAt.variable == null) {
                        coupleVarGetAt.variable = allocCopy(coupleVarGetAt.varDefBuffer);
                    }

                    adjust(coupleVarGetAt.varDefBuffer, (Var)coupleVarGetAt.variable);
                    return (Var)coupleVarGetAt.variable;
                }
            }
            if(varDef.occursItemSettings == null) // Not an occursed item, but get the nth char
            {
                int nAbsStart = varDef.getBodyAbsolutePosition(bufferPos);
                nAbsStart += x-1;

                VarDefBuffer varDefGetAt = varDef.allocCopy();
                varDef.adjustSettingForCharGetAt(varDefGetAt, nAbsStart);
                Var varGetAt = allocCopy(varDefGetAt);

                cache.addTempVar(nTypeId, varDefGetAt, varGetAt);
                return varGetAt;
            }
            else    // Real getAt the reached an occursed item
            {
                VarDefBuffer varDefGetAt = varDef.getAt(x);
                if (varDefGetAt == null) {
                    return this;
                }
                Var varGetAt = allocCopy(varDefGetAt);

                cache.addTempVar(nTypeId, varDefGetAt, varGetAt);
                return varGetAt;
            }
        }

        VarDefBuffer varDefItem = varDef.getAt(x);
        if (varDefItem == null) {
            return this;
        }
        Var var = allocCopy(varDefItem);
        return var;
    }

    /** Returns the at. */
    public Var getAt(int x, VarAndEdit varY)
    {
        int y = varY.getInt();
        return getAt(x, y);
//      VarDefBuffer varDefItem = varDef.getAt(x, y);
//      if(varDefItem == null)
//          return this;
//      Var var = allocCopy(varDefItem);
//      return var;
    }

    /** Returns the at. */
    public Var getAt(MathBase math, VarAndEdit varIndexY)
    {
        int x = math.d.intValue() ;
        int y = varIndexY.getInt();
        return getAt(x, y);
    }

    /** Returns the at. */
    public Var getAt(VarAndEdit vx, VarAndEdit varIndexY)
    {
        int x = vx.getInt() ;
        int y = varIndexY.getInt();
        return getAt(x, y);
    }

    /** Returns the at. */
    public Var getAt(VarAndEdit varIndexY, MathBase math)
    {
        int x = math.d.intValue() ;
        int y = varIndexY.getInt();
        return getAt(y, x);
    }
    /** Returns the at. */
    public Var getAt(VarAndEdit varIndexY, int x)
    {
        int y = varIndexY.getInt();
        return getAt(y, x);
    }

    /** Returns the at. */
    public Var getAt(int y, int x)
    {
        int nYBase0 = y-1;
        int nXBase0 = x-1;
        TempCache cache = TempCacheLocator.getTLSTempCache();
        if(cache != null)
        {
            int nTypeId = varDef.getTypeId();
            CoupleVar coupleVarGetAt = cache.getTempVar(nTypeId);
            if(coupleVarGetAt != null)
            {
                // Adjust varDefGetAt to varDef.getAt(x); It is already created in the correct type
                varDef.checkIndexes(nXBase0, nYBase0);  // PJD Check added even if the var is retrieved form caches
                int nAbsStart = varDef.getAbsStart(nXBase0, nYBase0);
                int nDebugIndex = VarDefBase.makeDebugIndex(y, x);
                varDef.adjustSetting(coupleVarGetAt.varDefBuffer, nAbsStart, nDebugIndex, 2, varDef.varDefParent);

                if (coupleVarGetAt.variable == null) {
                    coupleVarGetAt.variable = allocCopy(coupleVarGetAt.varDefBuffer);
                }

                adjust(coupleVarGetAt.varDefBuffer, (Var)coupleVarGetAt.variable);
                return (Var)coupleVarGetAt.variable;
            }
            VarDefBuffer varDefGetAt = varDef.getAt(y, x);
            if (varDefGetAt == null) {
                return this;
            }
            Var varGetAt = allocCopy(varDefGetAt);

            cache.addTempVar(nTypeId, varDefGetAt, varGetAt);

            return varGetAt;
        }

        VarDefBuffer varDefItem = varDef.getAt(y, x);
        if (varDefItem == null) {
            return this;
        }
        Var var = allocCopy(varDefItem);
        return var;
    }

    /** Returns the at. */
    public Var getAt(VarAndEdit varIndexX, VarAndEdit varIndexY, int z)
    {
        int x = varIndexX.getInt();
        int y = varIndexY.getInt();
        return getAt(x, y, z);
    }

    /** Returns the at. */
    public Var getAt(int x, VarAndEdit varIndexY, int z)
    {
        int y = varIndexY.getInt();
        return getAt(x, y, z);
    }

    /** Returns the at. */
    public Var getAt(VarAndEdit varIndexX, VarAndEdit varIndexY, VarAndEdit varIndexZ)
    {
        int x = varIndexX.getInt();
        int y = varIndexY.getInt();
        int z = varIndexZ.getInt();
        return getAt(x, y, z);
    }

    /** Returns the at. */
    public Var getAt(int x, VarAndEdit varIndexY, VarAndEdit varIndexZ)
    {
        int y = varIndexY.getInt();
        int z = varIndexZ.getInt();
        return getAt(x, y, z);
    }

    /** Returns the at. */
    public Var getAt(VarAndEdit varIndexX, int y, VarAndEdit varIndexZ)
    {
        int x = varIndexX.getInt();
        int z = varIndexZ.getInt();
        return getAt(x, y, z);
    }

    /** Returns the at. */
    public Var getAt(int z, int y, int x)
    {
        int nZBase0 = z-1;
        int nYBase0 = y-1;
        int nXBase0 = x-1;
        TempCache cache = TempCacheLocator.getTLSTempCache();
        if(cache != null)
        {
            int nTypeId = varDef.getTypeId();
            CoupleVar coupleVarGetAt = cache.getTempVar(nTypeId);
            if(coupleVarGetAt != null)
            {
                // Adjust varDefGetAt to varDef.getAt(x); It is already created in the correct type
                varDef.checkIndexes(nXBase0, nYBase0, nZBase0); // PJD Check added even if the var is retrieved form caches
                int nAbsStart = varDef.getAbsStart(nXBase0, nYBase0, nZBase0);
                int nDebugIndex = VarDefBase.makeDebugIndex(z, y, x);
                varDef.adjustSetting(coupleVarGetAt.varDefBuffer, nAbsStart, nDebugIndex, 3, varDef.varDefParent);

                if (coupleVarGetAt.variable == null) {
                    coupleVarGetAt.variable = allocCopy(coupleVarGetAt.varDefBuffer);
                }

                adjust(coupleVarGetAt.varDefBuffer, (Var)coupleVarGetAt.variable);
                return (Var)coupleVarGetAt.variable;
            }
            VarDefBuffer varDefGetAt = varDef.getAt(z, y, x);
            if (varDefGetAt == null) {
                return this;
            }
            Var varGetAt = allocCopy(varDefGetAt);

            cache.addTempVar(nTypeId, varDefGetAt, varGetAt);

            return varGetAt;
        }

        VarDefBuffer varDefItem = varDef.getAt(z, y, x);
        if (varDefItem == null) {
            return this;
        }
        Var var = allocCopy(varDefItem);
        return var;
    }

    /** Returns the string at. */
    public String getStringAt(int x)
    {
        Var var = getAt(x);
        return var.getString();
    }

    /** Returns the string at. */
    public String getStringAt(int x, int y)
    {
        Var var = getAt(x, y);
        return var.getString();
    }

    /** Returns the string at. */
    public String getStringAt(int x, int y, int z)
    {
        Var var = getAt(x, y, z);
        return var.getString();
    }

    /** Executes the adjust operation. */
    public void adjust(VarDefBuffer varDefGetAt, Var varGetAt)
    {
        // Fill varGetAt with custom setting of this
        varGetAt.varDef = varDefGetAt;
        adjust(varDefGetAt, varGetAt.bufferPos);
//      int nOffset = bufferPos.nAbsolutePosition - varDef.nDefaultAbsolutePosition;
//      varGetAt.bufferPos.shareDataBufferFrom(bufferPos);
//      varGetAt.bufferPos.nAbsolutePosition = varDefGetAt.nDefaultAbsolutePosition + nOffset;
    }

    /** Executes the adjust operation. */
    public void adjust(VarDefBuffer varDefGetAt, VarBufferPos varBufferPos)
    {
        // Fill varGetAt with custom setting of this
        varBufferPos.shareDataBufferFrom(bufferPos);
        int nOffset = bufferPos.nAbsolutePosition - varDef.nDefaultAbsolutePosition;
        varBufferPos.nAbsolutePosition = varDefGetAt.nDefaultAbsolutePosition + nOffset;
        //varBufferPos.setProgramManager(bufferPos.getProgramManager());
    }


    /** Executes the alloc copy operation. */
    public Var allocCopy(VarDefBuffer varDefItem)
    {
        VarBase varItem = allocCopy();
        varItem.varDef = varDefItem;

        int nOffset = bufferPos.nAbsolutePosition - varDef.nDefaultAbsolutePosition;
        varItem.bufferPos = new VarBufferPos(bufferPos, varDefItem.nDefaultAbsolutePosition + nOffset);
        varItem.varTypeId = varDefItem.getTypeId();

        //assertIfFalse(varItem.bufferPos.getProgramManager() == bufferPos.getProgramManager());

        return (Var)varItem;
    }

    protected abstract VarBase allocCopy();

    /** Returns the string. */
    public String getString()
    {
        CStr cstr = getOwnCStr();
        String cs = cstr.getAsString();
        //cstr.resetManagerCache();
        return cs;
    }

//  public CStr getCStr()
//  {
//      CStr cstr = bufferPos.getBufChunkAt(varDef.getBodyLength());
//      return cstr;
//  }

    /** Returns the own cstr. */
    public CStr getOwnCStr()
    {
        CStr cstr = bufferPos.getOwnCStr(varDef.getBodyLength());
        return cstr;
    }

    /** Returns the string including header. */
    public String getStringIncludingHeader()
    {
        //return varDef.getRawStringIncludingHeader(bufferPos);
        CStr cstr = bufferPos.getOwnCStr(varDef.getLength());
        String cs = cstr.getAsString();
        return cs;
    }

    public int getLength()
    {
        return varDef.getLength();
    }

//  public int getDependingLength()
//  {
//      return varDef.getRecordDependingLength(bufferPos);
//  }


    /** Executes the transfer to operation. */
    public void transferTo(Var varDest)
    {
        varDef.transfer(bufferPos, varDest);
        //varDest.inheritSemanticContext(this);
    }

    /** Executes the set operation. */
    public void set(Edit varSource)
    {
        if (varSource.isEditInMap()) {
            set((EditInMap) varSource);
        } else {
            set((EditInMapRedefine) varSource);
        }
    }

    /** Executes the set operation. */
    public void set(VarBase varSource)
    {
        if (varSource.isEdit()) {
            set((Edit) varSource);
        } else
        {
            varSource.varDef.transfer(varSource.bufferPos, this);
            //inheritSemanticContext(varSource);
        }
    }

    /** Executes the set operation. */
    public void set(EditInMap varSource)
    {
        varSource.varDef.transfer(varSource.bufferPos, this);
        //inheritSemanticContext(varSource);
    }

    /** Executes the set operation. */
    public void set(EditInMapRedefine varSource)
    {
        //varSource.var2EditInMap.varDef.transfer(varSource.var2EditInMap.buffer, this);
        int n = 0;
        assertIfFalse(false);
    }



    /** Executes the transfer to operation. */
    public void transferTo(Edit varDest)
    {
        varDef.transfer(bufferPos, varDest);    // PJD Var TO EditInMapRedefine; // PJD Var TO EditInMap
        //varDest.inheritSemanticContext(this);
    }

    /** Executes the equals operation. */
    public boolean equals(VarAndEdit varValue)
    {
        if (compareTo(ComparisonMode.Unicode, varValue) == 0) {
            return true;
        }
        return false;
    }

    /** Executes the compare to operation. */
    public int compareTo(ComparisonMode mode, VarAndEdit varValue)
    {
        return varDef.compare(mode, bufferPos, varValue);
    }



    /** Executes the equals operation. */
    public boolean equals(int nValue)
    {
        if (compareTo(nValue) == 0) {
            return true;
        }
        return false;
    }

    /** Executes the equals operation. */
    public boolean equals(double dValue)
    {
        if (compareTo(dValue) == 0) {
            return true;
        }
        return false;
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
    public int compareTo(ComparisonMode mode, String sValue)
    {
//       PJD removed for Batch optimization
        //String s = getString();
        //return StringAsciiEbcdicUtil.compare(mode, s, sValue);
//       PJD end removed for Batch optimization

//       PJD added for Batch optimization
        CStr cstr = getOwnCStr();
        int n = StringAsciiEbcdicUtil.compare(mode, cstr, sValue);
        //TempCacheLocator.getTLSTempCache().resetCStr();
//       PJD end added for Batch optimization
        return n;
    }

    /** Executes the equals operation. */
    public boolean equals(MathBase mathValue)
    {
        if (compareTo(mathValue) == 0) {
            return true;
        }
        return false;
    }

    /** Executes the compare to operation. */
    public int compareTo(MathBase mathValue)
    {
        int n = mathValue.compareTo(this);
        // Return opposite sign, as we changed the operand order
        if (n < 0) {
            return 1;
        }
        if (n > 0) {
            return -1;
        }
        return 0;
    }

    /** Executes the inc operation. */
    public void inc()
    {
        varDef.inc(bufferPos, 1);
    }

    /** Executes the inc operation. */
    public void inc(int nStep)
    {
        varDef.inc(bufferPos, nStep);
    }
    /** Executes the inc operation. */
    public void inc(double dStep)
    {
        BigDecimal bdStep = new BigDecimal(dStep);
        varDef.inc(bufferPos, bdStep);
    }

    /** Executes the inc operation. */
    public void inc(String csStep)
    {
        BigDecimal bdStep = new BigDecimal(csStep);
        varDef.inc(bufferPos, bdStep);
    }

    /** Executes the inc operation. */
    public void inc(Var varStep)
    {
        String csStep = varStep.getDottedSignedString();
        BigDecimal bdStep = new BigDecimal(csStep);
        varDef.inc(bufferPos, bdStep);
    }

    /** Executes the dec operation. */
    public void dec()
    {
        varDef.inc(bufferPos, -1);
    }

    /** Executes the dec operation. */
    public void dec(int nStep)
    {
        varDef.inc(bufferPos, -nStep);
    }

    /** Executes the dec operation. */
    public void dec(Var varStep)
    {
        String csStep = varStep.getDottedSignedString();
        BigDecimal bdStep = new BigDecimal(csStep);
        bdStep = bdStep.negate();
        varDef.inc(bufferPos, bdStep);
    }

    /** Executes the dec operation. */
    public void dec(String csStep)
    {
        BigDecimal bdStep = new BigDecimal(csStep);
        bdStep = bdStep.negate();
        varDef.inc(bufferPos, bdStep);
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

    /** Returns the as alpha num string. */
    public String getAsAlphaNumString()
    {
        String cs = varDef.getAsAlphaNumString(bufferPos).getAsString();
        return cs;
    }

    public int getAbsolutePosition()
    {
        return bufferPos.nAbsolutePosition; //varDef.DEBUGgetAbsolutePosition();
    }

    /** Executes the debugget absolute position operation. */
    public int DEBUGgetAbsolutePosition()
    {
        return bufferPos.nAbsolutePosition; //varDef.DEBUGgetAbsolutePosition();
    }

    EditAttributManager getEditAttributManager()
    {
        return null;
    }

    /** Executes the set operation. */
    public void set(boolean b)
    {
        Assert("Var.set(boolean) not implemented") ;
    }

    /** Executes the compare to operation. */
    public boolean compareTo(boolean b)
    {
        Assert("Var.compareTo(boolean) not implemented") ;
        return false ;
    }

    int getOffsetFromLevel01()
    {
        return varDef.getOffsetFromLevel01();
    }

    /** Executes the sub string operation. */
    public Var subString(int start, int length)
    {
        Var num = (Var) allocCopy();
        VarDefBuffer def = varDef.allocCopy();
        start--;
        def.nTotalSize = Math.min(varDef.nTotalSize, length - start);
        num.varDef = def;
        num.bufferPos = new VarBufferPos(bufferPos, bufferPos.nAbsolutePosition + start);
        return num;
    }

}
