package semantic.CICS;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;
import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;

/** Semantic representation of EXEC CICS SEND TEXT. */
public class CEntityCICSSendText extends CBaseActionEntity
{
    private CDataEntity dataFrom;
    private CDataEntity dataLength;
    private CDataEntity response;
    private CDataEntity response2;
    private boolean erase;
    private boolean freeKeyboard;

    /** Creates a SEND TEXT semantic action. */
    public CEntityCICSSendText(int line, CObjectCatalog catalog)
    {
        super(line, catalog);
        if (catalog != null)
        {
            catalog.SendNotifRequest(new NotifDeclareUseCICSPreprocessor());
        }
    }

    /** Sets source text and its optional length. */
    public void setDataFrom(CDataEntity source, CDataEntity length)
    {
        dataFrom = source;
        dataLength = length;
    }

    /** Sets terminal flags. */
    public void setFlags(boolean eraseValue, boolean freeKeyboardValue)
    {
        erase = eraseValue;
        freeKeyboard = freeKeyboardValue;
    }

    /** Sets optional RESP targets. */
    public void setResponses(CDataEntity responseValue, CDataEntity response2Value)
    {
        response = responseValue;
        response2 = response2Value;
    }

    @Override
    public boolean ignore()
    {
        return false;
    }

    public CDataEntity getDataFrom()
    {
        return dataFrom;
    }

    public CDataEntity getDataLength()
    {
        return dataLength;
    }

    public CDataEntity getResponse()
    {
        return response;
    }

    public CDataEntity getResponse2()
    {
        return response2;
    }

    public boolean isErase()
    {
        return erase;
    }

    public boolean isFreeKeyboard()
    {
        return freeKeyboard;
    }
}
