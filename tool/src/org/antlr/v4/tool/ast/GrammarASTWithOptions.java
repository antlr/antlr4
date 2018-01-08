/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;
import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.tool.ErrorType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class GrammarASTWithOptions extends GrammarAST {
    protected Map<String, GrammarAST> options;

	public GrammarASTWithOptions(GrammarASTWithOptions node) {
		super(node);
		this.options = node.options;
	}

	public GrammarASTWithOptions(Token t) { super(t); }
    public GrammarASTWithOptions(int type) { super(type); }
    public GrammarASTWithOptions(int type, Token t) { super(type, t); }
    public GrammarASTWithOptions(int type, Token t, String text) { super(type,t,text); }

    public void setOption(String key, GrammarAST node) {
        if ( options==null ) options = new HashMap<String, GrammarAST>();
        options.put(key, node);
    }

	public String getOptionString(String key) {
		GrammarAST value = getOptionAST(key);
		if ( value == null ) return null;
		if ( value instanceof ActionAST ) {
			return value.getText();
		}
		else {
			String v = value.getText();
			if ( v.startsWith("'") || v.startsWith("\"") ) {
				v = CharSupport.getStringFromGrammarStringLiteral(v);
				if (v == null) {
					g.tool.errMgr.grammarError(ErrorType.INVALID_ESCAPE_SEQUENCE, g.fileName, value.getToken(), value.getText());
					v = "";
				}
			}
			return v;
		}
	}

	/** Gets AST node holding value for option key; ignores default options
	 *  and command-line forced options.
	 */
    public GrammarAST getOptionAST(String key) {
        if ( options==null ) return null;
        return options.get(key);
    }

	public int getNumberOfOptions() {
		return options==null ? 0 : options.size();
	}

	@Override
	public abstract GrammarASTWithOptions dupNode();


	public Map<String, GrammarAST> getOptions() {
		if (options == null) {
			return Collections.emptyMap();
		}

		return options;
	}
}
