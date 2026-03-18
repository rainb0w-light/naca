/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 6 juin 2005
 *
 */
package utils.CobolTranscoder;

import java.util.Hashtable;
import java.util.Vector;

import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CEntityProcedure;
import semantic.CEntityProcedureDivision;
import semantic.CEntityProcedureSection;
import semantic.CProcedureReference;
import semantic.Verbs.CEntityCallFunction;
import semantic.Verbs.CEntityGoto;
import semantic.Verbs.CEntityReturn;
import utils.Transcoder;
import utils.NacaTransAssertException;

/**
 * @author U930CV
 *
 */
public class ProcedureCallTree
{
	private class BaseNode
	{
		boolean bExplicitCallByGoto = false ;
		boolean bExplicitCallAsProcedure = false ;
		boolean bImplicitCall = false ;
		boolean bHasExplicitGetOut = false ;
		Vector<CEntityCallFunction> arrProcedureCallRef = new Vector<CEntityCallFunction>() ;
		Vector<CEntityGoto> arrGotoRef = new Vector<CEntityGoto>() ;
		Vector<BaseNode> arrProcedureCallNode = new Vector<BaseNode>() ;
		Vector<BaseNode> arrGotoNode = new Vector<BaseNode>() ;
		public boolean isCalled()
		{
			return bExplicitCallByGoto || bExplicitCallAsProcedure || bImplicitCall ;
		}
	}
	
	private class RootNodeDivision extends BaseNode
	{
		public RootNodeDivision()
		{
			bImplicitCall = true ;
		}
		public CEntityProcedureDivision div = null ;
		public Vector<NodeSection> arrSections = new Vector<NodeSection>() ;
		public Vector<NodeProcedure> arrProcedures = new Vector<NodeProcedure>() ;
		public Vector<CProcedureReference> arrGlobalGotoRef = new Vector<CProcedureReference>() ;
	}
	private class NodeSection extends BaseNode
	{
		public NodeSection(CEntityProcedureSection div)
		{
			sec = div ;
		}
		public CEntityProcedureSection sec = null ;
		public Vector<NodeProcedure> arrProcedures = new Vector<NodeProcedure>() ;
		/**
		 * @return
		 */
	}
	private class NodeProcedure extends BaseNode
	{
		public NodeProcedure(CEntityProcedure div)
		{
			proc = div ;
		}
		public CEntityProcedure proc = null ;
	}
	
	/**
	 * @param division
	 */
	public void SetProcedureDivision(CEntityProcedureDivision division)
	{
		root.div = division ;
	}
	
	protected RootNodeDivision root = new RootNodeDivision() ;
	protected NodeSection currentNodeSection = null ;
	protected NodeProcedure currentNodeProcedure = null ;
	protected Hashtable<CEntityProcedure, BaseNode> tabProcedureNodes = new Hashtable<CEntityProcedure, BaseNode>() ;
	
	/**
	 * @param cont
	 */
	public void RegisterProcedure(CEntityProcedure cont)
	{
		NodeProcedure node = new NodeProcedure(cont) ;
		tabProcedureNodes.put(cont, node) ;
		currentNodeProcedure = node ;
		if (currentNodeSection != null)
		{
			currentNodeSection.arrProcedures.add(node) ;
		}
		else
		{
			root.arrProcedures.add(node) ;
		}
	}

	/**
	 * @param sec
	 */
	public void RegisterSection(CEntityProcedureSection sec)
	{
		NodeSection node = new NodeSection(sec) ;
		tabProcedureNodes.put(sec, node) ;
		currentNodeSection = node ;
		currentNodeProcedure = null ;
		root.arrSections.add(node) ;
	}

	/**
	 * @param ref
	 */
	public void RegisterGoto(CEntityGoto ref)
	{
		if (currentNodeProcedure != null)
		{
			currentNodeProcedure.arrGotoRef.addElement(ref) ;
		}
		else if (currentNodeSection != null)
		{
			currentNodeSection.arrGotoRef.addElement(ref) ;
		}
		else
		{
			root.arrGotoRef.addElement(ref) ;
		}
	}

	/**
	 * @param ref
	 */
	public void RegisterProcedureCall(CEntityCallFunction ref)
	{
		if (currentNodeProcedure != null)
		{
			currentNodeProcedure.arrProcedureCallRef.addElement(ref) ;
		}
		else if (currentNodeSection != null)
		{
			currentNodeSection.arrProcedureCallRef.addElement(ref) ;
		}
		else
		{
			root.arrProcedureCallRef.addElement(ref) ;
		}
	}

