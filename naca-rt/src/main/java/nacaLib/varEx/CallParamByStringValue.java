/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

/**
 * @author U930DI
 *
 */
public class CallParamByStringValue extends CCallParam
{
 /** Creates a new call param by string value instance. */
 public CallParamByStringValue(String cs)
 {
  this.cs = cs;
 }

 /** Returns the param length. */
 public int getParamLength()
 {
     if (cs != null) {
         return cs.length();
     }
  return 0;
 }

 /** Executes the map on operation. */
 public void MapOn(Var varLinkageSection)
 {
  varLinkageSection.set(cs);
 }

 public Var getCallerSourceVar()
 {
  return null;
 }

 private String cs = null;
}
