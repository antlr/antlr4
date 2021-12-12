/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.test.runtime.BaseCompositeLexerTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

import java.util.ArrayList;
import java.util.List;

public class CompositeLexersDescriptors {
	public static class LexerDelegatorInvokesDelegateRule extends BaseCompositeLexerTestDescriptor {
		public String input = "abc";
		public String output = """
		S.A
		[@0,0:0='a',<3>,1:0]
		[@1,1:1='b',<1>,1:1]
		[@2,2:2='c',<4>,1:2]
		[@3,3:2='<EOF>',<-1>,1:3]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "M";

		public String grammar = """
		 lexer grammar M;
		 import S;
		 B : 'b';
		 WS : (' '|'\n') -> skip ;
""";

		public String slaveGrammarS = """
		lexer grammar S;
		A : 'a' {<writeln("\"S.A\"")>};
		C : 'c' ;
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("S",slaveGrammarS));

			return slaves;
		}
	}

	public static class LexerDelegatorRuleOverridesDelegate extends BaseCompositeLexerTestDescriptor {
		public String input = "ab";
		public String output = """
		M.A
		[@0,0:1='ab',<1>,1:0]
		[@1,2:1='<EOF>',<-1>,1:2]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "M";

		public String grammar = """
		 lexer grammar M;
		 import S;
		 A : 'a' B {<writeln("\"M.A\"")>} ;
		 WS : (' '|'\n') -> skip ;
""";

		public String slaveGrammarS = """
		lexer grammar S;
		A : 'a' {<writeln("\"S.A\"")>} ;
		B : 'b' {<writeln("\"S.B\"")>} ;
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("S",slaveGrammarS));

			return slaves;
		}
	}
}
