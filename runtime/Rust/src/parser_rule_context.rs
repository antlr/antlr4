//! Full parser node
use std::any::type_name;
use std::borrow::{Borrow, BorrowMut};
use std::cell::Cell;
use std::fmt::{Debug, Error, Formatter};
use std::ops::{Deref, DerefMut};

use crate::errors::ANTLRError;
use crate::rule_context::{
    BaseRuleContext, CustomRuleContext, EmptyCustomRuleContext, RuleContext,
};
use crate::token::Token;
use crate::tree::{NodeInner, RuleNode as _, TerminalNode, Tree as _};
use crate::{token_factory, Arena};

/// Syntax tree node for particular parser rule.
///
/// Not yet good for custom implementations so currently easiest option
/// is to just copy `BaseParserRuleContext` or `BaseRuleContext` and strip/extend them
#[allow(missing_docs)]
pub trait ParserRuleContext<'input, 'arena>: RuleContext<'input, 'arena> + Debug
where
    'input: 'arena,
{
    /// Get the initial token in this context.
    ///
    /// Note that the range from start to stop is inclusive, so for rules that do not consume anything
    /// (for example, zero length or error productions) this token may exceed stop.
    fn start(&self) -> &'arena dyn Token {
        unimplemented!()
    }

    /// Get the final token in this context.
    ///
    /// Note that the range from start to stop is inclusive, so for rules that do not consume anything
    /// (for example, zero length or error productions) this token may precede start.
    fn stop(&self) -> &'arena dyn Token {
        unimplemented!()
    }

    fn get_parent_ctx(&self) -> Option<&'arena dyn ParserRuleContext<'input, 'arena>> {
        None
    }

    fn get_child_ctx(&self, _i: usize) -> Option<&'arena dyn ParserRuleContext<'input, 'arena>> {
        None
    }

    fn get_child_count(&self) -> usize;

    fn iter_children<'a>(
        &'a self,
    ) -> Box<dyn Iterator<Item = &'arena dyn ParserRuleContext<'input, 'arena>> + 'a>
    where
        'input: 'a,
        'arena: 'a,
    {
        Box::new(std::iter::empty())
    }

    fn get_token(&self, _ttype: i32, _pos: usize) -> Option<&TerminalNode<'input, 'arena>> {
        None
    }

    fn get_tokens(&self, _ttype: i32) -> Vec<&TerminalNode<'input, 'arena>> {
        vec![]
    }

    /// Return combined text of this AST node.
    /// To create resulting string it does traverse whole subtree,
    /// also it includes only tokens added to the parse tree
    ///
    /// Since tokens on hidden channels (e.g. whitespace or comments) are not
    /// added to the parse trees, they will not appear in the output of this
    /// method.
    fn get_text(&self) -> String;

    /// Print out a whole tree, not just a node, in LISP format `(root child1 ..
    /// childN)`. Print just a node if this is a leaf.
    fn to_string_tree(&self, rule_names: &[&str]) -> String {
        crate::trees::string_tree(self, rule_names)
    }
}

pub type EmptyParserRuleContext<'input, 'arena> =
    BaseParserRuleContext<'input, 'arena, EmptyCustomRuleContext<'input, 'arena>>;

/// Default rule context implementation that keeps everything provided by parser
pub struct BaseParserRuleContext<'input, 'arena, Ext>
where
    Ext: CustomRuleContext<'input, 'arena>,
{
    pub(crate) base: BaseRuleContext<'input, 'arena, Ext>,

    start: &'arena dyn Token,
    stop: &'arena dyn Token,
    /// error if there was any in this node
    pub exception: Cell<Option<bumpalo::boxed::Box<'arena, ANTLRError>>>,
    /// List of children of current node
    pub(crate) children: bumpalo::collections::Vec<'arena, &'arena Ext::Node>,
}

impl<'input, 'arena, Ext> Debug for BaseParserRuleContext<'input, 'arena, Ext>
where
    Ext: CustomRuleContext<'input, 'arena>,
{
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        f.write_str(type_name::<Self>())
    }
}

impl<'input, 'arena, Ext> RuleContext<'input, 'arena> for BaseParserRuleContext<'input, 'arena, Ext>
where
    'input: 'arena,
    Ext: CustomRuleContext<'input, 'arena> + 'arena,
{
    fn get_invoking_state(&self) -> i32 {
        self.base.get_invoking_state()
    }

    fn get_parent_ctx(&self) -> Option<&'arena dyn RuleContext<'input, 'arena>> {
        self.base.get_parent_ctx()
    }

    fn get_rule_index(&self) -> usize {
        self.base.get_rule_index()
    }

    fn get_alt_number(&self) -> i32 {
        self.base.get_alt_number()
    }
}

