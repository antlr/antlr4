/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

const Utils = require('./../Utils');
const {Token} = require('./../Token');
const {ErrorNode, TerminalNode, RuleNode} = require('./Tree');

/** A set of utility routines useful for all kinds of ANTLR trees. */
const Trees = {
    /**
     * Print out a whole tree in LISP form. {@link //getNodeText} is used on the
     *  node payloads to get the text for the nodes.  Detect
     *  parse trees and extract data appropriately.
     */
    toStringTree: function(tree, ruleNames, recog, prettyPrint, indentLvl) {
        ruleNames = ruleNames || null;
        recog = recog || null;
        prettyPrint = prettyPrint || false;
        indentLvl = indentLvl || 0;

        if(recog!==null) {
            ruleNames = recog.ruleNames;
        }

        let s = Trees.getNodeText(tree, ruleNames);
        s = Utils.escapeWhitespace(s, false);
        const c = tree.getChildCount();

        if(c===0) {
            if (prettyPrint) {
                return `\n${" ".repeat(indentLvl)}${s}`;
            }
            return s;
        }

        let res = "(" + s + " ";
        if (prettyPrint) {
            res = `\n${" ".repeat(indentLvl)}${res}`;
        }

        if(c>0) {
            s = Trees.toStringTree(
                tree.getChild(0), ruleNames, recog, prettyPrint, indentLvl + 1
            );
            res = res.concat(s);
        }

        for(let i=1;i<c;i++) {
            s = Trees.toStringTree(
                tree.getChild(i), ruleNames, recog, prettyPrint, indentLvl + 1
            );
            if (prettyPrint) {
                res = `${res}${" ".repeat(indentLvl)}${s}`;
            } else {
                res = res.concat(' ' + s);
            }
        }

        if (prettyPrint) {
            res = `${res}\n${" ".repeat(indentLvl)}`;
        }
        res = res.concat(")");
        return res;
    },

    getNodeText: function(t, ruleNames, recog) {
        ruleNames = ruleNames || null;
        recog = recog || null;
        if(recog!==null) {
            ruleNames = recog.ruleNames;
        }
        if(ruleNames!==null) {
            if (t instanceof RuleNode) {
                const context = t.getRuleContext()
                const altNumber = context.getAltNumber();
                // use const value of ATN.INVALID_ALT_NUMBER to avoid circular dependency
                if ( altNumber != 0 ) {
                    return ruleNames[t.ruleIndex]+":"+altNumber;
                }
                return ruleNames[t.ruleIndex];
            } else if ( t instanceof ErrorNode) {
                return t.toString();
            } else if(t instanceof TerminalNode) {
                if(t.symbol!==null) {
                    return t.symbol.text;
                }
            }
        }
        // no recog for rule names
        const payload = t.getPayload();
        if (payload instanceof Token ) {
            return payload.text;
        }
        return t.getPayload().toString();
    },

    /**
     * Return ordered list of all children of this node
     */
    getChildren: function(t) {
        const list = [];
        for(let i=0;i<t.getChildCount();i++) {
            list.push(t.getChild(i));
        }
        return list;
    },

    /**
     * Return a list of all ancestors of this node.  The first node of
     * list is the root and the last is the parent of this node.
     */
    getAncestors: function(t) {
        let ancestors = [];
        t = t.getParent();
        while(t!==null) {
            ancestors = [t].concat(ancestors);
            t = t.getParent();
        }
        return ancestors;
    },

    findAllTokenNodes: function(t, ttype) {
        return Trees.findAllNodes(t, ttype, true);
    },

    findAllRuleNodes: function(t, ruleIndex) {
        return Trees.findAllNodes(t, ruleIndex, false);
    },

    findAllNodes: function(t, index, findTokens) {
        const nodes = [];
        Trees._findAllNodes(t, index, findTokens, nodes);
        return nodes;
    },

    _findAllNodes: function(t, index, findTokens, nodes) {
        // check this node (the root) first
        if(findTokens && (t instanceof TerminalNode)) {
            if(t.symbol.type===index) {
                nodes.push(t);
            }
        } else if(!findTokens && (t instanceof RuleNode)) {
            if(t.ruleIndex===index) {
                nodes.push(t);
            }
        }
        // check children
        for(let i=0;i<t.getChildCount();i++) {
            Trees._findAllNodes(t.getChild(i), index, findTokens, nodes);
        }
    },

    descendants: function(t) {
        let nodes = [t];
        for(let i=0;i<t.getChildCount();i++) {
            nodes = nodes.concat(Trees.descendants(t.getChild(i)));
        }
        return nodes;
    }
}

module.exports = Trees;
