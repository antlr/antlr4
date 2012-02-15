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
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.Stack;

public class TestA {
	/** An example listener that uses a stack to store return values
	 *  so that exit methods executed on notes further up the parse tree
	 *  can see the results of these computations.
	 *
	 *  Because we are using only the exit routines, the same listener works
	 *  as a parse listener and is a parse tree listener. It generates
	 *  the following output from input 3+4*5:

		 Int: 3
		 Int: 4
		 Int: 5
		 Mult: 20
		 Add: 23
		 tree = (s (e (e 3) + (e (e 4) * (e 5))))
		 Int: 3
		 Int: 4
		 Int: 5
		 Mult: 20
		 Add: 23
		 result from tree walk = 23

	 *  The key things to notice are that there are no actions
	 *  and the labels in the grammar--it is completely language neutral.
	 *  Also, I have labeled each alternative so that we get a different
	 *  context object. That way we get a listener method for each
	 *  alternative.
	 *
	 *  Compare this to A2.g4, which adds a field to the context objects
	 *  by using a "returns [int v]" on the expression rule.
	 */
	public static class Do extends ABaseListener {
		Stack<Integer> results = new Stack<Integer>();

		@Override
		public void exit(AParser.AddContext ctx) {
			results.push( results.pop() + results.pop() );
			System.out.println("Add: " + results.peek());
		}

		@Override
		public void exit(AParser.IntContext ctx) {
			results.push( Integer.valueOf(ctx.INT().getText()) );
			System.out.println("Int: "+results.peek());
		}

		@Override
		public void exit(AParser.MultContext ctx) {
			results.push( results.pop() * results.pop() );
			System.out.println("Mult: " + results.peek());
		}

		@Override
		public void exit(AParser.ParensContext ctx) {
			// result already on stack
			System.out.println("Parens: "+results.peek());
		}
	}
	public static void main(String[] args) throws Exception {
		ALexer lexer = new ALexer(new ANTLRFileStream(args[0]));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		AParser p = new AParser(tokens);
		p.setBuildParseTree(true);
		p.addParseListener(new Do());
		ParserRuleContext<Token> t = p.s();
		System.out.println("tree = "+t.toStringTree(p));

		ParseTreeWalker walker = new ParseTreeWalker();
		Do doer = new Do();
		walker.walk(doer, t);
		System.out.println("result from tree walk = "+ doer.results.pop());
	}
}
