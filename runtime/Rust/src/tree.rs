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
use crate::token::Token;
use crate::{interval_set, token_factory, Arena};

#[allow(missing_docs)]
pub trait Tree<'arena>: Sized {
    fn get_parent(&self) -> Option<&'arena Self>;

    fn has_parent(&self) -> bool;

    fn get_payload(&self) -> Box<dyn Any> {
        unimplemented!()
    }

    fn get_child(&self, _i: usize) -> Option<&'arena Self> {
        None
    }

    fn get_child_count(&self) -> usize {
        0
    }

    fn get_children<'a>(&'a self) -> Box<dyn Iterator<Item = &'arena Self> + 'a>;
}

/// Tree that knows about underlying text
pub trait ParseTree<'input, 'arena>: Tree<'arena> {
    /// Return an {@link Interval} indicating the index in the
    /// {@link TokenStream} of the first and last token associated with this
    /// subtree. If this node is a leaf, then the interval represents a single
    /// token and has interval i..i for token index i.
    fn get_source_interval(&self) -> Interval {
        interval_set::INVALID
    }

    /// Return combined text of this AST node.
    /// To create resulting string it does traverse whole subtree,
    /// also it includes only tokens added to the parse tree
    ///
    /// Since tokens on hidden channels (e.g. whitespace or comments) are not
    /// added to the parse trees, they will not appear in the output of this
    /// method.
    fn get_text(&self) -> String {
        String::new()
    }
}

pub trait RuleNode<'input, 'arena>:
    ParseTree<'input, 'arena>
    + ParserRuleContext<'input, 'arena>
    + From<TerminalNode<'input, 'arena>>
    + From<ErrorNode<'input, 'arena>>
