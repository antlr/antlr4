//! Minimal parser node
use std::borrow::{Borrow, BorrowMut};
use std::fmt::{Debug, Formatter};
use std::iter::from_fn;
use std::marker::PhantomData;

use crate::atn::INVALID_ALT;
use crate::errors::ANTLRError;
use crate::parser_rule_context::{
    BaseParserRuleContext, EmptyParserRuleContext, ParserRuleContext,
};
use crate::tree::{
    ErrorNode, NodeInner, ParseTree, ParseTreeListener, RuleNode, TerminalNode, Tree,
};
use crate::{
    impl_defaults, impl_parse_tree, impl_parser_rule_context, impl_rule_context, impl_rule_node,
    impl_tree,
};
use std::any::type_name;

/// Minimal rule context functionality required for parser to work properly
pub trait RuleContext<'input, 'arena>
where
    'input: 'arena,
{
    /// Internal parser state
    fn get_invoking_state(&self) -> i32;

    /// A context is empty if there is no invoking state; meaning nobody called
    /// current context. Which is usually true for the root of the syntax tree
    fn is_empty(&self) -> bool {
        self.get_invoking_state() == -1
    }

    /// Get parent context
    fn get_parent_ctx(&self) -> Option<&'arena dyn RuleContext<'input, 'arena>>;

    /// Rule index that corresponds to this context type
    fn get_rule_index(&self) -> usize;

    fn get_alt_number(&self) -> i32;

    /// Returns text representation of current node type,
    /// rule name for context nodes and token text for terminal nodes
    fn get_node_text(&self, rule_names: &[&str]) -> String {
        let rule_index = self.get_rule_index();
        let rule_name = rule_names[rule_index];
        let alt_number = self.get_alt_number();
        if alt_number != INVALID_ALT {
            return format!("{}:{}", rule_name, alt_number);
        }
        rule_name.to_owned()
    }
}

pub(crate) fn states_stack<'input, 'arena, Node>(
    mut node: &'arena Node,
) -> impl Iterator<Item = i32> + 'arena
where
    'input: 'arena,
    Node: RuleNode<'input, 'arena> + 'arena,
{
    from_fn(move || {
        if node.get_invoking_state() < 0 {
            None
        } else {
            let state = node.get_invoking_state();
            node = Tree::get_parent(node).unwrap();
            Some(state)
        }
    })
}

/// Implemented by generated parser for context extension for particular rule
#[allow(missing_docs)]
pub trait CustomRuleContext<'input, 'arena>
where
    'input: 'arena,
{
    type Node: RuleNode<'input, 'arena>;

    /// Rule index that corresponds to this context type
    fn get_rule_index(&self) -> usize;

    /// For rule associated with this parse tree internal node, return the outer
    /// alternative number used to match the input. Default implementation does
    /// not compute nor store this alt num. Create a subclass of
    /// ParserRuleContext with backing field and set option contextSuperClass.
    /// to set it.
    ///
    /// @since 4.5.3
    fn get_alt_number(&self) -> i32 {
        INVALID_ALT
    }

    /// Set the outer alternative number for this context node. Default
    /// implementation does nothing to avoid backing field overhead for trees
    /// that don't need it.  Create a subclass of ParserRuleContext with backing
    /// field and set option contextSuperClass.
    ///
    /// @since 4.5.3
    fn set_alt_number(&mut self, _alt_number: i32) {}

    /// Returns text representation of current node type,
    /// rule name for context nodes and token text for terminal nodes
    fn get_node_text(&self, rule_names: &[&str]) -> String {
        let rule_index = self.get_rule_index();
        let rule_name = rule_names[rule_index];
        let alt_number = self.get_alt_number();
        if alt_number != INVALID_ALT {
            return format!("{}:{}", rule_name, alt_number);
        }
        rule_name.to_owned()
    }

    /// Extract a reference to the [BaseParserRuleContext] from a given tree
    /// node
    fn base_ref_from_node(
        node: &Self::Node,
    ) -> Option<&BaseParserRuleContext<'input, 'arena, Self>>
    where
        Self: Sized;

    fn base_mut_ref_from_node(
        node: &mut Self::Node,
    ) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>>
    where
        Self: Sized;
}

