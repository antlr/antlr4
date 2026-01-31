#![allow(clippy::mut_from_ref)]
use crate::{token::Token, tree::RuleNode};

#[derive(Debug)]
pub struct Arena {
    tokens: bumpalo::Bump,
    contexts: bumpalo::Bump,
    payloads: bumpalo::Bump,
}

impl Arena {
    /// Invoke the callback function with a reference to a local Arena.
    ///
    /// The arena will be dropped when the callback returns.
    pub fn with<F, R>(f: F) -> R
    where
        F: for<'a> FnOnce(&'a Arena) -> R,
    {
        let arena = Arena::new();
        f(&arena)
    }

    pub(crate) fn new() -> Self {
        Self {
            tokens: bumpalo::Bump::new(),
            contexts: bumpalo::Bump::new(),
            payloads: bumpalo::Bump::new(),
        }
    }

    pub(crate) fn children_arena(&self) -> &bumpalo::Bump {
        &self.payloads
    }

    pub fn alloc_token<T>(&self, value: T) -> &mut T
    where
        T: Token,
    {
        self.tokens.alloc(value)
    }

    pub fn alloc_context<'input, 'a, T>(&'a self, value: T) -> &'a mut T
    where
        'input: 'a,
        T: RuleNode<'input, 'a>,
    {
        let res = self.contexts.alloc(value);
        let self_ref: *const T = res;
        unsafe {
            res.set_self_ref(self_ref);
        }
        res
    }

    pub fn alloc_exception<'a, T>(&'a self, value: T) -> bumpalo::boxed::Box<'a, T> {
        bumpalo::boxed::Box::new_in(value, &self.payloads)
    }

    pub fn alloc_payload<T>(&self, value: T) -> &mut T {
        self.payloads.alloc(value)
    }

    pub fn alloc<T>(&self, value: T) -> &mut T {
        self.payloads.alloc(value)
    }
}
