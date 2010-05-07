package org.antlr.v4.codegen;

import org.antlr.v4.Tool;
import org.antlr.v4.codegen.src.OutputModelObject;
import org.antlr.v4.tool.ErrorType;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.compiler.FormalArgument;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/** Convert output model tree to template hierarchy */
public class OutputModelWalker {
	Tool tool;
	STGroup templates;
	Map<Class, String> modelToTemplateMap;

	public OutputModelWalker(Tool tool,
							 STGroup templates,
							 Map<Class, String> modelToTemplateMap)
	{
		this.tool = tool;
		this.templates = templates;
		this.modelToTemplateMap = modelToTemplateMap;
	}
	
	public ST walk(OutputModelObject omo) {
		// CREATE TEMPLATE FOR THIS OUTPUT OBJECT
		String templateName = modelToTemplateMap.get(omo.getClass());
		ST st = templates.getInstanceOf(templateName);
		if ( st.impl.formalArguments== FormalArgument.UNKNOWN ) {
			tool.errMgr.toolError(ErrorType.CODE_TEMPLATE_ARG_ISSUE, templateName, "<none>");
			return st;
		}
		// todo: chk arg-field mismtch
		Set<String> argNames = st.impl.formalArguments.keySet();
		String arg = argNames.iterator().next(); // should be just one

		// PASS IN OUTPUT OBJECT TO TEMPLATE
		st.add(arg, omo); // set template attribute of correct name

		for (String fieldName : omo.getChildren()) {
			if ( argNames.contains(fieldName) ) continue; // they won't use so don't compute
			try {
				Field fi = omo.getClass().getField(fieldName);
				Object o = fi.get(omo);
				if ( o instanceof OutputModelObject ) {
					OutputModelObject nestedOmo = (OutputModelObject)o;
					ST nestedST = walk(nestedOmo);
					st.add(fieldName, nestedST);
				}
				else if ( o instanceof Collection) {
					Collection<? extends OutputModelObject> nestedOmos = (Collection)o;
					for (OutputModelObject nestedOmo : nestedOmos) {
						ST nestedST = walk(nestedOmo);
						st.add(fieldName, nestedST);
					}
				}
				else if ( o!=null ) {
					tool.errMgr.toolError(ErrorType.CODE_TEMPLATE_ARG_ISSUE, templateName, fieldName);
				}
			}
			catch (NoSuchFieldException nsfe) {
				tool.errMgr.toolError(ErrorType.CODE_TEMPLATE_ARG_ISSUE, templateName, fieldName);
			}
			catch (IllegalAccessException iae) {
				tool.errMgr.toolError(ErrorType.CODE_TEMPLATE_ARG_ISSUE, templateName, fieldName);
			}
		}
		return st;
	}

}
