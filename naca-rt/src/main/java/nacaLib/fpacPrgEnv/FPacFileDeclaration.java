/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.fpacPrgEnv;

import nacaLib.programStructure.DataSectionFile;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: FPacFileDeclaration.java,v 1.5 2006/07/04 10:17:32 cvsadmin Exp $
 */
public class FPacFileDeclaration
{
	FPacVarSectionDeclaration section = null;
	String csName = null;
	
	private FPacRecordFiller fPacRecordFillerInput = new FPacRecordFiller((byte)0xff);
	private FPacRecordFiller fPacRecordFillerOutput = new FPacRecordFiller((byte) ' ');
	private boolean bRecordLengthForced = false;
	private int nRecordLength = 0;	
	
	FPacFileDeclaration(FPacVarSectionDeclaration section, String csName)
	{
		this.section = section;
		this.csName = csName;
	}
	
	public FPacFileDescriptor file()
	{
		DataSectionFile fileSection = section.fileSection();
		FPacFileDescriptor fpacFileDescriptor = new FPacFileDescriptor((FPacProgram)section.getProgram(), csName);
		if(bRecordLengthForced)
			fpacFileDescriptor.setRecordLengthForced(nRecordLength);
		fpacFileDescriptor.setRecordFillers(fPacRecordFillerInput, fPacRecordFillerOutput);
		fileSection.setCurrentFileDef(fpacFileDescriptor);
		return fpacFileDescriptor;
	}

	// Supersede the logical name declared record length 
	public FPacFileDeclaration forcedRecordLength(int n)
	{
		nRecordLength = n;
		bRecordLengthForced = true;
		return this;
	}
	
	public FPacFileDeclaration fillInputBuffer(byte by)
	{
		fPacRecordFillerInput.setFiller(by);
		return this;
	}
	
	public FPacFileDeclaration fillInputBuffer(char c)
	{
		fPacRecordFillerInput.setFiller(c);
		return this;
	}
	
	public FPacFileDeclaration fillOutputBuffer(byte by)
	{
		fPacRecordFillerOutput.setFiller(by);
		return this;	
	}
	
	public FPacFileDeclaration fillOutputBuffer(char c)
	{
		fPacRecordFillerOutput.setFiller(c);
		return this;
	}
	public FPacFileDeclaration fillOutputBuffer(String cs)
	{
		if (cs != null && cs.length() >= 1)
			fPacRecordFillerOutput.setFiller(cs.charAt(0));
		return this;
	}
}