where
    'input: 'arena,
    Self: 'arena,
{
    type Listener: ParseTreeListener<'input, 'arena, Self> + ?Sized;

    /// Return the [ParserRuleContext] object associated with this rule node.
    fn get_rule_context(&self) -> &dyn ParserRuleContext<'input, 'arena> {
        self
    }

    /// Return mutable reference to the [ParserRuleContext] object associated
    /// with this rule node.
    fn get_rule_context_mut(&mut self) -> &mut dyn ParserRuleContext<'input, 'arena> {
        self
    }

    /// Attempt to downcast this node to specific [ParserRuleContext] type
    fn as_rule_context<T>(&self) -> Option<&T>
    where
        T: ParserRuleContext<'input, 'arena> + NodeInner<'input, 'arena, Self>,
    {
        T::cast_from(self)
    }

    fn as_rule_context_mut<T>(&mut self) -> Option<&mut T>
    where
        T: ParserRuleContext<'input, 'arena> + NodeInner<'input, 'arena, Self>,
    {
        T::cast_from_mut(self)
    }

    /// Downcast this node to terminal node, if the associated context is a
    /// terminal node
    fn as_terminal_node(&self) -> Option<&TerminalNode<'input, 'arena>>;
    fn as_terminal_node_mut(&mut self) -> Option<&mut TerminalNode<'input, 'arena>>;

    /// Downcast this node to error node, if the associated context is an error
    /// node
    fn as_error_node(&self) -> Option<&ErrorNode<'input, 'arena>>;
    fn as_error_node_mut(&mut self) -> Option<&mut ErrorNode<'input, 'arena>>;

    // fn add_token_node(&self, token: TerminalNode<'input, Self::TF>) { }
    // fn add_error_node(&self, bad_token: ErrorNode<'input, Self::TF>) { }

    fn set_exception(&self, _e: ANTLRError, arena: &'arena Arena);

    /// Sets internal parser state
    fn set_invoking_state(&mut self, _t: i32);

    fn set_alt_number(&mut self, _alt_number: i32);

    fn set_start(&mut self, _t: Option<&'arena dyn Token>);

    fn set_stop(&mut self, _t: Option<&'arena dyn Token>);

    fn remove_last_child(&mut self);

    fn add_child(&mut self, _child: &'arena Self);

    fn set_parent(&mut self, _parent: Option<&'arena Self>);

    /// Sets self-reference pointer to allow upcasting from context to RuleNode
    ///
    /// # Safety
    /// DO NOT CALL -- internal method, only meant to be called by [Arena]
    unsafe fn set_self_ref(&mut self, _self_ref: *const Self);

    fn enter_rule(&self, listener: &mut Self::Listener) -> Result<(), ANTLRError>;

    fn exit_rule(&self, listener: &mut Self::Listener) -> Result<(), ANTLRError>;
}

/// Helper trait, implemented for all rule context types that can be wrapped
/// inside [RuleNode].
pub trait NodeInner<'input, 'arena, Node>
where
    'input: 'arena,
    Node: RuleNode<'input, 'arena>,
{
    /// Extract a reference to self from a reference to [RuleNode].
    ///
    /// Used for safe downcasting.
    fn cast_from(node: &Node) -> Option<&Self>
    where
        Self: Sized;

    /// Extract a mutable reference to self from a mutable reference to
    /// [RuleNode].
    ///
    /// Used for safe downcasting.
    fn cast_from_mut(node: &mut Node) -> Option<&mut Self>
    where
        Self: Sized;

    /// Upcast &self back to a `&'arena Node`.
    ///
    /// Only works on non-leaf nodes.
    fn try_as_node(&'arena self) -> Option<&'arena Node>;

    /// Iterate over children as [RuleNode]s
    fn iter_child_nodes<'a>(&'a self) -> Box<dyn Iterator<Item = &'arena Node> + 'a>;
}

#[doc(hidden)]
#[derive(Debug)]
pub struct NoError;

#[doc(hidden)]
#[derive(Debug)]
pub struct IsError;

/// Generic leaf AST node
pub struct LeafNode<'input, 'arena, E>
where
    'input: 'arena,
    E: 'static,
{
    /// Token, this leaf consist of
    pub symbol: &'arena (dyn Token + 'input),
    iserror: PhantomData<E>,
}

impl<'input, 'arena, E> RuleContext<'input, 'arena> for LeafNode<'input, 'arena, E>
where
    'input: 'arena,
    E: 'static,
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

    fn get_parent_ctx(&self) -> Option<&'arena dyn RuleContext<'input, 'arena>> {
        None
    }
}

impl<'input, 'arena, E> ParserRuleContext<'input, 'arena> for LeafNode<'input, 'arena, E>
where
    'input: 'arena,
    E: 'static,
{
    fn get_text(&self) -> String {
        self.symbol.get_text().to_display()
    }

    fn get_child_count(&self) -> usize {
        0
    }
}

impl<'input, 'arena, Node> NodeInner<'input, 'arena, Node> for LeafNode<'input, 'arena, NoError>
where
    'input: 'arena,
    Node: RuleNode<'input, 'arena>,
{
    fn cast_from(node: &Node) -> Option<&Self>
    where
        Self: Sized,
    {
        node.as_terminal_node()
    }

    fn cast_from_mut(node: &mut Node) -> Option<&mut Self>
    where
        Self: Sized,
    {
        node.as_terminal_node_mut()
    }

    fn iter_child_nodes<'a>(&'a self) -> Box<dyn Iterator<Item = &'arena Node> + 'a> {
        Box::new(std::iter::empty())
    }

    fn try_as_node(&'arena self) -> Option<&'arena Node> {
        None
    }
}

impl<'input, 'arena, Node> NodeInner<'input, 'arena, Node> for LeafNode<'input, 'arena, IsError>
where
    'input: 'arena,
    Node: RuleNode<'input, 'arena>,
{
    fn cast_from(node: &Node) -> Option<&Self>
    where
        Self: Sized,
    {
        node.as_error_node()
    }

    fn cast_from_mut(node: &mut Node) -> Option<&mut Self>
    where
        Self: Sized,
    {
        node.as_error_node_mut()
    }

    fn iter_child_nodes<'a>(&'a self) -> Box<dyn Iterator<Item = &'arena Node> + 'a> {
        Box::new(std::iter::empty())
    }

    fn try_as_node(&'arena self) -> Option<&'arena Node> {
        None
    }
}

impl<'input, 'arena, E> Default for LeafNode<'input, 'arena, E>
where
    'input: 'arena,
    E: 'static,
{
    fn default() -> Self {
        Self::new(token_factory::invalid())
    }
}

impl<'input, 'arena, E> Debug for LeafNode<'input, 'arena, E>
where
    E: 'static,
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

impl<'input, 'arena, E> LeafNode<'input, 'arena, E>
where
    'input: 'arena,
    E: 'static,
{
    /// creates new leaf node
    pub fn new(symbol: &'arena (dyn Token + 'input)) -> Self {
        Self {
            symbol,
            iserror: Default::default(),
        }
    }
}

/// non-error AST leaf node
pub type TerminalNode<'input, 'arena> = LeafNode<'input, 'arena, NoError>;

/// # Error Leaf
/// Created for each token created or consumed during recovery
pub type ErrorNode<'input, 'arena> = LeafNode<'input, 'arena, IsError>;

pub trait ParseTreeVisitor<'input, 'arena, Node>
where
    'input: 'arena,
    Node: RuleNode<'input, 'arena>,
{
    type Return: Default;

    fn visit(&mut self, node: &Node) -> Result<Self::Return, ANTLRError>;

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
        node: &dyn NodeInner<'input, 'arena, Node>,
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

    fn should_visit_next_child(&self, _node: &Node, _current: &Self::Return) -> bool {
        true
    }
}

/// Base parse listener interface
pub trait ParseTreeListener<'input, 'arena, Node>
where
    'input: 'arena,
    Node: RuleNode<'input, 'arena>,
{
    /// Called when parser creates terminal node
    fn visit_terminal(&mut self, _node: &TerminalNode<'input, 'arena>) -> Result<(), ANTLRError> {
        Ok(())
    }

    /// Called when parser creates error node
    fn visit_error_node(&mut self, _node: &ErrorNode<'input, 'arena>) -> Result<(), ANTLRError> {
        Ok(())
    }

    /// Called when parser enters any rule node
    fn enter_every_rule(&mut self, _ctx: &Node) -> Result<(), ANTLRError> {
        Ok(())
    }

    /// Called when parser exits any rule node
    fn exit_every_rule(&mut self, _ctx: &Node) -> Result<(), ANTLRError> {
        Ok(())
    }
}

/// Helper struct to accept parse listener on already generated tree
#[derive(Debug)]
pub struct ParseTreeWalker;

impl ParseTreeWalker {
    /// Walks recursively over tree `t` with `listener`
    pub fn walk<'input, 'arena, Node>(
        mut listener: Box<Node::Listener>,
        t: &Node,
    ) -> Result<Box<Node::Listener>, ANTLRError>
    where
        'input: 'arena,
        Node: RuleNode<'input, 'arena>,
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

    fn enter_rule<'input, 'arena, Node>(
        listener: &mut Node::Listener,
        t: &Node,
    ) -> Result<(), ANTLRError>
    where
        'input: 'arena,
        Node: RuleNode<'input, 'arena>,
    {
        listener.enter_every_rule(t)?;
        t.enter_rule(listener)
    }

    fn exit_rule<'input, 'arena, Node>(
        listener: &mut Node::Listener,
        t: &Node,
    ) -> Result<(), ANTLRError>
    where
        'input: 'arena,
        Node: RuleNode<'input, 'arena>,
    {
        t.exit_rule(listener)?;
        listener.exit_every_rule(t)
    }
}
