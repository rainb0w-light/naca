package generate.java.forms;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.forms.CEntityFieldArrayReference;
import semantic.forms.CEntityFieldAttribute;
import semantic.forms.CEntityFieldColor;
import semantic.forms.CEntityFieldData;
import semantic.forms.CEntityFieldFlag;
import semantic.forms.CEntityFieldHighlight;
import semantic.forms.CEntityFieldLength;
import semantic.forms.CEntityFieldOccurs;
import semantic.forms.CEntityFieldRedefine;
import semantic.forms.CEntityFieldValidated;
import semantic.forms.CEntityFormRedefine;
import semantic.forms.CEntityGetKeyPressed;
import semantic.forms.CEntityIsFieldAttribute;
import semantic.forms.CEntityIsFieldColor;
import semantic.forms.CEntityIsFieldCursor;
import semantic.forms.CEntityIsFieldFlag;
import semantic.forms.CEntityIsFieldHighlight;
import semantic.forms.CEntityIsFieldModified;
import semantic.forms.CEntityIsKeyPressed;
import semantic.forms.CEntityKeyPressed;
import semantic.forms.CEntityResetKeyPressed;
import semantic.forms.CEntityResourceField;
import semantic.forms.CEntityResourceFieldArray;
import semantic.forms.CEntityResourceForm;
import semantic.forms.CEntityResourceFormContainer;
import semantic.forms.CEntitySetAttribute;
import semantic.forms.CEntitySetColor;
import semantic.forms.CEntitySetCursor;
import semantic.forms.CEntitySetFlag;
import semantic.forms.CEntitySetHighligh;
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

	public static CEntitySetColor setColor(
		int line, CObjectCatalog catalog, CBaseLanguageExporter output,
		CDataEntity field)
	{
		return new CJavaSetColor(line, catalog, output, field);
	}

	public static CEntityFieldLength fieldLength(
		int line, String name, CObjectCatalog catalog,
		CBaseLanguageExporter output, CDataEntity field)
	{
		return new CJavaFieldLength(line, name, catalog, output, field);
	}

	public static CEntityFieldColor fieldColor(
		int line, String name, CObjectCatalog catalog,
		CBaseLanguageExporter output, CDataEntity field)
	{
		return new CJavaFieldColor(line, name, catalog, output, field);
	}

	public static CEntityFieldHighlight fieldHighlight(
		int line, String name, CObjectCatalog catalog,
		CBaseLanguageExporter output, CDataEntity field)
	{
		return new CJavaFieldHighligh(line, name, catalog, output, field);
	}

	public static CEntityFieldFlag fieldFlag(
		int line, String name, CObjectCatalog catalog,
		CBaseLanguageExporter output, CDataEntity field)
	{
		return new CJavaFieldFlag(line, name, catalog, output, field);
	}

	public static CEntitySetHighligh setHighlight(
		int line, CObjectCatalog catalog, CBaseLanguageExporter output,
		CDataEntity field)
	{
		return new CJavaSetHighlight(line, catalog, output, field);
	}

	public static CEntitySetFlag setFlag(
		int line, CObjectCatalog catalog, CBaseLanguageExporter output,
		CDataEntity field)
	{
		return new CJavaSetFlag(line, catalog, output, field);
	}

	public static CEntitySetCursor setCursor(
		int line, CObjectCatalog catalog, CBaseLanguageExporter output,
		CDataEntity field)
	{
		return new CJavaSetCursor(line, catalog, output, field);
	}

	public static CEntitySetAttribute setAttribute(
		int line, CObjectCatalog catalog, CBaseLanguageExporter output,
		CDataEntity field)
	{
		return new CJavaSetAttribute(line, catalog, output, field);
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
		return new CJavaSkipField(
			line, name, catalog, output, fields, level);
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
		return new CJavaLabelField(line, catalog, output);
	}

	public static CEntityFieldRedefine fieldRedefine(
		int line, String name, CObjectCatalog catalog,
		CBaseLanguageExporter output, String level)
	{
		return new CJavaFieldRedefine(line, name, catalog, output, level);
	}

	public static CEntityFormRedefine formRedefine(
		int line, String name, CObjectCatalog catalog,
		CBaseLanguageExporter output, CDataEntity form, boolean save)
	{
		return new CJavaFormRedefine(
			line, name, catalog, output, form, save);
	}

	public static CEntityIsFieldFlag isFieldFlag()
	{
		return new CJavaIsFieldFlag();
	}

	public static CEntityIsFieldColor isFieldColor()
	{
		return new CJavaIsFieldColor();
	}

	public static CEntityIsFieldAttribute isFieldAttribute()
	{
		return new CJavaIsFieldAttribute();
	}

	public static CEntityIsFieldHighlight isFieldHighlight(CDataEntity reference)
	{
		return new CJavaIsFieldHighlight(reference);
	}

	public static CEntityFieldValidated fieldValidated(
		int line, String name, CObjectCatalog catalog,
		CBaseLanguageExporter output, CDataEntity field)
	{
		return new CJavaFieldValidated(line, name, catalog, output, field);
	}

	public static CEntityIsFieldModified isFieldModified()
	{
		return new CJavaIsFieldModified();
	}

	public static CEntityFieldArrayReference fieldArrayReference(
		int line, CObjectCatalog catalog, CBaseLanguageExporter output)
	{
		return new CJavaFieldArrayReference(line, catalog, output);
	}

	public static CEntityKeyPressed keyPressed(
		int line, String name, CObjectCatalog catalog,
		CBaseLanguageExporter output, String caption)
	{
		return new CJavaKeyPressed(line, name, catalog, output, caption);
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
		return new CJavaFieldOccurs(line, name, catalog, output);
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
		return new CJavaFieldArray(line, name, catalog, output);
	}

	public static CEntityIsFieldCursor isFieldCursor()
	{
		return new CJavaIsFieldCursor();
	}
}
