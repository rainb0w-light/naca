
import nacaLib.program.* ;
import nacaLib.varEx.* ;

import nacaLib.batchPrgEnv.BatchProgram;
public class Batch1 extends BatchProgram
{
	DataSection WorkingStorageSection = declare.workingStorageSection() ;
Var CPT_IN = declare.level(77).picS9(7).comp3().valueZero().var() ;
Var CPT_OUT = declare.level(77).picS9(7).comp3().valueZero().var() ;
Var FIN_TRAIT = declare.level(77).picX(1).valueSpaces().var() ;
Var SYS_TIME = declare.level(01).pic9(8).valueZero().var() ;
Var Filler$1 = declare.level(1).redefines(SYS_TIME).filler() ;
Var SYS_TIME1 = declare.level(03).pic9(7).var() ;
	Msgzone msgzone = Msgzone.Copy(this) ;
	DataSection FileSection = declare.fileSection() ;
FileDescriptor FILEIN = declare.file("FILEIN") ;
Var FILEIN_Z = declare.level(1).var() ;
Var FILEIN_CODE = declare.level(05).picX(1).var() ;
Var Filler$2 = declare.level(05).picX(68).filler() ;
FileDescriptor FILEOUT = declare.file("FILEOUT") ;
	public void procedureDivision() {
	FILEIN.openInput();
	FILEOUT.openOutput();
	move(getTimeBatch(), SYS_TIME);
	display("DEBUG - TIME : " + val(SYS_TIME1));
	perform(READ_FILEIN) ;
	while (isDifferent(FIN_TRAIT, "F")) {
	perform(TRAITEMENT) ;
	}
	console().display("STAT FILEIN  - READ RECORDS   : " + val(CPT_IN));
	console().display("STAT FILEOUT - WRITE RECORDS  : " + val(CPT_OUT));
	FILEIN.close();
	FILEOUT.close();
	stopRun(0);
	}
	Paragraph READ_FILEIN = new Paragraph(this);
	public void READ_FILEIN() {
	if (read(FILEIN).atEnd()) {
	move("F", FIN_TRAIT);}
	}
	Paragraph TRAITEMENT = new Paragraph(this);
	public void TRAITEMENT() {
	inc(CPT_IN) ;
	if (isEqual(FILEIN_CODE, "1"))
	{
	display("DEBUG 1 - " + val(FILEIN_Z));
	writeFrom(FILEOUT, FILEIN_Z);
	inc(CPT_OUT) ;
	}
	else if (isEqual(FILEIN_CODE, "2"))
	{
	move("0001", msgzone.msg_No);
	call("Callmsg").using(msgzone.msg_Zone).executeCall();
	display("DEBUG 2 - " + val(msgzone.msg_Text));
	}
	perform(READ_FILEIN) ;
	}
}
