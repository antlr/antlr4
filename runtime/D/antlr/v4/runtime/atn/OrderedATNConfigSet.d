module antlr.v4.runtime.atn.OrderedATNConfigSet;

import antlr.v4.runtime.atn.ATNConfigSet;
import antlr.v4.runtime.atn.LexerConfigHashSet;

/**
 * TODO add class description
 */
class OrderedATNConfigSet : ATNConfigSet
{

    public this()
    {
        this.configLookup = new LexerConfigHashSet();
    }

}