#[derive(Debug)]
#[doc(hidden)]
pub struct EmptyCustomRuleContext<'input, 'arena>(pub(crate) PhantomData<(&'arena (), &'input ())>);

#[derive(Debug)]
pub enum EmptyRuleNode<'input, 'arena> {
    Empty(EmptyParserRuleContext<'input, 'arena>),
    Terminal(TerminalNode<'input, 'arena>),
    Error(ErrorNode<'input, 'arena>),
}

impl_tree! { EmptyRuleNode { Empty, } }
// impl<'input, 'arena> Tree<'arena> for EmptyRuleNode<'input, 'arena> {
//     fn get_parent(&self) -> Option<&'arena Self> {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ctx.get_parent(),
//             _ => None,
//         }
//     }

//     fn has_parent(&self) -> bool {
//         self.get_parent().is_some()
//     }

//     fn get_payload(&self) -> Box<dyn std::any::Any> {
//         Box::new(())
//     }

//     fn get_child(&self, i: usize) -> Option<&'arena Self> {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ctx.get_child(i),
//             _ => None,
//         }
//     }

//     fn get_child_count(&self) -> usize {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ctx.get_child_count(),
//             _ => 0,
//         }
//     }

//     fn get_children<'a>(&'a self) -> Box<dyn Iterator<Item = &'arena Self> + 'a> {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ctx.get_children(),
//             _ => Box::new(std::iter::empty()) as Box<dyn Iterator<Item = &'arena Self>>,
//         }
//     }

//     fn to_string_tree(&self) -> String {
//         std::unimplemented!()
//     }
// }

impl_parse_tree! { EmptyRuleNode { Empty, } }
// impl<'input, 'arena> ParseTree<'input, 'arena> for EmptyRuleNode<'input, 'arena> {
//     fn get_source_interval(&self) -> crate::interval_set::Interval {
//         crate::interval_set::INVALID
//     }

//     fn get_text(&self) -> String {
//         String::new()
//     }
// }

impl_rule_context! { EmptyRuleNode { Empty, Terminal, Error, } }
// impl<'input, 'arena> RuleContext<'input, 'arena> for EmptyRuleNode<'input, 'arena> {
//     fn get_rule_index(&self) -> usize {
//         match self {
//             EmptyRuleNode::Empty(ctx) => RuleContext::get_rule_index(ctx),
//             EmptyRuleNode::Terminal(leaf_node) => leaf_node.get_rule_index(),
//             EmptyRuleNode::Error(leaf_node) => leaf_node.get_rule_index(),
//         }
//     }

//     fn get_alt_number(&self) -> i32 {
//         match self {
//             EmptyRuleNode::Empty(ctx) => RuleContext::get_alt_number(ctx),
//             EmptyRuleNode::Terminal(leaf_node) => leaf_node.get_alt_number(),
//             EmptyRuleNode::Error(leaf_node) => leaf_node.get_alt_number(),
//         }
//     }

//     fn set_alt_number(&self, _alt_number: i32) {
//         match self {
//             EmptyRuleNode::Empty(ctx) => RuleContext::set_alt_number(ctx, _alt_number),
//             EmptyRuleNode::Terminal(leaf_node) => leaf_node.set_alt_number(_alt_number),
//             EmptyRuleNode::Error(leaf_node) => leaf_node.set_alt_number(_alt_number),
//         }
//     }

//     fn get_invoking_state(&self) -> i32 {
//         match self {
//             EmptyRuleNode::Empty(ctx) => RuleContext::get_invoking_state(ctx),
//             EmptyRuleNode::Terminal(leaf_node) => leaf_node.get_invoking_state(),
//             EmptyRuleNode::Error(leaf_node) => leaf_node.get_invoking_state(),
//         }
//     }

//     fn set_invoking_state(&self, _t: i32) {
//         match self {
//             EmptyRuleNode::Empty(ctx) => RuleContext::set_invoking_state(ctx, _t),
//             EmptyRuleNode::Terminal(leaf_node) => leaf_node.set_invoking_state(_t),
//             EmptyRuleNode::Error(leaf_node) => leaf_node.set_invoking_state(_t),
//         }
//     }

