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
use crate::token::{CommonToken, Token};
use crate::tree::{
    ErrorNode, NodeInner, NodeKindType, ParseTreeListener, TerminalNode, Tree, TreeNode,
};
use crate::{
    impl_node_inner, impl_node_kind, impl_parser_rule_context, impl_rule_context,
    impl_tree_trait_delegates,
};
use std::any::type_name;

/// Language-agnostic, dyn-compatible, read-only interface to the AST.
pub trait RuleContext<'arena> {
    /// Internal parser state
    fn get_invoking_state(&self) -> i32;

    /// A context is empty if there is no invoking state; meaning nobody called
    /// current context. Which is usually true for the root of the syntax tree
    fn is_empty(&self) -> bool {
        self.get_invoking_state() == -1
    }

    /// Get parent context
    fn get_parent_ctx(&self) -> Option<&'arena dyn RuleContext<'arena>>;

    /// Rule index that corresponds to this context type
    fn get_rule_index(&self) -> usize;

    fn get_alt_number(&self) -> i32;

    /// Returns text representation of current node type,
    /// rule name for context nodes and token text for terminal nodes
    fn get_node_text(&self, rule_names: &[&str]) -> String;
}

pub(crate) fn states_stack<'input, 'arena, Node, Tok>(
    mut node: &'arena TreeNode<'input, 'arena, Node, Tok>,
) -> impl Iterator<Item = i32> + 'arena
where
    'input: 'arena,
    Tok: Token + 'input,
    Node: NodeKindType<'arena, Tok> + 'arena,
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
pub trait CustomRuleContext<'input, 'arena, Tok>: Debug + Sized
where
    'input: 'arena,
    Tok: Token + 'input,
{
    type NodeKind: NodeKindType<'arena, Tok>;

    fn node_tag() -> Self::NodeKind;

    fn make_node(
        arena: &'arena crate::arena::Arena,
        ctx: BaseParserRuleContext<'input, 'arena, Self, Self::NodeKind, Tok>,
    ) -> *mut TreeNode<'input, 'arena, Self::NodeKind, Tok>;

    fn cast_from<'a>(
        node: &'a TreeNode<'input, 'arena, Self::NodeKind, Tok>,
    ) -> Option<&'a BaseParserRuleContext<'input, 'arena, Self, Self::NodeKind, Tok>>;

    fn cast_from_mut<'a>(
        node: &'a mut TreeNode<'input, 'arena, Self::NodeKind, Tok>,
    ) -> Option<&'a mut BaseParserRuleContext<'input, 'arena, Self, Self::NodeKind, Tok>>;

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
}

#[derive(Debug, Default)]
#[doc(hidden)]
pub struct EmptyCustomRuleContext<'input, 'arena>(
    pub(crate) PhantomData<(&'input (), *mut &'arena ())>,
);

#[derive(Clone, Copy, PartialEq, Eq, Debug)]
#[repr(u16)]
pub enum EmptyNodeKind {
    EmptyContext,
    Terminal,
    Error,
}

impl_node_kind! {EmptyNodeKind { EmptyContext(EmptyContextAll),; }; }

#[derive(Debug)]
#[repr(u16)]
pub enum EmptyContextAll<'input, 'arena, Tok: Token = CommonToken<'input>> {
    EmptyContext(EmptyParserRuleContext<'input, 'arena, Tok>),
    Error(EmptyParserRuleContext<'input, 'arena, Tok>),
}

impl_node_inner!(EmptyNodeKind::EmptyContext::EmptyContextAll {
    EmptyContext,
    Error,
});
impl_tree_trait_delegates!(EmptyNodeKind::EmptyContextAll {
    EmptyContext,
    Error,
});
impl_rule_context!(EmptyContextAll {} { EmptyContext, Error, });
impl_parser_rule_context!(EmptyContextAll {} { EmptyContext, Error, });
//impl_listener_dispatch!( EmptyListener::EmptyNodeKind::EmptyContextAll { EmptyContext(enter_empty, exit_empty), });

pub type EmptyRuleNode<'input, 'arena, Tok = CommonToken<'input>> =
    TreeNode<'input, 'arena, EmptyNodeKind, Tok>;

