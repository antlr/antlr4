//! General AST
use std::any::Any;

use std::fmt::{Debug, Formatter};
use std::marker::PhantomData;

use crate::char_stream::InputData;
use crate::errors::ANTLRError;
use crate::int_stream::EOF;
use crate::interval_set::Interval;
use crate::parser_rule_context::ParserRuleContext;
use crate::rule_context::RuleContext;
use crate::token::{CommonToken, Token};
use crate::{cast_unchecked, token_factory, Arena};

#[allow(missing_docs)]
pub trait Tree<'arena>: Sized {
    fn get_parent(&self) -> Option<&'arena Self>;

    fn has_parent(&self) -> bool;

    fn get_payload(&self) -> Box<dyn Any> {
        unimplemented!()
    }

    fn get_child(&self, _i: usize) -> Option<&'arena Self>;

    fn get_child_count(&self) -> usize;

    fn get_children<'a>(&'a self) -> Box<dyn Iterator<Item = &'arena Self> + 'a>;
}

/// Tree that knows about underlying text
pub trait ParseTree<'input, 'arena>: Tree<'arena> {
    /// Return an {@link Interval} indicating the index in the
    /// {@link TokenStream} of the first and last token associated with this
    /// subtree. If this node is a leaf, then the interval represents a single
    /// token and has interval i..i for token index i.
    fn get_source_interval(&self) -> Interval;

    /// Return combined text of this AST node.
    /// To create resulting string it does traverse whole subtree,
    /// also it includes only tokens added to the parse tree
    ///
    /// Since tokens on hidden channels (e.g. whitespace or comments) are not
    /// added to the parse trees, they will not appear in the output of this
    /// method.
    fn get_text(&self) -> String;
}

/// Helper trait, implemented for all rule context types that can be wrapped
/// inside [TreeNode].
pub trait NodeInner<'input, 'arena, Node, Tok = CommonToken<'input>>
where
    'input: 'arena,
    Node: NodeKindType<'arena, Tok>,
    Tok: Token + 'input,
{
    /// Extract a reference to self from a reference to [TreeNode].
    ///
    /// Used for safe downcasting.
    fn cast_from<'a>(node: &'a TreeNode<'input, 'arena, Node, Tok>) -> Option<&'a Self>
    where
        Self: Sized;

    /// Extract a mutable reference to self from a mutable reference to
    /// [TreeNode].
    ///
    /// Used for safe downcasting.
    fn cast_from_mut<'a>(node: &'a mut TreeNode<'input, 'arena, Node, Tok>) -> Option<&'a mut Self>
    where
        Self: Sized;

    /// Upcast &self back to a `&'arena Node`.
    fn as_node(&self) -> &TreeNode<'input, 'arena, Node, Tok>;

    fn as_node_mut(&mut self) -> &mut TreeNode<'input, 'arena, Node, Tok>;

    /// Iterate over children as [TreeNode]s
    fn iter_child_nodes<'a>(
        &'a self,
    ) -> Box<dyn Iterator<Item = &'arena TreeNode<'input, 'arena, Node, Tok>> + 'a>;
}

pub type CtxPtr<'input, 'arena> = *mut (&'input (), *mut &'arena ());

pub trait NodeKindType<'arena, Tok = CommonToken<'arena>>:
    Copy + Debug + PartialEq + Eq + 'static
