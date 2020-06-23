module antlr.v4.runtime.InterfaceParser;

import antlr.v4.runtime.RuleContext;
import antlr.v4.runtime.TokenStream;

/**
 * TODO add interface description
 */
interface InterfaceParser
{

    public string[] getRuleInvocationStack(RuleContext ruleContext);

    public string[] getRuleNames();

    public TokenStream getTokenStream();

    public void notifyErrorListeners(string msg);

}
