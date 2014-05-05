package org.antlr.v4.parse;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;

/** A CommonToken that can also track it's original location,
 *  derived from options on the element ref like BEGIN<line=34,...>.
 */
public class GrammarToken extends CommonToken {
    public int _line = -1;
    public int _charPos = -1;
    public int _charIndex = -1;

    public GrammarToken(Token oldToken) {
        super(oldToken);
    }

    public GrammarToken(CharStream input, int type, int channel, int start, int stop) {
        super(input, type, channel, start, stop);
    }

    @Override
    public int getCharPositionInLine() {
        if ( _charPos>=0 ) return _charPos;
        return super.getCharPositionInLine();
    }

    @Override
    public int getLine() {
        if ( _line>=0 ) return _line;
        return super.getLine();
    }

    @Override
    public int getStartIndex() {
        if ( _charIndex >=0 ) return _charIndex;
        return super.getStartIndex();
    }

    @Override
    public int getStopIndex() {
        int n = super.getStopIndex() - super.getStartIndex() + 1;
        return getStartIndex() + n - 1;
    }

    @Override
    public String toString() {
        String channelStr = "";
        if ( channel>0 ) {
            channelStr=",channel="+channel;
        }
        String txt = getText();
        if ( txt!=null ) {
            txt = txt.replaceAll("\n","\\\\n");
            txt = txt.replaceAll("\r","\\\\r");
            txt = txt.replaceAll("\t","\\\\t");
        }
        else {
            txt = "<no text>";
        }
        return "[@"+getTokenIndex()+","+getStartIndex()+":"+getStopIndex()+
               "='"+txt+"',<"+getType()+">"+channelStr+","+getLine()+":"+getCharPositionInLine()+"]";
    }
}
