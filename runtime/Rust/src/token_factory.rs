//! How Lexer should produce tokens
use std::borrow::Cow::{self, Borrowed, Owned};

use std::fmt::Debug;
use std::marker::PhantomData;

use std::sync::LazyLock;

use crate::char_stream::CharStream;
use crate::token::Token;
use crate::token::{CommonToken, OwningToken, TOKEN_INVALID_TYPE};
use crate::Arena;

pub(crate) static INVALID_COMMON: LazyLock<Box<CommonToken<'static>>> = LazyLock::new(|| {
    Box::new(CommonToken {
        token_type: TOKEN_INVALID_TYPE,
        channel: 0,
        start: -1,
        stop: -1,
        token_index: -1,
        line: 0,
        column: 0,
        text: "<invalid>".into(),
    })
});

pub fn invalid() -> &'static CommonToken<'static> {
    &INVALID_COMMON
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
            (Some(t), _) => Owned(t),
            (None, Some(x)) => {
                if stop >= x.size() || start >= x.size() {
                    Borrowed("<EOF>")
                } else {
                    x.get_text(start, stop)
                }
            }
            _ => Borrowed(""),
        };
        self.arena.alloc(CommonToken {
            token_type: ttype,
            channel: channel as i16,
            start: start as i32,
            stop: stop as i32,
            token_index: -1,
            line,
            column,
            text,
        })
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
        let tok = self
            .0
            .create(source, ttype, text, channel, start, stop, line, column);
        tok.text = Cow::from(tok.text.to_string());
        // SAFETY: the only use of 'input lifetime is for the text field which is now owned
        unsafe { std::mem::transmute::<&mut CommonToken, &mut OwningToken>(tok) }
    }
}