//     fn is_empty(&self) -> bool {
//         self.get_invoking_state() == -1
//     }

//     fn get_parent_ctx(&self) -> Option<&'arena dyn RuleContext<'input, 'arena>> {
//         match self {
//             EmptyRuleNode::Empty(ctx) => RuleContext::get_parent_ctx(ctx),
//             _ => None,
//         }
//     }

//     fn get_node_text(&self, rule_names: &[&str]) -> String {
//         match self {
//             EmptyRuleNode::Empty(ctx) => RuleContext::get_node_text(ctx, rule_names),
//             EmptyRuleNode::Terminal(leaf_node) => leaf_node.get_node_text(rule_names),
//             EmptyRuleNode::Error(leaf_node) => leaf_node.get_node_text(rule_names),
//         }
//     }
// }

impl_parser_rule_context!(EmptyRuleNode {
    Empty,
    Terminal,
    Error,
});
// impl<'input, 'arena> ParserRuleContext<'input, 'arena> for EmptyRuleNode<'input, 'arena> {
//     fn set_exception(&self, _e: crate::errors::ANTLRError) {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ParserRuleContext::set_exception(ctx, _e),
//             EmptyRuleNode::Terminal(leaf_node) => leaf_node.set_exception(_e),
//             EmptyRuleNode::Error(leaf_node) => leaf_node.set_exception(_e),
//         }
//     }

//     fn set_start(&self, _t: Option<&'arena dyn crate::token::Token>) {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ParserRuleContext::set_start(ctx, _t),
//             EmptyRuleNode::Terminal(leaf_node) => leaf_node.set_start(_t),
//             EmptyRuleNode::Error(leaf_node) => leaf_node.set_start(_t),
//         }
//     }

//     fn start(&self) -> &'arena dyn crate::token::Token {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ctx.start(),
//             EmptyRuleNode::Terminal(leaf_node) => leaf_node.start(),
//             EmptyRuleNode::Error(leaf_node) => leaf_node.start(),
//         }
//     }

//     fn set_stop(&self, _t: Option<&'arena dyn crate::token::Token>) {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ParserRuleContext::set_stop(ctx, _t),
//             EmptyRuleNode::Terminal(leaf_node) => leaf_node.set_stop(_t),
//             EmptyRuleNode::Error(leaf_node) => leaf_node.set_stop(_t),
//         }
//     }

//     fn stop(&self) -> &'arena dyn crate::token::Token {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ctx.stop(),
//             EmptyRuleNode::Terminal(leaf_node) => leaf_node.stop(),
//             EmptyRuleNode::Error(leaf_node) => leaf_node.stop(),
//         }
//     }

//     fn remove_last_child(&self) {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ctx.remove_last_child(),
//             _ => {}
//         }
//     }

//     fn get_parent_ctx(&self) -> Option<&'arena dyn ParserRuleContext<'input, 'arena>> {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ParserRuleContext::get_parent_ctx(ctx),
//             _ => None,
//         }
//     }

//     fn get_child_ctx(&self, _i: usize) -> Option<&'arena dyn ParserRuleContext<'input, 'arena>> {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ParserRuleContext::get_child_ctx(ctx, _i),
//             _ => None,
//         }
//     }

//     fn get_child_count(&self) -> usize {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ParserRuleContext::get_child_count(ctx),
//             _ => 0,
//         }
//     }

//     fn iter_children<'a>(
//         &'a self,
//     ) -> Box<dyn Iterator<Item = &'arena dyn ParserRuleContext<'input, 'arena>> + 'a>
//     where
//         'input: 'a,
//         'arena: 'a,
//     {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ctx.iter_children(),
//             _ => Box::new(std::iter::empty())
//                 as Box<dyn Iterator<Item = &'arena dyn ParserRuleContext<'input, 'arena>>>,
//         }
//     }