pub trait EmptyListener<'arena, Tok: Token + 'arena>:
    ParseTreeListener<'arena, EmptyNodeKind, Tok>
{
    fn enter_empty<'input>(
        &mut self,
        _ctx: &EmptyParserRuleContext<'input, 'arena>,
    ) -> Result<(), ANTLRError> {
        Ok(())
    }

    fn exit_empty<'input>(
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

    fn visit<Tok: Token + 'input>(
        &mut self,
        node: &EmptyRuleNode<'input, 'arena, Tok>,
    ) -> Result<Self::Return, ANTLRError>;

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

    fn visit_children<Tok: Token + 'input>(
        &mut self,
        node: &dyn NodeInner<'input, 'arena, EmptyNodeKind, Tok>,
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

    fn should_visit_next_child<Tok: Token + 'input>(
        &self,
        _node: &EmptyRuleNode<'input, 'arena, Tok>,
        _current: &Self::Return,
    ) -> bool {
        true
    }

    fn visit_empty<Tok: Token + 'input>(
        &mut self,
        ctx: &EmptyParserRuleContext<'input, 'arena, Tok>,
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

// impl_rule_node!(EmptyRuleNode { ; Empty(enter_empty, exit_empty, visit_empty), }; listener = dyn EmptyListener<'input, 'arena>, visitor = EmptyVisitor, );

// impl_defaults!(EmptyRuleNode);

impl<'input, 'arena, Tok> CustomRuleContext<'input, 'arena, Tok>
    for EmptyCustomRuleContext<'input, 'arena>
where
    'input: 'arena,
    Tok: Token + 'input,
{
    type NodeKind = EmptyNodeKind;

    fn node_tag() -> EmptyNodeKind {
        EmptyNodeKind::EmptyContext
    }

    fn get_rule_index(&self) -> usize {
        usize::MAX
    }

    fn make_node(
        arena: &'arena crate::arena::Arena,
        ctx: BaseParserRuleContext<'input, 'arena, Self, Self::NodeKind, Tok>,
    ) -> *mut TreeNode<'input, 'arena, EmptyNodeKind, Tok> {
        arena.alloc_labeled_node(EmptyContextAll::EmptyContext(ctx))
    }

    fn cast_from<'a>(
        node: &'a TreeNode<'input, 'arena, Self::NodeKind, Tok>,
    ) -> Option<&'a BaseParserRuleContext<'input, 'arena, Self, Self::NodeKind, Tok>> {
        if node.node_tag == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            let ctx =
                unsafe { &*(node as *const _ as *const EmptyContextAll<'input, 'arena, Tok>) };
            match ctx {
                EmptyContextAll::EmptyContext(ctx) => Some(ctx),
                EmptyContextAll::Error(ctx) => Some(ctx),
            }
        } else {
            None
        }
    }

    fn cast_from_mut<'a>(
        node: &'a mut TreeNode<'input, 'arena, Self::NodeKind, Tok>,
    ) -> Option<&'a mut BaseParserRuleContext<'input, 'arena, Self, Self::NodeKind, Tok>> {
        if node.node_tag == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            let ctx =
                unsafe { &mut *(node as *mut _ as *mut EmptyContextAll<'input, 'arena, Tok>) };
            match ctx {
                EmptyContextAll::EmptyContext(ctx) => Some(ctx),
                EmptyContextAll::Error(ctx) => Some(ctx),
            }
        } else {
            None
        }
    }
}

pub type EmptyRuleContext<'input, 'arena> =
    BaseRuleContext<'input, 'arena, EmptyCustomRuleContext<'input, 'arena>, EmptyNodeKind>;

/// Core rule context implementation -- this defines the minimal set of states
/// required for the Antlr parsing algorithm to function.
///
/// This is the Rust version of the `RuleContext` "abstract base class", it will
/// be specialized into language-specific concrete types by monomorphizing the
/// `ExtCtx` type parameter, which is implemented by generated code.
#[repr(C)]
pub struct BaseRuleContext<'input, 'arena, Ext, NodeKind, Tok = CommonToken<'input>>
where
    'input: 'arena,
    NodeKind: NodeKindType<'arena, Tok>,
    Ext: CustomRuleContext<'input, 'arena, Tok, NodeKind = NodeKind>,
    Tok: Token + 'input,
{
    parent: Option<&'arena TreeNode<'input, 'arena, NodeKind, Tok>>,
    pub(crate) ext: Ext,

    // Covariant over 'input, invariant over 'arena
    _marker: PhantomData<(&'input (), *mut &'arena ())>,
}