where
    Tok: Token + 'arena,
{
    type Listener: ParseTreeListener<'arena, Self, Tok> + ?Sized;

    fn cast_to_ctx<'n, 'input: 'arena>(
        node: &'n TreeNode<'input, 'arena, Self, Tok>,
    ) -> &'n dyn ParserRuleContext<'input, 'arena>;

    fn cast_to_ctx_mut<'n, 'input: 'arena>(
        node: &'n mut TreeNode<'input, 'arena, Self, Tok>,
    ) -> &'n mut dyn ParserRuleContext<'input, 'arena>;

    fn set_alt_number<'input: 'arena>(
        node: &mut TreeNode<'input, 'arena, Self, Tok>,
        alt_number: i32,
    );

    fn get_rule_index<'input: 'arena>(node: &TreeNode<'input, 'arena, Self, Tok>) -> usize;

    fn get_alt_number<'input: 'arena>(node: &TreeNode<'input, 'arena, Self, Tok>) -> i32;

    fn terminal() -> Self;

    fn error() -> Self;

    fn is_context(&self) -> bool;

    fn enter_rule<'input: 'arena>(
        node: &TreeNode<'input, 'arena, Self, Tok>,
        listener: &mut Self::Listener,
    ) -> Result<(), ANTLRError>;

    fn exit_rule<'input: 'arena>(
        node: &TreeNode<'input, 'arena, Self, Tok>,
        listener: &mut Self::Listener,
    ) -> Result<(), ANTLRError>;

    #[inline]
    fn is_terminal(&self) -> bool {
        *self == Self::terminal()
    }

    #[inline]
    fn is_error(&self) -> bool {
        *self == Self::error()
    }
}
#[repr(C, align(8))]
struct ParserRuleContextCommonLayout<'input, 'arena, Node, Tok = CommonToken<'input>>
where
    'input: 'arena,
    Node: NodeKindType<'arena, Tok>,
    Tok: Token + 'input,
{
    // --- BaseParserRuleContext fields ---
    start: Option<&'arena Tok>,
    stop: Option<&'arena Tok>,
    children: bumpalo::collections::Vec<'arena, &'arena TreeNode<'input, 'arena, Node, Tok>>,
    // --- BaseRuleContext fields ---
    parent: Option<&'arena TreeNode<'input, 'arena, Node, Tok>>,
}

#[derive(Debug)]
#[repr(C, align(8))]
pub struct TreeNode<'input, 'arena, NodeKind, Tok = CommonToken<'input>>
where
    'input: 'arena,
    NodeKind: NodeKindType<'arena, Tok>,
    Tok: Token + 'input,
{
    pub(crate) label_tag: u16,
    pub node_tag: NodeKind,
    invoking_state: i32,
    body: (),

    _mark: PhantomData<(&'input Tok, *mut &'arena ())>,
}

