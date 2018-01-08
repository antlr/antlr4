/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class ParseTreesDescriptors {
	public static class AltNum extends BaseParserTestDescriptor {
		public String input = "xyz";
		public String output = "(a:3 x (b:2 y) z)\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;

		 options { contextSuperClass=MyRuleNode; }

		 <TreeNodeWithAltNumField(X="T")>


		 s
		 @init {
		 <BuildParseTrees()>
		 }
		 @after {
		 <ToStringTree("$r.ctx"):writeln()>
		 }
		   : r=a ;

		 a : 'f'
		   | 'g'
		   | 'x' b 'z'
		   ;
		 b : 'e' {} | 'y'
		   ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class ExtraToken extends BaseParserTestDescriptor {
		public String input = "xzy";
		public String output = "(a x z y)\n";
		public String errors = "line 1:1 extraneous input 'z' expecting 'y'\n";
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s
		 @init {
		 <BuildParseTrees()>
		 }
		 @after {
		 <ToStringTree("$r.ctx"):writeln()>
		 }
		   : r=a ;
		 a : 'x' 'y'
		   ;
		 Z : 'z'
		   ;

		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class ExtraTokensAndAltLabels extends BaseParserTestDescriptor {
		public String input = "${ ? a ?}";
		public String output = "(s ${ (v ? a) ? })\n";
		public String errors =
			"line 1:3 extraneous input '?' expecting {'a', 'b'}\n"+
			"line 1:7 extraneous input '?' expecting '}'\n";
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;

		 s
		 @init {
		 <BuildParseTrees()>
		 }
		 @after {
		 <ToStringTree("$ctx"):writeln()>
		 }
   		   : '${' v '}'
		   ;

		 v : A #altA
		   | B #altB
		   ;

		 A : 'a' ;
		 B : 'b' ;

		 WHITESPACE : [ \n\t\r]+ -> channel(HIDDEN) ;

		 ERROR : . ;
		 */
		@CommentHasStringValue
		public String grammar;

		@Override
		public boolean ignore(String targetName) {
			return !targetName.matches("Java|Python2|Python3|Node|Swift|CSharp");
		}
	}

	public static class NoViableAlt extends BaseParserTestDescriptor {
		public String input = "z";
		public String output = "(a z)\n";
		public String errors = "line 1:0 mismatched input 'z' expecting {'x', 'y'}\n";
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s
		 @init {
		 <BuildParseTrees()>
		 }
		 @after {
		 <ToStringTree("$r.ctx"):writeln()>
		 }
		   : r=a ;
		 a : 'x' | 'y'
		   ;
		 Z : 'z'
		   ;

		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class RuleRef extends BaseParserTestDescriptor {
		public String input = "yx";
		public String output = "(a (b y) x)\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s
		 @init {
		 <BuildParseTrees()>
		 }
		 @after {
		 <ToStringTree("$r.ctx"):writeln()>
		 }
		   : r=a ;
		 a : b 'x'
		   ;
		 b : 'y'
		   ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class Sync extends BaseParserTestDescriptor {
		public String input = "xzyy!";
		public String output = "(a x z y y !)\n";
		public String errors = "line 1:1 extraneous input 'z' expecting {'y', '!'}\n";
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s
		 @init {
		 <BuildParseTrees()>
		 }
		 @after {
		 <ToStringTree("$r.ctx"):writeln()>
		 }
		   : r=a ;
		 a : 'x' 'y'* '!'
		   ;
		 Z : 'z'
		   ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class Token2 extends BaseParserTestDescriptor {
		public String input = "xy";
		public String output = "(a x y)\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s
		 @init {
		 <BuildParseTrees()>
		 }
		 @after {
		 <ToStringTree("$r.ctx"):writeln()>
		 }
		   : r=a ;
		 a : 'x' 'y'
		   ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class TokenAndRuleContextString extends BaseParserTestDescriptor {
		public String input = "x";
		/**
		[a, s]
		(a x)
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s
		 @init {
		 <BuildParseTrees()>
		 }
		 @after {
		 <ToStringTree("$r.ctx"):writeln()>
		 }
		   : r=a ;
		 a : 'x' {
		 <RuleInvocationStack():writeln()>
		 } ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class TwoAltLoop extends BaseParserTestDescriptor {
		public String input = "xyyxyxz";
		public String output = "(a x y y x y x z)\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s
		 @init {
		 <BuildParseTrees()>
		 }
		 @after {
		 <ToStringTree("$r.ctx"):writeln()>
		 }
		   : r=a ;
		 a : ('x' | 'y')* 'z'
		   ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class TwoAlts extends BaseParserTestDescriptor {
		public String input = "y";
		public String output = "(a y)\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s
		 @init {
		 <BuildParseTrees()>
		 }
		 @after {
		 <ToStringTree("$r.ctx"):writeln()>
		 }
		   : r=a ;
		 a : 'x' | 'y'
		   ;
		 */
		@CommentHasStringValue
		public String grammar;

	}
}
