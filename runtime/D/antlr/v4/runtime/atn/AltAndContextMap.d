module antlr.v4.runtime.atn.AltAndContextMap;

import antlr.v4.runtime.atn.ATNConfig;
import antlr.v4.runtime.misc.BitSet;
import antlr.v4.runtime.atn.ContextMapObjectEqualityComparator;

/**
 * A structure that ATN configurations maps to bit vectors
 */
struct AltAndContextMap
{

    public BitSet[ATNConfig] altAndContextMap;

    public bool hasKey(ATNConfig aTNConfig)
    {
        aTNConfig.hashOfFp = &ContextMapObjectEqualityComparator.toHash;
        aTNConfig.opEqualsFp = &ContextMapObjectEqualityComparator.opEquals;
        if (aTNConfig in altAndContextMap)
            return true;
        return false;
    }

    public BitSet get(ATNConfig c)
    {
        return altAndContextMap[c];
    }

    public void put(ATNConfig c, BitSet bitSet)
    {
        c.hashOfFp = &ContextMapObjectEqualityComparator.toHash;
        c.opEqualsFp = &ContextMapObjectEqualityComparator.opEquals;
        altAndContextMap[c] = bitSet;
    }

    public BitSet[] values()
    {
        BitSet[] res;
        foreach(el; altAndContextMap.values)
        {
            if(el.cardinality > 0)
                res ~= el;
        }
        return res;
    }

}
