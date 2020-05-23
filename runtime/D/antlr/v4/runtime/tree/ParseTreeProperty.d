module antlr.v4.runtime.tree.ParseTreeProperty;

import antlr.v4.runtime.tree.ParseTree;

/**
 * Associate a property with a parse tree node.

 * Useful with parse tree listeners that need to associate values with
 * particular tree nodes, kind of like specifying a return value for
 * the listener event method that visited a particular node. Example:
 *
 * <pre>
 * ParseTreeProperty&lt;Integer&gt; values = new ParseTreeProperty&lt;Integer&gt;();
 * values.put(tree, 36);
 * int x = values.get(tree);
 * values.removeFrom(tree);
 * </pre>
 *
 * You would make one decl (values here) in the listener and use lots of times
 * in your event methods.
 */
class ParseTreeProperty(V)
{

    public V[ParseTree] annotations;

    public V get(ParseTree node)
    {
        V Null;
        return (node in annotations) ?  annotations[node] : Null;
    }

    public void put(ParseTree node, V value)
    {
        annotations[node] = value;
    }

    public V removeFrom(ParseTree node)
    {
        auto n = annotations[node];
        if (annotations.remove(node)) {
            return n;
        }
        assert (false, "ParseTreeProperty object contains unknown element!");
    }

}
