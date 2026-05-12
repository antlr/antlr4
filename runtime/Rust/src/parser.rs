//! Base parser implementation
use std::borrow::{Borrow, Cow};
use std::cell::Cell;
use std::marker::PhantomData;
use std::ops::{Deref, DerefMut};

use crate::arena::Arena;
use crate::atn::ATN;
use crate::atn_simulator::{IATNSimulator, ParserATNSimulatorManager};
use crate::error_listener::{
    ConsoleErrorListener, ErrorListener, ErrorListenerDelegate, ProxyErrorListener,
};
use crate::error_strategy::ErrorStrategy;
use crate::errors::ANTLRError;
use crate::interval_set::IntervalSet;
use crate::parser_atn_simulator::ParserATNSimulator;
use crate::recognizer::{Actions, Recognizer};
use crate::rule_context::{states_stack, RuleContext as _};
use crate::token::{Token, TOKEN_EOF};
use crate::token_factory::TokenFactory;
use crate::token_stream::TokenStream;
use crate::tree::{NodeKindType, ParseTreeListener, Tree as _, TreeNode};
use crate::utils::cell_update;
use crate::vocabulary::Vocabulary;

#[cfg(feature = "recursion-limit")]
const DEFAULT_RECURSION_LIMIT: u32 = 2000;

/// parser functionality required for `ParserATNSimulator` to work
#[allow(missing_docs)]
pub trait Parser<'input, 'arena, TF>: Recognizer<'input, 'arena, TF::Tok>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
{
    fn get_arena(&self) -> &'arena Arena;

    fn get_interpreter(&self) -> &'arena ParserATNSimulator<'arena>;

    fn get_token_factory(&self) -> &TF;

    fn get_current_context(&self) -> &'arena TreeNode<'input, 'arena, Self::Node, TF::Tok>;

    fn consume(
        &mut self,
        err_handler: &mut impl ErrorStrategy<'input, 'arena, TF, Self>,
    ) -> Result<(), ANTLRError>
    where
        Self: Sized;

    fn precpred(
        &self,
        localctx: Option<&TreeNode<'input, 'arena, Self::Node, TF::Tok>>,
        precedence: i32,
    ) -> bool;

    fn get_input_stream_mut(&mut self) -> &mut dyn TokenStream<'input, 'arena, TF>;
    fn get_input_stream(&self) -> &dyn TokenStream<'input, 'arena, TF>;
    fn get_current_token(&self) -> &'arena TF::Tok;
    fn get_expected_tokens<'a>(&'a self) -> Cow<'a, IntervalSet>;

    fn add_error_listener(
        &mut self,
        listener: Box<dyn ErrorListener<'input, 'arena, Self, TF::Tok> + 'input>,
    ) where
        Self: Sized;

    fn remove_error_listeners(&mut self);

    fn notify_error_listeners(
        &self,
        msg: String,
        offending_token: Option<isize>,
        err: Option<&ANTLRError>,
    );
    fn get_error_lister_dispatch<'a>(
        &'a self,
    ) -> Box<dyn ErrorListener<'input, 'arena, Self, TF::Tok> + 'a>
    where
        Self: Sized;

    fn is_expected_token(&self, symbol: i32) -> bool;
    fn get_precedence(&self) -> i32;

    fn get_state(&self) -> i32;
    fn set_state(&mut self, v: i32);
    fn get_rule_invocation_stack(&self) -> Vec<String>;

    #[cfg(feature = "recursion-limit")]
    fn get_recursion_limit(&self) -> u32;
    #[cfg(feature = "recursion-limit")]
    fn set_recursion_limit(&mut self, v: u32);
}

