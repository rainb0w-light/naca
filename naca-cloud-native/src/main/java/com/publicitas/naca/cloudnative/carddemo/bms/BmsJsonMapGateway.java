package com.publicitas.naca.cloudnative.carddemo.bms;

import com.publicitas.naca.cloudnative.carddemo.api.BmsFieldInput;
import com.publicitas.naca.cloudnative.carddemo.api.BmsFieldOutput;
import com.publicitas.naca.cloudnative.carddemo.api.BmsTerminalFlags;
import com.publicitas.naca.cloudnative.carddemo.api.BmsTerminalRequest;
import com.publicitas.naca.cloudnative.carddemo.api.BmsTerminalResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Converts the public JSON contract to/from NacaRT's internal Form DOM contract. */
@Component
public class BmsJsonMapGateway
{
    private static final String FIELD_ELEMENT = "field";
    private static final String FORM_ELEMENT = "form";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String VALUE_ATTRIBUTE = "value";
    private static final String MODIFIED_ATTRIBUTE = "modified";
    private static final String CURSOR_ATTRIBUTE = "cursor";
    private static final String UPDATED_ATTRIBUTE = "updated";
    private static final String PASSWORD_TOKEN = "PWD";
    private static final String COBOL_PASSWORD_TOKEN = "PASSWD";

    /** Builds a DOM in memory; no client-provided XML is ever parsed. */
    public Document toReceiveDocument(BmsTerminalRequest request)
    {
        try
        {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element form = document.createElement(FORM_ELEMENT);
            form.setAttribute(NAME_ATTRIBUTE, request.map());
            form.setAttribute("mapSet", request.mapSet());
            form.setAttribute("keypressed", request.aid());
            if (request.cursorField() != null)
            {
                form.setAttribute(CURSOR_ATTRIBUTE, request.cursorField());
            }
            document.appendChild(form);
            for (Map.Entry<String, BmsFieldInput> entry : request.fields().entrySet())
            {
                BmsFieldInput input = entry.getValue();
                Element field = document.createElement(FIELD_ELEMENT);
                field.setAttribute(NAME_ATTRIBUTE, entry.getKey());
                field.setAttribute(VALUE_ATTRIBUTE, input.normalizedValue());
                field.setAttribute(UPDATED_ATTRIBUTE, Boolean.toString(input.effectiveModified()));
                form.appendChild(field);
            }
            return document;
        }
        catch (ParserConfigurationException failure)
        {
            throw new IllegalStateException("Cannot construct the internal BMS receive document", failure);
        }
    }

    /** Converts a Form DOM emitted by NacaRT into the public response DTO. */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public BmsTerminalResponse fromSendDocument(BmsTerminalRequest request, String program,
        Document sendDocument, BmsTerminalFlags terminal)
    {
        Map<String, BmsFieldOutput> fields = new ConcurrentHashMap<>();
        NodeList fieldElements = sendDocument.getDocumentElement().getElementsByTagName(FIELD_ELEMENT);
        for (int index = 0; index < fieldElements.getLength(); index++)
        {
            Element field = (Element) fieldElements.item(index);
            String name = field.getAttribute(NAME_ATTRIBUTE);
            Map<String, String> attributes = attributesExceptIdentity(field);
            fields.put(name, new BmsFieldOutput(
                publicFieldValue(name, field.getAttribute(VALUE_ATTRIBUTE)),
                Boolean.parseBoolean(field.getAttribute(MODIFIED_ATTRIBUTE)),
                Boolean.parseBoolean(field.getAttribute(CURSOR_ATTRIBUTE)),
                attributes));
        }
        return new BmsTerminalResponse(
            request.requestId(), request.conversationId(), request.transactionId(), program,
            request.mapSet(), request.map(), fields, terminal);
    }

    /** Diagnostic vertical slice used until the first CardDemo program can lower successfully. */
    public BmsTerminalResponse roundTrip(BmsTerminalRequest request)
    {
        Document internal = toReceiveDocument(request);
        Element form = internal.getDocumentElement();
        NodeList fields = form.getElementsByTagName(FIELD_ELEMENT);
        for (int index = 0; index < fields.getLength(); index++)
        {
            Element field = (Element) fields.item(index);
            field.setAttribute(MODIFIED_ATTRIBUTE, field.getAttribute(UPDATED_ATTRIBUTE));
            if (field.getAttribute(NAME_ATTRIBUTE).equals(request.cursorField()))
            {
                field.setAttribute(CURSOR_ATTRIBUTE, "true");
            }
        }
        BmsTerminalFlags terminal = new BmsTerminalFlags(true, false, true, request.cursorField());
        return fromSendDocument(request, "BMSJSON", internal, terminal);
    }

    private static Map<String, String> attributesExceptIdentity(Element field)
    {
        Map<String, String> attributes = new ConcurrentHashMap<>();
        NamedNodeMap nodes = field.getAttributes();
        for (int index = 0; index < nodes.getLength(); index++)
        {
            Node node = nodes.item(index);
            if (!NAME_ATTRIBUTE.equals(node.getNodeName()) && !VALUE_ATTRIBUTE.equals(node.getNodeName())
                && !MODIFIED_ATTRIBUTE.equals(node.getNodeName())
                && !CURSOR_ATTRIBUTE.equals(node.getNodeName())
                && !UPDATED_ATTRIBUTE.equals(node.getNodeName()))
            {
                attributes.put(node.getNodeName(), node.getNodeValue());
            }
        }
        return attributes;
    }

    private static String publicFieldValue(String name, String value)
    {
        return name.contains(PASSWORD_TOKEN) || name.contains(COBOL_PASSWORD_TOKEN)
            || name.contains("PASSWORD") ? "" : value;
    }
}