impl<'input, 'arena, NodeKind, Tok> TreeNode<'input, 'arena, NodeKind, Tok>
where
    'input: 'arena,
    NodeKind: NodeKindType<'arena, Tok>,
    Tok: Token + 'input,
{
    pub fn create_token_node(arena: &'arena Arena, symbol: &'arena Tok) -> *mut Self {
        let header = TreeNode {
            node_tag: NodeKind::terminal(),
            label_tag: 0,
            invoking_state: -1,
            body: (),
            _mark: PhantomData,
        };
        let leaf = TerminalNode::new(symbol);
        arena.alloc_node(header, leaf)
    }

    pub fn create_error_node(arena: &'arena Arena, symbol: &'arena Tok) -> *mut Self {
        let header = TreeNode {
            node_tag: NodeKind::error(),
            label_tag: 0,
            invoking_state: -1,
            body: (),
            _mark: PhantomData,
        };
        let leaf = ErrorNode::new(symbol);
        arena.alloc_node(header, leaf)
    }

    #[inline]
    pub fn node_tag(&self) -> NodeKind {
        self.node_tag
    }

    /// Return the [ParserRuleContext] object associated with this rule node.
    pub fn get_rule_context(&self) -> &dyn ParserRuleContext<'input, 'arena> {
        NodeKind::cast_to_ctx(self)
    }

    /// Return mutable reference to the [ParserRuleContext] object associated
    /// with this rule node.
    pub fn get_rule_context_mut(&mut self) -> &mut dyn ParserRuleContext<'input, 'arena> {
        NodeKind::cast_to_ctx_mut(self)
    }

    #[inline]
    pub fn ctx_ptr(&self) -> CtxPtr<'input, 'arena> {
        &self.body as *const _ as *mut (&'input (), *mut &'arena ())
    }

    pub(crate) fn get_start_token(&self) -> Option<&'arena Tok> {
        self.as_rule_context_common().and_then(|ctx| ctx.start)
    }

    #[allow(dead_code)]
    pub(crate) fn get_stop_token(&self) -> Option<&'arena Tok> {
        self.as_rule_context_common().and_then(|ctx| ctx.stop)
    }

    /// # Safety
    /// Caller must ensure that the provided pointer is a valid pointer to a
    /// properly instantiated [NodeInner] object:
    /// - For leaf nodes, it must be created via [TreeNode::create_token_node]
    ///   or [TreeNode::create_error_node];
    /// - For rule context nodes, it must be created via
    ///   [BaseParserRuleContext::create].
    #[inline]
    pub(crate) unsafe fn from_ctx_ptr(ctx_ptr: CtxPtr<'input, 'arena>) -> &'arena Self {
        &*((ctx_ptr as *const u8).sub(std::mem::offset_of!(Self, body)) as *const Self)
    }

    /// # Safety
    /// Caller must ensure that the provided pointer is a valid pointer to a
    /// properly instantiated [NodeInner] object:
    /// - For leaf nodes, it must be created via [TreeNode::create_token_node]
    ///   or [TreeNode::create_error_node];
    /// - For rule context nodes, it must be created via
    ///   [BaseParserRuleContext::create].
    #[inline]
    pub(crate) unsafe fn from_ctx_ptr_mut(ctx_ptr: CtxPtr<'input, 'arena>) -> &'arena mut Self {
        &mut *((ctx_ptr as *const u8).sub(std::mem::offset_of!(Self, body)) as *mut Self)
    }

    #[inline]
    fn as_rule_context_common(
        &self,
    ) -> Option<&ParserRuleContextCommonLayout<'input, 'arena, NodeKind, Tok>> {
        if self.node_tag.is_context() {
            Some(
                cast_unchecked!(self.ctx_ptr() => ParserRuleContextCommonLayout<'input, 'arena, NodeKind, Tok>),
            )
        } else {
            None
        }
    }

    #[inline]
    fn as_rule_context_common_mut(
        &mut self,
    ) -> Option<&mut ParserRuleContextCommonLayout<'input, 'arena, NodeKind, Tok>> {
        if self.node_tag.is_context() {
            Some(
                cast_unchecked!(self.ctx_ptr() => mut ParserRuleContextCommonLayout<'input, 'arena, NodeKind, Tok>),
            )
        } else {
            None
        }
    }

    /// Attempt to downcast this node to specific [ParserRuleContext] type
    pub fn as_rule_context<T>(&self) -> Option<&T>
    where
        T: ParserRuleContext<'input, 'arena> + NodeInner<'input, 'arena, NodeKind, Tok>,
    {
        T::cast_from(self)
    }

    /// Attempt to downcast this node to specific [ParserRuleContext] type
    pub fn as_rule_context_mut<T>(&mut self) -> Option<&mut T>
    where
        T: ParserRuleContext<'input, 'arena> + NodeInner<'input, 'arena, NodeKind, Tok>,
    {
        T::cast_from_mut(self)
    }

    /// Downcast this node to terminal node, if the associated context is a
    /// terminal node
    pub fn as_terminal_node(&self) -> Option<&TerminalNode<'input, 'arena, Tok>> {
        TerminalNode::cast_from(self)
    }

    /// Downcast this node to terminal node, if the associated context is a
    /// terminal node
    pub fn as_terminal_node_mut(&mut self) -> Option<&mut TerminalNode<'input, 'arena, Tok>> {
        TerminalNode::cast_from_mut(self)
    }

    /// Downcast this node to error node, if the associated context is an error
    /// node
    pub fn as_error_node(&self) -> Option<&ErrorNode<'input, 'arena, Tok>> {
        ErrorNode::cast_from(self)
    }

    /// Downcast this node to error node, if the associated context is an error
    /// node
    pub fn as_error_node_mut(&mut self) -> Option<&mut ErrorNode<'input, 'arena, Tok>> {
        ErrorNode::cast_from_mut(self)
    }

    // pub fn add_token_node(&self, token: TerminalNode<'input, Self::TF>) { }
    // pub fn add_error_node(&self, bad_token: ErrorNode<'input, Self::TF>) { }

    pub fn set_exception(&self, _e: ANTLRError, _arena: &'arena Arena) {}

    /// Sets internal parser state
    pub fn set_invoking_state(&mut self, s: i32) {
        self.invoking_state = s;
    }

    pub fn set_alt_number(&mut self, _alt_number: i32) {
        NodeKind::set_alt_number(self, _alt_number);
    }

    pub fn set_start(&mut self, t: Option<&'arena Tok>) {
        let Some(ctx) = self.as_rule_context_common_mut() else {
            return;
        };

        ctx.start = t;
    }

    pub fn set_stop(&mut self, t: Option<&'arena Tok>) {
        let Some(ctx) = self.as_rule_context_common_mut() else {
            return;
        };

        ctx.stop = t;
    }

    pub fn remove_last_child(&mut self) {
        let Some(ctx) = self.as_rule_context_common_mut() else {
            return;
        };
        ctx.children.pop();
    }

    pub fn add_child(&mut self, _child: &'arena Self) {
        let Some(ctx) = self.as_rule_context_common_mut() else {
            return;
        };
        ctx.children.push(_child);
    }

    pub fn set_parent(&mut self, parent: Option<&'arena Self>) {
        let Some(ctx) = self.as_rule_context_common_mut() else {
            return;
        };
        ctx.parent = parent;
    }

    pub fn enter_rule(&self, listener: &mut NodeKind::Listener) -> Result<(), ANTLRError> {
        NodeKind::enter_rule(self, listener)
    }

    pub fn exit_rule(&self, listener: &mut NodeKind::Listener) -> Result<(), ANTLRError> {
        NodeKind::exit_rule(self, listener)
    }
}