/// Abstract base parser implementation
///
/// Only meant to be instantiated by generated parsers
pub struct BaseParser<'input, 'arena, Ext, Node, Input, TF>
where
    'input: 'arena,
    // Grammar-specific implementation of parser functionality required for
    // `ParserATNSimulator` to work
    Ext: ParserRecog<'input, 'arena, Self, TF::Tok>,
    // Token factory used by the input stream
    TF: TokenFactory<'input, 'arena> + 'arena,
    // Token stream (lexer)
    Input: TokenStream<'input, 'arena, TF>,
    Node: NodeKindType<'arena, TF::Tok>,
{
    interp: Option<ParserATNSimulator<'arena>>,
    atn_manager: &'static ParserATNSimulatorManager,
    global_cache_threshold: usize,

    /// Rule context parser is currently processing
    ctx: *mut (),

    /// Track the {@link ParserRuleContext} objects during the parse and hook
    /// them up using the {@link ParserRuleContext#children} list so that it
    /// forms a parse tree. The {@link ParserRuleContext} returned from the start
    /// rule represents the root of the parse tree.
    ///
    /// <p>Note that if we are not building parse trees, rule contexts only point
    /// upwards. When a rule exits, it returns the context bute that gets garbage
    /// collected if nobody holds a reference. It points upwards but nobody
    /// points at it. </p>
    ///
    /// <p>When we build parse trees, we are adding all of these contexts to
    /// {@link ParserRuleContext#children} list. Contexts are then not candidates
    /// for garbage collection.</p>
    ///
    /// Returns {@code true} if a complete parse tree will be constructed while
    /// parsing, otherwise {@code false}
    pub build_parse_trees: bool,

    /// true if parser reached EOF
    pub matched_eof: bool,

    state: i32,
    /// Token stream that is currently used by this parser
    pub input: Input,
    precedence_stack: Vec<i32>,
    #[cfg(feature = "recursion-limit")]
    pub recursion_limit: u32,
    #[cfg(feature = "recursion-limit")]
    current_recursion_depth: u32,

    parse_listeners: Vec<Box<Node::Listener>>,
    _syntax_errors: Cell<i32>,
    error_listeners: Vec<ErrorListenerDelegate<'input, 'arena, Self, TF::Tok>>,

    pub arena: &'arena Arena,
    ext: Ext,
    pd: PhantomData<(
        &'input (),
        &'arena TF,
        //        &'arena TreeNode<'input, 'arena, Node, TF::Tok>,
    )>,
}

impl<'input, 'arena, Ext, Node, Input, TF> Deref
    for BaseParser<'input, 'arena, Ext, Node, Input, TF>
where
    'input: 'arena,
    Ext: ParserRecog<'input, 'arena, Self, TF::Tok>,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF>,
    Node: NodeKindType<'arena, TF::Tok>,
{
    type Target = Ext;

    fn deref(&self) -> &Self::Target {
        &self.ext
    }
}

impl<'input, 'arena, Ext, Node, Input, TF> DerefMut
    for BaseParser<'input, 'arena, Ext, Node, Input, TF>
where
    'input: 'arena,
    Ext: ParserRecog<'input, 'arena, Self, TF::Tok>,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF>,
    Node: NodeKindType<'arena, TF::Tok>,
{
    fn deref_mut(&mut self) -> &mut Self::Target {
        &mut self.ext
    }
}

pub trait ParserRecog<'input, 'arena, R, Tok>: Actions<'input, 'arena, R, Tok>
where
    'input: 'arena,
    R: Recognizer<'input, 'arena, Tok>,
    Tok: Token + 'input,
{
    fn get_atn_simulator_man(&self) -> &'static ParserATNSimulatorManager;
}

impl<'input, 'arena, Ext, Node, Input, TF> Recognizer<'input, 'arena, TF::Tok>
    for BaseParser<'input, 'arena, Ext, Node, Input, TF>
