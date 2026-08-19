package nacaLib.stringSupport;

class UnstringDelimiter
{
    UnstringDelimiter(String cs, boolean bAll)
    {
        this.cs = cs;
        this.isall = bAll;
    }

    String getRemaingStringAfterSeparator(String csSource)
    {
        int nStringLength = cs.length();
        if(isall)
        {
            while(csSource.startsWith(cs))
            {
                csSource = csSource.substring(nStringLength);
            }
        }
        else
        {
            csSource = csSource.substring(nStringLength);
        }
        return csSource;
    }

    int removeDelimiterString(String csSource, int nPosStart)
    {
        if(isall)
        {
            int nLength = cs.length();
            boolean iscontinue = true;
            while(iscontinue)
            {
                iscontinue = false;
                if(csSource.length() >= nPosStart + nLength)
                {
                    String csChunk = csSource.substring(nPosStart, nPosStart + nLength);
                    if(csChunk.equals(cs))
                    {
                        nPosStart += nLength;
                        iscontinue = true;
                    }
                }
            }
        }
        else
        {
            int nLength = cs.length();
            nPosStart += nLength;
        }
        return nPosStart;
    }

    String cs = null;
    boolean isall = false;
}