impl<'input, 'arena, NodeKind, Tok> Tree<'arena> for TreeNode<'input, 'arena, NodeKind, Tok>
where
    'input: 'arena,
    NodeKind: NodeKindType<'arena, Tok>,
    Tok: Token + 'input,
{
    #[inline]
    fn get_parent(&self) -> Option<&'arena Self> {
        self.as_rule_context_common().and_then(|ctx| ctx.parent)
    }

    fn has_parent(&self) -> bool {
        self.get_parent().is_some()
    }

    fn get_child(&self, i: usize) -> Option<&'arena Self> {
        self.as_rule_context_common()
            .and_then(|ctx| ctx.children.get(i).copied())
    }

    #[inline]
    fn get_child_count(&self) -> usize {
        self.as_rule_context_common()
            .map_or(0, |ctx| ctx.children.len())
    }

    #[inline]
    fn get_children<'a>(&'a self) -> Box<dyn Iterator<Item = &'arena Self> + 'a> {
        if let Some(ctx) = self.as_rule_context_common() {
            Box::new(ctx.children.iter().copied())
        } else {
            Box::new(std::iter::empty())
        }
    }
}

impl<'input, 'arena, Node, Tok> ParseTree<'input, 'arena> for TreeNode<'input, 'arena, Node, Tok>
where
    'input: 'arena,
    Node: NodeKindType<'arena, Tok>,
    Tok: Token,
{
    fn get_source_interval(&self) -> Interval {
        if let Some(terminal) = self.as_terminal_node() {
            Interval::new(
                terminal.symbol.get_start_index() as i32,
                terminal.symbol.get_stop_index() as i32,
            )
        } else if let Some(error) = self.as_error_node() {
            Interval::new(
                error.symbol.get_start_index() as i32,
                error.symbol.get_stop_index() as i32,
            )
        } else {
            let ctx = self
                .as_rule_context_common()
                .expect("Can only be rule context at this point");
            match (ctx.start, ctx.stop) {
                (Some(start), Some(stop)) => {
                    Interval::new(start.get_start_index() as i32, stop.get_stop_index() as i32)
                }
                _ => Interval::invalid(),
            }
        }
    }

    fn get_text(&self) -> String {
        if let Some(terminal) = self.as_terminal_node() {
            return terminal.get_text();
        } else if let Some(error) = self.as_error_node() {
            return error.get_text();
        }

        let mut result = String::new();
        for child in self.get_children() {
            result.push_str(&ParseTree::get_text(child));
        }
        result
    }
}

