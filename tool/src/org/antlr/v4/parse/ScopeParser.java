/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.parse;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CommonToken;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.Tuple;
import org.antlr.v4.runtime.misc.Tuple2;
import org.antlr.v4.tool.Attribute;
import org.antlr.v4.tool.AttributeDict;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.ActionAST;

import java.util.ArrayList;
import java.util.List;

/** Parse args, return values, locals
 *
 *  rule[arg1, arg2, ..., argN] returns [ret1, ..., retN]
 *
 *  text is target language dependent.  Java/C#/C/C++ would
 *  use "int i" but ruby/python would use "i".
 */
public class ScopeParser {
    /** Given an arg or retval scope definition list like
     *
     *  Map<String, String>, int[] j3, char *foo32[3]
     *
     *  or
     *
     *  int i=3, j=a[34]+20
     *
     *  convert to an attribute scope.
     */
	public static AttributeDict parseTypedArgList(@Nullable ActionAST action, String s, Grammar g) {
		return parse(action, s, ',', g);
	}

    public static AttributeDict parse(@Nullable ActionAST action, String s, char separator, Grammar g) {
        AttributeDict dict = new AttributeDict();
		List<Tuple2<String, Integer>> decls = splitDecls(s, separator);
		for (Tuple2<String, Integer> decl : decls) {
//            System.out.println("decl="+decl);
            if ( decl.getItem1().trim().length()>0 ) {
                Attribute a = parseAttributeDef(action, decl, g);
                dict.add(a);
            }
		}
        return dict;
    }

    /** For decls like "String foo" or "char *foo32[]" compute the ID
     *  and type declarations.  Also handle "int x=3" and 'T t = new T("foo")'
     *  but if the separator is ',' you cannot use ',' in the initvalue
     *  unless you escape use "\," escape.
     */
    public static Attribute parseAttributeDef(@Nullable ActionAST action, @NotNull Tuple2<String, Integer> decl, Grammar g) {
        if ( decl.getItem1()==null ) return null;
        Attribute attr = new Attribute();
        boolean inID = false;
        int start = -1;
        int rightEdgeOfDeclarator = decl.getItem1().length()-1;
        int equalsIndex = decl.getItem1().indexOf('=');
        if ( equalsIndex>0 ) {
            // everything after the '=' is the init value
            attr.initValue = decl.getItem1().substring(equalsIndex+1,decl.getItem1().length());
            rightEdgeOfDeclarator = equalsIndex-1;
        }
        // walk backwards looking for start of an ID
        for (int i=rightEdgeOfDeclarator; i>=0; i--) {
            // if we haven't found the end yet, keep going
            if ( !inID && Character.isLetterOrDigit(decl.getItem1().charAt(i)) ) {
                inID = true;
            }
            else if ( inID &&
                      !(Character.isLetterOrDigit(decl.getItem1().charAt(i))||
                       decl.getItem1().charAt(i)=='_') ) {
                start = i+1;
                break;
            }
        }
        if ( start<0 && inID ) {
            start = 0;
        }
        if ( start<0 ) {
            g.tool.errMgr.grammarError(ErrorType.CANNOT_FIND_ATTRIBUTE_NAME_IN_DECL, g.fileName, action.token, decl);
        }
        // walk forwards looking for end of an ID
        int stop=-1;
        for (int i=start; i<=rightEdgeOfDeclarator; i++) {
            // if we haven't found the end yet, keep going
            if ( !(Character.isLetterOrDigit(decl.getItem1().charAt(i))||
                decl.getItem1().charAt(i)=='_') )
            {
                stop = i;
                break;
            }
            if ( i==rightEdgeOfDeclarator ) {
                stop = i+1;
            }
        }

        // the name is the last ID
        attr.name = decl.getItem1().substring(start,stop);

        // the type is the decl minus the ID (could be empty)
        attr.type = decl.getItem1().substring(0,start);
        if ( stop<=rightEdgeOfDeclarator ) {
            attr.type += decl.getItem1().substring(stop,rightEdgeOfDeclarator+1);
        }
        attr.type = attr.type.trim();
        if ( attr.type.length()==0 ) {
            attr.type = null;
        }

        attr.decl = decl.getItem1();

		if (action != null) {
			String actionText = action.getText();
			int[] lines = new int[actionText.length()];
			int[] charPositionInLines = new int[actionText.length()];
			for (int i = 0, line = 0, col = 0; i < actionText.length(); i++, col++) {
				lines[i] = line;
				charPositionInLines[i] = col;
				if (actionText.charAt(i) == '\n') {
					line++;
					col = -1;
				}
			}

			int[] charIndexes = new int[actionText.length()];
			for (int i = 0, j = 0; i < actionText.length(); i++, j++) {
				charIndexes[j] = i;
				if (i < actionText.length() - 1 && actionText.charAt(i) == '/' && actionText.charAt(i + 1) == '/') {
					while (i < actionText.length() && actionText.charAt(i) != '\n') {
						i++;
					}
				}
			}

			int declOffset = charIndexes[decl.getItem2()];
			int declLine = lines[declOffset + start];

			int line = action.getToken().getLine() + declLine;
			int charPositionInLine = charPositionInLines[declOffset + start];
			if (declLine == 0) {
				/* offset for the start position of the ARG_ACTION token, plus 1
				 * since the ARG_ACTION text had the leading '[' stripped before
				 * reaching the scope parser.
				 */
				charPositionInLine += action.getToken().getCharPositionInLine() + 1;
			}

			int offset = ((CommonToken)action.getToken()).getStartIndex();
			attr.token = new CommonToken(action.getToken().getInputStream(), ANTLRParser.ID, BaseRecognizer.DEFAULT_TOKEN_CHANNEL, offset + declOffset + start + 1, offset + declOffset + stop);
			attr.token.setLine(line);
			attr.token.setCharPositionInLine(charPositionInLine);
			assert attr.name.equals(attr.token.getText()) : "Attribute text should match the pseudo-token text at this point.";
		}

        return attr;
    }

