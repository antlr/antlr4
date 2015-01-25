/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.antlr.mojo.antlr4.testgen;

import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;

/**
 *
 * @author sam
 */
public class STGroupModelAdaptor implements ModelAdaptor {

	@Override
	public Object getProperty(Interpreter interp, ST self, Object o, Object property, String propertyName) throws STNoSuchPropertyException {
		STGroup group = (STGroup)o;
		if (group.isDictionary(propertyName)) {
			return group.rawGetDictionary(propertyName);
		}

		ST template = group.getInstanceOf(propertyName);
		if (template != null) {
			return template;
		}

		if ("name".equalsIgnoreCase(propertyName)) {
			return group.getName();
		}

		for (STGroup importedGroup : group.getImportedGroups()) {
			Object result = getProperty(interp, self, importedGroup, property, propertyName);
			if (result != null) {
				return result;
			}
		}

		return null;
	}

}
