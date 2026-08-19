package generate.java.forms;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.forms.CEntityFieldAttribute;
import semantic.forms.CEntityFieldData;
import semantic.forms.CEntityFieldOccurs;
import semantic.forms.CEntityFieldRedefine;
import semantic.forms.CEntityFormRedefine;
import semantic.forms.CEntityGetKeyPressed;
import semantic.forms.CEntityIsFieldAttribute;
import semantic.forms.CEntityIsFieldColor;
import semantic.forms.CEntityIsFieldHighlight;
import semantic.forms.CEntityIsKeyPressed;
import semantic.forms.CEntityKeyPressed;
import semantic.forms.CEntityLabelField;
import semantic.forms.CEntityResetKeyPressed;
import semantic.forms.CEntityResourceField;
import semantic.forms.CEntityResourceFieldArray;
import semantic.forms.CEntityResourceForm;
import semantic.forms.CEntityResourceFormContainer;
import semantic.forms.CEntitySkipFields;
import semantic.forms.CResourceStrings;
import utils.CObjectCatalog;

/**
 * Compatibility construction boundary for the separate BMS resource pipeline.
 *
 * <p>BMS is not part of the COBOL semantic-tree assembler migration. Keeping its
 * target-specific resource entities here prevents the COBOL/SQL/CICS factory
 * from selecting backend subclasses while preserving the existing BMS artifact
 * contract.
 */
public final class BmsJavaEntities
{
    private BmsJavaEntities()
    {
    }

    public static CEntityResourceFormContainer formContainer(
        int line, String name, CObjectCatalog catalog,
        CBaseLanguageExporter output, boolean save)
    {
        // Retired direct backend generate.java.forms.CJavaFormContainer: the factory now builds the
        // pure semantic entity. Its data-reference protocol renders through the
        // recursiveFormContainerEntity binding (semantic-runtime-bindings.properties); its class
        // skeleton (imports, "class <name> extends Map {", the two Copy methods, the constructor) and
        // the map block render through the recursiveFormContainerDeclarationEntity template + the
        // skeleton renderer injected below (the retired backend's DoExport body, relocated to this
        // generate-layer boundary so the semantic tree names no generate.* class). The exporter bind
        // preserves the retired backend constructor's LegacyLanguageRenderer.bind side effect for the
        // BMS traversal boundary; the injected identifier formatter stands in for
        // LegacyLanguageRenderer.formatIdentifier and is consumed ONLY by the Java reference getter —
        // the BMS XML/.res artifact (MakeXMLOutput) keeps the neutral getFormattedName() normalization,
        // exactly as before this retirement.
        CEntityResourceFormContainer entity =
            new CEntityResourceFormContainer(line, name, catalog, save);
        generate.LanguageArtifactOutputRegistry.register(entity, output);
        entity.setIdentifierFormatter(identifier -> formatIdentifier(output, identifier));
        return entity;
    }

    public static CEntityResourceForm form(
        int line, String name, CObjectCatalog catalog,
        CBaseLanguageExporter output, boolean save)
    {
        // Retired direct backend generate.java.forms.CJavaForm: the factory now builds the pure
        // semantic entity. Its data-reference protocol renders through the recursiveFormEntity
        // binding (semantic-runtime-bindings.properties); its declaration block renders through the
        // recursiveFormDeclarationEntity template, driven by the declaration renderer injected below
        // (the retired backend's DoExport body, relocated to this generate-layer boundary so the
        // semantic tree names no generate.* class). The exporter bind preserves the retired backend
        // constructor's LegacyLanguageRenderer.bind side effect for the BMS traversal boundary; the
        // injected identifier formatter stands in for LegacyLanguageRenderer.formatIdentifier.
        CEntityResourceForm entity = new CEntityResourceForm(line, name, catalog, save);
        entity.setIdentifierFormatter(identifier -> formatIdentifier(output, identifier));
        return entity;
    }

    public static CEntityFieldAttribute fieldAttribute(
        int line, String name, CObjectCatalog catalog,
        CBaseLanguageExporter output, CDataEntity owner)
    {
        CEntityFieldAttribute entity =
            new CEntityFieldAttribute(line, name, catalog, owner);
        return entity;
    }

