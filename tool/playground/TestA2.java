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

public class TestA2 {
	/** An example listener that squirrels away a return value in a field
	 *  called v that we get added to the expression context objects
	 *  by adding a return value to rule e. This is a version of A.g4
	 *  that performs actions during the parse with user-defined actions.
	 *  AND, we pass in a listener that gets executed during the parse
	 *  and we use a listener on a tree walk that executes after the parse.
	 *  So, it affect, we compute the result of the expression 3 times.
	 */
	public static class Do extends A2BaseListener {
		A2Parser p;
		public Do(A2Parser p) { this.p = p; }
		@Override
		public void exitAdd(A2Parser.AddContext ctx) {
			ctx.v = ctx.e(0).v + ctx.e(1).v;
			System.out.println("Add: " + ctx.v);
		}

		@Override
		public void exitInt(A2Parser.IntContext ctx) {
			ctx.v = Integer.valueOf(ctx.INT().getText());
			System.out.println("Int: "+ctx.v);
		}

		@Override
		public void exitMult(A2Parser.MultContext ctx) {
			ctx.v = ctx.e(0).v * ctx.e(1).v;
			System.out.println("Mult: " + ctx.v);
		}

		@Override
		public void exitParens(A2Parser.ParensContext ctx) {
			ctx.v = ctx.e().v;
			System.out.println("Parens: "+ctx.v);
		}
	}
	public static void main(String[] args) throws Exception {
		A2Lexer lexer = new A2Lexer(new ANTLRFileStream(args[0]));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		A2Parser p = new A2Parser(tokens);
		p.setBuildParseTree(true);
		ParserRuleContext<Token> t = p.s();
		System.out.println("tree = "+t.toStringTree(p));

		ParseTreeWalker walker = new ParseTreeWalker();
		Do doer = new Do(p);
		walker.walk(doer, t);
		A2Parser.EContext ectx = (A2Parser.EContext)t.getChild(0);
		System.out.println("result from tree walk = "+ ectx.v);
	}
}