	/**
	 * 
	 */
	public void ComputeTree()
	{
		if (root.div == null)
		{
			return ;
		}
		// compute all the tree : analyse calls in each procedure
		ComputeNodeCalls(root) ;
		for (int i=0; i<root.arrProcedures.size(); i++)
		{
			NodeProcedure node = root.arrProcedures.get(i) ;
			ComputeNodeCalls(node) ;
		}
		for (int i=0; i<root.arrSections.size(); i++)
		{
			NodeSection node = root.arrSections.get(i) ;
			ComputeNodeCalls(node) ;
			for (int j=0; j<node.arrProcedures.size(); j++)
			{
				NodeProcedure nodeP = node.arrProcedures.get(j) ;
				ComputeNodeCalls(nodeP) ;
			}
		}
		
		// analyse status of each procedure
		boolean bProcedureDivisionFinished = root.div.hasExplicitGetout() ;
		root.bHasExplicitGetOut = bProcedureDivisionFinished ;
		
		boolean bPrecedentFinished = bProcedureDivisionFinished ;
		for (int i=0; i<root.arrProcedures.size(); i++)
		{
			NodeProcedure node = root.arrProcedures.get(i) ;
			String nameP = node.proc.GetName() ;
			node.bImplicitCall = !bPrecedentFinished ;
			if (node.bImplicitCall)
			{
				Transcoder.logDebug("Procedure implicitly called : "+nameP) ;
			}
			node.bHasExplicitGetOut = node.proc.hasExplicitGetOut() ;

			bPrecedentFinished = node.bExplicitCallAsProcedure || node.bHasExplicitGetOut ;
		}
		
		for (int i=0; i<root.arrSections.size(); i++)
		{
			NodeSection node = root.arrSections.get(i) ;
			String name = node.sec.GetName() ;
			node.bImplicitCall = !bPrecedentFinished ;
			if (node.bImplicitCall)
			{
				Transcoder.logDebug("Section implicitly called : "+name) ;
			}
			node.bHasExplicitGetOut = node.sec.hasExplicitGetOut() ;
			
			boolean bPrecedentParagraphFinished = !(node.isCalled() && !node.bHasExplicitGetOut) ;
			for (int j=0; j<node.arrProcedures.size(); j++)
			{
				NodeProcedure nodeP = node.arrProcedures.get(j) ;
				String nameP = nodeP.proc.GetName() ;
				nodeP.bImplicitCall = !bPrecedentParagraphFinished ;
				if (nodeP.bImplicitCall)
				{
					Transcoder.logDebug("Procedure implicitly called : "+nameP) ;
				}
				nodeP.bHasExplicitGetOut = nodeP.proc.hasExplicitGetOut() ;

				bPrecedentParagraphFinished = nodeP.bExplicitCallAsProcedure || nodeP.bHasExplicitGetOut ;
			}

			bPrecedentFinished = node.bExplicitCallAsProcedure || !node.isCalled() || node.bHasExplicitGetOut  ;
			
			
		}
		
	}

	/**
	 * @param root
	 */
	private void ComputeNodeCalls(RootNodeDivision node)
	{
		ComputeNodeCalls((BaseNode)node) ;
		for (int i=0; i<node.arrGlobalGotoRef.size(); i++)
		{
			CProcedureReference ref = node.arrGlobalGotoRef.get(i) ;
			CEntityProcedure proc = ref.getProcedure() ;
			BaseNode n = tabProcedureNodes.get(proc) ;
			n.bExplicitCallByGoto = true ;
		}
	}

	/**
	 * @param root
	 */
	private void ComputeNodeCalls(BaseNode node)
	{
		for (int i=0; i<node.arrGotoRef.size(); i++)
		{
			CEntityGoto gto = node.arrGotoRef.get(i);
			CProcedureReference ref = gto.getReference() ;
			CEntityProcedure proc = ref.getProcedure() ;
			if (proc != null)
			{
				BaseNode p = tabProcedureNodes.get(proc) ;
				if (p!=null)
				{
					p.bExplicitCallByGoto = true ;
					node.arrGotoNode.add(p) ;
				}
			}
		}
		for (int i=0; i<node.arrProcedureCallRef.size(); i++)
		{
			CEntityCallFunction call = node.arrProcedureCallRef.get(i);
			CProcedureReference ref = call.getReference() ; 
			CEntityProcedure proc = ref.getProcedure() ;
			if (proc != null)
			{
				BaseNode p = tabProcedureNodes.get(proc) ;
				if (p!=null)
				{
					p.bExplicitCallAsProcedure = true ;
					node.arrProcedureCallNode.add(p) ;
				}
			}
		}
	}