//     fn get_token(&self, _ttype: i32, _pos: usize) -> Option<&TerminalNode<'input, 'arena>> {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ctx.get_token(_ttype, _pos),
//             _ => None,
//         }
//     }

//     fn get_tokens(&self, _ttype: i32) -> Vec<&TerminalNode<'input, 'arena>> {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ctx.get_tokens(_ttype),
//             _ => vec![],
//         }
//     }

//     fn get_text(&self) -> String {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ctx.get_text(),
//             EmptyRuleNode::Terminal(leaf_node) => leaf_node.get_text(),
//             EmptyRuleNode::Error(leaf_node) => leaf_node.get_text(),
//         }
//     }
// }

pub trait EmptyListener<'input, 'arena>:
    ParseTreeListener<'input, 'arena, EmptyRuleNode<'input, 'arena>>
where
    'input: 'arena,
{
    fn enter_empty(
        &mut self,
        _ctx: &EmptyParserRuleContext<'input, 'arena>,
    ) -> Result<(), ANTLRError> {
        Ok(())
    }

    fn exit_empty(
        &mut self,
        _ctx: &EmptyParserRuleContext<'input, 'arena>,
    ) -> Result<(), ANTLRError> {
        Ok(())
    }
}

pub trait EmptyVisitor<'input, 'arena>
where
    'input: 'arena,
{
    type Return: Default;

    fn visit(&mut self, node: &EmptyRuleNode<'input, 'arena>) -> Result<Self::Return, ANTLRError>;

    /// Called on terminal(leaf) node
    fn visit_terminal(
        &mut self,
        _node: &TerminalNode<'input, 'arena>,
    ) -> Result<Self::Return, ANTLRError> {
        Ok(Self::Return::default())
    }

    /// Called on error node
    fn visit_error_node(
        &mut self,
        _node: &ErrorNode<'input, 'arena>,
    ) -> Result<Self::Return, ANTLRError> {
        Ok(Self::Return::default())
    }

    fn visit_children(
        &mut self,
        node: &dyn NodeInner<'input, 'arena, EmptyRuleNode<'input, 'arena>>,
    ) -> Result<Self::Return, ANTLRError> {
        let mut result = Self::Return::default();
        for child in node.iter_child_nodes() {
            if !self.should_visit_next_child(child, &result) {
                break;
            }

            let child_result = self.visit(child)?;
            result = self.aggregate_results(result, child_result)?;
        }
        Ok(result)
    }

    fn aggregate_results(
        &self,
        _aggregate: Self::Return,
        next: Self::Return,
    ) -> Result<Self::Return, ANTLRError> {
        Ok(next)
    }

    fn should_visit_next_child(
        &self,
        _node: &EmptyRuleNode<'input, 'arena>,
        _current: &Self::Return,
    ) -> bool {
        true
    }

    fn visit_empty(
        &mut self,
        ctx: &EmptyParserRuleContext<'input, 'arena>,
    ) -> Result<Self::Return, ANTLRError> {
        self.visit_children(ctx)
    }
}

pub trait Visitable<'input, 'arena>
where
    'input: 'arena,
{
    fn accept<V>(
        &'arena self,
        visitor: &mut V,
    ) -> Result<<V as EmptyVisitor<'input, 'arena>>::Return, ANTLRError>
    where
        V: EmptyVisitor<'input, 'arena> + ?Sized;
}

impl_rule_node!(EmptyRuleNode { ; Empty(enter_empty, exit_empty, visit_empty), }; listener = dyn EmptyListener<'input, 'arena>, visitor = EmptyVisitor, );
// impl<'input, 'arena> RuleNode<'input, 'arena> for EmptyRuleNode<'input, 'arena> {
//     type Listener = dyn crate::tree::ParseTreeListener<'input, 'arena, Self>;
//     type Visitor = dyn crate::tree::ParseTreeVisitor<'input, 'arena, Self, Return = ()>;

