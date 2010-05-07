package org.antlr.v4.codegen.src;

import java.util.List;

/** */
public abstract class OutputModelObject {
	//public abstract ST getSt();

	/** If the output model object encloses some other model objects,
	 *  we need to be able to walk them. Rather than make each class
	 *  properly walk any nested objects, I'm going to use a generic
	 *  external walker. This method lets me look at the output model
	 *  as a homogeneous tree structure.  Returns a list of field names
	 *  of type OutputModelObject that should be walked to complete model.
	 */
	public List<String> getChildren() {
		return null;
	}
}
