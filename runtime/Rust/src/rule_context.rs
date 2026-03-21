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

/// Language-agnostic, dyn-compatible, read-only interface to the AST.
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
impl_parse_tree! { EmptyRuleNode { Empty, } }
impl_rule_context! { EmptyRuleNode { Empty, Terminal, Error, } }
impl_parser_rule_context!(EmptyRuleNode {
    Empty,
    Terminal,
    Error,
});

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

pub type EmptyRuleContext<'input, 'arena> = BaseRuleContextInner<
    'input,
    'arena,
    EmptyCustomRuleContext<'input, 'arena>,
    EmptyRuleNode<'input, 'arena>,
>;

/// Core rule context implementation -- this defines the minimal set of states
/// required for the Antlr parsing algorithm to function.
///
/// This is the Rust version of the `RuleContext` "abstract base class", it will
/// be specialized into language-specific concrete types by monomorphizing the
/// `ExtCtx` type parameter, which is implemented by generated code.
pub struct BaseRuleContextInner<'input, 'arena, ExtCtx, Node>
where
    'input: 'arena,
    ExtCtx: CustomRuleContext<'input, 'arena, Node = Node>,
    // Note: `Node` is redundant as a type parameter -- its sole purpose here is
    // to "lift" out the `ExtCtx::Node` associated type, to work around the
    // limitation that Rust's variance propagation doesn't work over type
    // projections. Without this, all rule context types would be invariant over
    // 'input and 'arena.
    Node: RuleNode<'input, 'arena>,
{
    parent: Option<&'arena Node>,
    invoking_state: i32,
    pub(crate) ext: ExtCtx,

    // This is an unfortunate memory overhead, however it's the price we have to
    // pay to support dynamic type casting, which is so central to Antlr's AST
    // API.
    self_ref: *const Node,

    // Carries 'input so Rust sees it as covariant rather than unused/invariant.
    _input: PhantomData<&'input ()>,
}

/// Convenience alias — resolves the `Node` parameter automatically from `ExtCtx::Node`.
pub type BaseRuleContext<'input, 'arena, ExtCtx> = BaseRuleContextInner<
    'input,
    'arena,
    ExtCtx,
    <ExtCtx as CustomRuleContext<'input, 'arena>>::Node,
>;

#[allow(missing_docs)]
impl<'input, 'arena, Ext, Node> BaseRuleContextInner<'input, 'arena, Ext, Node>
where
    'input: 'arena,
    Ext: CustomRuleContext<'input, 'arena, Node = Node>,
    Node: RuleNode<'input, 'arena>,
{
    pub(crate) fn new(parent: Option<&'arena Node>, invoking_state: i32, ext: Ext) -> Self {
        Self {
            parent,
            self_ref: std::ptr::null(),
            invoking_state,
            ext,
            _input: PhantomData,
        }
    }

    pub(crate) fn copy_from<Src>(
        node: BaseRuleContextInner<'input, 'arena, Src, Node>,
        ctor: impl FnOnce(Src) -> Ext,
    ) -> Self
    where
        Src: CustomRuleContext<'input, 'arena, Node = Node>,
    {
        Self {
            parent: node.parent,
            // Note: we inherit self_ref as `copy_from`'d contexts are meant to
            // be directly overwritten into the same node location. Needless to
            // say, unsafe AF this is.
            self_ref: node.self_ref,
            invoking_state: node.invoking_state,
            ext: ctor(node.ext),
            _input: PhantomData,
        }
    }

    pub(crate) fn morph<Tgt>(
        self,
        ctor: impl FnOnce(Ext) -> Tgt,
    ) -> BaseRuleContextInner<'input, 'arena, Tgt, Node>
    where
        Tgt: CustomRuleContext<'input, 'arena, Node = Node>,
    {
        BaseRuleContextInner {
            parent: self.parent,
            self_ref: self.self_ref,
            invoking_state: self.invoking_state,
            ext: ctor(self.ext),
            _input: PhantomData,
        }
    }

    #[inline]
    pub fn parent(&self) -> Option<&'arena Node> {
        self.parent
    }

    #[inline]
    pub fn has_parent(&self) -> bool {
        self.parent.is_some()
    }

    #[inline]
    pub fn try_as_node(&self) -> Option<&'arena Node> {
        if self.self_ref.is_null() {
            None
        } else {
            unsafe { Some(&*self.self_ref) }
        }
    }

    pub(crate) fn set_self_ref(&mut self, self_ref: *const Node) {
        self.self_ref = self_ref;
    }

    pub(crate) fn set_parent(&mut self, parent: Option<&'arena Node>) {
        self.parent = parent;
    }

    pub(crate) fn set_invoking_state(&mut self, t: i32) {
        self.invoking_state = t;
    }

    pub(crate) fn set_alt_number(&mut self, _alt_number: i32) {
        self.ext.set_alt_number(_alt_number)
    }
}

impl<'input, 'arena, ExtCtx, Node> Borrow<ExtCtx>
    for BaseRuleContextInner<'input, 'arena, ExtCtx, Node>
where
    'input: 'arena,
    ExtCtx: CustomRuleContext<'input, 'arena, Node = Node>,
    Node: RuleNode<'input, 'arena>,
{
    fn borrow(&self) -> &ExtCtx {
        &self.ext
    }
}

impl<'input, 'arena, ExtCtx, Node> BorrowMut<ExtCtx>
    for BaseRuleContextInner<'input, 'arena, ExtCtx, Node>
where
    'input: 'arena,
    ExtCtx: CustomRuleContext<'input, 'arena, Node = Node>,
    Node: RuleNode<'input, 'arena>,
{
    fn borrow_mut(&mut self) -> &mut ExtCtx {
        &mut self.ext
    }
}

impl<'input, 'arena, Ctx, Node> RuleContext<'input, 'arena>
    for BaseRuleContextInner<'input, 'arena, Ctx, Node>
where
    'input: 'arena,
    Ctx: CustomRuleContext<'input, 'arena, Node = Node> + 'arena,
    Node: RuleNode<'input, 'arena>,
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

impl<'input, 'arena, ExtCtx, Node> Debug for BaseRuleContextInner<'input, 'arena, ExtCtx, Node>
where
    'input: 'arena,
    ExtCtx: CustomRuleContext<'input, 'arena, Node = Node>,
    Node: RuleNode<'input, 'arena>,
{
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        f.debug_struct(type_name::<Self>())
            .field("invoking_state", &self.invoking_state)
            .field("..", &"..")
            .finish()
    }
}
