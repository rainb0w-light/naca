/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * @author sly
 *
 */
public class CResourceStrings
{
    public static String LANG_FRENCH = "FR" ;
    public static String LANG_GERMAN = "DE" ;
    public static String LANG_ITALIAN = "IT" ;
    public static String LANG_ENGLISH = "EN" ;
    public static String getOfficialLanguageCode(String lang)
    {
        lang = lang.trim() ;
        if (lang.endsWith("F"))
        {
            return LANG_FRENCH;
        }
        else if (lang.endsWith("D"))
        {
            return LANG_GERMAN;
        }
        else if (lang.endsWith("I"))
        {
            return LANG_ITALIAN;
        }
        else if (lang.endsWith("G"))
        {
            return LANG_ENGLISH;
        }
        else
        {
            return lang;
        }
    }

    protected ArrayList<String> langId = new ArrayList<String>() ;
    public static final class CLocalizedValue
    {
        private final String languageCode;
        private final String text;

        CLocalizedValue(String languageCode, String text)
        {
            this.languageCode = languageCode;
            this.text = text;
        }

        public String getLanguageCode() { return languageCode; }
        public String getText() { return text; }
    }

    public static final class CLocalizedText
    {
        private String id = "" ;
        private final LinkedHashMap<String, String> textTable = new LinkedHashMap<>();
        private int length = 0;

        public String getId() { return id; }
        public int getLength() { return length; }
        public List<CLocalizedValue> getTexts()
        {
            ArrayList<CLocalizedValue> values = new ArrayList<>();
            for (Map.Entry<String, String> entry : textTable.entrySet())
            {
                values.add(new CLocalizedValue(entry.getKey(), entry.getValue()));
            }
            return Collections.unmodifiableList(values);
        }
    }
    public CResourceStrings(int nbLines, int nbCols)
    {
        this.nbCols = nbCols ;
        this.nbLines = nbLines ;
        lines = new CLocalizedText[nbLines+1][];
        tabTexts = new LinkedHashMap<String, CLocalizedText>() ;
    }
    public void SetResourceText(int line, int col, String text, String langID, int length)
    {
        String csLang = getOfficialLanguageCode(langID) ;
        if (!langId.contains(csLang))
        {
            langId.add(csLang) ;
        }
        CLocalizedText lText = GetResourceAt(line, col) ;
        lText.length = length ;
        lText.textTable.put(csLang, text) ;
    }
    public void SetResourceText(int line, int col, String text, String langID, String id, int length)
    {
        String csLang = getOfficialLanguageCode(langID) ;
        if (!langId.contains(csLang))
        {
            langId.add(csLang) ;
        }
        CLocalizedText lText = GetResourceAt(line, col) ;
        lText.length = length ;
        lText.textTable.put(csLang, text) ;
        if (!id.equals(""))
        {
            lText.id = id ;
            tabTexts.put(id, lText) ;
        }
    }
    protected CLocalizedText GetResourceAt(int line, int col)
    {
        CLocalizedText text = null ;
        if (lines[line] == null)
        {
            lines[line] = new CLocalizedText[nbCols+1] ;
        }
        text = lines[line][col] ;
        if (text == null)
        {
            text = new CLocalizedText() ;
            lines[line][col] = text;
        }
        return text;
    }
    public String CreateName(String radical)
    {
        return radical + "_LABEL_" + lastIndex++;
    }
    protected int lastIndex = 0;
    protected int nbLines = 0 ;
    protected int nbCols = 0 ;
    protected CLocalizedText[][] lines = null ;
    protected LinkedHashMap<String, CLocalizedText> tabTexts = null ;

    public CLocalizedText getLocalizedText(String name)
    {
        return tabTexts.get(name);
    }

    public List<CLocalizedText> getLocalizedTexts()
    {
        return Collections.unmodifiableList(new ArrayList<>(tabTexts.values()));
    }

    public Node exportResource(String name, Document doc)
    {
        CLocalizedText res = tabTexts.get(name) ;
        if (res == null)
        {
            return null;
        }
        Element eText = doc.createElement("texts");
        for (CLocalizedValue value : res.getTexts())
        {
            Element e = doc.createElement("text");
            e.setAttribute("lang", value.getLanguageCode()) ;
            eText.appendChild(e);
            e.appendChild(doc.createTextNode(value.getText()));
        }
        return eText ;
    }

    /** Semantic normalization used by TITLE fields; target formatting is handled by the renderer. */
    public void FormatResource(String name)
    {
        CLocalizedText resource = tabTexts.get(name);
        if (resource == null)
        {
            return;
        }
        resource.textTable.replaceAll((language, text) -> text.trim());
    }

    public String exportAllLangId()
    {
        String cs = "" ;
        for (int i = 0; i< langId.size(); i++)
        {
            if (i>0)
            {
                cs += ";" ;
            }
            cs += langId.get(i) ;
        }
        return cs ;
    }
    /**
     * @param posLine
     * @param posCol
     * @return
     */
    public boolean isExistingField(int line, int col, int length)
    {
        if (line > nbLines || col > nbCols)
        {
            return false ;
        }
        CLocalizedText text = null ;
        if (lines[line] == null)
        {
            return  false ;
        }
        text = lines[line][col] ;
        if (text == null)
        {
            return false ;
        }
        if (text.length != length)
        {
            return false ;
        }
        return true ;
    }
}
