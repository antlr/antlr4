//! How Lexer should produce tokens
use std::borrow::Cow;

use std::fmt::Debug;
use std::marker::PhantomData;

use crate::char_stream::CharStream;
use crate::token::Token;
use crate::token::{CommonToken, OwningToken};
use crate::Arena;

pub fn invalid() -> &'static CommonToken<'static> {
    Default::default()
}

/// Trait for creating tokens.
pub trait TokenFactory<'input, 'arena>: Debug + Sized
where
    'input: 'arena,
{
    /// Type of tokens emitted by this factory.
    type Tok: Token + 'input;

    fn new(arena: &'arena Arena) -> Self;

    /// Creates token either from `sourse` or from pure data in `text`
    /// Either `source` or `text` are not None
    #[allow(clippy::too_many_arguments)]
    fn create<T>(
        &self,
        source: Option<&mut T>,
        ttype: i32,
        text: Option<String>,
        channel: i32,
        start: isize,
        stop: isize,
        line: u32,
        column: i32,
    ) -> &'arena mut Self::Tok
    where
        T: CharStream<'input> + ?Sized;
}

/// Default token factory
#[derive(Debug)]
pub struct CommonTokenFactory<'input, 'arena> {
    arena: &'arena Arena,
    _input: PhantomData<&'input str>,
}

impl<'input, 'arena> CommonTokenFactory<'input, 'arena>
where
    'input: 'arena,
{
    /// Creates new `CommonTokenFactory`
    pub fn new(arena: &'arena Arena) -> Self {
        Self {
            arena,
            _input: PhantomData,
        }
    }
}

impl<'input, 'arena> TokenFactory<'input, 'arena> for CommonTokenFactory<'input, 'arena>
where
    'input: 'arena,
{
    type Tok = CommonToken<'input>;

    fn new(arena: &'arena Arena) -> Self {
        Self::new(arena)
    }

    #[inline]
    fn create<T>(
        &self,
        source: Option<&mut T>,
        ttype: i32,
        text: Option<String>,
        channel: i32,
        start: isize,
        stop: isize,
        line: u32,
        column: i32,
    ) -> &'arena mut Self::Tok
    where
        T: CharStream<'input> + ?Sized,
    {
        let text = match (text, source) {
            // Safety: obviously unsafe AF -- we're masquerading a string backed
            // by 'arena as one that can live for 'input. The reason this won't blow up, is
            //   1) Overriding text is only used by the error recovery mechanism
            //      in the parser, not the lexer;
            //   2) When we're in the parser, the lexer is no longer exposed to the user;
            //   3) The parser, along with the AST created by it, never return
            //      any &'input references to the user, thus any references to
            //      this text can never be held more than 'arena.
            (Some(t), _) => unsafe {
                std::mem::transmute::<&'arena str, &'input str>(self.arena.alloc_string(t))
            },
            (None, Some(x)) => {
                if stop >= x.size() || start >= x.size() {
                    "<EOF>"
                } else {
                    match x.get_text(start, stop) {
                        Cow::Borrowed(t) => t,
                        Cow::Owned(_) => panic!(
                            "CommonTokenFactory can not be used with CharStream implementations that return owned strings from get_text()"
                        ),
                    }
                }
            }
            _ => "",
        };
        self.arena.alloc(CommonToken::new(
            ttype,
            channel as i16,
            start as i32,
            stop as i32,
            -1,
            line,
            column,
            text,
        ))
    }
}

/// Token factory that creates `OwningToken`s
#[derive(Debug)]
pub struct OwningTokenFactory<'arena>(CommonTokenFactory<'static, 'arena>);

impl<'input, 'arena> TokenFactory<'input, 'arena> for OwningTokenFactory<'arena>
where
    'input: 'arena,
{
    type Tok = OwningToken;

    fn new(arena: &'arena Arena) -> Self {
        Self(CommonTokenFactory::new(arena))
    }

    #[inline]
    fn create<T>(
        &self,
        source: Option<&mut T>,
        ttype: i32,
        text: Option<String>,
        channel: i32,
        start: isize,
        stop: isize,
        line: u32,
        column: i32,
    ) -> &'arena mut Self::Tok
    where
        T: CharStream<'input> + ?Sized,
    {
        let text = match (text, source) {
            (Some(t), _) => t,
            (None, Some(x)) => {
                if stop >= x.size() || start >= x.size() {
                    "<EOF>".to_string()
                } else {
                    x.get_text(start, stop).to_string()
                }
            }
            _ => "".to_string(),
        };
        self.0.arena.alloc(OwningToken::new(
            ttype,
            channel as i16,
            start as i32,
            stop as i32,
            -1,
            line,
            column,
            text,
        ))
    }
}
