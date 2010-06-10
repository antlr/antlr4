package org.antlr.v4.codegen;

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.v4.codegen.pda.AcceptInstr;
import org.antlr.v4.codegen.pda.RetInstr;
import org.antlr.v4.codegen.pda.SplitInstr;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.runtime.pda.Bytecode;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;

/** */
public class LexerCompiler {
	LexerGrammar lg;
	public LexerCompiler(LexerGrammar lg) {
		this.lg = lg;
	}
	
	public CompiledPDA compileMode(String modeName) {
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		PDABytecodeGenerator gen = new PDABytecodeGenerator(lg.getMaxTokenType());
		PDABytecodeTriggers trigger = new PDABytecodeTriggers(null, gen);

		// add split for s0 to hook up rules (fill in operands as we gen rules)
		int numRules = lg.modes.get(modeName).size();
		int numFragmentRules = 0;
		for (Rule r : lg.modes.get(modeName)) { if ( r.isFragment() ) numFragmentRules++; }
		SplitInstr s0 = new SplitInstr(numRules - numFragmentRules);
		gen.emit(s0);

		for (Rule r : lg.modes.get(modeName)) { // for each rule in mode
			gen.currentRule = r;
			GrammarAST blk = (GrammarAST)r.ast.getFirstChildWithType(ANTLRParser.BLOCK);
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(adaptor,blk);
			trigger.setTreeNodeStream(nodes);
			int ttype = lg.getTokenType(r.name);
			gen.defineRuleAddr(r.name, gen.ip);
			if ( !r.isFragment() ) {
				s0.addrs.add(gen.ip);
				gen.defineTokenTypeToAddr(ttype, gen.ip);
			}
			try {
				trigger.block(); // GEN Instr OBJECTS
				int ruleTokenType = lg.getTokenType(r.name);
				if ( !r.isFragment() ) {
					gen.emit(new AcceptInstr(ruleTokenType));
				}
				else {
					gen.emit(new RetInstr());
				}
			}
			catch (Exception e){
				e.printStackTrace(System.err);
			}
		}

//		for (Rule r : lg.modes.get(modeName)) {
//			if ( !r.isFragment() ) {
//				LinearApproximator approx = new LinearApproximator(lg, NFA.INVALID_DECISION_NUMBER);
//				IntervalSet fset = approx.FIRST(lg.nfa.ruleToStartState.get(r));
//				System.out.println("first of "+r.name+"="+fset);
//				for (int c : fset.toArray()) {
//					if ( c>=0 && c<=255 ) {
//						int a = gen.obj.ruleToAddr.get(r.name);
//						List addrs = gen.obj.charToAddr[c];
//						if ( addrs==null ) {
//							addrs = new ArrayList();
//							gen.obj.charToAddr[c] = addrs;
//						}
//						addrs.add(a);
//					}
//				}
//			}
//		}
//		for (int c=0; c<=255; c++) {
//			System.out.println(c+": "+gen.obj.charToAddr[c]);
//		}

		gen.compile();
		gen.obj.nLabels = gen.labelIndex;
		System.out.println(Bytecode.disassemble(gen.obj.code));
		System.out.println("rule addrs="+ gen.obj.ruleToAddr);
		return gen.obj;
	}
}
