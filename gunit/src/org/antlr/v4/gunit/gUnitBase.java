package org.antlr.v4.gunit;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.TreeAdaptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class gUnitBase {
    public String lexerClassName;
    public String parserClassName;
    public String adaptorClassName;

    public Object execParser(
        String ruleName,
        String input,
        int scriptLine)
        throws Exception
    {
        ANTLRStringStream is = new ANTLRStringStream(input);
        Class<? extends TokenSource> lexerClass = Class.forName(lexerClassName).asSubclass(TokenSource.class);
        Constructor<? extends TokenSource> lexConstructor = lexerClass.getConstructor(CharStream.class);
		TokenSource lexer = lexConstructor.newInstance(is);
        is.setLine(scriptLine);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        Class<? extends Parser> parserClass = Class.forName(parserClassName).asSubclass(Parser.class);
        Constructor<? extends Parser> parConstructor = parserClass.getConstructor(TokenStream.class);
        Parser parser = parConstructor.newInstance(tokens);

        // set up customized tree adaptor if necessary
        if ( adaptorClassName!=null ) {
            Method m = parserClass.getMethod("setTreeAdaptor", TreeAdaptor.class);
            Class<? extends TreeAdaptor> adaptorClass = Class.forName(adaptorClassName).asSubclass(TreeAdaptor.class);
            m.invoke(parser, adaptorClass.newInstance());
        }

        Method ruleMethod = parserClass.getMethod(ruleName);

        // INVOKE RULE
        return ruleMethod.invoke(parser);
    }
}
