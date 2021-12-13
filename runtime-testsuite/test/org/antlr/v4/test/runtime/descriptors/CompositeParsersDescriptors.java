/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.test.runtime.BaseCompositeParserTestDescriptor;

import java.util.ArrayList;
import java.util.List;

public class CompositeParsersDescriptors {
	public static class BringInLiteralsFromDelegate extends BaseCompositeParserTestDescriptor {
		public String input = "=a";
		public String output = "S.a\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "M";

		public String grammar = """
		 grammar M;
		 import S;
		 s : a ;
		 WS : (' '|'\\n') -> skip ;
""";

		public String slaveGrammarS = """
		parser grammar S;
		a : '=' 'a' {<write("\\"S.a\\"")>};
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("S",stringIndentation(slaveGrammarS)));

			return slaves;
		}
	}

	public static class CombinedImportsCombined extends BaseCompositeParserTestDescriptor {
		public String input = "x 34 9";
		public String output = "S.x\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "M";

		public String grammar = """
		 grammar M;
		 import S;
		 s : x INT;
""";

		public String slaveGrammarS = """
		parser grammar S;
		tokens { A, B, C }
		x : 'x' INT {<writeln("\\"S.x\\"")>};
		INT : '0'..'9'+ ;
		WS : (' '|'\\n') -> skip ;
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("S",stringIndentation(slaveGrammarS)));

