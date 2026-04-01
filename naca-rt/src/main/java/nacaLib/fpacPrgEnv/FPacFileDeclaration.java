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
	
	private FPacRecordFiller pacRecordFillerInput = new FPacRecordFiller((byte)0xff);
	private FPacRecordFiller pacRecordFillerOutput = new FPacRecordFiller((byte) ' ');
	private boolean isrecordLengthForced = false;
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
		if(isrecordLengthForced)
			fpacFileDescriptor.setRecordLengthForced(nRecordLength);
		fpacFileDescriptor.setRecordFillers(pacRecordFillerInput, pacRecordFillerOutput);
		fileSection.setCurrentFileDef(fpacFileDescriptor);
		return fpacFileDescriptor;
	}

	// Supersede the logical name declared record length 
	public FPacFileDeclaration forcedRecordLength(int n)
	{
		nRecordLength = n;
		isrecordLengthForced = true;
		return this;
	}
	
	public FPacFileDeclaration fillInputBuffer(byte by)
	{
		pacRecordFillerInput.setFiller(by);
		return this;
	}
	
	public FPacFileDeclaration fillInputBuffer(char c)
	{
		pacRecordFillerInput.setFiller(c);
		return this;
	}
	
	public FPacFileDeclaration fillOutputBuffer(byte by)
	{
		pacRecordFillerOutput.setFiller(by);
		return this;	
	}
	
	public FPacFileDeclaration fillOutputBuffer(char c)
	{
		pacRecordFillerOutput.setFiller(c);
		return this;
	}
	public FPacFileDeclaration fillOutputBuffer(String cs)
	{
		if (cs != null && cs.length() >= 1)
			pacRecordFillerOutput.setFiller(cs.charAt(0));
		return this;
	}
}

