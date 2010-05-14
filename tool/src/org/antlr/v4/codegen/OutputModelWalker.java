package org.antlr.v4.codegen;

import org.antlr.v4.Tool;
import org.antlr.v4.codegen.src.OutputModelObject;
import org.antlr.v4.tool.ErrorType;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.compiler.FormalArgument;
import org.stringtemplate.v4.misc.BlankST;

import java.lang.reflect.Field;
import java.util.*;

/** Convert output model tree to template hierarchy */
public class OutputModelWalker {
	Tool tool;
	STGroup templates;
	//Map<Class, String> modelToTemplateMap;

	public OutputModelWalker(Tool tool,
							 STGroup templates)
	{
		this.tool = tool;
		this.templates = templates;
		//this.modelToTemplateMap = modelToTemplateMap;
	}
	
	public ST walk(OutputModelObject omo) {
		// CREATE TEMPLATE FOR THIS OUTPUT OBJECT
		String templateName = omo.getClass().getSimpleName();
		if ( templateName == null ) {
			tool.errMgr.toolError(ErrorType.NO_MODEL_TO_TEMPLATE_MAPPING, omo.getClass().getSimpleName());
			return new BlankST();
		}
		ST st = templates.getInstanceOf(templateName);
		if ( st == null ) {
			tool.errMgr.toolError(ErrorType.CODE_GEN_TEMPLATES_INCOMPLETE, templateName);
			return new BlankST();
		}
		if ( st.impl.formalArguments == FormalArgument.UNKNOWN ) {
			tool.errMgr.toolError(ErrorType.CODE_TEMPLATE_ARG_ISSUE, templateName, "<none>");
			return st;
		}

		List<String> kids = omo.getChildren();

		LinkedHashMap<String,FormalArgument> formalArgs = st.impl.formalArguments;
		Set<String> argNames = formalArgs.keySet();
		Iterator<String> it = argNames.iterator();

		// PASS IN OUTPUT MODEL OBJECT TO TEMPLATE
		String modelArgName = it.next(); // ordered so this is first arg
		st.add(modelArgName, omo);

		// ENSURE TEMPLATE ARGS AND CHILD FIELDS MATCH UP
		while ( it.hasNext() ) {
			String argName = it.next();
			if ( !kids.contains(argName) ) {
				tool.errMgr.toolError(ErrorType.CODE_TEMPLATE_ARG_ISSUE, templateName, argName);
				return st;
			}
		}

		// COMPUTE STs FOR EACH NESTED MODEL OBJECT NAMED AS ARG BY TEMPLATE
		if ( kids!=null ) for (String fieldName : kids) {
			if ( !argNames.contains(fieldName) ) continue; // they won't use so don't compute
			System.out.println("computing ST for field "+fieldName+" of "+omo.getClass());
			try {
				Field fi = omo.getClass().getField(fieldName);
				Object o = fi.get(omo);
				if ( o instanceof OutputModelObject ) {  // SINGLE MODEL OBJECT?
					OutputModelObject nestedOmo = (OutputModelObject)o;
					ST nestedST = walk(nestedOmo);
					st.add(fieldName, nestedST);
				}
				else if ( o instanceof Collection || o instanceof OutputModelObject[] ) {
					// LIST OF MODEL OBJECTS?
					if ( o instanceof OutputModelObject[] ) {
						o = Arrays.asList((OutputModelObject[])o);
					}
					Collection<? extends OutputModelObject> nestedOmos = (Collection)o;
					for (OutputModelObject nestedOmo : nestedOmos) {
						if ( nestedOmo==null ) {
							System.out.println("collection has nulls: "+nestedOmos);
						}
						ST nestedST = walk(nestedOmo);
						st.add(fieldName, nestedST);
					}
				}
				else if ( o!=null ) {
					tool.errMgr.toolError(ErrorType.CODE_TEMPLATE_ARG_ISSUE, templateName, fieldName);
				}
			}
			catch (NoSuchFieldException nsfe) {
				tool.errMgr.toolError(ErrorType.CODE_TEMPLATE_ARG_ISSUE, templateName, nsfe.getMessage());
			}
			catch (IllegalAccessException iae) {
				tool.errMgr.toolError(ErrorType.CODE_TEMPLATE_ARG_ISSUE, templateName, fieldName);
			}
		}
		st.impl.dump();
		return st;
	}

}