impl<'input, 'arena, Ext> Deref for BaseParserRuleContext<'input, 'arena, Ext>
where
    Ext: CustomRuleContext<'input, 'arena>,
{
    type Target = Ext;
    fn deref(&self) -> &Self::Target {
        &self.base.ext
    }
}

impl<'input, 'arena, Ext> DerefMut for BaseParserRuleContext<'input, 'arena, Ext>
where
    Ext: CustomRuleContext<'input, 'arena>,
{
    fn deref_mut(&mut self) -> &mut Self::Target {
        &mut self.base.ext
    }
}

impl<'input, 'arena, Ext> Borrow<Ext> for BaseParserRuleContext<'input, 'arena, Ext>
where
    Ext: CustomRuleContext<'input, 'arena>,
{
    fn borrow(&self) -> &Ext {
        &self.base.ext
    }
}

impl<'input, 'arena, Ext> BorrowMut<Ext> for BaseParserRuleContext<'input, 'arena, Ext>
where
    Ext: CustomRuleContext<'input, 'arena>,
{
    fn borrow_mut(&mut self) -> &mut Ext {
        &mut self.base.ext
    }
}

impl<'input, 'arena, Ext> ParserRuleContext<'input, 'arena>
    for BaseParserRuleContext<'input, 'arena, Ext>
where
    'input: 'arena,
    Ext: CustomRuleContext<'input, 'arena> + 'arena,
{
    #[inline]
    fn start(&self) -> &'arena dyn Token {
        self.start
    }

    #[inline]
    fn stop(&self) -> &'arena dyn Token {
        self.stop
    }

    fn get_parent_ctx(&self) -> Option<&'arena dyn ParserRuleContext<'input, 'arena>> {
        self.base
            .parent()
            .map(move |rc| rc as &dyn ParserRuleContext<'input, 'arena>)
    }

    fn get_child_ctx(&self, i: usize) -> Option<&'arena dyn ParserRuleContext<'input, 'arena>> {
        self.children
            .get(i)
            .map(|item| *item as &dyn ParserRuleContext<'input, 'arena>)
    }

    fn get_child_count(&self) -> usize {
        self.children.len()
    }

    fn iter_children<'a>(
        &'a self,
    ) -> Box<dyn Iterator<Item = &'arena dyn ParserRuleContext<'input, 'arena>> + 'a>
    where
        'input: 'a,
        'arena: 'a,
    {
        Box::new(
            self.children
                .iter()
                .map(|item| *item as &dyn ParserRuleContext<'input, 'arena>)
                .collect::<Vec<_>>()
                .into_iter(),
        )
    }

    fn get_token(&self, ttype: i32, pos: usize) -> Option<&TerminalNode<'input, 'arena>> {
        self.children
            .iter()
            .filter_map(|it| it.as_terminal_node())
            .filter(|it| it.symbol.get_token_type() == ttype)
            .nth(pos)
    }

    fn get_tokens(&self, ttype: i32) -> Vec<&TerminalNode<'input, 'arena>> {
        self.children
            .iter()
            .filter_map(|it| it.as_terminal_node())
            .filter(|it| it.symbol.get_token_type() == ttype)
            .collect()
    }

    fn get_text(&self) -> String {
        let mut result = String::new();

        for child in self.children.iter() {
            result += &child.get_text()
        }

        result
    }
}

impl<'input, 'arena, Ctx> NodeInner<'input, 'arena, Ctx::Node>
    for BaseParserRuleContext<'input, 'arena, Ctx>
where
    Ctx: CustomRuleContext<'input, 'arena> + 'arena,
{
    fn cast_from(node: &Ctx::Node) -> Option<&Self> {
        Ctx::base_ref_from_node(node)
    }

    fn cast_from_mut(node: &mut Ctx::Node) -> Option<&mut Self>
    where
        Self: Sized,
    {
        Ctx::base_mut_ref_from_node(node)
    }

    fn iter_child_nodes<'a>(&'a self) -> Box<dyn Iterator<Item = &'arena Ctx::Node> + 'a> {
        self.get_children()
    }

    fn try_as_node(&'arena self) -> Option<&'arena Ctx::Node> {
        self.base.try_as_node()
    }
}

