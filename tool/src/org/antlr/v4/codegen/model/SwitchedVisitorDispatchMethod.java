package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.tool.Rule;

import java.util.ArrayList;
import java.util.List;

public class SwitchedVisitorDispatchMethod extends VisitorDispatchMethod {
	public List<String> listenerNames = new ArrayList<String>();

	public SwitchedVisitorDispatchMethod(OutputModelFactory factory, Rule r, boolean isEnter) {
		super(factory, r, isEnter);
		RuleFunction rf = factory.getCurrentRuleFunction();
		this.isEnter = isEnter;
		List<String> labels = r.getAltLabels();
		for (String label : labels) {
			String labelCapitalized = CharSupport.capitalize(label);
			listenerNames.add(labelCapitalized);
		}
	}
}