impl<'input, 'arena, NodeKind, Tok> RuleContext<'arena> for TreeNode<'input, 'arena, NodeKind, Tok>
where
    'input: 'arena,
    NodeKind: NodeKindType<'arena, Tok>,
    Tok: Token,
{
    fn get_rule_index(&self) -> usize {
        NodeKind::get_rule_index(self)
    }

    fn get_alt_number(&self) -> i32 {
        NodeKind::get_alt_number(self)
    }

    #[inline]
    fn get_invoking_state(&self) -> i32 {
        self.invoking_state
    }

    fn get_parent_ctx(&self) -> Option<&'arena dyn RuleContext<'arena>> {
        self.get_parent()
            .map(|p| p as &'arena dyn RuleContext<'arena>)
    }

    fn get_node_text(&self, rule_names: &[&str]) -> String {
        NodeKind::cast_to_ctx(self).get_node_text(rule_names)
    }
}

impl<'input, 'arena, Node, Tok> ParserRuleContext<'input, 'arena>
    for TreeNode<'input, 'arena, Node, Tok>
where
    'input: 'arena,
    Node: NodeKindType<'arena, Tok>,
    Tok: Token,
{
    fn get_text(&self) -> String {
        ParseTree::get_text(self)
    }

    fn get_child_count(&self) -> usize {
        Tree::get_child_count(self)
    }

    fn start(&self) -> &'arena dyn Token {
        if let Some(terminal) = self.as_terminal_node() {
            return terminal.symbol;
        } else if let Some(error) = self.as_error_node() {
            return error.symbol;
        }
        let ctx = self
            .as_rule_context_common()
            .expect("Can only be rule context at this point");
        ctx.start.map_or_else(
            || token_factory::invalid() as &dyn Token,
            |t| t as &dyn Token,
        )
    }

    fn stop(&self) -> &'arena dyn Token {
        if let Some(terminal) = self.as_terminal_node() {
            return terminal.symbol;
        } else if let Some(error) = self.as_error_node() {
            return error.symbol;
        }
        let ctx = self
            .as_rule_context_common()
            .expect("Can only be rule context at this point");
        ctx.stop.map_or_else(
            || token_factory::invalid() as &dyn Token,
            |t| t as &dyn Token,
        )
    }

    fn get_parent_ctx(&self) -> Option<&'arena dyn ParserRuleContext<'input, 'arena>> {
        self.get_parent()
            .map(|p| p as &'arena dyn ParserRuleContext<'input, 'arena>)
    }

    fn get_child_ctx(&self, i: usize) -> Option<&'arena dyn ParserRuleContext<'input, 'arena>> {
        self.get_child(i)
            .map(|child| child.get_rule_context() as &dyn ParserRuleContext<'input, 'arena>)
    }

    fn iter_children<'a>(
        &'a self,
    ) -> Box<dyn Iterator<Item = &'arena dyn ParserRuleContext<'input, 'arena>> + 'a>
    where
        'input: 'a,
        'arena: 'a,
    {
        let Some(ctx) = self.as_rule_context_common() else {
            return Box::new(std::iter::empty());
        };
        Box::new(
            ctx.children
                .iter()
                .map(|child| child.get_rule_context() as &dyn ParserRuleContext<'input, 'arena>),
        )
    }

    fn get_token(&self, _ttype: i32, _pos: usize) -> Option<&dyn Token> {
        let ctx = self.as_rule_context_common()?;
        ctx.children
            .iter()
            .filter_map(|child| child.as_terminal_node())
            .filter(|t| t.symbol.get_token_type() == _ttype)
            .nth(_pos)
            .map(|t| t.symbol as &dyn Token)
    }

    fn get_tokens(&self, _ttype: i32) -> Vec<&dyn Token> {
        let Some(ctx) = self.as_rule_context_common() else {
            return vec![];
        };
        ctx.children
            .iter()
            .filter_map(|child| child.as_terminal_node())
            .filter(|t| t.symbol.get_token_type() == _ttype)
            .map(|t| t.symbol as &dyn Token)
            .collect()
    }

    fn to_string_tree(&self, rule_names: &[&str]) -> String {
        crate::trees::string_tree(self, rule_names)
    }
}

