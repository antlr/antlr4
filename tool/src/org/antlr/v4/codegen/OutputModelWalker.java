package org.antlr.v4.codegen;

import org.antlr.v4.Tool;
import org.antlr.v4.codegen.model.OutputModelObject;
import org.antlr.v4.tool.ErrorType;
import org.stringtemplate.v4.*;
import org.stringtemplate.v4.compiler.FormalArgument;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/** Convert an output model tree to template hierarchy by walking
 *  the output model. Each output model object has a corresponding template
 *  of the same name.  An output model object can have nested objects.
 *  We identify those nested objects by the list of arguments in the template
 *  definition. For example, here is the definition of the parser template:
 *
 *  Parser(parser, scopes, funcs) ::= <<...>>
 *
 *  The first template argument is always the output model object from which
 *  this walker will create the template. Any other arguments identify
 *  the field names within the output model object of nested model objects.
 *  So, in this case, template Parser is saying that output model object
 *  Parser has two fields the walker should chase called a scopes and funcs.
 *
 *  This simple mechanism means we don't have to include code in every
 *  output model object that says how to create the corresponding template.
 */
public class OutputModelWalker {
	Tool tool;
	STGroup templates;

	public OutputModelWalker(Tool tool,
							 STGroup templates)
	{
		this.tool = tool;
		this.templates = templates;
	}

	public ST walk(OutputModelObject omo) {
		// CREATE TEMPLATE FOR THIS OUTPUT OBJECT
		Class<? extends OutputModelObject> cl = omo.getClass();
		String templateName = cl.getSimpleName();
		if ( templateName == null ) {
			tool.errMgr.toolError(ErrorType.NO_MODEL_TO_TEMPLATE_MAPPING, cl.getSimpleName());
			return new ST("["+templateName+" invalid]");
		}
		ST st = templates.getInstanceOf(templateName);
		if ( st == null ) {
			tool.errMgr.toolError(ErrorType.CODE_GEN_TEMPLATES_INCOMPLETE, templateName);
			return new ST("["+templateName+" invalid]");
		}
		if ( st.impl.formalArguments == null ) {
			tool.errMgr.toolError(ErrorType.CODE_TEMPLATE_ARG_ISSUE, templateName, "<none>");
			return st;
		}

		Map<String,FormalArgument> formalArgs = st.impl.formalArguments;

		// PASS IN OUTPUT MODEL OBJECT TO TEMPLATE AS FIRST ARG
		Set<String> argNames = formalArgs.keySet();
		Iterator<String> arg_it = argNames.iterator();
		String modelArgName = arg_it.next(); // ordered so this is first arg
		st.add(modelArgName, omo);

		// COMPUTE STs FOR EACH NESTED MODEL OBJECT MARKED WITH @ModelElement AND MAKE ST ATTRIBUTE
		Field fields[] = cl.getFields();
		for (Field fi : fields) {
			Annotation[] annotations = fi.getAnnotations();
			if ( annotations.length==0 ) continue;
			String fieldName = fi.getName();
			// Just don't set @ModelElement fields w/o formal arg in target ST
			if ( formalArgs.get(fieldName)==null ) continue;
			try {
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
						if ( nestedOmo==null ) continue;
						ST nestedST = walk(nestedOmo);
						st.add(fieldName, nestedST);
					}
				}
				else if ( o instanceof Map ) {
					Map<Object, OutputModelObject> nestedOmoMap = (Map<Object, OutputModelObject>)o;
					Map<Object, ST> m = new HashMap<Object, ST>();
					for (Object key : nestedOmoMap.keySet()) {
						ST nestedST = walk(nestedOmoMap.get(key));
						m.put(key, nestedST);
					}
					st.add(fieldName, m);
				}
				else if ( o!=null ) {
					tool.errMgr.toolError(ErrorType.INTERNAL_ERROR, "not recognized nested model element: "+fieldName);
				}
			}
			catch (IllegalAccessException iae) {
				tool.errMgr.toolError(ErrorType.CODE_TEMPLATE_ARG_ISSUE, templateName, fieldName);
			}
		}
		//st.impl.dump();
		return st;
	}

}
