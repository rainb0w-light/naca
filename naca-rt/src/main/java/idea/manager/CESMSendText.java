package idea.manager;

import nacaLib.CESM.CESMReturnCode;
import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.cics.CicsTextOutput;
import nacaLib.varEx.Var;

/** Fluent runtime command for EXEC CICS SEND TEXT. */
public class CESMSendText extends CJMapObject
{
    private final BaseEnvironment environment;
    private final String source;
    private int length;
    private boolean erase;
    private boolean freeKeyboard;

    /** Creates and immediately publishes a text response with its full source length. */
    public CESMSendText(BaseEnvironment environment, String source)
    {
        this.environment = environment;
        this.source = source;
        length = source.length();
        publish();
    }

    /** Applies the COBOL LENGTH value. */
    public CESMSendText length(Var value)
    {
        return length(value.getInt());
    }

    /** Applies a literal LENGTH value. */
    public CESMSendText length(int value)
    {
        length = Math.max(0, Math.min(value, source.length()));
        publish();
        return this;
    }

    /** Applies the ERASE terminal flag. */
    public CESMSendText erase()
    {
        erase = true;
        publish();
        return this;
    }

    /** Applies the FREEKB terminal flag. */
    public CESMSendText freeKB()
    {
        freeKeyboard = true;
        publish();
        return this;
    }

    /** Writes a normal RESP value. */
    public CESMSendText resp(Var value)
    {
        value.set(CESMReturnCode.NORMAL.getCondition());
        return this;
    }

    /** Writes a normal RESP2 value. */
    public CESMSendText resp2(Var value)
    {
        value.set(0);
        return this;
    }

    private void publish()
    {
        environment.setCicsTextOutput(
            new CicsTextOutput(source.substring(0, length), erase, freeKeyboard));
        environment.setCommandReturnCode(CESMReturnCode.NORMAL);
    }
}
