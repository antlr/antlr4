package org.antlr.v4.codegen;

import org.antlr.v4.codegen.src.*;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.TerminalAST;

import java.util.List;

/** */
public class ParserFactory extends OutputModelFactory {
//	public static final Map<Class, String> modelToTemplateMap = new HashMap<Class, String>() {{
//		put(ParserFile.class, "parserFile");
//		put(Parser.class, "parser");
//		put(RuleFunction.class, "parserFunction");
//		put(DFADef.class, "DFA");
//		put(CodeBlock.class, "codeBlock");
//		put(LL1Choice.class, "switch");
//		put(MatchToken.class, "matchToken");
//	}};

	public ParserFactory(CodeGenerator gen) {
		super(gen);
	}

	public OutputModelObject buildOutputModel() {
		return new ParserFile(this, gen.getRecognizerFileName());
	}

	@Override
	public List<SrcOp> ruleRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		InvokeRule r = new InvokeRule(this, ID, label);
		return Utils.list(r);
	}

	@Override
	public List<SrcOp> tokenRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		return Utils.list(new MatchToken(this, (TerminalAST)ID, label));
	}

	@Override
	public List<SrcOp> stringRef(GrammarAST ID, GrammarAST label) {
		return tokenRef(ID, label, null);
	}

	public void defineBitSet(BitSetDef b) { ((ParserFile)file).defineBitSet(b); }
}