where
    'input: 'arena,
    Ext: ParserRecog<'input, 'arena, Self, TF::Tok>,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF>,
    Node: NodeKindType<'arena, TF::Tok>,
{
    type Node = Node;

    fn sempred(
        &mut self,
        localctx: Option<&'arena TreeNode<'input, 'arena, Node, TF::Tok>>,
        rule_index: i32,
        action_index: i32,
    ) -> bool {
        Ext::sempred(localctx, rule_index, action_index, self)
    }

    fn get_rule_names(&self) -> &[&str] {
        self.ext.get_rule_names()
    }

    fn get_vocabulary(&self) -> &dyn Vocabulary {
        self.ext.get_vocabulary()
    }

    fn get_grammar_file_name(&self) -> &str {
        self.ext.get_grammar_file_name()
    }

    fn get_atn(&self) -> &ATN {
        self.interp.as_ref().unwrap().atn()
    }
}

impl<'input, 'arena, Ext, Node, Input, TF> Parser<'input, 'arena, TF>
    for BaseParser<'input, 'arena, Ext, Node, Input, TF>
where
    'input: 'arena,
    Ext: ParserRecog<'input, 'arena, Self, TF::Tok>,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF>,
    Node: NodeKindType<'arena, TF::Tok>,
{
    fn get_arena(&self) -> &'arena Arena {
        self.arena
    }

    #[inline(always)]
    fn get_interpreter(&self) -> &'arena ParserATNSimulator<'arena> {
        unsafe {
            std::mem::transmute::<&ParserATNSimulator<'arena>, &'arena ParserATNSimulator<'arena>>(
                self.interp.as_ref().unwrap(),
            )
        }
    }

    fn get_token_factory(&self) -> &TF {
        self.input.get_token_source().get_token_factory()
    }

    #[inline(always)]
    fn get_current_context(&self) -> &'arena TreeNode<'input, 'arena, Node, TF::Tok> {
        self.ctx().unwrap()
    }

    fn consume(
        &mut self,
        err_handler: &mut impl ErrorStrategy<'input, 'arena, TF, Self>,
    ) -> Result<(), ANTLRError> {
        let o = self.get_current_token();
        if o.borrow().get_token_type() != TOKEN_EOF {
            self.input.consume();
        }
        if self.build_parse_trees || !self.parse_listeners.is_empty() {
            if err_handler.in_error_recovery_mode(self) {
                // todo report ructc inference issue
                let node = self.create_error_node(o)?;
                self.add_child_to_ctx(node);
                for listener in &mut self.parse_listeners {
                    listener.visit_error_node(
                        node.as_error_node()
                            .expect("node was created as error node"),
                    )?
                }
            } else {
                let node = self.create_token_node(o)?;
                self.add_child_to_ctx(node);
                for listener in &mut self.parse_listeners {
                    listener.visit_terminal(
                        node.as_terminal_node()
                            .expect("node was created as terminal node"),
                    )?
                }
            }
        }
        Ok(())
    }

    fn precpred(
        &self,
        _localctx: Option<&TreeNode<'input, 'arena, Node, TF::Tok>>,
        precedence: i32,
    ) -> bool {
        //        localctx.map(|it|println!("check at{}",it.to_string_tree(self)));
        //        println!("{}",self.get_precedence());
        precedence >= self.get_precedence()
    }

    fn get_input_stream_mut(&mut self) -> &mut dyn TokenStream<'input, 'arena, TF> {
        &mut self.input //.as_mut()
    }

    fn get_input_stream(&self) -> &dyn TokenStream<'input, 'arena, TF> {
        &self.input
    }

    #[inline]
    fn get_current_token(&self) -> &'arena TF::Tok {
        self.input.get(self.input.index())
    }

    fn get_expected_tokens<'a>(&'a self) -> Cow<'a, IntervalSet> {
        let states_stack = states_stack(self.ctx().unwrap());
        self.interp
            .as_ref()
            .unwrap()
            .atn()
            .get_expected_tokens::<TF::Tok>(self.state, states_stack)
    }

    fn add_error_listener(
        &mut self,
        listener: Box<dyn ErrorListener<'input, 'arena, Self, TF::Tok> + 'input>,
    ) {
        self.error_listeners
            .push(ErrorListenerDelegate::new(listener))
    }

    fn remove_error_listeners(&mut self) {
        self.error_listeners.clear();
    }

    fn notify_error_listeners(
        &self,
        msg: String,
        offending_token: Option<isize>,
        err: Option<&ANTLRError>,
    ) {
        cell_update(&self._syntax_errors, |it| it + 1);
        let offending_token: Option<&_> = match offending_token {
            None => Some(self.get_current_token().borrow()),
            Some(x) => Some(self.input.get(x).borrow()),
        };
        let line = offending_token.map(|x| x.get_line()).unwrap_or(0);
        let column = offending_token
            .map(|x| x.get_char_position_in_line())
            .unwrap_or(-1);

        for listener in self.error_listeners.iter() {
            listener.syntax_error(
                self,
                offending_token.map(|x| x as _),
                line,
                column,
                &msg,
                err,
            )
        }
    }

    fn get_error_lister_dispatch<'a>(
        &'a self,
    ) -> Box<dyn ErrorListener<'input, 'arena, Self, TF::Tok> + 'a> {
        Box::new(ProxyErrorListener {
            delegates: self.error_listeners.borrow(),
        })
    }

    fn is_expected_token(&self, _symbol: i32) -> bool {
        unimplemented!()
    }

    fn get_precedence(&self) -> i32 {
        *self.precedence_stack.last().unwrap_or(&-1)
    }

    #[inline(always)]
    fn get_state(&self) -> i32 {
        self.state
    }

    #[inline(always)]
    fn set_state(&mut self, v: i32) {
        self.state = v;
    }

    fn get_rule_invocation_stack(&self) -> Vec<String> {
        let mut vec = Vec::new();
        let rule_names = self.get_rule_names();
        let mut ctx = self.get_current_context();
        loop {
            let rule_index = ctx.get_rule_index();
            vec.push(rule_names.get(rule_index).unwrap_or(&"n/a").to_string());
            ctx = if let Some(parent) = ctx.get_parent() {
                parent
            } else {
                break;
            }
        }
        vec
    }

    #[cfg(feature = "recursion-limit")]
    fn get_recursion_limit(&self) -> u32 {
        self.recursion_limit
    }

    #[cfg(feature = "recursion-limit")]
    fn set_recursion_limit(&mut self, v: u32) {
        self.recursion_limit = v;
    }

    //    fn get_rule_invocation_stack(&self, c: _) -> Vec<String> {
    //        unimplemented!()
    //    }
}

