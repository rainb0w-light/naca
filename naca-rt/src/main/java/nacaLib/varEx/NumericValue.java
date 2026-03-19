/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 17 mars 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.varEx;

/**
 * @author U930DI
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NumericValue
{
	NumericValue()
	{
	}
	
	void set(boolean bSigned, int nNbDigitInteger, int nNbDigitDecimal)
	{
		this.bSigned = bSigned;
		this.nNbDigitInteger = nNbDigitInteger;
		this.nNbDigitDecimal = nNbDigitDecimal;
		this.bSignSeparated = false;
		this.bSignLeading = false;
		this.nComp = 0;
	}
	
	NumericValue(NumericValue master)
	{
		bSigned = master.bSigned;
		nNbDigitInteger = master.nNbDigitInteger;
		nNbDigitDecimal = master.nNbDigitDecimal;
		nComp = master.nComp;
		bSignSeparated = master.bSignSeparated;
		bSignLeading = master.bSignLeading;
	}
	
	VarDefBuffer createVarDefFPacNum(VarDefBase varDefParent, DeclareType9 declareType9)
	{
		if(nComp == -3)	// Comp-3 specified: 2 digits by char (1 by nibble), with the sign in the rightmost nibble
		{
			if(bSigned)
			{
				if(nNbDigitDecimal == 0)
				{
					if(isIntEnough())
						return new VarDefFPacNumIntSignComp3(varDefParent, declareType9, this);
				}
			}
		}
		return createVarDef(varDefParent, declareType9);
	}
	
	VarDefBuffer createVarDef(VarDefBase varDefParent, DeclareType9 declareType9)
	{
		System.out.println("DEBUG createVarDef: bSigned=" + bSigned + ", nNbDigitInteger=" + nNbDigitInteger + ", nNbDigitDecimal=" + nNbDigitDecimal + ", nComp=" + nComp);
		if(nComp == 0)	// No Comp-... specified: 1 char is a digit, except maybe the sign that may be embbed in the last char
		{
			if(nNbDigitDecimal == 0)
			{
				if(bSigned && bSignSeparated)
				{
					if(bSignLeading)
					{
						if(isIntEnough())
							return new VarDefNumIntSignLeadingComp0(varDefParent, declareType9, this);
						else
							return new VarDefNumIntSignLeadingComp0Long(varDefParent, declareType9, this);
					}
					else	// Trailing
					{
						if(isIntEnough())
							return new VarDefNumIntSignTrailingComp0(varDefParent, declareType9, this);
						else
							return new VarDefNumIntSignTrailingComp0Long(varDefParent, declareType9, this);
					}
				}
				else if(bSigned)
				{
					if(isIntEnough())
						return new VarDefNumIntSignComp0(varDefParent, declareType9, this);
					else
						return new VarDefNumIntSignComp0Long(varDefParent, declareType9, this);
				}
				else
				{
					if(isIntEnough())
						return new VarDefNumIntComp0(varDefParent, declareType9, this);
					else
						return new VarDefNumIntComp0Long(varDefParent, declareType9, this);
				}
			}
			else
			{
				if(bSigned && bSignSeparated)
				{
					if(bSignLeading)
						return new VarDefNumDecSignLeadingComp0(varDefParent, declareType9, this);
					else	// Trailing
						return new VarDefNumDecSignTrailingComp0(varDefParent, declareType9, this);
				}
				else if(bSigned)
					return new VarDefNumDecSignComp0(varDefParent, declareType9, this);
				else
					return new VarDefNumDecComp0(varDefParent, declareType9, this);
			}
		}
		else if(nComp == -3)	// Comp-3 specified: 2 digits by char (1 by nibble), with the sign in the rightmost nibble
		{
			if(!bSigned)
			{
				if(nNbDigitDecimal == 0)
				{
					if(isIntEnough())
						return new VarDefNumIntComp3(varDefParent, declareType9, this);
					else
						return new VarDefNumIntComp3Long(varDefParent, declareType9, this);
				}
				else
					return new VarDefNumDecComp3(varDefParent, declareType9, this);
			}
			else
			{
				if(nNbDigitDecimal == 0)
				{
					if(isIntEnough())
						return new VarDefNumIntSignComp3(varDefParent, declareType9, this);
					else
						return new VarDefNumIntSignComp3Long(varDefParent, declareType9, this);
				}
				else
					return new VarDefNumDecSignComp3(varDefParent, declareType9, this);
			}
		}
		else if(nComp == -4)	// Binary
		{
			if(nNbDigitDecimal == 0)
			{
				if(!bSigned)
				{
					if(isIntEnough())
						return new VarDefNumIntComp4(varDefParent, declareType9, this);
					else
						return new VarDefNumIntComp4Long(varDefParent, declareType9, this);
				}					
				else
				{
					if(isIntEnough())
						return new VarDefNumIntSignComp4(varDefParent, declareType9, this);
					else
						return new VarDefNumIntSignComp4Long(varDefParent, declareType9, this);
				}
			}
			else 
			{
				if(!bSigned)
					return new VarDefNumDecComp4(varDefParent, declareType9, this);
				else
					return new VarDefNumDecSignComp4(varDefParent, declareType9, this);
			}			
		}
		return null;
	}
	
	private boolean isIntEnough()
	{
		return IntLongDeterminator.isIntEnough(nNbDigitInteger);
	}
	
	VarNum createVar(DeclareType9 declareType9)
	{		
		if(nComp == 0)
		{
			if(!bSigned)
			{
				if(isIntEnough())
				{
					if(nNbDigitDecimal == 0)
						return new VarNumIntComp0(declareType9);
					else
						return new VarNumDecComp0(declareType9);
				}
				else
				{
					if(nNbDigitDecimal == 0)
						return new VarNumIntComp0Long(declareType9);
					else
						return new VarNumDecComp0Long(declareType9);
				}
			}
			else
			{
				if(nNbDigitDecimal == 0)
				{
					if(bSignSeparated)
					{
						if(bSignLeading)
							return new VarNumIntSignLeadingComp0(declareType9);
						else
							return new VarNumIntSignTrailingComp0(declareType9);
					}
					else
						return new VarNumIntSignComp0(declareType9);
				}
				else
				{
					if(bSignSeparated)
					{
						if(bSignLeading)
							return new VarNumDecSignLeadingComp0(declareType9);
						else
							return new VarNumDecSignTrailingComp0(declareType9);
					}
					else
						return new VarNumDecSignComp0(declareType9);
				}
			}
		}
		else if(nComp == -3)
		{
			if(!bSigned)
			{
				if(nNbDigitDecimal == 0)
					return new VarNumIntComp3(declareType9);
				else
					return new VarNumDecComp3(declareType9);
			}
			else
			{
				if(nNbDigitDecimal == 0)
					return new VarNumIntSignComp3(declareType9);
				else
					return new VarNumDecSignComp3(declareType9);
			}
		}
		else if(nComp == -4)
		{
			if(nNbDigitDecimal == 0)
			{
				if(!bSigned)
				{
					if(isIntEnough())
						return new VarNumIntComp4(declareType9);
					else
						return new VarNumIntComp4Long(declareType9);
				}
				else
				{
					if(isIntEnough())
						return new VarNumIntSignComp4(declareType9);
					else
						return new VarNumIntSignComp4Long(declareType9);
				}
			}
			else
			{
				if(!bSigned)
					return new VarNumDecComp4(declareType9);
				else
					return new VarNumDecSignComp4(declareType9);
			}
		}
		return null;
	}

	void setSignLeadingSeparated(boolean bLeading)
	{
		bSigned = true;
		bSignSeparated = true;
		bSignLeading = bLeading;
	}
	
	boolean bSigned = false;
	boolean bSignSeparated = false;
	boolean bSignLeading = false;
	int nComp = 0;
	int nNbDigitInteger = 0; 
	int nNbDigitDecimal = 0;
}
