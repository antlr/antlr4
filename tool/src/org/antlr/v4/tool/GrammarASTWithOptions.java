package org.antlr.v4.tool;

import org.antlr.runtime.Token;

import java.util.HashMap;
import java.util.Map;

public class GrammarASTWithOptions extends GrammarAST {
    protected Map<String, String> options;

    public GrammarASTWithOptions(Token t) { super(t); }
    public GrammarASTWithOptions(int type) { super(type); }
    public GrammarASTWithOptions(int type, Token t) { super(type, t); }
    public GrammarASTWithOptions(int type, Token t, String text) { super(type,t,text); }

    public void setOption(String key, String value) {
        if ( options==null ) options = new HashMap<String, String>();
        options.put(key, value);
    }

    public String getOption(String key) {
        if ( options==null ) return null;
        return options.get(key);
    }

    public Map<String, String> getOptions() { return options; }
}