#[allow(missing_docs)] // todo docs
impl<'input, 'arena, Ext, Node, Input, TF> BaseParser<'input, 'arena, Ext, Node, Input, TF>
where
    'input: 'arena,
    Ext: ParserRecog<'input, 'arena, Self, TF::Tok>,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF>,
    Node: NodeKindType<'arena, TF::Tok>,
{
    pub fn new_base_parser(arena: &'arena Arena, input: Input, ext: Ext) -> Self {
        let atn_manager = ext.get_atn_simulator_man();
        let interp = ParserATNSimulator::new(atn_manager.get_simulator(arena));
        Self {
            atn_manager,
            interp: Some(interp),
            global_cache_threshold: 0,
            ctx: std::ptr::null_mut(),
            build_parse_trees: true,
            matched_eof: false,
            state: -1,
            input,
            precedence_stack: vec![0],
            #[cfg(feature = "recursion-limit")]
            recursion_limit: DEFAULT_RECURSION_LIMIT,
            #[cfg(feature = "recursion-limit")]
            current_recursion_depth: 0,
            parse_listeners: vec![],
            _syntax_errors: Cell::new(0),
            error_listeners: vec![ErrorListenerDelegate::new(
                Box::new(ConsoleErrorListener {})
                    as Box<dyn ErrorListener<'input, 'arena, Self, TF::Tok> + 'input>,
            )],
            arena,
            ext,
            pd: PhantomData,
        }
    }

    pub fn set_global_cache_threshold(&mut self, threshold: usize) {
        self.global_cache_threshold = threshold;
    }

    pub fn get_interpreter_mut(&mut self) -> &mut ParserATNSimulator<'arena> {
        self.interp.as_mut().unwrap()
    }

    /// If current context is same as the given one by pointer comparison
    #[inline]
    pub fn ctx_is(&self, other: Option<&'arena TreeNode<'input, 'arena, Node, TF::Tok>>) -> bool {
        if self.ctx.is_null() && other.is_none() {
            true
        } else if self.ctx.is_null() || other.is_none() {
            false
        } else {
            std::ptr::eq(self.ctx, other.unwrap() as *const _ as *const ())
        }
    }

    /// Gets a reference to current context.
    #[inline]
    pub fn ctx(&self) -> Option<&'arena TreeNode<'input, 'arena, Node, TF::Tok>> {
        if self.ctx.is_null() {
            None
        } else {
            unsafe { Some(&*(self.ctx as *const TreeNode<'input, 'arena, Node, TF::Tok>)) }
        }
    }

    /// Gets a mutable reference to current context.
    ///
    /// # Safety
    /// Follows the same safety rules as dereferencing *mut to &mut
    #[inline]
    pub unsafe fn ctx_mut(
        &mut self,
    ) -> Option<&'arena mut TreeNode<'input, 'arena, Node, TF::Tok>> {
        if self.ctx.is_null() {
            None
        } else {
            Some(&mut *(self.ctx as *mut TreeNode<'input, 'arena, Node, TF::Tok>))
        }
    }

    #[inline]
    fn parent_ctx(&self) -> Option<&'arena TreeNode<'input, 'arena, Node, TF::Tok>> {
        self.ctx().and_then(|it| it.get_parent())
    }

    #[inline]
    fn set_current_ctx(&mut self, ctx: Option<&'arena TreeNode<'input, 'arena, Node, TF::Tok>>) {
        if let Some(ctx) = ctx {
            self.ctx = ctx as *const TreeNode<'input, 'arena, Node, TF::Tok>
                as *mut TreeNode<'input, 'arena, Node, TF::Tok> as *mut ();
        } else {
            self.ctx = std::ptr::null_mut();
        }
    }

    pub fn take_ctx(&mut self) -> Option<&'arena TreeNode<'input, 'arena, Node, TF::Tok>> {
        if self.ctx.is_null() {
            None
        } else {
            let ret = unsafe { &*(self.ctx as *const TreeNode<'input, 'arena, Node, TF::Tok>) };
            self.ctx = std::ptr::null_mut();
            Some(ret)
        }
    }

    #[inline]
    fn add_child_to_ctx(&mut self, child: &'arena TreeNode<'input, 'arena, Node, TF::Tok>) {
        if !self.ctx.is_null() {
            unsafe {
                (*(self.ctx as *mut TreeNode<'input, 'arena, Node, TF::Tok>)).add_child(child);
            }
        }
    }

    #[inline]
    pub fn with_mut_ctx<F, R>(&mut self, f: F) -> R
    where
        F: FnOnce(&mut TreeNode<'input, 'arena, Node, TF::Tok>) -> R,
    {
        assert!(!self.ctx.is_null());
        unsafe { f(&mut *(self.ctx as *mut TreeNode<'input, 'arena, Node, TF::Tok>)) }
    }

    #[inline]
    pub fn match_token(
        &mut self,
        ttype: i32,
        err_handler: &mut impl ErrorStrategy<'input, 'arena, TF, Self>,
    ) -> Result<&'arena TF::Tok, ANTLRError> {
        let mut token = self.get_current_token();
        if token.get_token_type() == ttype {
            if ttype == TOKEN_EOF {
                self.matched_eof = true;
            }

            err_handler.report_match(self);
            self.consume(err_handler)?;
        } else {
            token = err_handler.recover_inline(self)?;
            if self.build_parse_trees && token.get_token_index() == -1 {
                self.add_child_to_ctx(self.create_error_node(token)?);
            }
        }
        Ok(token)
    }

    #[inline]
    pub fn match_wildcard(
        &mut self,
        err_handler: &mut impl ErrorStrategy<'input, 'arena, TF, Self>,
    ) -> Result<&'arena TF::Tok, ANTLRError> {
        let mut token = self.get_current_token();
        if token.get_token_type() > 0 {
            err_handler.report_match(self);
            self.consume(err_handler)?;
        } else {
            token = err_handler.recover_inline(self)?;
            if self.build_parse_trees && token.get_token_index() == -1 {
                self.add_child_to_ctx(self.create_error_node(token)?);
            }
        }
        Ok(token)
    }

    /// Adds parse listener for this parser
    /// returns `listener_id` that can be used later to get listener back
    ///
    /// ### Example for listener usage:
    /// todo
    pub fn add_dyn_parse_listener(&mut self, listener: Box<Node::Listener>) {
        self.parse_listeners.push(listener);
    }

    /// Removes parse listener with corresponding `listener_id`, casts it back to user type and returns it to the caller.
    /// `listener_id` is returned when listener is added via `add_parse_listener`
    pub fn remove_parse_listener<L>(&mut self, listener_id: ListenerId<L>) -> Box<L>
    where
        L: ParseTreeListener<'arena, Node, TF::Tok>,
    {
        let index = self
            .parse_listeners
            .iter()
            .position(|it| ListenerId::new(it).actual_id == listener_id.actual_id)
            .expect("listener not found");
        unsafe { listener_id.into_listener(self.parse_listeners.remove(index)) }
    }

    /// Removes all added parse listeners without returning them
    pub fn remove_parse_listeners(&mut self) {
        self.parse_listeners.clear()
    }

    pub fn trigger_enter_rule_event(&mut self) -> Result<(), ANTLRError> {
        let ctx = self.ctx().unwrap();
        for listener in self.parse_listeners.iter_mut() {
            listener.enter_every_rule(ctx)?;
            ctx.enter_rule(listener)?;
        }
        Ok(())
    }

    pub fn trigger_exit_rule_event(&mut self) -> Result<(), ANTLRError> {
        let ctx = self.ctx().unwrap();
        for listener in self.parse_listeners.iter_mut().rev() {
            ctx.exit_rule(listener)?;
            listener.exit_every_rule(ctx)?;
        }
        Ok(())
    }

    //    fn get_atn_with_bypass_alts(&self) { unimplemented!() }
    //
    //    fn compile_parse_tree_pattern(&self, pattern, patternRuleIndex: Lexer, lexer: Lexer) { unimplemented!() }

    #[inline]
    pub fn enter_rule(
        &mut self,
        localctx: &'arena mut TreeNode<'input, 'arena, Node, TF::Tok>,
        state: i32,
        _rule_index: usize,
    ) -> Result<(), ANTLRError> {
        let child = localctx;
        if self.build_parse_trees {
            self.set_current_ctx(child.get_parent());
            self.add_child_to_ctx(child);
        }

        self.set_state(state);
        self.set_current_ctx(Some(child));
        let start = self.input.lt(1);
        self.with_mut_ctx(|ctx| {
            ctx.set_start(start);
        });

        if !self.parse_listeners.is_empty() {
            self.trigger_enter_rule_event()?;
        }

        #[cfg(feature = "recursion-limit")]
        {
            self.current_recursion_depth += 1;
            if self.current_recursion_depth > self.recursion_limit {
                return Err(ANTLRError::recursion_limit_exceeded(self.recursion_limit));
            }
        }
        Ok(())
    }

    #[inline]
    pub fn exit_rule(
        &mut self,
    ) -> Result<&'arena TreeNode<'input, 'arena, Node, TF::Tok>, ANTLRError> {
        assert!(self.ctx().is_some());

        #[cfg(feature = "recursion-limit")]
        {
            self.current_recursion_depth -= 1;
        }
        if self.matched_eof {
            // if we have matched EOF, it cannot consume past EOF so we use LT(1) here
            let stop = self.input.lt(1);
            self.with_mut_ctx(|ctx| {
                ctx.set_stop(stop);
            });
        } else {
            // stop node is what we just matched
            let stop = self.input.lt(-1);
            self.with_mut_ctx(|ctx| {
                ctx.set_stop(stop);
            });
        }
        if !self.parse_listeners.is_empty() {
            self.trigger_exit_rule_event()?;
        }

        self.set_state(self.ctx().unwrap().get_invoking_state());
        let child = self.ctx().unwrap();
        self.set_current_ctx(child.get_parent());

        Ok(child)
    }

    pub fn enter_recursion_rule(
        &mut self,
        localctx: &'arena mut TreeNode<'input, 'arena, Node, TF::Tok>,
        state: i32,
        _rule_index: usize,
        precedence: i32,
    ) -> Result<(), ANTLRError> {
        self.set_state(state);
        self.precedence_stack.push(precedence);
        self.set_current_ctx(Some(localctx));
        let start = self.input.lt(1);
        self.with_mut_ctx(|ctx| {
            ctx.set_start(start);
        });
        if !self.parse_listeners.is_empty() {
            self.trigger_enter_rule_event()?;
        }
        //println!("{}",self.input.lt(1).map(Token::to_owned).unwrap());

        #[cfg(feature = "recursion-limit")]
        {
            self.current_recursion_depth += 1;
            if self.current_recursion_depth > self.recursion_limit {
                return Err(ANTLRError::recursion_limit_exceeded(self.recursion_limit));
            }
        }
        Ok(())
    }

    pub fn push_new_recursion_context(
        &mut self,
        localctx: &'arena mut TreeNode<'input, 'arena, Node, TF::Tok>,
        state: i32,
        _rule_index: usize,
    ) -> Result<&'arena TreeNode<'input, 'arena, Node, TF::Tok>, ANTLRError> {
        let stop = self.input.lt(-1);
        self.with_mut_ctx(|ctx| {
            ctx.set_parent(Some(localctx));
            ctx.set_invoking_state(state);
            ctx.set_stop(stop);
        });

        let prev = self.take_ctx().unwrap();
        self.set_current_ctx(Some(localctx));
        let start = prev.get_start_token();
        self.with_mut_ctx(|ctx| {
            ctx.set_start(start);
        });
        if self.build_parse_trees {
            self.add_child_to_ctx(prev);
        }
        if !self.parse_listeners.is_empty() {
            self.trigger_enter_rule_event()?;
        }
        Ok(prev)
    }

    pub fn unroll_recursion_context(
        &mut self,
        parent_ctx: Option<&'arena TreeNode<'input, 'arena, Node, TF::Tok>>,
    ) -> Result<&'arena TreeNode<'input, 'arena, Node, TF::Tok>, ANTLRError> {
        assert!(self.ctx().is_some());

        #[cfg(feature = "recursion-limit")]
        {
            self.current_recursion_depth -= 1;
        }
        self.precedence_stack.pop();
        let stop = self.input.lt(-1);
        self.with_mut_ctx(|ctx| {
            ctx.set_stop(stop);
        });
        let retctx = self.ctx;

        // unroll so _ctx is as it was before call to recursive method
        if !self.parse_listeners.is_empty() {
            while !self.ctx_is(parent_ctx) {
                self.trigger_exit_rule_event()?;
                self.set_current_ctx(self.parent_ctx());
            }
        } else {
            self.set_current_ctx(parent_ctx);
        }

        // hook into tree
        unsafe {
            (*(retctx as *mut TreeNode<'input, 'arena, Node, TF::Tok>)).set_parent(parent_ctx);
        }

        //        println!("{:?}",self.ctx.as_ref().map(|it|it.to_string_tree(self)));
        if self.build_parse_trees && parent_ctx.is_some() {
            self.add_child_to_ctx(unsafe {
                &*(retctx as *const TreeNode<'input, 'arena, Node, TF::Tok>)
            });
        }
        Ok(unsafe { &*(retctx as *const TreeNode<'input, 'arena, Node, TF::Tok>) })
    }

    #[allow(clippy::mut_from_ref)] // &mut is from the arena allocation
    fn create_token_node(
        &self,
        token: &'arena TF::Tok,
    ) -> Result<&'arena mut TreeNode<'input, 'arena, Node, TF::Tok>, ANTLRError> {
        let ptr = TreeNode::create_token_node(self.arena, token);
        if ptr.is_null() {
            #[cfg(feature = "arena-allocation-limit")]
            {
                Err(ANTLRError::arena_allocation_limit_exceeded(
                    self.arena.allocation_limit_bytes(),
                    self.arena.total_allocated_bytes(),
                ))
            }
            #[cfg(not(feature = "arena-allocation-limit"))]
            {
                std::alloc::handle_alloc_error(std::alloc::Layout::new::<
                    TreeNode<'input, 'arena, Node, TF::Tok>,
                >())
            }
        } else {
            Ok(unsafe { &mut *ptr })
        }
    }

    #[allow(clippy::mut_from_ref)] // &mut is from the arena allocation
    fn create_error_node(
        &self,
        token: &'arena TF::Tok,
    ) -> Result<&'arena mut TreeNode<'input, 'arena, Node, TF::Tok>, ANTLRError> {
        let ptr = TreeNode::create_error_node(self.arena, token);
        if ptr.is_null() {
            Err(ANTLRError::dfa_cache_limit_exceeded(0, 0, 0))
        } else {
            Ok(unsafe { &mut *ptr })
        }
    }

    /// Text representation of generated DFA for debugging purposes
    pub fn dump_dfa(&self) {
        let mut seen_one = false;
        for i in 0..self.get_atn().decision_to_state.len() {
            let dfa = self
                .get_interpreter()
                .decision_to_dfa(i)
                .expect("dfa should exist for each decision");
            if !dfa.is_empty() {
                if seen_one {
                    println!()
                }
                println!("Decision {}:", dfa.decision);
                print!("{}", dfa.to_string(self.get_vocabulary()));
                seen_one = true;
            }
        }
    }
}