#[allow(missing_docs)]
impl<'input, 'arena, Ext> BaseParserRuleContext<'input, 'arena, Ext>
where
    Ext: CustomRuleContext<'input, 'arena> + 'arena,
{
    pub fn new(
        arena: &'arena Arena,
        parent: Option<&'arena Ext::Node>,
        invoking_state: i32,
        ext: Ext,
    ) -> Self {
        Self {
            base: BaseRuleContext::new(parent, invoking_state, ext),
            start: token_factory::invalid(),
            stop: token_factory::invalid(),
            exception: Cell::new(None),
            children: bumpalo::vec![in arena.children_arena()],
        }
    }

    pub fn copy_from<Src>(
        node: BaseParserRuleContext<'input, 'arena, Src>,
        ctor: impl FnOnce(Src) -> Ext,
    ) -> Self
    where
        Src: CustomRuleContext<'input, 'arena, Node = Ext::Node>,
    {
        Self {
            base: BaseRuleContext::copy_from(node.base, ctor),
            start: node.start,
            stop: node.stop,
            exception: Cell::new(None),
            children: node.children,
        }
    }

    pub fn morph<Tgt>(
        self,
        ctor: impl FnOnce(Ext) -> Tgt,
    ) -> BaseParserRuleContext<'input, 'arena, Tgt>
    where
        Tgt: CustomRuleContext<'input, 'arena, Node = Ext::Node>,
    {
        BaseParserRuleContext {
            base: self.base.morph(ctor),
            start: self.start,
            stop: self.stop,
            exception: self.exception,
            children: self.children,
        }
    }

    pub fn get_parent(&self) -> Option<&'arena Ext::Node> {
        self.base.parent()
    }

    pub fn has_parent(&self) -> bool {
        self.base.has_parent()
    }

    pub fn set_self_ref(&mut self, self_ref: *const Ext::Node) {
        self.base.set_self_ref(self_ref);
    }

    pub fn set_parent(&mut self, parent: Option<&'arena Ext::Node>) {
        self.base.set_parent(parent);
    }

    pub fn set_exception(&self, e: ANTLRError, arena: &'arena Arena) {
        self.exception.set(Some(arena.alloc_exception(e)));
    }

    pub fn set_invoking_state(&mut self, t: i32) {
        self.base.set_invoking_state(t)
    }

    pub fn set_alt_number(&mut self, _alt_number: i32) {
        self.base.set_alt_number(_alt_number)
    }

    pub fn set_start(&mut self, t: Option<&'arena dyn Token>) {
        self.start = t.unwrap_or_else(|| token_factory::invalid());
    }

    pub fn set_stop(&mut self, t: Option<&'arena dyn Token>) {
        self.stop = t.unwrap_or_else(|| token_factory::invalid());
    }

    pub fn remove_last_child(&mut self) {
        self.children.pop();
    }

    pub fn add_child(&mut self, child: &'arena Ext::Node) {
        self.children.push(child);
    }

    pub fn get_child(&self, i: usize) -> Option<&'arena Ext::Node> {
        self.children.get(i).copied()
    }

    pub fn get_children<'a>(&'a self) -> Box<dyn Iterator<Item = &'arena Ext::Node> + 'a> {
        let mut index = 0;
        let iter = std::iter::from_fn(move || {
            if index < self.get_child_count() {
                index += 1;
                self.get_child(index - 1)
            } else {
                None
            }
        });

        Box::new(iter)
    }

    pub fn child_of_type<'a, T>(&'a self, pos: usize) -> Option<&'arena T>
    where
        'input: 'a,
        'arena: 'a,
        T: ParserRuleContext<'input, 'arena> + NodeInner<'input, 'arena, Ext::Node>,
    {
        self.children
            .iter()
            .filter_map(|it| it.as_rule_context::<T>())
            .nth(pos)
    }

    // todo, return iterator
    pub fn children_of_type<'a, T>(&'a self) -> Vec<&'arena T>
    where
        'input: 'a,
        'arena: 'a,
        T: ParserRuleContext<'input, 'arena> + NodeInner<'input, 'arena, Ext::Node>,
    {
        self.children
            .iter()
            .filter_map(|it| it.as_rule_context::<T>())
            .collect()
    }

    // pub fn to_string(self: Rc<Self>, rule_names: Option<&[&str]>, stop: Option<Rc<Ctx::Ctx::Type>>) -> String {
    //     (self as Rc<dyn ParserRuleContext>).to_string(rule_names, stop)
    // }

    /// Prints list of parent rules
    pub fn to_string(
        &'arena self,
        rule_names: Option<&[&str]>,
        stop: Option<&'arena Ext::Node>,
    ) -> String {
        let mut result = String::from("[");
        let mut next = Some(self.try_as_node().unwrap());
        while let Some(p) = next {
            if stop.is_some_and(|s| std::ptr::eq(s, p)) {
                break;
            }

            if let Some(rule_names) = rule_names {
                let rule_index = p.get_rule_index();
                let rule_name = rule_names
                    .get(rule_index)
                    .map(|&it| it.to_owned())
                    .unwrap_or_else(|| rule_index.to_string());
                result.push_str(&rule_name);
                result.push(' ');
            } else if !p.is_empty() {
                result.push_str(&p.get_invoking_state().to_string());
                result.push(' ');
            }

            next = p.get_parent();
        }

        if result.ends_with(' ') {
            result.pop();
        }

        result.push(']');
        result
    }
}