    public static CEntityFieldData fieldData(
        int line, String name, CObjectCatalog catalog,
        CBaseLanguageExporter output, CDataEntity field)
    {
        // Retired direct backend generate.java.forms.CJavaFieldData: the factory now builds the
        // pure semantic entity. This is the dead-wiring tier (like CEntityLabelField /
        // CEntityResourceFieldArray): the entity emits no Java and no XML of its own (the retired
        // backend's DoExport was empty), so there is no ST4 template/binding for it. Its data-entity
        // protocols (FIELD data type, no accessors, val needed, empty write-accessor/DoExport) are
        // preserved target-neutrally in semantic.forms.CEntityFieldData. The retired backend's single
        // generate-layer call — ExportReference returning LegacyDataRenderer.renderReference(reference,
        // getLine()) (the owner field's reference) — is injected here as a neutral BiFunction so the
        // semantic tree carries no generate.* coupling. The exporter bind preserves the retired backend
        // constructor's LegacyLanguageRenderer.bind side effect for the BMS traversal boundary.
        CEntityFieldData entity = new CEntityFieldData(line, name, catalog, field);
        return entity;
    }

    public static CResourceStrings resourceStrings(int lines, int columns)
    {
        return new CResourceStrings(lines, columns);
    }

    public static String renderLocalizedStringDeclaration(
        CResourceStrings resources, String resourceName, String displayName)
    {
        CResourceStrings.CLocalizedText text = resources.getLocalizedText(resourceName);
        if (text == null)
        {
            return "";
        }
        return generate.templates.TemplateLoader.getRecursiveAssembler(
            generate.templates.JavaTemplatePipeline.BMS)
            .template("bmsLocalizedStringDeclaration")
            .add("display", displayName)
            .add("texts", text.getTexts())
            .render();
    }

    public static CEntitySkipFields skipFields(
        int line, String name, CObjectCatalog catalog,
        CBaseLanguageExporter output, int fields, String level)
    {
        // Retired direct backend generate.java.forms.CJavaSkipField: the factory now builds the
        // pure semantic entity, rendered by recursiveSkipFieldEntity (REFERENCE binding in
        // semantic-runtime-bindings.properties) and by the recursiveSkipFieldDeclarationEntity
        // declaration template, driven by the declaration renderer injected below (the retired
        // backend's DoExport body, relocated to this generate-layer boundary so the semantic tree
        // names no generate.* class). The exporter bind preserves the retired backend
        // constructor's LegacyLanguageRenderer.bind side effect for the BMS traversal boundary;
        // the injected identifier formatter stands in for LegacyLanguageRenderer.formatIdentifier.
        CEntitySkipFields entity = new CEntitySkipFields(line, name, catalog, fields, level);
        entity.setIdentifierFormatter(identifier -> formatIdentifier(output, identifier));
        return entity;
    }

    public static CEntityResourceField entryField(
        int line, String name, CObjectCatalog catalog,
        CBaseLanguageExporter output)
    {
        CEntityResourceField entity = new CEntityResourceField(line, name, catalog);
        entity.setIdentifierFormatter(identifier -> formatIdentifier(output, identifier));
        entity.setArtifactRenderer((document, resources) ->
            BmsFieldArtifactRenderer.render(entity, document, resources));
        return entity;
    }

    public static CEntityResourceField labelField(
        int line, CObjectCatalog catalog, CBaseLanguageExporter output)
    {
        // Retired direct backend generate.java.forms.CJavaLabelField: the factory now builds
        // the pure semantic entity. A label field emits no Java (its DoExport was empty); its
        // only live output protocol is the BMS XML/.res artifact DoXMLExport, which stays
        // target-neutral in semantic.forms.CEntityLabelField. The retired backend's single
        // generate-layer call (LegacyLanguageRenderer.formatIdentifier for the
        // linkedActiveChoice branch) is injected here as a neutral formatter, so the semantic
        // tree carries no generate.* coupling. The exporter bind preserves the retired backend
        // constructor's LegacyLanguageRenderer.bind side effect for the BMS traversal boundary.
        CEntityLabelField entity = new CEntityLabelField(line, catalog);
        entity.setIdentifierFormatter(identifier -> formatIdentifier(output, identifier));
        return entity;
    }

