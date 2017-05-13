package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class JavaExpressionsLeftRecursionDescriptors {
	
	public static abstract class JavaExpressions extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : e EOF ; // must indicate EOF can follow
		 expressionList
		     :   e (',' e)*
		     ;
		 e   :   '(' e ')'
		     |   'this'
		     |   'super'
		     |   INT
		     |   ID
		     |   typespec '.' 'class'
		     |   e '.' ID
		     |   e '.' 'this'
		     |   e '.' 'super' '(' expressionList? ')'
		     |   e '.' 'new' ID '(' expressionList? ')'
		 	 |   'new' typespec ( '(' expressionList? ')' | ('[' e ']')+)
		     |   e '[' e ']'
		     |   '(' typespec ')' e
		     |   e ('++' | '--')
		     |   e '(' expressionList? ')'
		     |   ('+'|'-'|'++'|'--') e
		     |   ('~'|'!') e
		     |   e ('*'|'/'|'%') e
		     |   e ('+'|'-') e
		     |   e ('\<\<' | '>>>' | '>>') e
		     |   e ('\<=' | '>=' | '>' | '\<') e
		     |   e 'instanceof' e
		     |   e ('==' | '!=') e
		     |   e '&' e
		     |\<assoc=right> e '^' e
		     |   e '|' e
		     |   e '&&' e
		     |   e '||' e
		     |   e '?' e ':' e
		     |\<assoc=right>
		         e ('='
		           |'+='
		           |'-='
		           |'*='
		           |'/='
		           |'&='
		           |'|='
		           |'^='
		           |'>>='
		           |'>>>='
		           |'\<\<='
		           |'%=') e
		     ;
		 typespec
		     : ID
		     | ID '[' ']'
		     | 'int'
		 	 | 'int' '[' ']'
		     ;
		 ID  : ('a'..'z'|'A'..'Z'|'_'|'$')+;
		 INT : '0'..'9'+ ;
		 WS  : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class JavaExpressions_1 extends JavaExpressions {
		public String input = "a|b&c";
		public String output = "(s (e (e a) | (e (e b) & (e c))) <EOF>)\n";
	}

	public static class JavaExpressions_2 extends JavaExpressions {
		public String input = "(a|b)&c";
		public String output = "(s (e (e ( (e (e a) | (e b)) )) & (e c)) <EOF>)\n";
	}

	public static class JavaExpressions_3 extends JavaExpressions {
		public String input = "a > b";
		public String output = "(s (e (e a) > (e b)) <EOF>)\n";
	}

	public static class JavaExpressions_4 extends JavaExpressions {
		public String input = "a >> b";
		public String output = "(s (e (e a) >> (e b)) <EOF>)\n";
	}

	public static class JavaExpressions_5 extends JavaExpressions {
		public String input = "a=b=c";
		public String output = "(s (e (e a) = (e (e b) = (e c))) <EOF>)\n";
	}

	public static class JavaExpressions_6 extends JavaExpressions {
		public String input = "a^b^c";
		public String output = "(s (e (e a) ^ (e (e b) ^ (e c))) <EOF>)\n";
	}

	public static class JavaExpressions_7 extends JavaExpressions {
		public String input = "(T)x";
		public String output = "(s (e ( (typespec T) ) (e x)) <EOF>)\n";
	}

	public static class JavaExpressions_8 extends JavaExpressions {
		public String input = "new A().b";
		public String output = "(s (e (e new (typespec A) ( )) . b) <EOF>)\n";
	}

	public static class JavaExpressions_9 extends JavaExpressions {
		public String input = "(T)t.f()";
		public String output = "(s (e (e ( (typespec T) ) (e (e t) . f)) ( )) <EOF>)\n";
	}

	public static class JavaExpressions_10 extends JavaExpressions {
		public String input = "a.f(x)==T.c";
		public String output = "(s (e (e (e (e a) . f) ( (expressionList (e x)) )) == (e (e T) . c)) <EOF>)\n";
	}

	public static class JavaExpressions_11 extends JavaExpressions {
		public String input = "a.f().g(x,1)";
		public String output = "(s (e (e (e (e (e a) . f) ( )) . g) ( (expressionList (e x) , (e 1)) )) <EOF>)\n";
	}

	public static class JavaExpressions_12 extends JavaExpressions {
		public String input = "new T[((n-1) * x) + 1]";
		public String output = "(s (e new (typespec T) [ (e (e ( (e (e ( (e (e n) - (e 1)) )) * (e x)) )) + (e 1)) ]) <EOF>)\n";
	}

}
