package org.antlr.v4.test;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.WritableToken;

import java.util.HashSet;
import java.util.Set;

public  class FilteringTokenStream extends CommonTokenStream {
    public FilteringTokenStream(TokenSource src) { super(src); }
    Set<Integer> hide = new HashSet<Integer>();
    @Override
    protected boolean sync(int i) {
        if (!super.sync(i)) {
            return false;
        }

        Token t = get(i);
        if ( hide.contains(t.getType()) ) {
            ((WritableToken)t).setChannel(Token.HIDDEN_CHANNEL);
        }

        return true;
    }
    public void setTokenTypeChannel(int ttype, int channel) {
        hide.add(ttype);
    }
}