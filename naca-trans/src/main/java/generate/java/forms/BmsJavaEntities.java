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
		return new CJavaFormContainer(line, name, catalog, output, save);
	}

	public static CEntityResourceForm form(
		int line, String name, CObjectCatalog catalog,
		CBaseLanguageExporter output, boolean save)
	{
		return new CJavaForm(line, name, catalog, output, save);
	}

	public static CEntityFieldAttribute fieldAttribute(
		int line, String name, CObjectCatalog catalog,
		CBaseLanguageExporter output, CDataEntity owner)
	{
		return new CJavaFieldAttribute(line, name, catalog, output, owner);
	}

	public static CEntityFieldData fieldData(
		int line, String name, CObjectCatalog catalog,
		CBaseLanguageExporter output, CDataEntity field)
	{
		return new CJavaFieldData(line, name, catalog, output, field);
	}

	public static CResourceStrings resourceStrings(int lines, int columns)
	{
		return new CJavaResourceStrings(lines, columns);
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
		generate.LegacyLanguageRenderer.bind(entity, output);
		entity.setIdentifierFormatter(identifier -> output.FormatIdentifier(identifier));
		entity.setDeclarationRenderer(BmsJavaEntities::renderSkipFieldDeclaration);
		return entity;
	}

	/**
	 * The retired {@code CJavaSkipField.DoExport} body, relocated to this generate-layer
	 * boundary. Renders the declaration line declaratively through the recursive ST4 assembly
	 * contract ({@code recursiveSkipFieldDeclarationEntity}), then drives the {@code { ... }}
	 * block over any child fields — which keep rendering through the still-direct BMS field
	 * backends the surrounding {@code CJavaForm}/{@code CJavaFieldRedefine} traversal invokes.
	 */
	private static void renderSkipFieldDeclaration(CEntitySkipFields entity)
	{
		String declaration = generate.templates.TemplateLoader.getRecursiveAssembler()
			.template("recursiveSkipFieldDeclarationEntity")
			.add("entity", entity)
			.render();
		generate.LegacyLanguageRenderer.writeLine(entity, declaration);
		generate.LegacyLanguageRenderer.startBlock(entity);
		generate.LegacyLanguageRenderer.exportChildren(entity, false);
		generate.LegacyLanguageRenderer.endBlock(entity);
	}

	public static CEntityResourceField entryField(
		int line, String name, CObjectCatalog catalog,
		CBaseLanguageExporter output)
	{
		return new CJavaField(line, name, catalog, output);
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
		generate.LegacyLanguageRenderer.bind(entity, output);
		entity.setIdentifierFormatter(identifier -> output.FormatIdentifier(identifier));
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
		generate.LegacyLanguageRenderer.bind(entity, output);
		entity.setIdentifierFormatter(identifier -> output.FormatIdentifier(identifier));
		entity.setDeclarationRenderer(BmsJavaEntities::renderFieldRedefineDeclaration);
		return entity;
	}

	/**
	 * The retired {@code CJavaFieldRedefine.DoExport} body, relocated to this generate-layer
	 * boundary. Renders the declaration line declaratively through the recursive ST4 assembly
	 * contract ({@code recursiveFieldRedefineDeclarationEntity}), then drives the {@code { ... }}
	 * block over the field's child attributes — which keep rendering through the still-direct BMS
	 * field backends the surrounding {@code CJavaForm} legacy traversal invokes.
	 */
	private static void renderFieldRedefineDeclaration(CEntityFieldRedefine entity)
	{
		String declaration = generate.templates.TemplateLoader.getRecursiveAssembler()
			.template("recursiveFieldRedefineDeclarationEntity")
			.add("entity", entity)
			.render();
		generate.LegacyLanguageRenderer.writeLine(entity, declaration);
		generate.LegacyLanguageRenderer.startBlock(entity);
		generate.LegacyLanguageRenderer.exportChildren(entity, false);
		generate.LegacyLanguageRenderer.endBlock(entity);
	}

	public static CEntityFormRedefine formRedefine(
		int line, String name, CObjectCatalog catalog,
		CBaseLanguageExporter output, CDataEntity form, boolean save)
	{
		return new CJavaFormRedefine(
			line, name, catalog, output, form, save);
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
		generate.LegacyLanguageRenderer.bind(entity, output);
		return entity;
	}

	public static CEntityGetKeyPressed getKeyPressed(
		String name, CObjectCatalog catalog, CBaseLanguageExporter output)
	{
		return new CJavaGetKeyPressed(name, catalog, output);
	}

	public static CEntityIsKeyPressed isKeyPressed()
	{
		return new CJavaIsKeyPressed();
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
		generate.LegacyLanguageRenderer.bind(entity, output);
		entity.setIdentifierFormatter(identifier -> output.FormatIdentifier(identifier));
		entity.setDeclarationRenderer(BmsJavaEntities::renderFieldOccursDeclaration);
		return entity;
	}

	/**
	 * The retired {@code CJavaFieldOccurs.DoExport} body, relocated to this generate-layer
	 * boundary. Pre-renders the {@code OCCURS} reference through the exact legacy
	 * {@code LegacyDataRenderer.renderReference} protocol (preserving its
	 * ExportReference-first-then-assembler byte parity), renders the declaration line
	 * declaratively through the recursive ST4 assembly contract
	 * ({@code recursiveFieldOccursDeclarationEntity}), then drives the {@code { ... }} block
	 * over the group's child fields — which keep rendering through the still-direct BMS field
	 * backends the surrounding {@code CJavaForm}/{@code CJavaFieldRedefine} traversal invokes.
	 */
	private static void renderFieldOccursDeclaration(CEntityFieldOccurs entity)
	{
		entity.setOccursReference(
			generate.LegacyDataRenderer.renderReference(entity.getOccurs(), entity.getLine()));
		String declaration = generate.templates.TemplateLoader.getRecursiveAssembler()
			.template("recursiveFieldOccursDeclarationEntity")
			.add("entity", entity)
			.render();
		generate.LegacyLanguageRenderer.writeLine(entity, declaration);
		generate.LegacyLanguageRenderer.startBlock(entity);
		generate.LegacyLanguageRenderer.exportChildren(entity, false);
		generate.LegacyLanguageRenderer.endBlock(entity);
	}

	public static CEntityResetKeyPressed resetKeyPressed(
		int line, CObjectCatalog catalog, CBaseLanguageExporter output)
	{
		return new CJavaResetKeyPressed(line, catalog, output);
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
		generate.LegacyLanguageRenderer.bind(entity, output);
		entity.setDeclarationRenderer(BmsJavaEntities::renderFieldArrayChildren);
		return entity;
	}

	/**
	 * The retired {@code CJavaFieldArray.DoExport} body, relocated to this generate-layer
	 * boundary. A field array emits no declaration line and no block of its own; it only drives
	 * the legacy traversal over its child motif fields — which keep rendering through the
	 * still-direct BMS field backends the surrounding {@code CJavaForm} traversal invokes.
	 */
	private static void renderFieldArrayChildren(CEntityResourceFieldArray entity)
	{
		generate.LegacyLanguageRenderer.exportChildren(entity, false);
	}
}