			return slaves;
		}
	}

	public static class DelegatesSeeSameTokenType extends BaseCompositeParserTestDescriptor {
		public String input = "aa";
		public String output = """
		S.x
		T.y
""";

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "M";

		public String grammar = """
		 // The lexer will create rules to match letters a, b, c.
		 // The associated token types A, B, C must have the same value
		 // and all import'd parsers.  Since ANTLR regenerates all imports
		 // for use with the delegator M, it can generate the same token type
		 // mapping in each parser:
		 // public static final int C=6;
		 // public static final int EOF=-1;
		 // public static final int B=5;
		 // public static final int WS=7;
		 // public static final int A=4;
		 grammar M;
		 import S,T;
		 s : x y ; // matches AA, which should be 'aa'
		 B : 'b' ; // another order: B, A, C
		 A : 'a' ;
		 C : 'c' ;
		 WS : (' '|'\\n') -> skip ;
""";

		public String slaveGrammarT = """
		parser grammar T;
		tokens { C, B, A } // reverse order
		y : A {<writeln("\\"T.y\\"")>};
""";
		public String slaveGrammarS = """
		parser grammar S;
		tokens { A, B, C }
		x : A {<writeln("\\"S.x\\"")>};
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("T",stringIndentation(slaveGrammarT)));
			slaves.add(new Pair<String, String>("S",stringIndentation(slaveGrammarS)));

			return slaves;
		}
	}

	public static class DelegatorAccessesDelegateMembers extends BaseCompositeParserTestDescriptor {
		public String input = "b";
		public String output = "foo\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "M";

		public String grammar = """
		 grammar M; // uses no rules from the import
		 import S;
		 s : 'b' {<Invoke_foo()>} ; // gS is import pointer
		 WS : (' '|'\\n') -> skip ;
""";

		public String slaveGrammarS = """
		parser grammar S;
		@parser::members {
		<Declare_foo()>
		}
		a : B;
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("S",stringIndentation(slaveGrammarS)));

			return slaves;
		}
	}

	public static class DelegatorInvokesDelegateRule extends BaseCompositeParserTestDescriptor {
		public String input = "b";
		public String output = "S.a\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "M";

		public String grammar = """
		 grammar M;
		 import S;
		 s : a ;
		 B : 'b' ; // defines B from inherited token space
		 WS : (' '|'\\n') -> skip ;
""";

		public String slaveGrammarS = """
		parser grammar S;
		a : B {<writeln("\\"S.a\\"")>};
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("S",stringIndentation(slaveGrammarS)));

			return slaves;
		}
	}

	public static class DelegatorInvokesDelegateRuleWithArgs extends BaseCompositeParserTestDescriptor {
		public String input = "b";
		public String output = "S.a1000\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "M";

		public String grammar = """
		 grammar M;
		 import S;
		 s : label=a[3] {<writeln("$label.y")>} ;
		 B : 'b' ; // defines B from inherited token space
		 WS : (' '|'\\n') -> skip ;
""";

		public String slaveGrammarS = """
		parser grammar S;
		a[int x] returns [int y] : B {<write("\\"S.a\\"")>} {$y=1000;} ;
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("S",stringIndentation(slaveGrammarS)));

			return slaves;
		}
	}

	public static class DelegatorInvokesDelegateRuleWithReturnStruct extends BaseCompositeParserTestDescriptor {
		public String input = "b";
		public String output = "S.ab\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "M";

		public String grammar = """
		 grammar M;
		 import S;
		 s : a {<write("$a.text")>} ;
		 B : 'b' ; // defines B from inherited token space
		 WS : (' '|'\\n') -> skip ;
""";

		public String slaveGrammarS = """
		parser grammar S;
		a : B {<write("\\"S.a\\"")>} ;
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("S",stringIndentation(slaveGrammarS)));

			return slaves;
		}
	}

	public static class DelegatorInvokesFirstVersionOfDelegateRule extends BaseCompositeParserTestDescriptor {
		public String input = "b";
		public String output = "S.a\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "M";

		public String grammar = """
		 grammar M;
		 import S,T;
		 s : a ;
		 B : 'b' ; // defines B from inherited token space
		 WS : (' '|'\\n') -> skip ;
""";

		public String slaveGrammarT = """
		parser grammar T;
		a : B {<writeln("\\"T.a\\"")>};<! hidden by S.a !>
""";
		public String slaveGrammarS = """
		parser grammar S;
		a : b {<writeln("\\"S.a\\"")>};
		b : B;
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("T",stringIndentation(slaveGrammarT)));
			slaves.add(new Pair<String, String>("S",stringIndentation(slaveGrammarS)));

			return slaves;
		}
	}

	public static class DelegatorRuleOverridesDelegate extends BaseCompositeParserTestDescriptor {
		public String input = "c";
		public String output = "S.a\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "M";

		public String grammar = """
		 grammar M;
		 import S;
		 b : 'b'|'c';
		 WS : (' '|'\\n') -> skip ;
""";

		public String slaveGrammarS = """
		parser grammar S;
		a : b {<write("\\"S.a\\"")>};
		b : B ;
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("S",stringIndentation(slaveGrammarS)));

			return slaves;
		}
	}

	public static class DelegatorRuleOverridesDelegates extends BaseCompositeParserTestDescriptor {
		public String input = "c";
		public String output = """
		M.b
		S.a
""";

		public String errors = null;
		public String startRule = "a";
		public String grammarName = "M";

		public String grammar = """
		 grammar M;
		 import S, T;
		 b : 'b'|'c' {<writeln("\\"M.b\\"")>}|B|A;
		 WS : (' '|'\\n') -> skip ;
""";

		public String slaveGrammarT = """
		parser grammar T;
		tokens { A }
		b : 'b' {<writeln("\\"T.b\\"")>};
""";
		public String slaveGrammarS = """
		parser grammar S;
		a : b {<writeln("\\"S.a\\"")>};
		b : 'b' ;
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("T",stringIndentation(slaveGrammarT)));
			slaves.add(new Pair<String, String>("S",stringIndentation(slaveGrammarS)));

			return slaves;
		}
	}

	public static class DelegatorRuleOverridesLookaheadInDelegate extends BaseCompositeParserTestDescriptor {
		public String input = "float x = 3;";
		public String output = "JavaDecl: floatx=3;\n";
		public String errors = null;
		public String startRule = "prog";
		public String grammarName = "M";

		public String grammar = """
		 grammar M;
		 import S;
		 prog : decl ;
		 type_ : 'int' | 'float' ;
		 ID  : 'a'..'z'+ ;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\\n') -> skip;
""";

		public String slaveGrammarS = """
		parser grammar S;
		type_ : 'int' ;
		decl : type_ ID ';'
			| type_ ID init_ ';' {<AppendStr("\\"JavaDecl: \\"","$text"):writeln()>};
		init_ : '=' INT;
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("S",stringIndentation(slaveGrammarS)));

			return slaves;
		}
	}

	/*
	 * This is a regression test for antlr/antlr4#248 "Including grammar with only
	 * fragments breaks generated lexer".
	 * https://github.com/antlr/antlr4/issues/248
	 */
	public static class ImportLexerWithOnlyFragmentRules extends BaseCompositeParserTestDescriptor {
		public String input = "test test";
		public String output = null;
		public String errors = null;
		public String startRule = "program";
		public String grammarName = "Test";

		public String grammar = """
		 grammar Test;
		 import Unicode;

		 program : 'test' 'test';

		 WS : (UNICODE_CLASS_Zs)+ -> skip;
""";

		public String slaveGrammarUnicode = """
		lexer grammar Unicode;

		fragment
		UNICODE_CLASS_Zs    : '\u0020' | '\u00A0' | '\u1680' | '\u180E'
		                    | '\u2000'..'\u200A'
		                    | '\u202F' | '\u205F' | '\u3000'
		                    ;
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("Unicode",stringIndentation(slaveGrammarUnicode)));

			return slaves;
		}
	}

	public static class ImportedGrammarWithEmptyOptions extends BaseCompositeParserTestDescriptor {
		public String input = "b";
		public String output = null;
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "M";

		public String grammar = """
		 grammar M;
		 import S;
		 s : a ;
		 B : 'b' ;
		 WS : (' '|'\\n') -> skip ;
""";

		public String slaveGrammarS = """
		parser grammar S;
		options {}
		a : B ;
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("S",stringIndentation(slaveGrammarS)));

			return slaves;
		}
	}

	public static class ImportedRuleWithAction extends BaseCompositeParserTestDescriptor {
		public String input = "b";
		public String output = null;
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "M";

		public String grammar = """
		 grammar M;
		 import S;
		 s : a;
		 B : 'b';
		 WS : (' '|'\\n') -> skip ;
""";

		public String slaveGrammarS = """
		parser grammar S;
		a @after {<InitIntVar("x","0")>} : B;
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("S",stringIndentation(slaveGrammarS)));

			return slaves;
		}
	}

	public static class KeywordVSIDOrder extends BaseCompositeParserTestDescriptor {
		public String input = "abc";
		public String output = """
		M.A
		M.a: [@0,0:2='abc',<1>,1:0]
""";

		public String errors = null;
		public String startRule = "a";
		public String grammarName = "M";

		public String grammar = """
		 grammar M;
		 import S;
		 a : A {<Append("\\"M.a: \\"","$A"):writeln()>};
		 A : 'abc' {<writeln("\\"M.A\\"")>};
		 WS : (' '|'\\n') -> skip ;
""";

		public String slaveGrammarS = """
		lexer grammar S;
		ID : 'a'..'z'+;
""";

		@Override
		public List<Pair<String, String>> getSlaveGrammars() {
			List<Pair<String,String>> slaves = new ArrayList<Pair<String, String>>();
			slaves.add(new Pair<String, String>("S",stringIndentation(slaveGrammarS)));

			return slaves;
		}
	}
}
