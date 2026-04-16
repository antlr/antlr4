//! Symbols that parser works on
use std::borrow::{Borrow, Cow};

use std::fmt::Formatter;
use std::fmt::{Debug, Display};

use crate::token_factory::INVALID_COMMON;

/// Type of tokens that parser considers invalid
pub const TOKEN_INVALID_TYPE: i32 = 0;
/// Type of tokens that DFA can use to advance to next state without consuming actual input token.
/// Should not be created by downstream implementations.
pub const TOKEN_EPSILON: i32 = -2;
/// Min token type that can be assigned to tokens created by downstream implementations.
pub const TOKEN_MIN_USER_TOKEN_TYPE: i32 = 1;
/// Type of EOF token
pub const TOKEN_EOF: i32 = crate::int_stream::EOF;
/// Default channel lexer emits tokens to
pub const TOKEN_DEFAULT_CHANNEL: i32 = 0;
/// Predefined additional channel for lexer to assign tokens to
pub const TOKEN_HIDDEN_CHANNEL: i32 = 1;
/// Shorthand for TOKEN_HIDDEN_CHANNEL
pub const HIDDEN: i32 = TOKEN_HIDDEN_CHANNEL;

/// Implemented by tokens that are produced by a `TokenFactory`
#[allow(missing_docs)]
pub trait Token: Debug + Display {
    /***
     * Begin `Token` interface
     */

    /// Get the text of the token
    fn get_text(&self) -> &str;

    /// Get the token type of the token
    fn get_token_type(&self) -> i32;

    /// The line number on which the first character of this token was
    /// matched. The first line in the input is line 1.
    fn get_line(&self) -> u32;

    /// The index of the first character of this token relative to the beginning
    /// of the line at which it occurs. The first character on a line has
    /// position 0.
    fn get_char_position_in_line(&self) -> i32;

    /// Returns the channel number this token was assigned to. Each token can
    /// arrive on a different channel, but the parser only "tunes" to a single
    /// channel. The parser ignores everything not on DEFAULT_CHANNEL.
    fn get_channel(&self) -> i32;

    /// An index from 0..n-1 of the token object in the token stream.
    /// This must be valid in order to print token streams and use
    /// token stream rewinding.
    ///
    /// Return -1 to indicate that the token was conjured up and does not
    /// have a valid index.
    fn get_token_index(&self) -> isize;

    /// The starting character index of the token
    ///
    /// This method is optional; return -1 if not implemented.
    fn get_start_index(&self) -> isize;

    /// The last character index of the token
    ///
    /// This method is optional; return -1 if not implemented.
    fn get_stop_index(&self) -> isize;

    // fn get_token_source(&self) -> &dyn TokenSource;
    // fn get_input_stream(&self) -> &dyn CharStream;

    /***
     * Begin `WritableToken` interface
     */

    fn set_text(&mut self, _text: String);

    fn set_type(&mut self, _ttype: i32);

    fn set_line(&mut self, _line: u32);

    fn set_char_position_in_line(&mut self, _pos: i32);

    fn set_channel(&mut self, _channel: i32);

    fn set_token_index(&mut self, _v: isize);
}

/// Token that owns its data
pub type OwningToken = CommonToken<'static>;
/// Most versatile Token that uses Cow to save data
/// Can be used seamlessly switch from owned to zero-copy parsing

#[derive(Clone, Debug)]
#[allow(missing_docs)]
pub struct CommonToken<'input> {
    //    source: Option<(Box<TokenSource>,Box<CharStream>)>,
    pub token_type: i32,
    pub channel: i16,
    pub start: i32,
    pub stop: i32,
    pub token_index: i32,
    pub line: u32,
    pub column: i32,
    pub text: Cow<'input, str>,
}

impl Display for CommonToken<'_> {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        let txt = if self.token_type == TOKEN_EOF {
            "<EOF>"
        } else {
            self.text.borrow()
        };
        let txt = txt.replace("\n", "\\n");
        let txt = txt.replace("\r", "\\r");
        let txt = txt.replace("\t", "\\t");
        //        let txt = escape_whitespaces(txt,false);
        f.write_fmt(format_args!(
            "[@{},{}:{}='{}',<{}>{},{}:{}]",
            self.get_token_index(),
            self.start,
            self.stop,
            txt,
            self.token_type,
            if self.channel > 0 {
                ",channel=".to_string() + self.channel.to_string().as_str()
            } else {
                String::new()
            },
            self.line,
            self.column
        ))
    }
}

impl<'input> Token for CommonToken<'input> {
    fn get_token_type(&self) -> i32 {
        self.token_type as i32
    }

    fn get_channel(&self) -> i32 {
        self.channel as i32
    }

    fn get_start_index(&self) -> isize {
        self.start as isize
    }

    fn get_stop_index(&self) -> isize {
        self.stop as isize
    }

    fn get_line(&self) -> u32 {
        self.line
    }

    fn get_char_position_in_line(&self) -> i32 {
        self.column
    }

    // fn get_source(&self) -> Option<(Box<dyn TokenSource>, Box<dyn CharStream>)> {
    //     unimplemented!()
    // }

    fn get_text(&self) -> &str {
        if self.token_type == TOKEN_EOF {
            "<EOF>"
        } else {
            self.text.borrow()
        }
    }

    fn get_token_index(&self) -> isize {
        self.token_index as isize
    }

    fn set_token_index(&mut self, _v: isize) {
        self.token_index = _v as i32;
    }

    fn set_text(&mut self, _text: String) {
        self.text = Cow::from(_text);
    }

    fn set_type(&mut self, _ttype: i32) {
        self.token_type = _ttype;
    }

    fn set_line(&mut self, _line: u32) {
        self.line = _line;
    }

    fn set_char_position_in_line(&mut self, _pos: i32) {
        self.column = _pos;
    }

    fn set_channel(&mut self, _channel: i32) {
        self.channel = _channel as i16;
    }
}

impl Default for &'_ CommonToken<'_> {
    fn default() -> Self {
        &INVALID_COMMON
    }
}

impl From<&dyn Token> for CommonToken<'static> {
    fn from(value: &dyn Token) -> Self {
        CommonToken {
            token_type: value.get_token_type(),
            channel: value.get_channel() as i16,
            start: value.get_start_index() as i32,
            stop: value.get_stop_index() as i32,
            token_index: value.get_token_index() as i32,
            line: value.get_line(),
            column: value.get_char_position_in_line(),
            text: value.get_text().to_string().into(),
        }
    }
}
