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

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class TestA {
//	public static class Do extends BlankAListener {
//		Parser p;
//		public Do(Parser p ) { this.p = p; }
//		@Override
//		public void exitEveryRule(ParserRuleContext<Token> ctx) {
//			System.out.println("exit "+ctx.toStringTree(p));
//		}
//
//		@Override
//		public void enter(AParser.eContext ctx) {
//			System.out.println("enter alt w/o -> label: "+ctx.toInfoString(p));
//		}
//
//		@Override
//		public void visitTerminal(ParserRuleContext<Token> ctx, Token symbol) {
//			if ( ctx instanceof AParser.eContext && symbol.getType()==AParser.INT ) {
//				AParser.eContext ectx = (AParser.eContext)ctx;
//				ectx.v = Integer.valueOf(symbol.getText());
//			}
//		}
//
//		@Override
//		public void exit(AParser.multContext ctx) {
//			System.out.println("mult "+ctx.a.v+" * "+ctx.b.v);
//			ctx.v = ctx.a.v * ctx.b.v; // repeat of what parser did--set return value
//		}
//	}
//	public static void main(String[] args) throws Exception {
//		ALexer lexer = new ALexer(new ANTLRFileStream(args[0]));
//		CommonTokenStream tokens = new CommonTokenStream(lexer);
//		AParser p = new AParser(tokens);
//		p.setBuildParseTree(true);
////		p.addParseListener(new Do(p));
//		ParserRuleContext<Token> t = p.s();
//		System.out.println("tree = "+t.toStringTree(p));
//
//		ParseTreeWalker walker = new ParseTreeWalker();
//		Do doer = new Do(p);
//		walker.walk(doer, t);
//		AParser.eContext ectx = (AParser.eContext)t.getChild(0);
//		System.out.println("result from tree walk = "+ ectx.v);
//	}
}