	/**
	 * 
	 */
	public void DoFilterSections(CBaseEntityFactory factory)
	{
		for (int i=0; i<root.arrProcedures.size(); i++)
		{
			NodeProcedure nodeP = root.arrProcedures.get(i) ;
			String nameP = nodeP.proc.GetName() ;
			if (!nodeP.isCalled())
			{
				// this procedure is never called...
				Transcoder.logDebug("Procedure ignored (4) : "+nameP) ;
				nodeP.proc.setIgnore() ;
			}
			else if (nodeP.proc.isEmpty())
			{
				// this procedure do nothing...
				Transcoder.logDebug("Procedure ignored (5) : "+nameP) ;
				nodeP.proc.setIgnore() ;
			}
			else if (i==0 && nodeP.bImplicitCall && nodeP.bHasExplicitGetOut)
			{
				if (root.div.getProcedureBloc() != null)
				{
					CEntityCallFunction ePerform = factory.NewEntityCallFunction(0, nameP, "", null) ;
					root.div.getProcedureBloc().AddChild(ePerform) ;
					Transcoder.logDebug("Perform to "+nameP+" added to procedure division") ;
					nodeP.bImplicitCall = false ;
					nodeP.bExplicitCallAsProcedure = true ;
				}
			}
		}
		
		boolean bAllSectionsAreReduced = true ;  // flag to tell is all sections before the current one have been reduced ;
												// if so, we can reduce current one, else we can't
		for (int i=0; i<root.arrSections.size(); i++)
		{
			NodeSection node = root.arrSections.get(i) ;
			String name = node.sec.GetName() ;
			
			boolean bIgnoreAllProcedures = true ;
			boolean bCanReduceCurrentSection = true ;  // flag to tell if current section can be reduced :
						// -> no procedure in it, or procedures can be ignored (never called or empty)
						// -> no implicit call between procedures, and all procedures are called by perform : section can be reduce to procedure
			int nbValidProcedures = 0 ;
			NodeProcedure lastValidProcedure = null ;
			for (int j=0; j<node.arrProcedures.size(); j++)
			{
				NodeProcedure nodeP = node.arrProcedures.get(j) ;
				String nameP = nodeP.proc.GetName() ;
				if (!nodeP.isCalled())
				{
					// this procedure is never called...
 					Transcoder.logDebug("Procedure ignored (3) : "+nameP) ;
					nodeP.proc.setIgnore() ;
				}
				else if (nodeP.proc.isEmpty())
				{
					// this procedure do nothing...
					if (nodeP.bImplicitCall && !nodeP.bExplicitCallAsProcedure && !nodeP.bExplicitCallByGoto)
					{
						// simply ignore current procedure
						Transcoder.logDebug("Procedure ignored (2) : "+nameP) ;
						nodeP.proc.setIgnore() ;
					}
					else if (nodeP.bExplicitCallAsProcedure)
					{
						// simply ignore current procedure, call will be ignore too
						Transcoder.logDebug("Procedure ignored (1) : "+nameP) ;
						nodeP.proc.setIgnore() ;
					}
					else if (nodeP.bExplicitCallByGoto)
					{
						if (j==node.arrProcedures.size()-1  && node.bExplicitCallAsProcedure)
						{ // if this is the last procedure => EXIT procedure
							for (int k=0; k<node.arrGotoRef.size(); k++)
							{
								CEntityGoto gto = node.arrGotoRef.get(k) ;
//								Vector v = gto.GetParent().GetListOfChildren() ;
//								int index = v.size()-1  ;
//								if (v.get(index) == gto)
//								{
//									CEntityNoAction ret = factory.NewEntityNoAction(gto.GetLine()) ;
//									gto.Replace(ret) ;
//									ret.SetParent(gto.GetParent()) ;
//								}
//								else
//								{
									CEntityReturn ret = factory.NewEntityReturn(gto.getLine()) ;
									ret.SetOnlyReturnFromProcedure() ;
									gto.Replace(ret) ;
									ret.SetParent(gto.GetParent()) ;
//								}
							}
							Transcoder.logDebug("Procedure ignored (6) : "+nameP) ;
							nodeP.proc.setIgnore() ;
						}
						else
						{ // change GOTO to the next procedure
							NodeProcedure nextnode = node.arrProcedures.get(j+1) ;
							throw new NacaTransAssertException("unmanaged situation") ;
						}
					}
				}
				else if (!nodeP.bExplicitCallAsProcedure && !nodeP.bExplicitCallByGoto && nodeP.bImplicitCall)
				{  // in this case, the procedure is never called by itself, and can be suppressed, its content added to the previous procedure.
					if (nbValidProcedures == 0)
					{
						CBaseLanguageEntity[] lst = nodeP.proc.GetChildrenList(null, null) ;
						if (node.sec.getSectionBloc() == null)
						{
							node.sec.SetSectionBloc(factory.NewEntityBloc(node.sec.getLine())) ;
						}
						for (int k=0; k<lst.length; k++)
						{
							CBaseLanguageEntity le = lst[k] ;
							node.sec.getSectionBloc().AddChild(le) ;
						}
						Transcoder.logDebug("Procedure "+nameP+" merged into "+name) ;
						nodeP.proc.setIgnore() ;
					}
					else
					{
						throw new NacaTransAssertException("unmanaged situation") ;
					}
				}
				else if (nodeP.bExplicitCallAsProcedure && !nodeP.bExplicitCallByGoto && !nodeP.bImplicitCall)
				{  // in this case, the section can be reduced, because the procedure doesn't need a section and can be alone in the programme.
					bIgnoreAllProcedures = false ;
					lastValidProcedure = nodeP ;
					nbValidProcedures ++;
				}
				else
				{
					nbValidProcedures ++;
					lastValidProcedure = nodeP ;
					bIgnoreAllProcedures = false ;
					bCanReduceCurrentSection = false ;
				}
			}			
			
			if (!node.isCalled())
			{
				// this section is never called...
				if (bIgnoreAllProcedures)
				{	// all of the procedures are never called
					node.sec.setIgnore() ;
					Transcoder.logDebug("Section ignored : "+name) ;
				}
				else if (bAllSectionsAreReduced)
				{ // remove section object, and leave all procedures alone in program
					//CBaseTranscoder.logInfo("Section reduced : "+name) ;
					node.sec.ReduceToProcedure() ;
				}
				else if (bCanReduceCurrentSection)
				{
					//CBaseTranscoder.logInfo("Section reduced : "+name) ;
					node.sec.ReduceToProcedure() ;
				}
				else if (bAllSectionsAreReduced)
				{
					Transcoder.logDebug("Section not reduced : "+name) ;
					bAllSectionsAreReduced = false ;
				}
			}
			else if (bCanReduceCurrentSection && bAllSectionsAreReduced)
			{
				//CBaseTranscoder.logInfo("Section reduced : "+name) ;
				node.sec.ReduceToProcedure() ;
			}
			else if (nbValidProcedures == 1)
			{
				if (node.isCalled() && !lastValidProcedure.isCalled())
				{
					String cs = node.sec.GetName() ;
					node.sec.Rename("$"+cs+"$") ;
					lastValidProcedure.proc.Rename(cs) ;
					node.sec.ReduceToProcedure() ;
				}
				else if (node.isCalled() && lastValidProcedure.bImplicitCall && lastValidProcedure.bExplicitCallAsProcedure)
				{
					String nameP = lastValidProcedure.proc.GetName() ;
					CEntityCallFunction ePerform = factory.NewEntityCallFunction(0, nameP, "", node.sec) ;
					node.sec.getSectionBloc().AddChild(ePerform) ;
					Transcoder.logDebug("Section reduced : "+name+" and perform to "+nameP+" added") ;
					node.sec.ReduceToProcedure() ;
				}
				else
				{
					Transcoder.logDebug("Section not reduced : "+name) ;
					bAllSectionsAreReduced = false ;
				}
			}
			else if (bAllSectionsAreReduced)
			{
				Transcoder.logDebug("Section not reduced : "+name) ;
				bAllSectionsAreReduced = false ;
			}
		}
	}

	/**
	 * @param ref
	 */
	public void RegisterGlobalGoto(CProcedureReference ref)
	{
		root.arrGlobalGotoRef.add(ref) ;
	}
}
