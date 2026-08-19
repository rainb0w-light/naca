/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.xml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author PJD
 *
 */
public class ValCursor
{
    public ValCursor()
    {
    }

    void setEnumVal(NamedNodeMap nodeMap)
    {
        this.nodeMap = nodeMap;
    }

    public String getFirstVal()
    {
        nIndex = 0;
        return getNextVal();
    }

    public String getNextVal()
    {
        int nNbIndex = nodeMap.getLength();
        if(nIndex < nNbIndex)
        {
            Node node = nodeMap.item(nIndex);
            nIndex++;
            String cs = node.getNodeValue();
            return cs;
        }
        return null;
    }

    public Node getFirstParam()
    {
        nIndex = 0;
        return getNextParam();
    }

    public Node getNextParam()
    {
        int nNbIndex = nodeMap.getLength();
        if(nIndex < nNbIndex)
        {
            Node node = nodeMap.item(nIndex);
            nIndex++;
            return node;
        }
        return null;
    }



    NamedNodeMap nodeMap = null;
    int nIndex = 0;
}