#[doc(hidden)]
#[derive(Debug)]
pub struct NoError;

#[doc(hidden)]
#[derive(Debug)]
pub struct IsError;

/// Generic leaf AST node
pub struct LeafNodeInner<'input, 'arena, E, Tok>
where
    'input: 'arena,
    Tok: Token + 'input,
    E: 'static,
{
    /// Token, this leaf consist of
    pub symbol: &'arena Tok,
    iserror: PhantomData<(E, &'input ())>,
}

impl<'input, 'arena, E, Tok> RuleContext<'arena> for LeafNodeInner<'input, 'arena, E, Tok>
where
    'input: 'arena,
    E: 'static,
    Tok: Token + 'input,
{
    fn get_rule_index(&self) -> usize {
        usize::MAX
    }

    fn get_alt_number(&self) -> i32 {
        crate::atn::INVALID_ALT
    }

    fn get_node_text(&self, _rule_names: &[&str]) -> String {
        self.symbol.get_text().to_display()
    }

    fn get_invoking_state(&self) -> i32 {
        -1
    }

    fn get_parent_ctx(&self) -> Option<&'arena dyn RuleContext<'arena>> {
        None
    }
}

impl<'input, 'arena, E, Tok> ParserRuleContext<'input, 'arena>
    for LeafNodeInner<'input, 'arena, E, Tok>
where
    'input: 'arena,
    E: 'static,
    Tok: Token + 'input,
{
    fn get_text(&self) -> String {
        self.symbol.get_text().to_display()
    }

    fn get_child_count(&self) -> usize {
        0
    }

    fn iter_children<'a>(
        &'a self,
    ) -> Box<dyn Iterator<Item = &'arena dyn ParserRuleContext<'input, 'arena>> + 'a>
    where
        'input: 'a,
        'arena: 'a,
    {
        Box::new(std::iter::empty())
    }

    fn get_token(&self, _ttype: i32, _pos: usize) -> Option<&dyn Token> {
        None
    }

    fn get_tokens(&self, _ttype: i32) -> Vec<&dyn Token> {
        vec![]
    }
}

impl<'input, 'arena, Node, Tok> NodeInner<'input, 'arena, Node, Tok>
    for LeafNodeInner<'input, 'arena, NoError, Tok>
where
    'input: 'arena,
    Node: NodeKindType<'arena, Tok>,
    Tok: Token + 'input,
{
    fn cast_from<'a>(node: &'a TreeNode<'input, 'arena, Node, Tok>) -> Option<&'a Self>
    where
        Self: Sized,
    {
        if node.node_tag.is_terminal() {
            Some(unsafe { &*(node.ctx_ptr() as *const Self) })
        } else {
            None
        }
    }

    fn cast_from_mut<'a>(node: &'a mut TreeNode<'input, 'arena, Node, Tok>) -> Option<&'a mut Self>
    where
        Self: Sized,
    {
        if node.node_tag.is_terminal() {
            Some(unsafe { &mut *(node.ctx_ptr() as *mut Self) })
        } else {
            None
        }
    }

    fn as_node(&self) -> &TreeNode<'input, 'arena, Node, Tok> {
        // SAFETY: All [NodeInner] instances are allocated by
        // [Arena::alloc_node] with a valid [RuleNodeImpl] header
        unsafe { TreeNode::from_ctx_ptr(self as *const Self as CtxPtr<'input, 'arena>) }
    }

    fn as_node_mut(&mut self) -> &mut TreeNode<'input, 'arena, Node, Tok> {
        // SAFETY: All [NodeInner] instances are allocated by
        // [Arena::alloc_node] with a valid [RuleNodeImpl] header
        unsafe { TreeNode::from_ctx_ptr_mut(self as *mut Self as CtxPtr<'input, 'arena>) }
    }

    fn iter_child_nodes<'a>(
        &'a self,
    ) -> Box<dyn Iterator<Item = &'arena TreeNode<'input, 'arena, Node, Tok>> + 'a> {
        Box::new(std::iter::empty())
    }
}