impl<'input, 'arena, Ext, Node, Input, TF> Drop for BaseParser<'input, 'arena, Ext, Node, Input, TF>
where
    'input: 'arena,
    Ext: ParserRecog<'input, 'arena, Self, TF::Tok>,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF>,
    Node: NodeKindType<'arena, TF::Tok>,
{
    fn drop(&mut self) {
        if self.global_cache_threshold == 0 {
            return;
        }

        let Some(interp) = self.interp.take() else {
            return;
        };
        let cache_bytes = interp.total_allocated_bytes();
        drop(interp);

        if cache_bytes > self.global_cache_threshold {
            self.atn_manager.reset_all();
        }
    }
}

/// Allows to safely cast listener back to user type
#[derive(Debug)]
pub struct ListenerId<T: ?Sized> {
    pub(crate) actual_id: usize,
    phantom: PhantomData<fn() -> T>,
}

impl<T: ?Sized> ListenerId<T> {
    #[allow(clippy::borrowed_box)]
    pub fn new(listener: &Box<T>) -> ListenerId<T> {
        ListenerId {
            actual_id: listener.as_ref() as *const T as *const () as usize,
            phantom: Default::default(),
        }
    }
}

impl<T> ListenerId<T> {
    unsafe fn into_listener<U: ?Sized>(self, boxed: Box<U>) -> Box<T> {
        Box::from_raw(Box::into_raw(boxed) as *mut T)
    }
}
