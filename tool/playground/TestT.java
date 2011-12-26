/*
 [The "BSD license"]
  Copyright (c) 2011 Terence Parr
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions
  are met:

  1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
  3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class TestT {
	public static void main(String[] args) throws Exception {
		TLexer t = new TLexer(new ANTLRFileStream(args[0]));
		CommonTokenStream tokens = new CommonTokenStream(t);
		tokens.fill();
		for (Object tok : tokens.getTokens()) {
			System.out.println(tok);
		}
		TParser p = new TParser(tokens);
		p.setBuildParseTree(true);
		final TParser.sContext tree = p.s();
		System.out.println(tree.toStringTree(p));
//		TreeViewer v = new TreeViewer(p, tree);
//		v.setHighlightedBoxColor(TreeViewer.LIGHT_RED);
//		v.addHighlightedNodes(new ArrayList<Tree>() {{
//			ParseTree c0 = tree.getChild(0);
//			add(c0);
//			add(c0.getChild(0));
//		}});
//		v.open();
//		tree.inspect(p);
//
//		ParseTreeWalker walker = new ParseTreeWalker();
//		TListener listener = new BlankTListener() {
//			public void enterEveryRule(ParserRuleContext ctx) {
//				System.out.println("enter rule "+TParser.ruleNames[ctx.ruleIndex]);
//			}
//			public void exitRule(TParser.DoIfContext ctx) { // specific to rule ifstat
//				System.out.println("exit rule ifstat");
//			}
//		};
//		walker.walk(listener, tree);
	}
}
