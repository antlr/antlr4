//! Full parser node
use std::any::type_name;
use std::borrow::{Borrow, BorrowMut};
use std::fmt::{Debug, Error, Formatter};
use std::ops::{Deref, DerefMut};

use crate::errors::ANTLRError;
use crate::rule_context::{
    BaseRuleContextInner, CustomRuleContext, EmptyCustomRuleContext, EmptyRuleNode, RuleContext,
};
use crate::token::Token;
use crate::tree::{NodeInner, RuleNode, TerminalNode};
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

pub type EmptyParserRuleContext<'input, 'arena> = BaseParserRuleContextInner<
    'input,
    'arena,
    EmptyCustomRuleContext<'input, 'arena>,
    EmptyRuleNode<'input, 'arena>,
>;

/// Core AST node type -- this augments [BaseParserRuleContextInner] with
/// additional states that allows it to be strung together into a tree, as well
/// as tying it back to the corresponding input.
///
/// This is Rust's version of the `ParserRuleContext` "abstract base class", it
/// will be specialized into language-specific concrete types by monomorphizing
/// the `Ext` type parameter, which is implemented by generated code.
pub struct BaseParserRuleContextInner<'input, 'arena, Ext, Node>
where
    'input: 'arena,
    Ext: CustomRuleContext<'input, 'arena, Node = Node>,
    // Note: `Node` is redundant as a type parameter -- its sole purpose here is
    // to "lift" out the `ExtCtx::Node` associated type, to work around the
    // limitation that Rust's variance propagation doesn't work over type
    // projections. Without this, all rule context types would be invariant over
    // 'input and 'arena.
    Node: RuleNode<'input, 'arena>,
{
    pub(crate) base: BaseRuleContextInner<'input, 'arena, Ext, Node>,

    /// List of children of current node
    pub(crate) children: bumpalo::collections::Vec<'arena, &'arena Node>,
    start: *const (),
    stop: *const (),

    // TODO: figure out what on earth this is used for and whether we can get
    // rid of it
    exception: (),
}

/// Convenience alias — resolves the `Node` parameter automatically from `Ext::Node`.
pub type BaseParserRuleContext<'input, 'arena, Ext> = BaseParserRuleContextInner<
    'input,
    'arena,
    Ext,
    <Ext as CustomRuleContext<'input, 'arena>>::Node,
>;

impl<'input, 'arena, Ext, Node> Debug for BaseParserRuleContextInner<'input, 'arena, Ext, Node>
where
    'input: 'arena,
    Ext: CustomRuleContext<'input, 'arena, Node = Node>,
    Node: RuleNode<'input, 'arena>,
{
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        f.write_str(type_name::<Self>())
    }
}

impl<'input, 'arena, Ext, Node> RuleContext<'arena>
    for BaseParserRuleContextInner<'input, 'arena, Ext, Node>
where
    'input: 'arena,
    Ext: CustomRuleContext<'input, 'arena, Node = Node> + 'arena,
    Node: RuleNode<'input, 'arena>,
{
    fn get_invoking_state(&self) -> i32 {
        self.base.get_invoking_state()
    }

    fn get_parent_ctx(&self) -> Option<&'arena dyn RuleContext<'arena>> {
        self.base.get_parent_ctx()
    }

    fn get_rule_index(&self) -> usize {
        self.base.get_rule_index()
    }

    fn get_alt_number(&self) -> i32 {
        self.base.get_alt_number()
    }
}

impl<'input, 'arena, Ext, Node> Deref for BaseParserRuleContextInner<'input, 'arena, Ext, Node>
where
    'input: 'arena,
    Ext: CustomRuleContext<'input, 'arena, Node = Node>,
    Node: RuleNode<'input, 'arena>,
{
    type Target = Ext;
    fn deref(&self) -> &Self::Target {
        &self.base.ext
    }
}

