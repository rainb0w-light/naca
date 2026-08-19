package idea.manager;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.programPool.SharedProgramInstanceData;
import nacaLib.varEx.Var;
import nacaLib.varEx.VarBase;
import nacaLib.varEx.VarEnumerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/** Bridges a generated symbolic BMS copybook buffer to NacaRT's Form DOM. */
final class SymbolicBmsMapAdapter
{
    private static final String FIELD_ELEMENT = "field";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String VALUE_ATTRIBUTE = "value";

    private SymbolicBmsMapAdapter()
    {
    }

    /** Loads public map fields into the corresponding {@code *I} symbolic fields. */
    static void receive(Document document, Var symbolicBuffer)
    {
        if (document == null || document.getDocumentElement() == null)
        {
            return;
        }
        Map<String, Var> fields = leafFields(symbolicBuffer, "I");
        NodeList inputs = document.getDocumentElement().getElementsByTagName(FIELD_ELEMENT);
        for (int index = 0; index < inputs.getLength(); index++)
        {
            Element input = (Element) inputs.item(index);
            Var target = fields.get(normalize(input.getAttribute(NAME_ATTRIBUTE)) + "I");
            if (target != null)
            {
                target.set(input.getAttribute(VALUE_ATTRIBUTE));
            }
        }
    }

    /** Emits the corresponding {@code *O} symbolic fields as a Form-compatible DOM. */
    static Document send(Var symbolicBuffer, String mapName, String language, int cursorPosition)
    {
        return send(symbolicBuffer, symbolicBuffer.getProgramManager(),
            symbolicBuffer.getSharedProgramInstanceData(), mapName, language, cursorPosition);
    }

    /** Emits a symbolic buffer after its program has been popped from the runtime stack. */
    static Document send(Var symbolicBuffer, BaseProgramManager manager,
        SharedProgramInstanceData shared, String mapName, String language, int cursorPosition)
    {
        try
        {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element form = document.createElement("form");
            form.setAttribute(NAME_ATTRIBUTE, mapName);
            form.setAttribute("lang", language);
            if (cursorPosition != 0)
            {
                form.setAttribute("cursorPosition", Integer.toString(cursorPosition));
            }
            document.appendChild(form);
            for (Map.Entry<String, Var> output :
                leafFields(symbolicBuffer, manager, shared, "O").entrySet())
            {
                Element field = document.createElement(FIELD_ELEMENT);
                field.setAttribute(NAME_ATTRIBUTE,
                    output.getKey().substring(0, output.getKey().length() - 1));
                field.setAttribute(VALUE_ATTRIBUTE, output.getValue().getString());
                form.appendChild(field);
            }
            return document;
        }
        catch (ParserConfigurationException failure)
        {
            throw new IllegalStateException("Cannot create symbolic BMS output document", failure);
        }
    }

    private static Map<String, Var> leafFields(Var root, String suffix)
    {
        BaseProgramManager manager = root.getProgramManager();
        SharedProgramInstanceData shared = root.getSharedProgramInstanceData();
        return leafFields(root, manager, shared, suffix);
    }

    private static Map<String, Var> leafFields(Var root, BaseProgramManager manager,
        SharedProgramInstanceData shared, String suffix)
    {
        Map<String, Var> fields = new LinkedHashMap<>();
        collect(root, manager, shared, suffix, fields);
        return fields;
    }

    private static void collect(VarBase current, BaseProgramManager manager,
        SharedProgramInstanceData shared, String suffix, Map<String, Var> fields)
    {
        VarEnumerator children = new VarEnumerator(manager, current);
        VarBase child = children.getFirstVarChild();
        if (child == null)
        {
            if (current instanceof Var variable)
            {
                String name = normalize(current.getVarDef().getUnprefixedName(shared));
                if (name.endsWith(suffix))
                {
                    fields.putIfAbsent(name, variable);
                }
            }
            return;
        }
        while (child != null)
        {
            collect(child, manager, shared, suffix, fields);
            child = children.getNextVarChild();
        }
    }

    private static String normalize(String name)
    {
        int separator = name.lastIndexOf('.');
        String local = separator < 0 ? name : name.substring(separator + 1);
        int debugIndex = local.indexOf('$');
        if (debugIndex >= 0)
        {
            local = local.substring(0, debugIndex);
        }
        return local.toUpperCase(Locale.ROOT);
    }
}
