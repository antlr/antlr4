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
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

public class TestVisitor {
	public static class MyVisitor extends ParseTreeVisitor<Integer> implements AVisitor<Integer> {
		@Override
		public Integer visit(AParser.AddContext ctx) {
			return ctx.e(0).accept(this) + ctx.e(1).accept(this);
		}

		@Override
		public Integer visit(AParser.IntContext ctx) {
			return Integer.valueOf(ctx.INT().getText());
		}

		@Override
		public Integer visit(AParser.MultContext ctx) {
//			return ctx.e(0).accept(this) * ctx.e(1).accept(this);
			return visit(ctx.e(0)) * visit(ctx.e(1));
		}

		@Override
		public Integer visit(AParser.ParensContext ctx) {
			return ctx.e().accept(this);
		}

		@Override
		public Integer visit(AParser.sContext ctx) {
			return visit(ctx.e());
			//return ctx.e().accept(this);
		}
	}

	public static void main(String[] args) throws Exception {
		ALexer lexer = new ALexer(new ANTLRFileStream(args[0]));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		AParser p = new AParser(tokens);
		p.setBuildParseTree(true);
		ParserRuleContext<Token> t = p.s();
		System.out.println("tree = "+t.toStringTree(p));

		MyVisitor visitor = new MyVisitor();
		Integer result = visitor.visit(t);
//		Integer result = t.accept(visitor);
		System.out.println("result from tree walk = " + result);
	}
}
