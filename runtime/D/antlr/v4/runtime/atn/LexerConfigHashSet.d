module antlr.v4.runtime.atn.LexerConfigHashSet;

import antlr.v4.runtime.atn.AbstractConfigHashSet;
import antlr.v4.runtime.misc.ObjectEqualityComparator;

/**
 * TODO add class description
 */
class LexerConfigHashSet : AbstractConfigHashSet
{

    public this()
    {
        super(&ObjectEqualityComparator.hashOf, &ObjectEqualityComparator.opEquals);
    }

}
