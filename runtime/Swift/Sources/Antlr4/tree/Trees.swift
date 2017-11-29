/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/// A set of utility routines useful for all kinds of ANTLR trees.

public class Trees {
    /*
    public class func getPS(t: Tree, _ ruleNames: Array<String>,
    _ fontName: String, _ fontSize: Int) -> String {
    let psgen: TreePostScriptGenerator =
    TreePostScriptGenerator(ruleNames, t, fontName, fontSize)
    return psgen.getPS()
    }

    public class func getPS(t: Tree, _ ruleNames: Array<String>) -> String {
    return getPS(t, ruleNames, "Helvetica", 11)
    }
    //TODO: write to file

    public class func writePS(t: Tree, _ ruleNames: Array<String>,
    _ fileName: String,
    _ fontName: String, _ fontSize: Int)
    throws {
    var ps: String = getPS(t, ruleNames, fontName, fontSize)
    var f: FileWriter = FileWriter(fileName)
    var bw: BufferedWriter = BufferedWriter(f)
    try {
    bw.write(ps)
    }
    defer {
    bw.close()
    }
    }

    public class func writePS(t: Tree, _ ruleNames: Array<String>, _ fileName: String)
    throws {
    writePS(t, ruleNames, fileName, "Helvetica", 11)
    }
    */
    /// Print out a whole tree in LISP form. _#getNodeText_ is used on the
    /// node payloads to get the text for the nodes.  Detect
    /// parse trees and extract data appropriately.
    /// 
    public static func toStringTree(_ t: Tree) -> String {
        let rulsName: Array<String>? = nil
        return toStringTree(t, rulsName)
    }

    /// Print out a whole tree in LISP form. _#getNodeText_ is used on the
    /// node payloads to get the text for the nodes.  Detect
    /// parse trees and extract data appropriately.
    /// 
    public static func toStringTree(_ t: Tree, _ recog: Parser?) -> String {
        let ruleNames: [String]? = recog != nil ? recog!.getRuleNames() : nil
        let ruleNamesList: Array<String>? = ruleNames ?? nil
        return toStringTree(t, ruleNamesList)
    }

    /// Print out a whole tree in LISP form. _#getNodeText_ is used on the
    /// node payloads to get the text for the nodes.  Detect
    /// parse trees and extract data appropriately.
    /// 
    public static func toStringTree(_ t: Tree, _ ruleNames: Array<String>?) -> String {
        let s = Utils.escapeWhitespace(getNodeText(t, ruleNames), false)
        if t.getChildCount() == 0 {
            return s
        }
        var buf = "(\(s) "
        let length = t.getChildCount()
        for i in 0..<length {
            if i > 0 {
                buf += " "
            }
            buf += toStringTree(t.getChild(i)!, ruleNames)
        }
        buf += ")"
        return buf
    }

    public static func getNodeText(_ t: Tree, _ recog: Parser?) -> String {
        let ruleNames: [String]? = recog != nil ? recog!.getRuleNames() : nil
        let ruleNamesList: Array<String>? = ruleNames ?? nil
        return getNodeText(t, ruleNamesList)
    }

    public static func getNodeText(_ t: Tree, _ ruleNames: Array<String>?) -> String {
        if ruleNames != nil {
            if let ruleNode = t as? RuleNode {
                let ruleIndex: Int = ruleNode.getRuleContext().getRuleIndex()
                let ruleName: String = ruleNames![ruleIndex]
                let altNumber = (t as! RuleContext).getAltNumber()
                if altNumber != ATN.INVALID_ALT_NUMBER  {
                    return "\(ruleName):\(altNumber)"
                }
                return ruleName
            } else {
                if let errorNode = t as? ErrorNode {
                    return errorNode.description
                } else if let terminalNode = t as? TerminalNode {
                    if let symbol = terminalNode.getSymbol() {
                        let s: String = symbol.getText()!
                        return s
                    }
                }
            }
        }
        // no recog for rule names
        let payload: AnyObject = t.getPayload()
        if payload is Token {
            let token = payload as! Token
            return token.getText()!
        }
        return "\(t.getPayload())"

    }