impl<'input, 'arena, Node, Tok> NodeInner<'input, 'arena, Node, Tok>
    for LeafNodeInner<'input, 'arena, IsError, Tok>
where
    'input: 'arena,
    Node: NodeKindType<'arena, Tok>,
    Tok: Token + 'input,
{
    fn cast_from<'a>(node: &'a TreeNode<'input, 'arena, Node, Tok>) -> Option<&'a Self>
    where
        Self: Sized,
    {
        if node.node_tag.is_error() {
            Some(unsafe { &*(node.ctx_ptr() as *const Self) })
        } else {
            None
        }
    }

    fn cast_from_mut<'a>(node: &'a mut TreeNode<'input, 'arena, Node, Tok>) -> Option<&'a mut Self>
    where
        Self: Sized,
    {
        if node.node_tag.is_error() {
            Some(unsafe { &mut *(node.ctx_ptr() as *mut Self) })
        } else {
            None
        }
    }

    fn as_node(&self) -> &TreeNode<'input, 'arena, Node, Tok> {
        // SAFETY: All [NodeInner] instances are allocated by
        // [Arena::alloc_node] with a valid [RuleNodeImpl] header
        unsafe { TreeNode::from_ctx_ptr(self as *const Self as CtxPtr<'input, 'arena>) }
    }

    fn as_node_mut(&mut self) -> &mut TreeNode<'input, 'arena, Node, Tok> {
        // SAFETY: All [NodeInner] instances are allocated by
        // [Arena::alloc_node] with a valid [RuleNodeImpl] header
        unsafe { TreeNode::from_ctx_ptr_mut(self as *mut Self as CtxPtr<'input, 'arena>) }
    }

    fn iter_child_nodes<'a>(
        &'a self,
    ) -> Box<dyn Iterator<Item = &'arena TreeNode<'input, 'arena, Node, Tok>> + 'a> {
        Box::new(std::iter::empty())
    }
}

impl<'input, 'arena, E, Tok> Debug for LeafNodeInner<'input, 'arena, E, Tok>
where
    E: 'static,
    Tok: Token + 'input,
{
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        if self.symbol.get_token_type() == EOF {
            f.write_str("<EOF>")
        } else {
            let a = self.symbol.get_text().to_display();
            f.write_str(&a)
        }
    }
}

impl<'input, 'arena, E, Tok> LeafNodeInner<'input, 'arena, E, Tok>
where
    'input: 'arena,
    E: 'static,
    Tok: Token + 'input,
{
    /// Creates new leaf node
    ///
    /// Note: [LeafNodeInner] can not exist as a standalone entity, this
    /// constructor can only be called from [RuleNodeImpl::create_token_node] or
    /// [RuleNodeImpl::create_error_node]
    fn new(symbol: &'arena Tok) -> Self {
        Self {
            symbol,
            iserror: Default::default(),
        }
    }
}

/// non-error AST leaf node
pub type TerminalNode<'input, 'arena, Tok = CommonToken<'input>> =
    LeafNodeInner<'input, 'arena, NoError, Tok>;

/// # Error Leaf
/// Created for each token created or consumed during recovery
pub type ErrorNode<'input, 'arena, Tok = CommonToken<'input>> =
    LeafNodeInner<'input, 'arena, IsError, Tok>;