#[allow(missing_docs)]
impl<'input, 'arena, Ext, NodeKind, Tok> BaseRuleContext<'input, 'arena, Ext, NodeKind, Tok>
where
    'input: 'arena,
    NodeKind: NodeKindType<'arena, Tok>,
    Ext: CustomRuleContext<'input, 'arena, Tok, NodeKind = NodeKind>,
    Tok: Token + 'input,
{
    pub(crate) fn new(
        parent: Option<&'arena TreeNode<'input, 'arena, NodeKind, Tok>>,
        ext: Ext,
    ) -> Self {
        Self {
            parent,
            ext,
            _marker: PhantomData,
        }
    }

    pub(crate) fn morph<Tgt>(
        self,
        ctor: impl FnOnce(Ext) -> Tgt,
    ) -> BaseRuleContext<'input, 'arena, Tgt, NodeKind, Tok>
    where
        Tgt: CustomRuleContext<'input, 'arena, Tok, NodeKind = NodeKind>,
    {
        BaseRuleContext {
            parent: self.parent,
            ext: ctor(self.ext),
            _marker: PhantomData,
        }
    }

    #[inline]
    pub fn parent(&self) -> Option<&'arena TreeNode<'input, 'arena, NodeKind, Tok>> {
        self.parent
    }

    #[inline]
    pub fn has_parent(&self) -> bool {
        self.parent.is_some()
    }

    pub(crate) fn set_parent(
        &mut self,
        parent: Option<&'arena TreeNode<'input, 'arena, NodeKind, Tok>>,
    ) {
        self.parent = parent;
    }

    pub(crate) fn set_alt_number(&mut self, _alt_number: i32) {
        self.ext.set_alt_number(_alt_number)
    }

    pub(crate) fn get_alt_number(&self) -> i32 {
        self.ext.get_alt_number()
    }

    pub(crate) fn get_rule_index(&self) -> usize {
        self.ext.get_rule_index()
    }

    pub(crate) fn get_node_text(&self, rule_names: &[&str]) -> String {
        self.ext.get_node_text(rule_names)
    }
}

impl<'input, 'arena, Ext, NodeKind, Tok> Borrow<Ext>
    for BaseRuleContext<'input, 'arena, Ext, NodeKind, Tok>
where
    'input: 'arena,
    NodeKind: NodeKindType<'arena, Tok>,
    Ext: CustomRuleContext<'input, 'arena, Tok, NodeKind = NodeKind>,
    Tok: Token + 'input,
{
    fn borrow(&self) -> &Ext {
        &self.ext
    }
}

impl<'input, 'arena, Ext, NodeKind, Tok> BorrowMut<Ext>
    for BaseRuleContext<'input, 'arena, Ext, NodeKind, Tok>
where
    'input: 'arena,
    NodeKind: NodeKindType<'arena, Tok>,
    Ext: CustomRuleContext<'input, 'arena, Tok, NodeKind = NodeKind>,
    Tok: Token + 'input,
{
    fn borrow_mut(&mut self) -> &mut Ext {
        &mut self.ext
    }
}

// impl<'input, 'arena, Ext> RuleContext<'arena> for BaseRuleContext<'input, 'arena, Ext>
// where
//     'input: 'arena,
//     Ext: CustomRuleContext<'arena> + 'arena,
// {
//     #[inline(always)]
//     fn get_invoking_state(&self) -> i32 {
//         self.invoking_state
//     }

//     fn get_parent_ctx(&self) -> Option<&'arena dyn RuleContext<'arena>> {
//         self.parent
//             .map(|rc| rc.get_rule_context() as &dyn RuleContext<'arena>)
//     }

//     fn get_rule_index(&self) -> usize {
//         self.ext.get_rule_index()
//     }

//     fn get_alt_number(&self) -> i32 {
//         self.ext.get_alt_number()
//     }

//     fn is_empty(&self) -> bool {
//         self.get_invoking_state() == -1
//     }

//     fn get_node_text(&self, rule_names: &[&str]) -> String {
//         self.ext.get_node_text(rule_names)
//     }
// }

impl<'input, 'arena, Ext, NodeKind, Tok> Debug
    for BaseRuleContext<'input, 'arena, Ext, NodeKind, Tok>
where
    'input: 'arena,
    NodeKind: NodeKindType<'arena, Tok>,
    Ext: CustomRuleContext<'input, 'arena, Tok, NodeKind = NodeKind>,
    Tok: Token + 'input,
{
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        f.debug_struct(type_name::<Self>())
            .field("parent", &self.parent)
            .field("ext", &self.ext)
            .field("..", &"..")
            .finish()
    }
}
