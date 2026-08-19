package nacaLib.cics;

import java.nio.charset.StandardCharsets;
import nacaLib.CESM.CESMReturnCode;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.misc.CCESMFakeMethodContainer;
import nacaLib.program.CESMCommandCode;
import nacaLib.varEx.Var;

/** Executable fluent command used by generated EXEC CICS READ statements. */
public class CicsReadCommand extends CCESMFakeMethodContainer
{
    /** Supported keyed read modes. */
    public enum Mode
    {
        NORMAL,
        NEXT,
        PREVIOUS
    }

    private final BaseEnvironment environment;
    private final String fileName;
    private final Mode mode;
    private Var into;
    private Var length;
    private Integer literalLength;
    private Var recordId;
    private Var keyLength;
    private Integer literalKeyLength;
    private Var response;
    private Var response2;
    private boolean forUpdate;

    /** Creates a keyed read command. */
    public CicsReadCommand(BaseEnvironment environment, String fileName, Mode mode)
    {
        super(environment);
        this.environment = environment;
        this.fileName = fileName;
        this.mode = mode;
    }

    @Override
    public CicsReadCommand into(Var value)
    {
        into = value;
        return this;
    }

    @Override
    public CicsReadCommand length(Var value)
    {
        length = value;
        return this;
    }

    @Override
    public CicsReadCommand length(int value)
    {
        literalLength = value;
        return this;
    }

    @Override
    public CicsReadCommand recIDField(Var value)
    {
        recordId = value;
        return this;
    }

    @Override
    public CicsReadCommand keyLength(Var value)
    {
        keyLength = value;
        return this;
    }

    @Override
    public CicsReadCommand keyLength(int value)
    {
        literalKeyLength = value;
        return this;
    }

    @Override
    public CicsReadCommand equal()
    {
        return this;
    }

    @Override
    public CicsReadCommand update()
    {
        forUpdate = true;
        return this;
    }

    /** Captures the target of the RESP option. */
    public CicsReadCommand resp(Var value)
    {
        response = value;
        return this;
    }

    /** Captures the target of the RESP2 option. */
    public CicsReadCommand resp2(Var value)
    {
        response2 = value;
        return this;
    }

    /** Executes the fully configured read against the environment record-store port. */
    public void execute()
    {
        if (mode != Mode.NORMAL)
        {
            throw new UnsupportedOperationException(mode + " requires a CICS browse backend");
        }
        if (environment == null || environment.getCicsRecordStore() == null)
        {
            throw new UnsupportedOperationException("CICS READ requires a configured record store");
        }
        environment.setLastCommandCode(CESMCommandCode.READ_DATASET);
        if (into == null || recordId == null)
        {
            throw new IllegalStateException("CICS READ requires INTO and RIDFLD");
        }
        int maximumLength = literalLength != null
            ? literalLength : length == null ? into.getLength() : length.getInt();
        String keyText = recordId.getString();
        int requestedKeyLength = literalKeyLength != null
            ? literalKeyLength : keyLength == null ? keyText.length() : keyLength.getInt();
        int safeKeyLength = Math.max(0, Math.min(requestedKeyLength, keyText.length()));
        byte[] key = keyText.substring(0, safeKeyLength).getBytes(StandardCharsets.ISO_8859_1);
        CicsRecordReadResult result = environment.getCicsRecordStore().read(
            new CicsRecordReadRequest(fileName, key, maximumLength, forUpdate));
        if (result.isNormal())
        {
            into.set(new String(result.record(), StandardCharsets.ISO_8859_1));
        }
        if (length != null)
        {
            length.set(result.actualLength());
        }
        if (response != null)
        {
            response.set(result.response());
        }
        if (response2 != null)
        {
            response2.set(result.response2());
        }
        environment.setCommandReturnCode(switch (result.response())
        {
            case 0 -> CESMReturnCode.NORMAL;
            case 13 -> CESMReturnCode.NOT_FOUND;
            case 14 -> CESMReturnCode.DUPREC;
            case 20 -> CESMReturnCode.ENDFILE;
            case 22 -> CESMReturnCode.LENGERR;
            case 26 -> CESMReturnCode.ITEMERR;
            case 44 -> CESMReturnCode.QIDERR;
            case 99 -> CESMReturnCode.NET_NAME_ERROR;
            default -> CESMReturnCode.NET_NAME_ERROR;
        });
    }
}
