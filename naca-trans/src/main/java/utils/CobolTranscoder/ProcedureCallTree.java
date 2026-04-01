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
		boolean isexplicitCallByGoto = false ;
		boolean isexplicitCallAsProcedure = false ;
		boolean isimplicitCall = false ;
		boolean ishasExplicitGetOut = false ;
		Vector<CEntityCallFunction> procedureCallRef = new Vector<CEntityCallFunction>() ;
		Vector<CEntityGoto> gotoRef = new Vector<CEntityGoto>() ;
		Vector<BaseNode> procedureCallNode = new Vector<BaseNode>() ;
		Vector<BaseNode> gotoNode = new Vector<BaseNode>() ;
		public boolean isCalled()
		{
			return isexplicitCallByGoto || isexplicitCallAsProcedure || isimplicitCall;
		}
	}
	
	private class RootNodeDivision extends BaseNode
	{
		public RootNodeDivision()
		{
			isimplicitCall = true ;
		}
		public CEntityProcedureDivision div = null ;
		public Vector<NodeSection> sections = new Vector<NodeSection>() ;
		public Vector<NodeProcedure> procedures = new Vector<NodeProcedure>() ;
		public Vector<CProcedureReference> globalGotoRef = new Vector<CProcedureReference>() ;
	}
	private class NodeSection extends BaseNode
	{
		public NodeSection(CEntityProcedureSection div)
		{
			sec = div ;
		}
		public CEntityProcedureSection sec = null ;
		public Vector<NodeProcedure> procedures = new Vector<NodeProcedure>() ;
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
			currentNodeSection.procedures.add(node) ;
		}
		else
		{
			root.procedures.add(node) ;
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
		root.sections.add(node) ;
	}

	/**
	 * @param ref
	 */
	public void RegisterGoto(CEntityGoto ref)
	{
		if (currentNodeProcedure != null)
		{
			currentNodeProcedure.gotoRef.addElement(ref) ;
		}
		else if (currentNodeSection != null)
		{
			currentNodeSection.gotoRef.addElement(ref) ;
		}
		else
		{
			root.gotoRef.addElement(ref) ;
		}
	}

	/**
	 * @param ref
	 */
	public void RegisterProcedureCall(CEntityCallFunction ref)
	{
		if (currentNodeProcedure != null)
		{
			currentNodeProcedure.procedureCallRef.addElement(ref) ;
		}
		else if (currentNodeSection != null)
		{
			currentNodeSection.procedureCallRef.addElement(ref) ;
		}
		else
		{
			root.procedureCallRef.addElement(ref) ;
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
		for (int i = 0; i<root.procedures.size(); i++)
		{
			NodeProcedure node = root.procedures.get(i) ;
			ComputeNodeCalls(node) ;
		}
		for (int i = 0; i<root.sections.size(); i++)
		{
			NodeSection node = root.sections.get(i) ;
			ComputeNodeCalls(node) ;
			for (int j = 0; j<node.procedures.size(); j++)
			{
				NodeProcedure nodeP = node.procedures.get(j) ;
				ComputeNodeCalls(nodeP) ;
			}
		}
		
		// analyse status of each procedure
		boolean isprocedureDivisionFinished = root.div.hasExplicitGetout() ;
		root.ishasExplicitGetOut = isprocedureDivisionFinished;
		
		boolean isprecedentFinished = isprocedureDivisionFinished;
		for (int i = 0; i<root.procedures.size(); i++)
		{
			NodeProcedure node = root.procedures.get(i) ;
			String nameP = node.proc.GetName() ;
			node.isimplicitCall = !isprecedentFinished;
			if (node.isimplicitCall)
			{
				Transcoder.logDebug("Procedure implicitly called : "+nameP) ;
			}
			node.ishasExplicitGetOut = node.proc.hasExplicitGetOut() ;

			isprecedentFinished = node.isexplicitCallAsProcedure || node.ishasExplicitGetOut;
		}
		
		for (int i = 0; i<root.sections.size(); i++)
		{
			NodeSection node = root.sections.get(i) ;
			String name = node.sec.GetName() ;
			node.isimplicitCall = !isprecedentFinished;
			if (node.isimplicitCall)
			{
				Transcoder.logDebug("Section implicitly called : "+name) ;
			}
			node.ishasExplicitGetOut = node.sec.hasExplicitGetOut() ;
			
			boolean isprecedentParagraphFinished = !(node.isCalled() && !node.ishasExplicitGetOut) ;
			for (int j = 0; j<node.procedures.size(); j++)
			{
				NodeProcedure nodeP = node.procedures.get(j) ;
				String nameP = nodeP.proc.GetName() ;
				nodeP.isimplicitCall = !isprecedentParagraphFinished;
				if (nodeP.isimplicitCall)
				{
					Transcoder.logDebug("Procedure implicitly called : "+nameP) ;
				}
				nodeP.ishasExplicitGetOut = nodeP.proc.hasExplicitGetOut() ;

				isprecedentParagraphFinished = nodeP.isexplicitCallAsProcedure || nodeP.ishasExplicitGetOut;
			}

			isprecedentFinished = node.isexplicitCallAsProcedure || !node.isCalled() || node.ishasExplicitGetOut;
			
			
		}
		
	}

	/**
	 * @param root
	 */
	private void ComputeNodeCalls(RootNodeDivision node)
	{
		ComputeNodeCalls((BaseNode)node) ;
		for (int i = 0; i<node.globalGotoRef.size(); i++)
		{
			CProcedureReference ref = node.globalGotoRef.get(i) ;
			CEntityProcedure proc = ref.getProcedure() ;
			BaseNode n = tabProcedureNodes.get(proc) ;
			n.isexplicitCallByGoto = true ;
		}
	}

	/**
	 * @param root
	 */
	private void ComputeNodeCalls(BaseNode node)
	{
		for (int i = 0; i<node.gotoRef.size(); i++)
		{
			CEntityGoto gto = node.gotoRef.get(i);
			CProcedureReference ref = gto.getReference() ;
			CEntityProcedure proc = ref.getProcedure() ;
			if (proc != null)
			{
				BaseNode p = tabProcedureNodes.get(proc) ;
				if (p!=null)
				{
					p.isexplicitCallByGoto = true ;
					node.gotoNode.add(p) ;
				}
			}
		}
		for (int i = 0; i<node.procedureCallRef.size(); i++)
		{
			CEntityCallFunction call = node.procedureCallRef.get(i);
			CProcedureReference ref = call.getReference() ; 
			CEntityProcedure proc = ref.getProcedure() ;
			if (proc != null)
			{
				BaseNode p = tabProcedureNodes.get(proc) ;
				if (p!=null)
				{
					p.isexplicitCallAsProcedure = true ;
					node.procedureCallNode.add(p) ;
				}
			}
		}
	}

	/**
	 * 
	 */
	public void DoFilterSections(CBaseEntityFactory factory)
	{
		for (int i = 0; i<root.procedures.size(); i++)
		{
			NodeProcedure nodeP = root.procedures.get(i) ;
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
			else if (i==0 && nodeP.isimplicitCall && nodeP.ishasExplicitGetOut)
			{
				if (root.div.getProcedureBloc() != null)
				{
					CEntityCallFunction ePerform = factory.NewEntityCallFunction(0, nameP, "", null) ;
					root.div.getProcedureBloc().AddChild(ePerform) ;
					Transcoder.logDebug("Perform to "+nameP+" added to procedure division") ;
					nodeP.isimplicitCall = false ;
					nodeP.isexplicitCallAsProcedure = true ;
				}
			}
		}
		
		boolean isallSectionsAreReduced = true ;  // flag to tell is all sections before the current one have been reduced ;
												// if so, we can reduce current one, else we can't
		for (int i = 0; i<root.sections.size(); i++)
		{
			NodeSection node = root.sections.get(i) ;
			String name = node.sec.GetName() ;
			
			boolean isignoreAllProcedures = true ;
			boolean iscanReduceCurrentSection = true ;  // flag to tell if current section can be reduced :
						// -> no procedure in it, or procedures can be ignored (never called or empty)
						// -> no implicit call between procedures, and all procedures are called by perform : section can be reduce to procedure
			int nbValidProcedures = 0 ;
			NodeProcedure lastValidProcedure = null ;
			for (int j = 0; j<node.procedures.size(); j++)
			{
				NodeProcedure nodeP = node.procedures.get(j) ;
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
					if (nodeP.isimplicitCall && !nodeP.isexplicitCallAsProcedure && !nodeP.isexplicitCallByGoto)
					{
						// simply ignore current procedure
						Transcoder.logDebug("Procedure ignored (2) : "+nameP) ;
						nodeP.proc.setIgnore() ;
					}
					else if (nodeP.isexplicitCallAsProcedure)
					{
						// simply ignore current procedure, call will be ignore too
						Transcoder.logDebug("Procedure ignored (1) : "+nameP) ;
						nodeP.proc.setIgnore() ;
					}
					else if (nodeP.isexplicitCallByGoto)
					{
						if (j==node.procedures.size()-1  && node.isexplicitCallAsProcedure)
						{ // if this is the last procedure => EXIT procedure
							for (int k = 0; k<node.gotoRef.size(); k++)
							{
								CEntityGoto gto = node.gotoRef.get(k) ;
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
							NodeProcedure nextnode = node.procedures.get(j+1) ;
							throw new NacaTransAssertException("unmanaged situation") ;
						}
					}
				}
				else if (!nodeP.isexplicitCallAsProcedure && !nodeP.isexplicitCallByGoto && nodeP.isimplicitCall)
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
				else if (nodeP.isexplicitCallAsProcedure && !nodeP.isexplicitCallByGoto && !nodeP.isimplicitCall)
				{  // in this case, the section can be reduced, because the procedure doesn't need a section and can be alone in the programme.
					isignoreAllProcedures = false ;
					lastValidProcedure = nodeP ;
					nbValidProcedures ++;
				}
				else
				{
					nbValidProcedures ++;
					lastValidProcedure = nodeP ;
					isignoreAllProcedures = false ;
					iscanReduceCurrentSection = false ;
				}
			}			
			
			if (!node.isCalled())
			{
				// this section is never called...
				if (isignoreAllProcedures)
				{	// all of the procedures are never called
					node.sec.setIgnore() ;
					Transcoder.logDebug("Section ignored : "+name) ;
				}
				else if (isallSectionsAreReduced)
				{ // remove section object, and leave all procedures alone in program
					//CBaseTranscoder.logInfo("Section reduced : "+name) ;
					node.sec.ReduceToProcedure() ;
				}
				else if (iscanReduceCurrentSection)
				{
					//CBaseTranscoder.logInfo("Section reduced : "+name) ;
					node.sec.ReduceToProcedure() ;
				}
				else if (isallSectionsAreReduced)
				{
					Transcoder.logDebug("Section not reduced : "+name) ;
					isallSectionsAreReduced = false ;
				}
			}
			else if (iscanReduceCurrentSection && isallSectionsAreReduced)
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
				else if (node.isCalled() && lastValidProcedure.isimplicitCall && lastValidProcedure.isexplicitCallAsProcedure)
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
					isallSectionsAreReduced = false ;
				}
			}
			else if (isallSectionsAreReduced)
			{
				Transcoder.logDebug("Section not reduced : "+name) ;
				isallSectionsAreReduced = false ;
			}
		}
	}

	/**
	 * @param ref
	 */
	public void RegisterGlobalGoto(CProcedureReference ref)
	{
		root.globalGotoRef.add(ref) ;
	}
}
