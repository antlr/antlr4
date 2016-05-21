package antlr

import "fmt"

/** A set of utility routines useful for all kinds of ANTLR trees. */

// Print out a whole tree in LISP form. {@link //getNodeText} is used on the
//  node payloads to get the text for the nodes.  Detect
//  parse trees and extract data appropriately.
func TreesStringTree(tree Tree, ruleNames []string, recog Recognizer) string {

	if recog != nil {
		ruleNames = recog.GetRuleNames()
	}

	var s = TreesGetNodeText(tree, ruleNames, nil)

	s = EscapeWhitespace(s, false)
	var c = tree.GetChildCount()
	if c == 0 {
		return s
	}
	var res = "(" + s + " "
	if c > 0 {
		s = TreesStringTree(tree.GetChild(0), ruleNames, nil)
		res += s
	}
	for i := 1; i < c; i++ {
		s = TreesStringTree(tree.GetChild(i), ruleNames, nil)
		res += (" " + s)
	}
	res += ")"
	return res
}

func TreesGetNodeText(t Tree, ruleNames []string, recog Parser) string {

	if recog != nil {
		ruleNames = recog.GetRuleNames()
	}

	if ruleNames != nil {
		if t2, ok := t.(RuleNode); ok {
			return ruleNames[t2.GetRuleContext().GetRuleIndex()]
		} else if t2, ok := t.(ErrorNode); ok {
			return fmt.Sprint(t2)
		} else if t2, ok := t.(TerminalNode); ok {
			if t2.GetSymbol() != nil {
				return t2.GetSymbol().GetText()
			}
		}
	}

	// no recog for rule names
	var payload = t.GetPayload()
	if p2, ok := payload.(Token); ok {
		return p2.GetText()
	}

	return fmt.Sprint(t.GetPayload())
}

// Return ordered list of all children of this node
func TreesGetChildren(t Tree) []Tree {
	var list = make([]Tree, 0)
	for i := 0; i < t.GetChildCount(); i++ {
		list = append(list, t.GetChild(i))
	}
	return list
}

// Return a list of all ancestors of this node.  The first node of
//  list is the root and the last is the parent of this node.
//
func TreesgetAncestors(t Tree) []Tree {
	var ancestors = make([]Tree, 0)
	t = t.GetParent()
	for t != nil {
		f := []Tree{t}
		ancestors = append(f, ancestors...)
		t = t.GetParent()
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
	t3, ok2 := t.(ParserRuleContext)

	if findTokens && ok {
		if t2.GetSymbol().GetTokenType() == index {
			nodes = append(nodes, t2)
		}
	} else if !findTokens && ok2 {
		if t3.GetRuleIndex() == index {
			nodes = append(nodes, t3)
		}
	}
	// check children
	for i := 0; i < t.GetChildCount(); i++ {
		Trees_findAllNodes(t.GetChild(i).(ParseTree), index, findTokens, nodes)
	}
}

func Treesdescendants(t ParseTree) []ParseTree {
	var nodes = []ParseTree{t}
	for i := 0; i < t.GetChildCount(); i++ {
		nodes = append(nodes, Treesdescendants(t.GetChild(i).(ParseTree))...)
	}
	return nodes
}
