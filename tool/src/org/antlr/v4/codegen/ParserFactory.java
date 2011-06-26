package org.antlr.v4.codegen;

import org.antlr.v4.codegen.model.*;
import org.antlr.v4.codegen.model.ast.AddLeaf;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.*;

import java.util.List;

/** */
public class ParserFactory extends OutputModelFactory {
	public ParserFactory(CodeGenerator gen) {
		super(gen);
	}

	public OutputModelObject buildOutputModel() {
		return new ParserFile(this, gen.getRecognizerFileName());
	}

	@Override
	public List<SrcOp> ruleRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		InvokeRule r = new InvokeRule(this, ID, label);
		AddToLabelList a = null;
		if ( label!=null && label.parent.getType()==ANTLRParser.PLUS_ASSIGN ) {
			a = new AddToLabelList(this, gen.target.getListLabel(label.getText()), r);
		}
		return Utils.list(r, a);
	}

	@Override
	public List<SrcOp> tokenRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		MatchToken matchOp = new MatchToken(this, (TerminalAST) ID, label);
		AddToLabelList labelOp = null;
		if ( label!=null && label.parent.getType()==ANTLRParser.PLUS_ASSIGN ) {
			String listLabel = gen.target.getListLabel(label.getText());
			labelOp = new AddToLabelList(this, listLabel, matchOp);
		}
		SrcOp treeOp = null;
		if ( g.hasASTOption() ) {
			treeOp = new AddLeaf(this, ID, matchOp);
		}
		return Utils.list(matchOp, labelOp, treeOp);
	}

	@Override
	public List<SrcOp> stringRef(GrammarAST ID, GrammarAST label) {
		return tokenRef(ID, label, null);
	}

}
