use std::cmp::{max, min};

/// Represents interval equivalent to `a..=b`
#[derive(Copy, Clone, Eq, PartialEq, Debug)]
pub struct Interval {
    /// start
    pub a: i32,
    /// end >= start
    pub b: i32,
}

pub(super) const INVALID: Interval = Interval { a: -1, b: -2 };

impl Interval {
    /* stop is not included! */
    pub fn new(a: i32, b: i32) -> Interval {
        Interval { a, b }
    }

    pub fn invalid() -> Interval {
        INVALID
    }

    // fn contains(&self, _item: i32) -> bool { unimplemented!() }

    pub fn length(&self) -> i32 {
        self.b - self.a
    }

    pub fn union(&self, another: &Interval) -> Interval {
        Interval {
            a: min(self.a, another.a),
            b: max(self.b, another.b),
        }
    }

    /** Does self start completely before other? Disjoint */
    pub fn starts_before_disjoint(&self, other: &Interval) -> bool {
        self.a < other.a && self.b < other.a
    }

    /** Does self start at or before other? Nondisjoint */
    pub fn starts_before_non_disjoint(&self, other: &Interval) -> bool {
        self.a <= other.a && self.b >= other.a
    }

    /** Does self.a start after other.b? May or may not be disjoint */
    pub fn starts_after(&self, other: &Interval) -> bool {
        self.a > other.a
    }

    /** Does self start completely after other? Disjoint */
    pub fn starts_after_disjoint(&self, other: &Interval) -> bool {
        self.a > other.b
    }

    /** Does self start after other? NonDisjoint */
    pub fn starts_after_non_disjoint(&self, other: &Interval) -> bool {
        self.a > other.a && self.a <= other.b // self.b>=other.b implied
    }

    /** Are both ranges disjoint? I.e., no overlap? */
    pub fn disjoint(&self, other: &Interval) -> bool {
        self.starts_before_disjoint(other) || self.starts_after_disjoint(other)
    }

    /** Are two intervals adjacent such as 0..41 and 42..42? */
    pub fn adjacent(&self, other: &Interval) -> bool {
        self.a == other.b + 1 || self.b == other.a - 1
    }

    //    public boolean properlyContains(Interval other) {
    //    return other.a >= self.a && other.b <= self.b;
    //    }
    //
    //    /** Return the interval computed from combining self and other */
    //    public Interval union(Interval other) {
    //    return Interval.of(Math.min(a, other.a), Math.max(b, other.b));
    //    }
    //
    //    /** Return the interval in common between self and o */
    //    public Interval intersection(Interval other) {
    //    return Interval.of(Math.max(a, other.a), Math.min(b, other.b));
    //    }
}
