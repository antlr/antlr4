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
        Class lexerClass = Class.forName(lexerClassName);
        Class[] lexArgTypes = new Class[]{CharStream.class};
        Constructor lexConstructor = lexerClass.getConstructor(lexArgTypes);
        Object[] lexArgs = new Object[]{is};
		TokenSource lexer = (TokenSource)lexConstructor.newInstance(lexArgs);
        is.setLine(scriptLine);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        Class parserClass = Class.forName(parserClassName);
        Class[] parArgTypes = new Class[]{TokenStream.class};
        Constructor parConstructor = parserClass.getConstructor(parArgTypes);
        Object[] parArgs = new Object[]{tokens};
        Parser parser = (Parser)parConstructor.newInstance(parArgs);

        // set up customized tree adaptor if necessary
        if ( adaptorClassName!=null ) {
            parArgTypes = new Class[]{TreeAdaptor.class};
            Method m = parserClass.getMethod("setTreeAdaptor", parArgTypes);
            Class adaptorClass = Class.forName(adaptorClassName);
            m.invoke(parser, adaptorClass.newInstance());
        }

        Method ruleMethod = parserClass.getMethod(ruleName);

        // INVOKE RULE
        return ruleMethod.invoke(parser);
    }
}