impl<'input, 'arena, Ext, Node> DerefMut for BaseParserRuleContextInner<'input, 'arena, Ext, Node>
where
    'input: 'arena,
    Ext: CustomRuleContext<'input, 'arena, Node = Node>,
    Node: RuleNode<'input, 'arena>,
{
    fn deref_mut(&mut self) -> &mut Self::Target {
        &mut self.base.ext
    }
}

impl<'input, 'arena, Ext, Node> Borrow<Ext>
    for BaseParserRuleContextInner<'input, 'arena, Ext, Node>
where
    'input: 'arena,
    Ext: CustomRuleContext<'input, 'arena, Node = Node>,
    Node: RuleNode<'input, 'arena>,
{
    fn borrow(&self) -> &Ext {
        &self.base.ext
    }
}

impl<'input, 'arena, Ext, Node> BorrowMut<Ext>
    for BaseParserRuleContextInner<'input, 'arena, Ext, Node>
where
    'input: 'arena,
    Ext: CustomRuleContext<'input, 'arena, Node = Node>,
    Node: RuleNode<'input, 'arena>,
{
    fn borrow_mut(&mut self) -> &mut Ext {
        &mut self.base.ext
    }
}

impl<'input, 'arena, Ext, Node> ParserRuleContext<'input, 'arena>
    for BaseParserRuleContextInner<'input, 'arena, Ext, Node>
where
    'input: 'arena,
    Ext: CustomRuleContext<'input, 'arena, Node = Node> + 'arena,
    Node: RuleNode<'input, 'arena>,
{
    #[inline]
    fn start(&self) -> &'arena dyn Token {
        if self.start.is_null() {
            token_factory::invalid()
        } else {
            let vtable = self.get_token_vtable();
            unsafe {
                std::mem::transmute::<(*const (), *const ()), &dyn Token>((
                    self.start,
                    vtable as *const (),
                ))
            }
        }
    }

    #[inline]
    fn stop(&self) -> &'arena dyn Token {
        if self.stop.is_null() {
            token_factory::invalid()
        } else {
            let vtable = self.get_token_vtable();
            unsafe {
                std::mem::transmute::<(*const (), *const ()), &dyn Token>((
                    self.stop,
                    vtable as *const (),
                ))
            }
        }
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
            result += &ParserRuleContext::get_text(*child)
        }

        result
    }
}

impl<'input, 'arena, Ctx, Node> NodeInner<'input, 'arena, Node>
    for BaseParserRuleContextInner<'input, 'arena, Ctx, Node>
where
    'input: 'arena,
    Ctx: CustomRuleContext<'input, 'arena, Node = Node> + 'arena,
    Node: RuleNode<'input, 'arena>,
{
    fn cast_from(node: &Node) -> Option<&Self> {
        Ctx::base_ref_from_node(node)
    }

    fn cast_from_mut(node: &mut Node) -> Option<&mut Self>
    where
        Self: Sized,
    {
        Ctx::base_mut_ref_from_node(node)
    }

    fn iter_child_nodes<'a>(&'a self) -> Box<dyn Iterator<Item = &'arena Node> + 'a> {
        self.get_children()
    }

    fn try_as_node(&'arena self) -> Option<&'arena Node> {
        self.base.try_as_node()
    }
}

