package antlr4

import "fmt"

/** A set of utility routines useful for all kinds of ANTLR trees. */

// Print out a whole tree in LISP form. {@link //getNodeText} is used on the
//  node payloads to get the text for the nodes.  Detect
//  parse trees and extract data appropriately.
func TreestoStringTree(tree Tree, ruleNames []string, recog IRecognizer) string {

	if recog != nil {
		ruleNames = recog.getRuleNames()
	}

	var s = TreesgetNodeText(tree, ruleNames, nil)

	s = EscapeWhitespace(s, false)
	var c = tree.getChildCount()
	if c == 0 {
		return s
	}
	var res = "(" + s + " "
	if c > 0 {
		s = TreestoStringTree(tree.getChild(0), ruleNames, nil)
		res += s
	}
	for i := 1; i < c; i++ {
		s = TreestoStringTree(tree.getChild(i), ruleNames, nil)
		res += (" " + s)
	}
	res += ")"
	return res
}

func TreesgetNodeText(t Tree, ruleNames []string, recog *Parser) string {

	if recog != nil {
		ruleNames = recog.getRuleNames()
	}

	if ruleNames != nil {
		if t2, ok := t.(RuleNode); ok {
			return ruleNames[t2.getRuleContext().getRuleIndex()]
		} else if t2, ok := t.(ErrorNode); ok {
			return fmt.Sprint(t2)
		} else if t2, ok := t.(TerminalNode); ok {
			if t2.getSymbol() != nil {
				return t2.getSymbol().text()
			}
		}
	}

	// no recog for rule names
	var payload = t.getPayload()
	if p2, ok := payload.(*Token); ok {
		return p2.text()
	}

	return fmt.Sprint(t.getPayload())
}

// Return ordered list of all children of this node
func TreesgetChildren(t Tree) []Tree {
	var list = make([]Tree, 0)
	for i := 0; i < t.getChildCount(); i++ {
		list = append(list, t.getChild(i))
	}
	return list
}

// Return a list of all ancestors of this node.  The first node of
//  list is the root and the last is the parent of this node.
//
func TreesgetAncestors(t Tree) []Tree {
	var ancestors = make([]Tree, 0)
	t = t.getParent()
	for t != nil {
		f := []Tree{t}
		ancestors = append(f, ancestors...)
		t = t.getParent()
	}
	return ancestors
}

func TreesfindAllTokenNodes(t ParseTree, ttype int) []ParseTree {
	return TreesfindAllNodes(t, ttype, true)
}

func TreesfindAllRuleNodes(t ParseTree, ruleIndex int) []ParseTree {
	return TreesfindAllNodes(t, ruleIndex, false)
}

func TreesfindAllNodes(t ParseTree, index int, findTokens bool) []ParseTree {
	var nodes = make([]ParseTree, 0)
	Trees_findAllNodes(t, index, findTokens, nodes)
	return nodes
}

func Trees_findAllNodes(t ParseTree, index int, findTokens bool, nodes []ParseTree) {
	// check this node (the root) first

	t2, ok := t.(TerminalNode)
	t3, ok2 := t.(IParserRuleContext)

	if findTokens && ok {
		if t2.getSymbol().tokenType == index {
			nodes = append(nodes, t2)
		}
	} else if !findTokens && ok2 {
		if t3.getRuleIndex() == index {
			nodes = append(nodes, t3)
		}
	}
	// check children
	for i := 0; i < t.getChildCount(); i++ {
		Trees_findAllNodes(t.getChild(i).(ParseTree), index, findTokens, nodes)
	}
}

func Treesdescendants(t ParseTree) []ParseTree {
	var nodes = []ParseTree{t}
	for i := 0; i < t.getChildCount(); i++ {
		nodes = append(nodes, Treesdescendants(t.getChild(i).(ParseTree))...)
	}
	return nodes
}
