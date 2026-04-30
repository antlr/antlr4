//! Full parser node
use std::any::type_name;
use std::borrow::{Borrow, BorrowMut};
use std::fmt::{Debug, Error, Formatter};
use std::ops::{Deref, DerefMut};

use crate::errors::ANTLRError;
use crate::rule_context::{
    BaseRuleContext, CustomRuleContext, EmptyCustomRuleContext, EmptyNodeKind, RuleContext,
};
use crate::token::{CommonToken, Token};
use crate::tree::{CtxPtr, NodeInner, NodeKindType, Tree as _, TreeNode};
use crate::{token_factory, Arena};

/// Language-agnostic behaviors of the Antlr AST.
///
/// This is the language-agnostic, dyn-compatible interface for parser rule
/// contexts.
pub trait ParserRuleContext<'input, 'arena>: RuleContext<'arena> + Debug
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
        'arena: 'a;

    fn get_token(&self, _ttype: i32, _pos: usize) -> Option<&dyn Token>;

    fn get_tokens(&self, _ttype: i32) -> Vec<&dyn Token>;

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

pub type EmptyParserRuleContext<'input, 'arena, Tok = CommonToken<'input>> = BaseParserRuleContext<
    'input,
    'arena,
    EmptyCustomRuleContext<'input, 'arena>,
    EmptyNodeKind,
    Tok,
>;

/// Core AST node type -- this augments [BaseParserRuleContext] with
/// additional states that allows it to be strung together into a tree, as well
/// as tying it back to the corresponding input.
///
/// This is Rust's version of the `ParserRuleContext` "abstract base class", it
/// will be specialized into language-specific concrete types by monomorphizing
/// the `Ext` type parameter, which is implemented by generated code.
#[repr(C)]
pub struct BaseParserRuleContext<'input, 'arena, Ext, NodeKind, Tok = CommonToken<'input>>
where
    'input: 'arena,
    Tok: Token + 'input,
    NodeKind: NodeKindType<'arena, Tok>,
    Ext: CustomRuleContext<'input, 'arena, Tok, NodeKind = NodeKind>,
{
    start: Option<&'arena Tok>,
    stop: Option<&'arena Tok>,
    /// List of children of current node
    pub(crate) children:
        bumpalo::collections::Vec<'arena, &'arena TreeNode<'input, 'arena, NodeKind, Tok>>,

    pub(crate) base: BaseRuleContext<'input, 'arena, Ext, NodeKind, Tok>,

    // TODO: figure out what on earth this is used for and whether we can get
    // rid of it
    exception: (),
}

impl<'input, 'arena, Ext, NodeKind, Tok> Debug
    for BaseParserRuleContext<'input, 'arena, Ext, NodeKind, Tok>
where
    'input: 'arena,
    Tok: Token,
    NodeKind: NodeKindType<'arena, Tok>,
    Ext: CustomRuleContext<'input, 'arena, Tok, NodeKind = NodeKind>,
{
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        f.write_str(type_name::<Self>())
    }
}

impl<'input, 'arena, Ext, NodeKind, Tok> RuleContext<'arena>
    for BaseParserRuleContext<'input, 'arena, Ext, NodeKind, Tok>
