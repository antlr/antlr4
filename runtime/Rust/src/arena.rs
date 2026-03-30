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

pub(crate) fn is_ref_in_arena<T>(ptr: &T, arena: &bumpalo::Bump) -> bool {
    let p = ptr as *const T as usize;
    ptr_in_arena(p, arena)
}

pub(crate) fn is_slice_in_arena<T>(ptr: &[T], arena: &bumpalo::Bump) -> bool {
    let p = ptr.as_ptr() as usize;
    ptr_in_arena(p, arena)
}

fn ptr_in_arena(ptr: usize, arena: &bumpalo::Bump) -> bool {
    // SAFETY: We're not allocating from the arena while iterating over the
    // chunks
    unsafe {
        arena.iter_allocated_chunks_raw().any(|(start, size)| {
            let start = start as usize;
            let end = start + size;
            ptr >= start && ptr < end
        })
    }
}