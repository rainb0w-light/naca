/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import utils.*;

/**
 * @author sly
 *
 */
public class CEntityClass extends CBaseLanguageEntity
{

	/**
	 * @param name
	 * @param cat
	 */
	public CEntityClass(int l, String name, CObjectCatalog cat)
	{
		super(l, name, cat);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseSemanticEntity#RegisterMySelfToCatalog()
	 */
	protected void RegisterMySelfToCatalog()
	{
		programCatalog.RegisterContainer(GetName(), this);

	}

	public String GetProgramName()
	{
		return GetName() ;
	}

	/**
	 * Bean-convention accessor for the raw source program name, used by ST4
	 * ({@code <entity.programName>}); the Java class-name formation is applied
	 * template-side by the {@code javaClassName} renderer.
	 */
	public String getProgramName()
	{
		return GetName();
	}

	/**
	 * Target-neutral program kind, derived from the program type resolved during
	 * semantic analysis. Backends map this to a runtime base type; the semantic
	 * layer carries no target class name.
	 */
	public ProgramKind getProgramKind()
	{
		CTransApplicationGroup.EProgramType type = programCatalog.getProgramType();
		switch (type)
		{
			case TYPE_BATCH:
				return ProgramKind.BATCH;
			case TYPE_CALLED:
				return ProgramKind.CALLED;
			case TYPE_INCLUDED:
				return ProgramKind.INCLUDED;
			case TYPE_MAP:
				return ProgramKind.MAP;
			case TYPE_ONLINE:
				return ProgramKind.ONLINE;
			default:
				return ProgramKind.BATCH;
		}
	}

	/**
	 * Target-neutral program capabilities, established during semantic analysis
	 * (the catalog accumulates an import declaration each time a SQL / MAP /
	 * KEY-PRESSED construct is analyzed). Read-only; returns the same set on
	 * every call and never mutates the catalog or the tree.
	 */
	public Set<ProgramCapability> getCapabilities()
	{
		Set<ProgramCapability> capabilities = new LinkedHashSet<>();
		for (int i = 0; i < programCatalog.getNbImportDeclaration(); i++)
		{
			String marker = programCatalog.getImportDeclaration(i);
			if ("SQL".equals(marker))
			{
				capabilities.add(ProgramCapability.SQL);
			}
			else if ("MAP".equals(marker))
			{
				capabilities.add(ProgramCapability.MAP_SUPPORT);
			}
			else if ("KEYPRESSED".equals(marker))
			{
				capabilities.add(ProgramCapability.KEY_PRESSED);
			}
		}
		return Collections.unmodifiableSet(capabilities);
	}

	public boolean isSql()
	{
		return getCapabilities().contains(ProgramCapability.SQL);
	}

	public boolean isMapSupport()
	{
		return getCapabilities().contains(ProgramCapability.MAP_SUPPORT);
	}

	public boolean isKeyPressed()
	{
		return getCapabilities().contains(ProgramCapability.KEY_PRESSED);
	}

	public boolean isBatchProgram()
	{
		return getProgramKind() == ProgramKind.BATCH;
	}

	public boolean isCalledProgram()
	{
		return getProgramKind() == ProgramKind.CALLED;
	}

	public boolean isIncludedProgram()
	{
		return getProgramKind() == ProgramKind.INCLUDED;
	}

	public boolean isMapProgram()
	{
		return getProgramKind() == ProgramKind.MAP;
	}

	public boolean isOnlineProgram()
	{
		return getProgramKind() == ProgramKind.ONLINE;
	}

	/**
	 * Declaration-role root children: DATA SECTIONs (including SQL cursor
	 * declaration sections). Filtered exactly like the direct
	 * active-child traversal (ignoring {@code ignore()}d nodes) and classified
	 * by semantic type, never by backend runtime type.
	 */
	public List<CBaseLanguageEntity> getDeclarationChildren()
	{
		List<CBaseLanguageEntity> declarations = new ArrayList<>();
		for (CBaseLanguageEntity child : getActiveChildren())
		{
			if (child instanceof CEntityDataSection)
			{
				declarations.add(child);
			}
		}
		return Collections.unmodifiableList(declarations);
	}

	/**
	 * Executable-role root children: PROCEDURE DIVISION / SECTION / PARAGRAPH
	 * entities ({@link CEntityProcedureDivision} and {@link CEntityProcedure},
	 * the latter covering sections). Filtered exactly like the direct
	 * active-child traversal and classified by semantic type, never by backend
	 * runtime type.
	 */
	public List<CBaseLanguageEntity> getExecutableChildren()
	{
		List<CBaseLanguageEntity> executables = new ArrayList<>();
		for (CBaseLanguageEntity child : getActiveChildren())
		{
			if (child instanceof CEntityProcedureDivision
				|| child instanceof CEntityProcedure)
			{
				executables.add(child);
			}
		}
		return Collections.unmodifiableList(executables);
	}

	/**
	 * Comment-role root children: the reserved header comments emitted at the
	 * top of the class body, before the data section. Filtered exactly like the
	 * direct exporter (ignores {@code ignore()}d nodes via
	 * {@link #getActiveChildren()}) and classified by semantic type
	 * ({@link CEntityComment}), never by backend runtime type. Order follows
	 * {@code getActiveChildren()}.
	 */
	public List<CBaseLanguageEntity> getCommentChildren()
	{
		List<CBaseLanguageEntity> comments = new ArrayList<>();
		for (CBaseLanguageEntity child : getActiveChildren())
		{
			if (child instanceof CEntityComment && !child.ignore())
			{
				comments.add(child);
			}
		}
		return Collections.unmodifiableList(comments);
	}

	public boolean ignore()
	{
		return false;
	}
}