where
    'input: 'arena,
    Tok: Token + 'input,
    NodeKind: NodeKindType<'arena, Tok>,
    Ext: CustomRuleContext<'input, 'arena, Tok, NodeKind = NodeKind> + 'arena,
{
    fn get_invoking_state(&self) -> i32 {
        self.as_node().get_invoking_state()
    }

    fn get_parent_ctx(&self) -> Option<&'arena dyn RuleContext<'arena>> {
        self.base
            .parent()
            .map(move |rc| rc as &dyn RuleContext<'arena>)
    }

    fn get_rule_index(&self) -> usize {
        self.base.get_rule_index()
    }

    fn get_alt_number(&self) -> i32 {
        self.base.get_alt_number()
    }

    fn get_node_text(&self, rule_names: &[&str]) -> String {
        self.base.get_node_text(rule_names)
    }
}

impl<'input, 'arena, Ext, NodeKind, Tok> Deref
    for BaseParserRuleContext<'input, 'arena, Ext, NodeKind, Tok>
where
    'input: 'arena,
    Tok: Token,
    NodeKind: NodeKindType<'arena, Tok>,
    Ext: CustomRuleContext<'input, 'arena, Tok, NodeKind = NodeKind>,
{
    type Target = Ext;
    fn deref(&self) -> &Self::Target {
        &self.base.ext
    }
}

impl<'input, 'arena, Ext, NodeKind, Tok> DerefMut
    for BaseParserRuleContext<'input, 'arena, Ext, NodeKind, Tok>
where
    'input: 'arena,
    Tok: Token,
    NodeKind: NodeKindType<'arena, Tok>,
    Ext: CustomRuleContext<'input, 'arena, Tok, NodeKind = NodeKind>,
{
    fn deref_mut(&mut self) -> &mut Self::Target {
        &mut self.base.ext
    }
}

impl<'input, 'arena, Ext, NodeKind, Tok> Borrow<Ext>
    for BaseParserRuleContext<'input, 'arena, Ext, NodeKind, Tok>
where
    'input: 'arena,
    Tok: Token,
    NodeKind: NodeKindType<'arena, Tok>,
    Ext: CustomRuleContext<'input, 'arena, Tok, NodeKind = NodeKind>,
{
    fn borrow(&self) -> &Ext {
        &self.base.ext
    }
}

impl<'input, 'arena, Ext, NodeKind, Tok> BorrowMut<Ext>
    for BaseParserRuleContext<'input, 'arena, Ext, NodeKind, Tok>
where
    'input: 'arena,
    Tok: Token,
    NodeKind: NodeKindType<'arena, Tok>,
    Ext: CustomRuleContext<'input, 'arena, Tok, NodeKind = NodeKind>,
{
    fn borrow_mut(&mut self) -> &mut Ext {
        &mut self.base.ext
    }
}

impl<'input, 'arena, Ext, NodeKind, Tok> ParserRuleContext<'input, 'arena>
    for BaseParserRuleContext<'input, 'arena, Ext, NodeKind, Tok>
where
    'input: 'arena,
    Tok: Token + 'input,
    NodeKind: NodeKindType<'arena, Tok>,
    Ext: CustomRuleContext<'input, 'arena, Tok, NodeKind = NodeKind> + 'arena,
{
    #[inline]
    fn start(&self) -> &'arena dyn Token {
        self.start
            .map_or_else(|| token_factory::invalid() as _, |t| t as _)
    }

    #[inline]
    fn stop(&self) -> &'arena dyn Token {
        self.stop
            .map_or_else(|| token_factory::invalid() as _, |t| t as _)
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

    fn get_token(&self, ttype: i32, pos: usize) -> Option<&dyn Token> {
        self.children
            .iter()
            .filter_map(|it| it.as_terminal_node())
            .filter(|it| it.symbol.get_token_type() == ttype)
            .nth(pos)
            .map(|t| t.symbol as &dyn Token)
    }

    fn get_tokens(&self, ttype: i32) -> Vec<&dyn Token> {
        self.children
            .iter()
            .filter_map(|it| it.as_terminal_node())
            .filter(|it| it.symbol.get_token_type() == ttype)
            .map(|t| t.symbol as &dyn Token)
            .collect()
    }

    fn get_text(&self) -> String {
        let mut result = String::new();

        for child in self.children.iter() {
            result += &ParserRuleContext::get_text(*child)
        }

        result
    }
}

impl<'input, 'arena, Ctx, Node, Tok> NodeInner<'input, 'arena, Node, Tok>
    for BaseParserRuleContext<'input, 'arena, Ctx, Node, Tok>
where
    'input: 'arena,
    Tok: Token + 'input,
    Node: NodeKindType<'arena, Tok>,
    Ctx: CustomRuleContext<'input, 'arena, Tok, NodeKind = Node> + 'arena,
{
    fn cast_from<'a>(node: &'a TreeNode<'input, 'arena, Node, Tok>) -> Option<&'a Self> {
        Ctx::cast_from(node)
    }

    fn cast_from_mut<'a>(
        node: &'a mut TreeNode<'input, 'arena, Node, Tok>,
    ) -> Option<&'a mut Self> {
        Ctx::cast_from_mut(node)
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
        self.get_children()
    }
}

