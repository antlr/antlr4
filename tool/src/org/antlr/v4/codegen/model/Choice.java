package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.*;

/** The class hierarchy underneath SrcOp is pretty deep but makes sense that,
 *  for example LL1StarBlock is a kind of LL1Loop which is a kind of Choice.
 *  The problem is it's impossible to figure
 *  out how to construct one of these deeply nested objects because of the
 *  long super constructor call chain. Instead, I decided to in-line all of
 *  this and then look for opportunities to re-factor code into functions.
 *  It makes sense to use a class hierarchy to share data fields, but I don't
 *  think it makes sense to factor code using super constructors because
 *  it has too much work to do.
 */
public abstract class Choice extends RuleElement {
	public int decision = -1;

	@ModelElement public List<CodeBlock> alts;
	@ModelElement public List<SrcOp> preamble;

	public Choice(OutputModelFactory factory,
				  GrammarAST blkOrEbnfRootAST,
				  List<CodeBlock> alts)
	{
		super(factory, blkOrEbnfRootAST);
		this.alts = alts;
	}

	public void addPreambleOp(SrcOp op) {
		if ( preamble==null ) preamble = new ArrayList<SrcOp>();
		preamble.add(op);
	}

	public List<String[]> getAltLookaheadAsStringLists(IntervalSet[] altLookSets) {
		List<String[]> altLook = new ArrayList<String[]>();
		for (int a=1; a<altLookSets.length; a++) {
			IntervalSet s = altLookSets[a];
			altLook.add(factory.gen.target.getTokenTypesAsTargetLabels(factory.g, s.toArray()));
		}
		return altLook;
	}

	public SrcOp addCodeForLookaheadTempVar(IntervalSet look) {
		SrcOp expr = factory.getLL1Test(look, ast);
		if ( expr instanceof TestSetInline) {
			TestSetInline e = (TestSetInline)expr;
			Decl d = new TokenTypeDecl(factory, e.varName);
			factory.currentRule.peek().addDecl(d);
			CaptureNextTokenType nextType = new CaptureNextTokenType(e.varName);
			addPreambleOp(nextType);
		}
		return expr;
	}

}