    /** Given an argument list like
     *
     *  x, (*a).foo(21,33), 3.2+1, '\n',
     *  "a,oo\nick", {bl, "fdkj"eck}, ["cat\n,", x, 43]
     *
     *  convert to a list of attributes.  Allow nested square brackets etc...
     *  Set separatorChar to ';' or ',' or whatever you want.
     */
    public static List<Tuple2<String, Integer>> splitDecls(String s, int separatorChar) {
        List<Tuple2<String, Integer>> args = new ArrayList<Tuple2<String, Integer>>();
        _splitArgumentList(s, 0, -1, separatorChar, args);
        return args;
    }

    public static int _splitArgumentList(String actionText,
                                         int start,
                                         int targetChar,
                                         int separatorChar,
                                         List<Tuple2<String, Integer>> args)
    {
        if ( actionText==null ) {
            return -1;
        }

        actionText = actionText.replaceAll("//[^\\n]*", "");
        int n = actionText.length();
        //System.out.println("actionText@"+start+"->"+(char)targetChar+"="+actionText.substring(start,n));
        int p = start;
        int last = p;
        while ( p<n && actionText.charAt(p)!=targetChar ) {
            int c = actionText.charAt(p);
            switch ( c ) {
                case '\'' :
                    p++;
                    while ( p<n && actionText.charAt(p)!='\'' ) {
                        if ( actionText.charAt(p)=='\\' && (p+1)<n &&
                             actionText.charAt(p+1)=='\'' )
                        {
                            p++; // skip escaped quote
                        }
                        p++;
                    }
                    p++;
                    break;
                case '"' :
                    p++;
                    while ( p<n && actionText.charAt(p)!='\"' ) {
                        if ( actionText.charAt(p)=='\\' && (p+1)<n &&
                             actionText.charAt(p+1)=='\"' )
                        {
                            p++; // skip escaped quote
                        }
                        p++;
                    }
                    p++;
                    break;
                case '(' :
                    p = _splitArgumentList(actionText,p+1,')',separatorChar,args);
                    break;
                case '{' :
                    p = _splitArgumentList(actionText,p+1,'}',separatorChar,args);
                    break;
                case '<' :
                    if ( actionText.indexOf('>',p+1)>=p ) {
                        // do we see a matching '>' ahead?  if so, hope it's a generic
                        // and not less followed by expr with greater than
                        p = _splitArgumentList(actionText,p+1,'>',separatorChar,args);
                    }
                    else {
                        p++; // treat as normal char
                    }
                    break;
                case '[' :
                    p = _splitArgumentList(actionText,p+1,']',separatorChar,args);
                    break;
                default :
                    if ( c==separatorChar && targetChar==-1 ) {
                        String arg = actionText.substring(last, p);
						int index = last;
						while (index < p && Character.isWhitespace(actionText.charAt(index))) {
							index++;
						}
                        //System.out.println("arg="+arg);
                        args.add(Tuple.create(arg.trim(), index));
                        last = p+1;
                    }
                    p++;
                    break;
            }
        }
        if ( targetChar==-1 && p<=n ) {
            String arg = actionText.substring(last, p).trim();
			int index = last;
			while (index < p && Character.isWhitespace(actionText.charAt(index))) {
				index++;
			}
            //System.out.println("arg="+arg);
            if ( arg.length()>0 ) {
                args.add(Tuple.create(arg.trim(), index));
            }
        }
        p++;
        return p;
    }

}