    /// Return ordered list of all children of this node
    public static func getChildren(_ t: Tree) -> Array<Tree> {
        var kids: Array<Tree> = Array<Tree>()
        let length = t.getChildCount()
        for i in 0..<length {
            kids.append(t.getChild(i)!)
        }
        return kids
    }

    /// Return a list of all ancestors of this node.  The first node of
    /// list is the root and the last is the parent of this node.
    /// 

    public static func getAncestors(_ t: Tree) -> Array<Tree> {
        var ancestors: Array<Tree> = Array<Tree>()
        if t.getParent() == nil {

            return ancestors
            //return Collections.emptyList();
        }

        var tp = t.getParent()
        while let tpWrap = tp {
            ancestors.insert(t, at: 0)
            //ancestors.add(0, t); // insert at start
            tp = tpWrap.getParent()
        }
        return ancestors
    }

    public static func findAllTokenNodes(_ t: ParseTree, _ ttype: Int) -> Array<ParseTree> {
        return findAllNodes(t, ttype, true)
    }

    public static func findAllRuleNodes(_ t: ParseTree, _ ruleIndex: Int) -> Array<ParseTree> {
        return findAllNodes(t, ruleIndex, false)
    }

    public static func findAllNodes(_ t: ParseTree, _ index: Int, _ findTokens: Bool) -> Array<ParseTree> {
        var nodes: Array<ParseTree> = Array<ParseTree>()
        _findAllNodes(t, index, findTokens, &nodes)
        return nodes
    }

    public static func _findAllNodes(_ t: ParseTree,
                                    _ index: Int, _ findTokens: Bool, _ nodes: inout Array<ParseTree>) {
        // check this node (the root) first
        if let tnode = t as? TerminalNode , findTokens {
            if tnode.getSymbol()!.getType() == index {
                nodes.append(t)
            }
        } else {
            if let ctx = t as? ParserRuleContext , !findTokens {
                if ctx.getRuleIndex() == index {
                    nodes.append(t)
                }
            }
        }
        // check children
        let length = t.getChildCount()
        for i in 0..<length {
            _findAllNodes(t.getChild(i) as! ParseTree, index, findTokens, &nodes)
        }
    }

    public static func descendants(_ t: ParseTree) -> Array<ParseTree> {
        var nodes: Array<ParseTree> = Array<ParseTree>()
        nodes.append(t)

        let n: Int = t.getChildCount()
        for i in 0..<n {

            //nodes.addAll(descendants(t.getChild(i)));
            let child = t.getChild(i)
            if child != nil {
                nodes.concat(descendants(child as! ParseTree))
            }

        }
        return nodes
    }

    /// Find smallest subtree of t enclosing range startTokenIndex..stopTokenIndex
    /// inclusively using postorder traversal.  Recursive depth-first-search.
    /// 
    /// - Since: 4.5.1
    /// 
    public static func getRootOfSubtreeEnclosingRegion(_ t: ParseTree,
                                                      _ startTokenIndex: Int,
                                                      _ stopTokenIndex: Int) -> ParserRuleContext? {
        let n: Int = t.getChildCount()

        for i in 0..<n {
            //TODO t.getChild(i) nil
            let child: ParseTree? = t.getChild(i) as? ParseTree
            //Added by janyou
            if child == nil {
                return nil
            }
            let r: ParserRuleContext? = getRootOfSubtreeEnclosingRegion(child!, startTokenIndex, stopTokenIndex)
            if r != nil {
                return r!
            }
        }
        if let r = t as? ParserRuleContext {
            if startTokenIndex >= r.getStart()!.getTokenIndex() && // is range fully contained in t?
                    stopTokenIndex <= r.getStop()!.getTokenIndex() {
                return r
            }
        }
        return nil
    }

    private init() {
    }
}
