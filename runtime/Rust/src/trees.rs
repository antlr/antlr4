/*!
A set of utility routines useful for all kinds of ANTLR trees.
*/

use crate::parser_rule_context::ParserRuleContext;
use crate::utils;

/// Print out a whole tree, not just a node, in LISP format
/// {@code (root child1 .. childN)}. Print just a node if this is a leaf.
pub fn string_tree<'input, 'arena, T>(tree: &T, rule_names: &[&str]) -> String
where
    'input: 'arena,
    T: ParserRuleContext<'input, 'arena> + ?Sized,
{
    let s = utils::escape_whitespaces(tree.get_node_text(rule_names), false);
    if tree.get_child_count() == 0 {
        return s;
    }
    let mut result = String::new();
    result.push('(');
    result.push_str(&s);
    result = tree
        .iter_children()
        // .iter()
        .map(|child| string_tree(child, rule_names))
        .fold(result, |mut acc, text| {
            acc.push(' ');
            acc.push_str(&text);
            acc
        });
    result.push(')');
    result
}

//pub fn get_children(t: impl Tree) -> Vec<Rc<dyn Tree>> { unimplemented!() }
//
//pub fn get_ancestors(t: impl Tree) -> Vec<Rc<dyn Tree>> { unimplemented!() }
//
//pub fn find_all_token_nodes(t: impl ParseTree, ttype: i32) -> Vec<Rc<dyn ParseTree>> { unimplemented!() }
//
//pub fn find_all_rule_nodes(t: impl ParseTree, rule_index: i32) -> Vec<Rc<dyn ParseTree>> { unimplemented!() }
//
//pub fn find_all_nodes(t: impl ParseTree, index: i32, find_tokens: bool) -> Vec<Rc<dyn ParseTree>> { unimplemented!() }
//
////fn trees_find_all_nodes(t: ParseTree, index: i32, findTokens: bool, nodes: * Vec<ParseTree>) { unimplemented!() }
//
//pub fn descendants(t: impl ParseTree) -> Vec<dyn ParseTree> { unimplemented!() }