    public static CEntityFieldRedefine fieldRedefine(
        int line, String name, CObjectCatalog catalog,
        CBaseLanguageExporter output, String level)
    {
        // Retired direct backend generate.java.forms.CJavaFieldRedefine: the factory now builds
        // the pure semantic entity. Its data-reference protocol renders through the
        // recursiveFieldRedefineEntity binding (semantic-runtime-bindings.properties); its
        // declaration block renders through the recursiveFieldRedefineDeclarationEntity template,
        // driven by the declaration renderer injected below (the retired backend's DoExport body,
        // relocated to this generate-layer boundary so the semantic tree names no generate.*
        // class). The exporter bind preserves the retired backend constructor's
        // LegacyLanguageRenderer.bind side effect for the BMS traversal compatibility boundary;
        // the injected identifier formatter stands in for LegacyLanguageRenderer.formatIdentifier.
        CEntityFieldRedefine entity = new CEntityFieldRedefine(line, name, catalog, level);
        entity.setIdentifierFormatter(identifier -> formatIdentifier(output, identifier));
        return entity;
    }

    public static CEntityFormRedefine formRedefine(
        int line, String name, CObjectCatalog catalog,
        CBaseLanguageExporter output, CDataEntity form, boolean save)
    {
        // Retired direct backend generate.java.forms.CJavaFormRedefine: the factory now builds
        // the pure semantic entity. Its data-reference protocol renders through the
        // recursiveFormRedefineEntity binding (semantic-runtime-bindings.properties); its
        // declaration block renders through the recursiveFormRedefineDeclarationEntity template,
        // driven by the declaration renderer injected below (the retired backend's DoExport body,
        // relocated to this generate-layer boundary so the semantic tree names no generate.*
        // class). The exporter bind preserves the retired backend constructor's
        // LegacyLanguageRenderer.bind side effect for the BMS traversal compatibility boundary;
        // the injected identifier formatter stands in for LegacyLanguageRenderer.formatIdentifier.
        CEntityFormRedefine entity = new CEntityFormRedefine(line, name, catalog, form, save);
        entity.setIdentifierFormatter(identifier -> formatIdentifier(output, identifier));
        return entity;
    }

    public static CEntityIsFieldColor isFieldColor()
    {
        // Retired direct backend generate.java.forms.CJavaIsFieldColor: the factory now
        // builds the pure semantic entity, rendered by recursiveIsFieldColorEntity
        // (binding in semantic-runtime-bindings.properties).
        return new CEntityIsFieldColor();
    }

    public static CEntityIsFieldAttribute isFieldAttribute()
    {
        // Retired direct backend generate.java.forms.CJavaIsFieldAttribute: the factory now
        // builds the pure semantic entity, rendered by recursiveIsFieldAttributeEntity
        // (binding in semantic-runtime-bindings.properties).
        return new CEntityIsFieldAttribute();
    }

    public static CEntityIsFieldHighlight isFieldHighlight(CDataEntity reference)
    {
        // Retired direct backend generate.java.forms.CJavaIsFieldHighlight: the factory
        // now builds the pure semantic entity, rendered by
        // recursiveIsFieldHighlightEntity (binding in
        // semantic-runtime-bindings.properties).
        return new CEntityIsFieldHighlight(reference);
    }

    public static CEntityKeyPressed keyPressed(
        int line, String name, CObjectCatalog catalog,
        CBaseLanguageExporter output, String caption)
    {
        // Retired direct backend generate.java.forms.CJavaKeyPressed: the factory now
        // builds the pure semantic entity, rendered by recursiveKeyPressedEntity
        // (binding in semantic-runtime-bindings.properties). The exporter bind preserves
        // the retired backend constructor's LegacyLanguageRenderer.bind side effect for
        // the BMS traversal compatibility boundary.
        CEntityKeyPressed entity = new CEntityKeyPressed(line, name, catalog, caption);
        return entity;
    }

    public static CEntityGetKeyPressed getKeyPressed(
        String name, CObjectCatalog catalog, CBaseLanguageExporter output)
    {
        // builds the pure semantic entity, rendered by recursiveGetKeyPressedEntity
        // (binding in semantic-runtime-bindings.properties). The exporter bind preserves
        // the retired backend constructor's LegacyLanguageRenderer.bind side effect for
        // the BMS traversal compatibility boundary.
        CEntityGetKeyPressed entity = new CEntityGetKeyPressed(name, catalog);
        return entity;
    }

