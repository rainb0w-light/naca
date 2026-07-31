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
		generate.LegacyLanguageRenderer.bind(entity, output);
		entity.setIdentifierFormatter(identifier -> output.FormatIdentifier(identifier));
		entity.setContainerRenderer(BmsJavaEntities::renderFormContainerDeclaration);
		return entity;
	}

	/**
	 * The retired {@code CJavaFormContainer.DoExport} body, relocated to this generate-layer boundary.
	 * Reproduces the legacy mapset class skeleton byte-for-byte: the imports, the
	 * {@code class <name> extends Map { } header (rendered declaratively through the recursive ST4
	 * assembly contract {@code recursiveFormContainerDeclarationEntity}, reading only
	 * {@code entity.mapClassName} — the RAW {@code GetName()}, which the retired backend used for the
	 * class name without formatting), the two {@code Copy} factory methods and the constructor (fixed
	 * boilerplate over the raw class name), then the class-body {@code { ... }} block driven over
	 * {@code entity.getForms()} via the legacy {@code invokeExport} traversal — exactly the
	 * {@code arrForm} collection the retired backend iterated (the maps live in {@code arrForm},
	 * populated via {@code AddForm}, not the generic child list). The maps keep rendering through
	 * their own ST4 bindings/backends.
	 */
	private static void renderFormContainerDeclaration(CEntityResourceFormContainer entity)
	{
		generate.LegacyLanguageRenderer.writeEol(entity);
		generate.LegacyLanguageRenderer.writeLine(entity, "import nacaLib.mapSupport.* ;");
		generate.LegacyLanguageRenderer.writeLine(entity, "import nacaLib.varEx.* ;");
		generate.LegacyLanguageRenderer.writeLine(entity, "import nacaLib.program.* ;");
		generate.LegacyLanguageRenderer.writeLine(entity, "import nacaLib.basePrgEnv.* ;");
		generate.LegacyLanguageRenderer.writeEol(entity);
		String header = generate.templates.TemplateLoader.getRecursiveAssembler()
			.template("recursiveFormContainerDeclarationEntity")
			.add("entity", entity)
			.render();
		generate.LegacyLanguageRenderer.writeLine(entity, header);
		generate.LegacyLanguageRenderer.startBlock(entity);

		String name = entity.getMapClassName();
		generate.LegacyLanguageRenderer.writeLine(entity, "static "+name+" Copy(BaseProgram program) {");
		generate.LegacyLanguageRenderer.startBlock(entity);
		generate.LegacyLanguageRenderer.writeLine(entity, "return new "+name+"(program);");
		generate.LegacyLanguageRenderer.endBlock(entity);
		generate.LegacyLanguageRenderer.writeLine(entity, "}");

		generate.LegacyLanguageRenderer.writeLine(entity, "static "+name+" Copy(BaseProgram program, CopyReplacing rep)  {");
		generate.LegacyLanguageRenderer.startBlock(entity);
		generate.LegacyLanguageRenderer.writeLine(entity, "Assert(\"Unimplemented replacing for MAPs\") ;");
		generate.LegacyLanguageRenderer.writeLine(entity, "return null ;");
		generate.LegacyLanguageRenderer.endBlock(entity);
		generate.LegacyLanguageRenderer.writeLine(entity, "}");

		generate.LegacyLanguageRenderer.writeLine(entity, ""+name+"(BaseProgram program) {");
		generate.LegacyLanguageRenderer.startBlock(entity);
		generate.LegacyLanguageRenderer.writeLine(entity, "super(program);");
		generate.LegacyLanguageRenderer.endBlock(entity);
		generate.LegacyLanguageRenderer.writeLine(entity, "}");
		generate.LegacyLanguageRenderer.writeLine(entity, "");

		for (semantic.forms.CEntityResourceForm form : entity.getForms())
		{
			generate.LegacyLanguageRenderer.invokeExport(form);
		}
		generate.LegacyLanguageRenderer.writeLine(entity, "");

		generate.LegacyLanguageRenderer.endBlock(entity);
		generate.LegacyLanguageRenderer.writeLine(entity, "}");
		generate.LegacyLanguageRenderer.writeLine(entity, "");
		generate.LegacyLanguageRenderer.writeLine(entity, "");
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
		generate.LegacyLanguageRenderer.bind(entity, output);
		entity.setIdentifierFormatter(identifier -> output.FormatIdentifier(identifier));
		entity.setDeclarationRenderer(BmsJavaEntities::renderFormDeclaration);
		return entity;
	}

	/**
	 * The retired {@code CJavaForm.DoExport} body, relocated to this generate-layer boundary.
	 * Renders the declaration line declaratively through the recursive ST4 assembly contract
	 * ({@code recursiveFormDeclarationEntity}), then drives the {@code { ... }} block over the
	 * form's fields. The form's fields live in {@code arrFields} (populated via
	 * {@code CEntityResourceForm.AddField}), not in the generic child list, so this drives the
	 * legacy {@code invokeExport} traversal over {@code entity.getFields()} — exactly the collection
	 * the retired backend iterated — and the fields keep rendering through the still-direct BMS
	 * field backends.
	 */
	private static void renderFormDeclaration(CEntityResourceForm entity)
	{
		String declaration = generate.templates.TemplateLoader.getRecursiveAssembler()
			.template("recursiveFormDeclarationEntity")
			.add("entity", entity)
			.render();
		generate.LegacyLanguageRenderer.writeLine(entity, declaration);
		generate.LegacyLanguageRenderer.startBlock(entity);
		for (semantic.CBaseResourceEntity field : entity.getFields())
		{
			generate.LegacyLanguageRenderer.invokeExport(field);
		}
		generate.LegacyLanguageRenderer.endBlock(entity);
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
		generate.LegacyLanguageRenderer.bind(entity, output);
		entity.setReferenceRenderer((ref, l) -> generate.LegacyDataRenderer.renderReference(ref, l));
		return entity;
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
		generate.LegacyLanguageRenderer.bind(entity, output);
		entity.setIdentifierFormatter(identifier -> output.FormatIdentifier(identifier));
		entity.setDeclarationRenderer(BmsJavaEntities::renderFormRedefineDeclaration);
		return entity;
	}

	/**
	 * The retired {@code CJavaFormRedefine.DoExport} body, relocated to this generate-layer
	 * boundary. Pre-renders the {@code redefinesMap(<ref>)} origin form reference through the
	 * exact legacy {@code LegacyDataRenderer.renderReference} protocol (preserving byte parity),
	 * renders the declaration line declaratively through the recursive ST4 assembly contract
	 * ({@code recursiveFormRedefineDeclarationEntity}), then drives the {@code { ... }} block
	 * over the form redefine's children — which keep rendering through the still-direct BMS field
	 * backends the surrounding {@code CJavaFormContainer} traversal invokes.
	 *
	 * <p>Accepts {@code CEntityResourceForm} to match the inherited
	 * {@code Consumer<CEntityResourceForm>} declaration-renderer type; the factory only installs
	 * this on {@link CEntityFormRedefine} instances.
	 */
	private static void renderFormRedefineDeclaration(CEntityResourceForm rawEntity)
	{
		CEntityFormRedefine entity = (CEntityFormRedefine) rawEntity;
		entity.setRedefinesReference(
			generate.LegacyDataRenderer.renderReference(entity.getForm(), entity.getLine()));
		String declaration = generate.templates.TemplateLoader.getRecursiveAssembler()
			.template("recursiveFormRedefineDeclarationEntity")
			.add("entity", entity)
			.render();
		generate.LegacyLanguageRenderer.writeLine(entity, declaration);
		generate.LegacyLanguageRenderer.startBlock(entity);
		generate.LegacyLanguageRenderer.exportChildren(entity, false);
		generate.LegacyLanguageRenderer.endBlock(entity);
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
		// builds the pure semantic entity, rendered by recursiveGetKeyPressedEntity
		// (binding in semantic-runtime-bindings.properties). The exporter bind preserves
		// the retired backend constructor's LegacyLanguageRenderer.bind side effect for
		// the BMS traversal compatibility boundary.
		CEntityGetKeyPressed entity = new CEntityGetKeyPressed(name, catalog);
		generate.LegacyLanguageRenderer.bind(entity, output);
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
		generate.LegacyLanguageRenderer.bind(entity, output);
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
