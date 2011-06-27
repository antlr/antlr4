package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.CoreOutputModelFactory;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** An optional block is just an alternative block where the last alternative
 *  is epsilon. The analysis takes care of adding to the empty alternative.
 *
 *  (A | B | C)?
 */
public class LL1OptionalBlock extends LL1AltBlock {
	public LL1OptionalBlock(CoreOutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);
	}
}