//     fn get_rule_context(&self) -> &dyn ParserRuleContext<'input, 'arena> {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ctx as &dyn ParserRuleContext<'input, 'arena>,
//             EmptyRuleNode::Terminal(leaf_node) => {
//                 leaf_node as &dyn ParserRuleContext<'input, 'arena>
//             }
//             EmptyRuleNode::Error(leaf_node) => {
//                 &*leaf_node as &dyn ParserRuleContext<'input, 'arena>
//             }
//         }
//     }

//     fn as_terminal_node(&self) -> Option<&TerminalNode<'input, 'arena>> {
//         match self {
//             EmptyRuleNode::Terminal(leaf_node) => Some(leaf_node),
//             _ => None,
//         }
//     }

//     fn as_error_node(&self) -> Option<&ErrorNode<'input, 'arena>> {
//         match self {
//             EmptyRuleNode::Error(leaf_node) => Some(leaf_node),
//             _ => None,
//         }
//     }

//     fn add_child(&self, _child: &'arena Self) {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ctx.add_child(_child),
//             _ => {}
//         }
//     }

//     fn set_parent(&self, _parent: Option<&'arena Self>) {
//         match self {
//             EmptyRuleNode::Empty(ctx) => ctx.set_parent(_parent),
//             _ => {}
//         }
//     }

//     fn enter_rule(&self, listener: &mut Self::Listener) -> Result<(), ANTLRError> {
//         match self {
//             EmptyRuleNode::Empty(_) => Ok(()),
//             EmptyRuleNode::Terminal(leaf_node) => {
//                 listener.visit_terminal(leaf_node)?;
//                 Ok(())
//             }
//             EmptyRuleNode::Error(leaf_node) => {
//                 listener.visit_error_node(leaf_node)?;
//                 Ok(())
//             }
//         }
//     }

//     fn exit_rule(&self, listener: &mut Self::Listener) -> Result<(), ANTLRError> {
//         match self {
//             EmptyRuleNode::Empty(_) => Ok(()),
//             EmptyRuleNode::Terminal(leaf_node) => {
//                 listener.visit_terminal(leaf_node)?;
//                 Ok(())
//             }
//             EmptyRuleNode::Error(leaf_node) => {
//                 listener.visit_error_node(leaf_node)?;
//                 Ok(())
//             }
//         }
//     }

//     fn accept(&self, visitor: &mut Self::Visitor) -> Result<(), ANTLRError> {
//         match self {
//             EmptyRuleNode::Empty(_) => Ok(()),
//             EmptyRuleNode::Terminal(leaf_node) => visitor.visit_terminal(leaf_node),
//             EmptyRuleNode::Error(leaf_node) => visitor.visit_error_node(leaf_node),
//         }
//     }
// }

impl_defaults!(EmptyRuleNode);

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for EmptyCustomRuleContext<'input, 'arena>
where
    'input: 'arena,
{
    type Node = EmptyRuleNode<'input, 'arena>;

    fn get_rule_index(&self) -> usize {
        usize::MAX
    }

    fn base_ref_from_node(
        node: &Self::Node,
    ) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            EmptyRuleNode::Empty(ctx) => Some(ctx),
            _ => None,
        }
    }

    fn base_mut_ref_from_node(
        node: &mut Self::Node,
    ) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            EmptyRuleNode::Empty(ctx) => Some(ctx),
            _ => None,
        }
    }
}

pub type EmptyRuleContext<'input, 'arena> =
    BaseRuleContext<'input, 'arena, EmptyCustomRuleContext<'input, 'arena>>;

/// Minimal parse tree node implementation, that stores only data required for correct parsing
pub struct BaseRuleContext<'input, 'arena, ExtCtx>
where
    ExtCtx: CustomRuleContext<'input, 'arena>,
{
    parent: Option<&'arena ExtCtx::Node>,
    self_ref: *const ExtCtx::Node,
    invoking_state: i32,
    pub(crate) ext: ExtCtx,
}

