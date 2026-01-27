use crate::int_stream::IntStream;
use crate::token_factory::TokenFactory;

/// Produces tokens to be used by parser (aka Lexer).
///
/// `TokenStream` implementations are responsible for buffering tokens for
/// parser lookahead
pub trait TokenSource<'input, 'arena, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
{
    /// Return a {@link Token} object from your input stream (usually a {@link
    /// CharStream}). Do not fail/return upon lexing error; keep chewing on the
    /// characters until you get a good one; errors are not passed through to
    /// the parser.
    fn next_token(&mut self) -> &'arena mut TF::Tok;

    /// Get the line number for the current position in the input stream. The
    /// first line in the input is line 1.
    ///
    /// Returns the line number for the current position in the input stream,
    /// or 0 if the current token source does not track line numbers.
    fn get_line(&self) -> u32 {
        0
    }

    /// Get the index into the current line for the current position in the
    /// input stream. The first character on a line has position 0.
    ///
    /// Returns the line number for the current position in the input stream, or
    /// -1 if the current token source does not track character positions.
    fn get_char_position_in_line(&self) -> i32 {
        -1
    }

    /// Returns underlying input stream
    fn get_input_stream(&mut self) -> Option<&mut dyn IntStream>;

    /// Returns string identifier of underlying input e.g. file name
    fn get_source_name(&self) -> String;

    /// Gets the `TokenFactory` this token source is currently using for
    /// creating `Token` objects from the input.
    ///
    /// Required by `Parser` for creating missing tokens.
    fn get_token_factory(&self) -> &TF;

    fn get_dfa_string(&self) -> String;
}

// allows user to call parser with &mut reference to Lexer
impl<'input, 'arena, T, TF> TokenSource<'input, 'arena, TF> for &mut T
where
    'input: 'arena,
    T: TokenSource<'input, 'arena, TF>,
    TF: TokenFactory<'input, 'arena> + 'arena,
{
    #[inline(always)]
    fn next_token(&mut self) -> &'arena mut TF::Tok {
        (**self).next_token()
    }

    #[inline(always)]
    fn get_line(&self) -> u32 {
        (**self).get_line()
    }

    #[inline(always)]
    fn get_char_position_in_line(&self) -> i32 {
        (**self).get_char_position_in_line()
    }

    #[inline(always)]
    fn get_input_stream(&mut self) -> Option<&mut dyn IntStream> {
        (**self).get_input_stream()
    }

    #[inline(always)]
    fn get_source_name(&self) -> String {
        (**self).get_source_name()
    }

    #[inline(always)]
    fn get_token_factory(&self) -> &TF {
        (**self).get_token_factory()
    }

    #[inline(always)]
    fn get_dfa_string(&self) -> String {
        (**self).get_dfa_string()
    }
}

// / adaptor to feed parser with existing tokens
// pub struct IterTokenSource<S, F> where S: Iterator, S::Item: Token, F: TokenFactory<Tok=S::Item> {
//     iter: S,
//     fact: F,
// }
//
// impl<S, F> TokenSource for IterTokenSource<S, F> where S: Iterator, S::Item: Token, F: TokenFactory<Tok=S::Item> {
//     type Tok = S::Item;
//
//     fn next_token(&mut self) -> Box<Self::Tok> {
//         self.iter.next().map(Box::new).unwrap_or_else(
//             || self.get_token_factory().create(
//                 None,
//                 EOF,
//                 TOKEN_DEFAULT_CHANNEL,
//                 -1,
//                 -1,
//                 self.get_line(),
//                 self.get_char_position_in_line(),
//             )
//         )
//     }
//
//     fn get_line(&self) -> isize {
//         0
//     }
//
//     fn get_char_position_in_line(&self) -> isize {
//         -1
//     }
//
//     fn get_input_stream(&mut self) -> Option<&mut dyn CharStream> {
//         None
//     }
//
//     fn get_source_name(&self) -> String {
//         "<iterator>".to_string()
//     }
//
//     fn get_token_factory(&self) -> &dyn TokenFactory<Tok=Self::Tok> {
//         &self.fact
//     }
// }
