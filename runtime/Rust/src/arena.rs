#![allow(clippy::mut_from_ref)]
use crate::{
    cast_unchecked,
    token::Token,
    tree::{NodeInner, NodeKindType, TreeNode},
};

#[derive(Debug)]
pub struct Arena {
    tokens: bumpalo::Bump,
    contexts: bumpalo::Bump,
    payloads: bumpalo::Bump,
    #[cfg(feature = "arena-allocation-limit")]
    limit_bytes: usize,
}

macro_rules! check_allocation_limit {
    ($arena:expr) => {
        #[cfg(feature = "arena-allocation-limit")]
        {
            if $arena.limit_bytes > 0 && $arena.total_allocated_bytes() > $arena.limit_bytes {
                return std::ptr::null_mut();
            }
        }
    };
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

    #[cfg(feature = "arena-allocation-limit")]
    pub fn with_limit<F, R>(limit_bytes: usize, f: F) -> R
    where
        F: for<'a> FnOnce(&'a Arena) -> R,
    {
        let mut arena = Arena::new();
        arena.limit_bytes = limit_bytes;
        f(&arena)
    }

    pub(crate) fn new() -> Self {
        Self {
            tokens: bumpalo::Bump::new(),
            contexts: bumpalo::Bump::new(),
            payloads: bumpalo::Bump::new(),
            #[cfg(feature = "arena-allocation-limit")]
            limit_bytes: get_default_arena_alloc_limit(),
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

    pub fn alloc_string(&self, value: String) -> &str {
        self.tokens.alloc_str(value.as_str())
    }

    pub fn alloc_labeled_node<'input, 'a, N, I, Tok>(
        &self,
        value: I,
    ) -> *mut TreeNode<'input, 'a, N, Tok>
    where
        'input: 'a,
        N: NodeKindType<'a, Tok>,
        I: NodeInner<'input, 'a, N, Tok>,
        Tok: Token + 'input,
    {
        check_allocation_limit!(self);
        self.contexts.alloc(value) as *mut _ as *mut TreeNode<'input, 'a, N, Tok>
    }

    pub fn alloc_zeroed_node<'input, 'a, N, I, Tok>(
        &'a self,
        body: I,
    ) -> *mut TreeNode<'input, 'a, N, Tok>
    where
        'input: 'a,
        N: NodeKindType<'a, Tok>,
        I: NodeInner<'input, 'a, N, Tok>,
        Tok: Token + 'input,
    {
        check_allocation_limit!(self);
        let zeroed_header = unsafe { std::mem::zeroed::<TreeNode<'input, 'a, N, Tok>>() };
        let ptr = self.contexts.alloc((zeroed_header, body));
        ptr as *mut _ as *mut TreeNode<'input, 'a, N, Tok>
    }

    pub fn alloc_node<'input, 'a, N, I, Tok>(
        &'a self,
        header: TreeNode<'input, 'a, N, Tok>,
        body: I,
    ) -> *mut TreeNode<'input, 'a, N, Tok>
    where
        'input: 'a,
        N: NodeKindType<'a, Tok>,
        I: NodeInner<'input, 'a, N, Tok>,
        Tok: Token + 'input,
    {
        check_allocation_limit!(self);
        let ptr = self.contexts.alloc((header, body));
        // Safety: casting to the header portion:
        cast_unchecked!(ptr => mut TreeNode<'input, 'a, N, Tok>)
    }

    pub fn alloc_exception<'a, T>(&'a self, value: T) -> bumpalo::boxed::Box<'a, T> {
        bumpalo::boxed::Box::new_in(value, &self.payloads)
    }

    pub fn alloc_payload<T>(&self, value: T) -> &mut T {
        self.payloads.alloc(value)
    }

    pub fn total_allocated_bytes(&self) -> usize {
        self.tokens.allocated_bytes()
            + self.contexts.allocated_bytes()
            + self.payloads.allocated_bytes()
    }

    #[cfg(feature = "arena-allocation-limit")]
    pub fn allocation_limit_bytes(&self) -> usize {
        self.limit_bytes
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

#[cfg(feature = "arena-allocation-limit")]
pub fn set_default_arena_alloc_limit(limit_bytes: usize) {
    ARENA_ALLOC_LIMIT.store(limit_bytes, std::sync::atomic::Ordering::SeqCst);
}

#[cfg(feature = "arena-allocation-limit")]
pub fn get_default_arena_alloc_limit() -> usize {
    ARENA_ALLOC_LIMIT.load(std::sync::atomic::Ordering::SeqCst)
}

#[cfg(feature = "arena-allocation-limit")]
static ARENA_ALLOC_LIMIT: std::sync::atomic::AtomicUsize = std::sync::atomic::AtomicUsize::new(0);
