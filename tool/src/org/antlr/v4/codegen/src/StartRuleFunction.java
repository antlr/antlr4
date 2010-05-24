package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.Rule;

import java.util.ArrayList;

/** */
public class StartRuleFunction extends RuleFunction {
	public StartRuleFunction(OutputModelFactory factory, Rule r) {
		super(factory);
		this.name = r.name;
		if ( r.modifiers!=null && r.modifiers.size()>0 ) {
			this.modifiers = new ArrayList<String>();
			for (GrammarAST t : r.modifiers) modifiers.add(t.getText());
		}
		modifiers = Utils.nodesToStrings(r.modifiers);

		ctxType = factory.gen.target.getRuleFunctionContextStructName(r);

		if ( r.args!=null ) {
			args = r.args.attributes.values();
		}
	}
}