#[allow(missing_docs)]
impl<'input, 'arena, Ext, Node, Tok> BaseParserRuleContext<'input, 'arena, Ext, Node, Tok>
where
    'input: 'arena,
    Tok: Token + 'input,
    Node: NodeKindType<'arena, Tok>,
    Ext: CustomRuleContext<'input, 'arena, Tok, NodeKind = Node> + 'arena,
{
    /// Construct a new parser rule context with the given parent, invoking
    /// state, and extension data.
    pub fn create(
        arena: &'arena Arena,
        parent: Option<&'arena TreeNode<'input, 'arena, Ext::NodeKind, Tok>>,
        invoking_state: i32,
        ext: Ext,
    ) -> &'arena mut TreeNode<'input, 'arena, Ext::NodeKind, Tok> {
        // let header = unsafe {
        //     TreeNode::<'input, 'arena, Ext>::new(Ext::node_tag(), Ext::label_tag(), invoking_state)
        // };
        let ctx = Self {
            start: None,
            stop: None,
            children: bumpalo::vec![in arena.children_arena()],
            base: BaseRuleContext::new(parent, ext),
            exception: (),
        };
        let node = unsafe { &mut *Ext::make_node(arena, ctx) };
        node.node_tag = Ext::node_tag();
        node.set_invoking_state(invoking_state);
        node
    }

    pub fn morph<Tgt>(
        self,
        ctor: impl FnOnce(Ext) -> Tgt,
    ) -> BaseParserRuleContext<'input, 'arena, Tgt, Node, Tok>
    where
        Tgt: CustomRuleContext<'input, 'arena, Tok, NodeKind = Node>,
    {
        BaseParserRuleContext {
            base: self.base.morph(ctor),
            start: self.start,
            stop: self.stop,
            exception: self.exception,
            children: self.children,
        }
    }

    pub fn get_parent(&self) -> Option<&'arena TreeNode<'input, 'arena, Ext::NodeKind, Tok>> {
        self.base.parent()
    }

    pub fn has_parent(&self) -> bool {
        self.base.has_parent()
    }

    pub fn set_parent(
        &mut self,
        parent: Option<&'arena TreeNode<'input, 'arena, Ext::NodeKind, Tok>>,
    ) {
        self.base.set_parent(parent);
    }

    pub fn set_exception(&self, _e: ANTLRError, _arena: &'arena Arena) {
        // alloc returns &mut T from the bump arena; converting to NonNull is
        // always non-null and the allocation lives for 'arena.
        // let ptr = NonNull::from(arena.alloc_payload(e));
        // self.exception.set(Some(ptr));
    }

    pub fn set_invoking_state(&mut self, t: i32) {
        self.as_node_mut().set_invoking_state(t);
    }

    pub fn set_alt_number(&mut self, _alt_number: i32) {
        self.base.set_alt_number(_alt_number)
    }

    pub fn set_start(&mut self, t: Option<&'arena Tok>) {
        self.start = t;
    }

    pub fn set_stop(&mut self, t: Option<&'arena Tok>) {
        self.stop = t;
    }

    pub fn remove_last_child(&mut self) {
        self.children.pop();
    }

    pub fn add_child(&mut self, child: &'arena TreeNode<'input, 'arena, Ext::NodeKind, Tok>) {
        self.children.push(child);
    }

    pub fn get_child(
        &self,
        i: usize,
    ) -> Option<&'arena TreeNode<'input, 'arena, Ext::NodeKind, Tok>> {
        self.children.get(i).copied()
    }

    pub fn get_children<'a>(
        &'a self,
    ) -> Box<dyn Iterator<Item = &'arena TreeNode<'input, 'arena, Ext::NodeKind, Tok>> + 'a> {
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
        T: ParserRuleContext<'input, 'arena> + NodeInner<'input, 'arena, Node, Tok>,
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
        T: ParserRuleContext<'input, 'arena> + NodeInner<'input, 'arena, Node, Tok>,
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
        stop: Option<&'arena TreeNode<'input, 'arena, Ext::NodeKind, Tok>>,
    ) -> String {
        let mut result = String::from("[");
        let mut next = Some(self.as_node());
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