#[allow(missing_docs)]
impl<'input, 'arena, Ext, Node> BaseParserRuleContextInner<'input, 'arena, Ext, Node>
where
    'input: 'arena,
    Ext: CustomRuleContext<'input, 'arena, Node = Node> + 'arena,
    Node: RuleNode<'input, 'arena>,
{
    /// # Safety
    ///
    /// This is an internal method only meant to be called by generated code --
    /// there is NO safe way to construct these types outside of the parser!
    pub unsafe fn new(
        arena: &'arena Arena,
        parent: Option<&'arena Node>,
        invoking_state: i32,
        ext: Ext,
    ) -> Self {
        Self {
            base: BaseRuleContextInner::new(parent, invoking_state, ext),
            start: Default::default(),
            stop: Default::default(),
            exception: (),
            children: bumpalo::vec![in arena.children_arena()],
        }
    }

    /// # Safety
    ///
    /// This is an internal method only meant to be called by generated code --
    /// there is NO safe way to construct these types outside of the parser!
    pub unsafe fn copy_from<Src>(
        node: BaseParserRuleContextInner<'input, 'arena, Src, Node>,
        ctor: impl FnOnce(Src) -> Ext,
    ) -> Self
    where
        Src: CustomRuleContext<'input, 'arena, Node = Node>,
    {
        Self {
            base: BaseRuleContextInner::copy_from(node.base, ctor),
            start: node.start,
            stop: node.stop,
            exception: (),
            children: node.children,
        }
    }

    pub fn morph<Tgt>(
        self,
        ctor: impl FnOnce(Ext) -> Tgt,
    ) -> BaseParserRuleContextInner<'input, 'arena, Tgt, Node>
    where
        Tgt: CustomRuleContext<'input, 'arena, Node = Node>,
    {
        BaseParserRuleContextInner {
            base: self.base.morph(ctor),
            start: self.start,
            stop: self.stop,
            exception: self.exception,
            children: self.children,
        }
    }

    pub fn get_parent(&self) -> Option<&'arena Node> {
        self.base.parent()
    }

    pub fn has_parent(&self) -> bool {
        self.base.has_parent()
    }

    pub fn set_self_ref(&mut self, self_ref: *const Node) {
        self.base.set_self_ref(self_ref);
    }

    pub fn set_parent(&mut self, parent: Option<&'arena Node>) {
        self.base.set_parent(parent);
    }

    pub fn set_exception(&self, _e: ANTLRError, _arena: &'arena Arena) {
        // alloc returns &mut T from the bump arena; converting to NonNull is
        // always non-null and the allocation lives for 'arena.
        // let ptr = NonNull::from(arena.alloc_payload(e));
        // self.exception.set(Some(ptr));
    }

    pub fn set_invoking_state(&mut self, t: i32) {
        self.base.set_invoking_state(t)
    }

    pub fn set_alt_number(&mut self, _alt_number: i32) {
        self.base.set_alt_number(_alt_number)
    }

    pub fn set_start(&mut self, t: Option<&'arena dyn Token>) {
        if let Some(t) = t {
            let (ptr, vtable) =
                unsafe { std::mem::transmute::<&'arena dyn Token, (*const (), *const ())>(t) };
            self.set_token_vtable(vtable);
            self.start = ptr;
        } else {
            self.start = std::ptr::null();
        }
    }

    pub fn set_stop(&mut self, t: Option<&'arena dyn Token>) {
        if let Some(t) = t {
            let (ptr, vtable) =
                unsafe { std::mem::transmute::<&'arena dyn Token, (*const (), *const ())>(t) };
            self.set_token_vtable(vtable);
            self.stop = ptr;
        } else {
            self.stop = std::ptr::null();
        }
    }

    pub fn remove_last_child(&mut self) {
        self.children.pop();
    }

    pub fn add_child(&mut self, child: &'arena Node) {
        self.children.push(child);
    }

    pub fn get_child(&self, i: usize) -> Option<&'arena Node> {
        self.children.get(i).copied()
    }

    pub fn get_children<'a>(&'a self) -> Box<dyn Iterator<Item = &'arena Node> + 'a> {
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
        T: ParserRuleContext<'input, 'arena> + NodeInner<'input, 'arena, Node>,
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
        T: ParserRuleContext<'input, 'arena> + NodeInner<'input, 'arena, Node>,
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
        stop: Option<&'arena Node>,
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

    fn get_token_vtable(&self) -> *const () {
        let node_ptr = self.base.get_self_ref() as *const usize;
        unsafe { std::ptr::read(node_ptr.add(2)) as *const () }
    }

    fn set_token_vtable(&mut self, vtable: *const ()) {
        let node_ptr = self.base.get_self_ref() as *const usize as *mut usize;
        unsafe { std::ptr::write(node_ptr.add(2), vtable as usize) }
    }
}