    public static CEntityIsKeyPressed isKeyPressed()
    {
        // Retired direct backend generate.java.forms.CJavaIsKeyPressed: the factory now builds
        // the pure semantic entity. Its condition protocol renders through the
        // recursiveIsKeyPressedEntity binding (semantic-runtime-bindings.properties), branching
        // on the entity's bIsNot flag to select the protected BaseProgram.isKeyPressed /
        // isNotKeyPressed condition call and unfolding the console-key sub-entity recursively
        // (recursiveKeyPressedEntity -> KeyPressed.<constant>) — reproducing the legacy Export
        // byte-for-byte. No exporter bind is needed: the condition renders through the recursive
        // assembler, not the reflective LegacyDataRenderer/LegacyLanguageRenderer boundary.
        return new CEntityIsKeyPressed();
    }

    public static CEntityFieldOccurs fieldOccurs(
        int line, String name, CObjectCatalog catalog,
        CBaseLanguageExporter output)
    {
        // Retired direct backend generate.java.forms.CJavaFieldOccurs: the factory now builds
        // the pure semantic entity. Its data-reference protocol renders through the
        // recursiveFieldOccursEntity binding (semantic-runtime-bindings.properties); its
        // declaration block renders through the recursiveFieldOccursDeclarationEntity template,
        // driven by the declaration renderer injected below (the retired backend's DoExport
        // body, relocated to this generate-layer boundary so the semantic tree names no
        // generate.* class). The exporter bind preserves the retired backend constructor's
        // LegacyLanguageRenderer.bind side effect for the BMS traversal compatibility boundary;
        // the injected identifier formatter stands in for LegacyLanguageRenderer.formatIdentifier.
        CEntityFieldOccurs entity = new CEntityFieldOccurs(line, name, catalog);
        entity.setIdentifierFormatter(identifier -> formatIdentifier(output, identifier));
        return entity;
    }

    public static CEntityResetKeyPressed resetKeyPressed(
        int line, CObjectCatalog catalog, CBaseLanguageExporter output)
    {
        // Retired direct backend generate.java.forms.CJavaResetKeyPressed: the factory now builds
        // the pure semantic entity, rendered by recursiveResetKeyPressedEntity (REFERENCE binding
        // in semantic-runtime-bindings.properties), which emits the protected
        // BaseProgram.resetKeyPressed() statement byte-for-byte as the retired backend's DoExport
        // did ("resetKeyPressed();"). Like the sibling CEntitySetCursor action, the statement
        // renders purely through the recursive assembler's REFERENCE role, so no declaration
        // renderer is injected and the semantic tree names no generate.* class. The exporter bind
        // preserves the retired backend constructor's LegacyLanguageRenderer.bind side effect for
        // the BMS traversal compatibility boundary. The action is dead wiring in production: it is
        // built only by CEntityGetKeyPressed.GetSpecialAssignment for a MOVE SPACE TO KEYPRESSED,
        // which no shipped BMS program performs.
        CEntityResetKeyPressed entity = new CEntityResetKeyPressed(line, catalog);
        return entity;
    }

    public static CEntityResourceFieldArray fieldArray(
        int line, String name, CObjectCatalog catalog,
        CBaseLanguageExporter output)
    {
        // Retired direct backend generate.java.forms.CJavaFieldArray: the factory now builds the
        // pure semantic entity. A field array emits no Java of its own — the retired backend's
        // DoExport only traversed its child motif fields — so there is no ST4 template/binding
        // for it (its ledger item carries manifestBinding/template == null, like CEntityLabelField);
        // its only own output protocol is the BMS XML/.res artifact DoXMLExport, which stays
        // target-neutral in semantic.forms.CEntityResourceFieldArray. The retired backend's single
        // generate-layer operation (the DoExport child traversal) is relocated to this
        // generate-layer boundary so the semantic tree names no generate.* class. The exporter
        // bind preserves the retired backend constructor's LegacyLanguageRenderer.bind side effect
        // for the BMS traversal boundary.
        CEntityResourceFieldArray entity = new CEntityResourceFieldArray(line, name, catalog);
        return entity;
    }

    private static String formatIdentifier(
        CBaseLanguageExporter output, String identifier)
    {
        return output == null
            ? identifier.replace('-', '_').replace('#', '$')
            : output.FormatIdentifier(identifier);
    }
}
