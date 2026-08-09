package generate.java.forms;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import semantic.forms.CEntityResourceField;
import semantic.forms.CResourceStrings;

/** Renders the BMS XML artifact for a neutral entry-field semantic model. */
final class BmsFieldArtifactRenderer
{
    private BmsFieldArtifactRenderer() { }

    static Element render(
        CEntityResourceField field, Document document, CResourceStrings resources)
    {
        Element element;
        if (field.isTitleMode())
        {
            element = document.createElement("title");
        }
        else if (field.isSwitchMode())
        {
            element = document.createElement("switch");
            element.setAttribute("linkedvalue", field.getFormattedLinkedValue());
            element.setAttribute("name", field.getFormattedLinkedValue());
            element.setAttribute("length", String.valueOf(field.getLength()));
            for (CEntityResourceField.SwitchCaseModel model : field.getSwitchCases())
            {
                Element branch;
                if (model.getValue() != null)
                {
                    branch = document.createElement("case");
                    branch.setAttribute("value", model.getValue());
                }
                else if (model.getProtection() != null)
                {
                    branch = document.createElement("case");
                    branch.setAttribute("protection", model.getProtection());
                }
                else
                {
                    branch = document.createElement("default");
                }
                element.appendChild(branch);
                branch.appendChild(document.importNode(model.getTag(), true));
            }
            return element;
        }
        else
        {
            element = document.createElement("edit");
        }

        if (field.isInitialValuePresent())
        {
            Node texts = resources.exportResource(field.getInitialValueName(), document);
            if (texts != null)
            {
                element.appendChild(texts);
            }
        }

        if (field.isCheckboxMode())
        {
            element.setAttribute("type", "checkbox");
            element.setAttribute("valueOn", field.getCheckboxValueOn());
            element.setAttribute("valueOff", field.getCheckboxValueOff());
        }
        else if (field.isActiveChoiceMode())
        {
            element.setAttribute("type", "activeChoice");
            element.setAttribute("activeChoiceValue", field.getActiveChoiceValue());
            element.setAttribute("activeChoiceTarget", field.getActiveChoiceTarget());
            element.setAttribute("activeChoiceSubmit",
                field.isActiveChoiceSubmit() ? "true" : "false");
        }
        else if (field.isLinkedActiveChoiceMode())
        {
            element.setAttribute("type", "linkedActiveChoice");
            element.setAttribute("activeChoiceLink", field.getFormattedActiveChoiceValue());
            element.setAttribute("activeChoiceTarget", field.getActiveChoiceTarget());
            element.setAttribute("activeChoiceSubmit",
                field.isActiveChoiceSubmit() ? "true" : "false");
        }

        if (field.isHiddenMode())
        {
            element.setAttribute("type", "hidden");
            element.setAttribute("length", "0");
            element.setAttribute("line", "0");
            element.setAttribute("col", "0");
        }
        else
        {
            element.setAttribute("length", String.valueOf(field.getLength()));
            element.setAttribute("line", String.valueOf(field.getPositionLine()));
            element.setAttribute("col", String.valueOf(field.getPositionColumn()));
        }

        element.setAttribute("linkedvalue", field.getFormattedLinkedValue());
        if (!field.GetName().isEmpty())
        {
            String displayName = field.getFormattedLinkedValue();
            element.setAttribute("name", displayName);
            if (!field.getFormattedSourceName().equals(displayName))
            {
                element.setAttribute("namecopy", field.getFormattedSourceName());
            }
        }
        if (!field.getColorName().isEmpty())
        {
            element.setAttribute("color", field.getColorName().toLowerCase());
        }
        if (!field.getHighlightName().isEmpty())
        {
            element.setAttribute("highlighting", field.getHighlightName().toLowerCase());
        }
        if (!field.getBrightnessName().isEmpty())
        {
            element.setAttribute("intensity", field.getBrightnessName().toLowerCase());
        }
        if (!field.getProtectionName().isEmpty())
        {
            element.setAttribute("protection", field.getProtectionName().toLowerCase());
        }
        if (field.isCursor())
        {
            element.setAttribute("cursor", "true");
        }
        if (field.isModified())
        {
            element.setAttribute("modified", "true");
        }
        if (field.isReplayMutable())
        {
            element.setAttribute("replayMutable", "true");
        }
        element.setAttribute("justify", field.isRightJustified() ? "right" : "left");
        element.setAttribute("fill", field.getFillValue().toLowerCase());
        return element;
    }
}
