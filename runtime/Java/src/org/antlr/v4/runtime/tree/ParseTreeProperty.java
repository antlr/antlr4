package org.antlr.v4.runtime.tree;

/** Associate a property with a parse tree node. Useful with parse tree
 *  listeners that need to associate values with particular tree nodes,
 *  kind of like specifying a return value for the listener event method
 *  that visited a particular node. Example:

	ParseTreeProperty<Integer> values = new ParseTreeProperty<Integer>();
	values.put(tree, 36);
	int x = values.get(tree);
	values.removeFrom(tree);

    You would make one decl (values here) in the listener and use lots of
    times in your event methods.
 */
public class ParseTreeProperty<V> {
	protected IdentityMap<ParseTree, V> annotations = new IdentityMap<ParseTree, V>();

	public V get(ParseTree node) { return annotations.get(node); }
	public void put(ParseTree node, V value) { annotations.put(node, value); }
	public V removeFrom(ParseTree node) { return annotations.get(node); }
}