pub trait ParseTreeVisitor<'input, 'arena, Node, Tok>
where
    'input: 'arena,
    Node: NodeKindType<'arena, Tok>,
    Tok: Token + 'input,
{
    type Return: Default;

    fn visit(
        &mut self,
        node: &TreeNode<'input, 'arena, Node, Tok>,
    ) -> Result<Self::Return, ANTLRError>;

    /// Called on terminal(leaf) node
    fn visit_terminal(
        &mut self,
        _node: &TerminalNode<'input, 'arena, Tok>,
    ) -> Result<Self::Return, ANTLRError> {
        Ok(Self::Return::default())
    }

    /// Called on error node
    fn visit_error_node(
        &mut self,
        _node: &ErrorNode<'input, 'arena, Tok>,
    ) -> Result<Self::Return, ANTLRError> {
        Ok(Self::Return::default())
    }

    fn visit_children(
        &mut self,
        node: &dyn NodeInner<'input, 'arena, Node, Tok>,
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
        _node: &TreeNode<'input, 'arena, Node, Tok>,
        _current: &Self::Return,
    ) -> bool {
        true
    }
}

/// Base parse listener interface
pub trait ParseTreeListener<'arena, Node, Tok>
where
    Node: NodeKindType<'arena, Tok>,
    Tok: Token + 'arena,
{
    /// Called when parser creates terminal node
    fn visit_terminal(&mut self, _node: &TerminalNode<'_, 'arena, Tok>) -> Result<(), ANTLRError> {
        Ok(())
    }

    /// Called when parser creates error node
    fn visit_error_node(&mut self, _node: &ErrorNode<'_, 'arena, Tok>) -> Result<(), ANTLRError> {
        Ok(())
    }

    /// Called when parser enters any rule node
    fn enter_every_rule(
        &mut self,
        _ctx: &TreeNode<'_, 'arena, Node, Tok>,
    ) -> Result<(), ANTLRError> {
        Ok(())
    }

    /// Called when parser exits any rule node
    fn exit_every_rule(
        &mut self,
        _ctx: &TreeNode<'_, 'arena, Node, Tok>,
    ) -> Result<(), ANTLRError> {
        Ok(())
    }
}

/// Helper struct to accept parse listener on already generated tree
#[derive(Debug)]
pub struct ParseTreeWalker;

impl ParseTreeWalker {
    /// Walks recursively over tree `t` with `listener`
    pub fn walk<'input, 'arena, Node, Tok>(
        mut listener: Box<Node::Listener>,
        t: &TreeNode<'input, 'arena, Node, Tok>,
    ) -> Result<Box<Node::Listener>, ANTLRError>
    where
        'input: 'arena,
        Node: NodeKindType<'arena, Tok>,
        Tok: Token + 'input,
    {
        if let Some(terminal) = t.as_terminal_node() {
            listener.visit_terminal(terminal)?;
            return Ok(listener);
        }
        if let Some(error) = t.as_error_node() {
            listener.visit_error_node(error)?;
            return Ok(listener);
        }

        Self::enter_rule(&mut *listener, t)?;
        for child in t.get_children() {
            listener = Self::walk(listener, child)?;
        }
        Self::exit_rule(&mut *listener, t)?;

        Ok(listener)
    }

    fn enter_rule<'input, 'arena, Node, Tok>(
        listener: &mut Node::Listener,
        t: &TreeNode<'input, 'arena, Node, Tok>,
    ) -> Result<(), ANTLRError>
    where
        'input: 'arena,
        Node: NodeKindType<'arena, Tok>,
        Tok: Token + 'input,
    {
        listener.enter_every_rule(t)?;
        t.enter_rule(listener)
    }

    fn exit_rule<'input, 'arena, Node, Tok>(
        listener: &mut Node::Listener,
        t: &TreeNode<'input, 'arena, Node, Tok>,
    ) -> Result<(), ANTLRError>
    where
        'input: 'arena,
        Node: NodeKindType<'arena, Tok>,
        Tok: Token + 'input,
    {
        t.exit_rule(listener)?;
        listener.exit_every_rule(t)
    }
}