#[allow(missing_docs)]
impl<'input, 'arena, Ext> BaseRuleContext<'input, 'arena, Ext>
where
    Ext: CustomRuleContext<'input, 'arena>,
{
    pub(crate) fn new(parent: Option<&'arena Ext::Node>, invoking_state: i32, ext: Ext) -> Self {
        Self {
            parent,
            self_ref: std::ptr::null(),
            invoking_state,
            ext,
        }
    }

    pub(crate) fn copy_from<Src>(
        node: BaseRuleContext<'input, 'arena, Src>,
        ctor: impl FnOnce(Src) -> Ext,
    ) -> Self
    where
        Src: CustomRuleContext<'input, 'arena, Node = Ext::Node>,
    {
        Self {
            parent: node.parent,
            // Note: we inherit self_ref as `copy_from`'d contexts are meant to
            // be directly overwritten into the same node location. Needless to
            // say, unsafe AF this is.
            self_ref: node.self_ref,
            invoking_state: node.invoking_state,
            ext: ctor(node.ext),
        }
    }

    pub(crate) fn morph<Tgt>(
        self,
        ctor: impl FnOnce(Ext) -> Tgt,
    ) -> BaseRuleContext<'input, 'arena, Tgt>
    where
        Tgt: CustomRuleContext<'input, 'arena, Node = Ext::Node>,
    {
        BaseRuleContext {
            parent: self.parent,
            self_ref: self.self_ref,
            invoking_state: self.invoking_state,
            ext: ctor(self.ext),
        }
    }

    #[inline]
    pub fn parent(&self) -> Option<&'arena Ext::Node> {
        self.parent
    }

    #[inline]
    pub fn has_parent(&self) -> bool {
        self.parent.is_some()
    }

    #[inline]
    pub fn try_as_node(&self) -> Option<&'arena Ext::Node> {
        if self.self_ref.is_null() {
            None
        } else {
            unsafe { Some(&*self.self_ref) }
        }
    }

    pub(crate) fn set_self_ref(&mut self, self_ref: *const Ext::Node) {
        self.self_ref = self_ref;
    }

    pub(crate) fn set_parent(&mut self, parent: Option<&'arena Ext::Node>) {
        self.parent = parent;
    }

    pub(crate) fn set_invoking_state(&mut self, t: i32) {
        self.invoking_state = t;
    }

    pub(crate) fn set_alt_number(&mut self, _alt_number: i32) {
        self.ext.set_alt_number(_alt_number)
    }
}

impl<'input, 'arena, ExtCtx> Borrow<ExtCtx> for BaseRuleContext<'input, 'arena, ExtCtx>
where
    ExtCtx: CustomRuleContext<'input, 'arena>,
{
    fn borrow(&self) -> &ExtCtx {
        &self.ext
    }
}

impl<'input, 'arena, ExtCtx> BorrowMut<ExtCtx> for BaseRuleContext<'input, 'arena, ExtCtx>
where
    ExtCtx: CustomRuleContext<'input, 'arena>,
{
    fn borrow_mut(&mut self) -> &mut ExtCtx {
        &mut self.ext
    }
}

impl<'input, 'arena, Ctx> RuleContext<'input, 'arena> for BaseRuleContext<'input, 'arena, Ctx>
where
    'input: 'arena,
    Ctx: CustomRuleContext<'input, 'arena> + 'arena,
{
    #[inline(always)]
    fn get_invoking_state(&self) -> i32 {
        self.invoking_state
    }

    fn get_parent_ctx(&self) -> Option<&'arena dyn RuleContext<'input, 'arena>> {
        self.parent.map(|rc| rc as &dyn RuleContext<'input, 'arena>)
    }

    fn get_rule_index(&self) -> usize {
        self.ext.get_rule_index()
    }

    fn get_alt_number(&self) -> i32 {
        self.ext.get_alt_number()
    }

    fn is_empty(&self) -> bool {
        self.get_invoking_state() == -1
    }

    fn get_node_text(&self, rule_names: &[&str]) -> String {
        self.ext.get_node_text(rule_names)
    }
}

impl<'input, 'arena, ExtCtx> Debug for BaseRuleContext<'input, 'arena, ExtCtx>
where
    ExtCtx: CustomRuleContext<'input, 'arena>,
{
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        f.debug_struct(type_name::<Self>())
            .field("invoking_state", &self.invoking_state)
            .field("..", &"..")
            .finish()
    }
}
