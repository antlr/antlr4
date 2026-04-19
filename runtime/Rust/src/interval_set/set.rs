use std::borrow::Cow::Borrowed;
use std::borrow::{Borrow, Cow};
use std::cmp::Ordering;
use std::ops::Deref;

use crate::interval_set::Interval;
use crate::token::{TOKEN_EOF, TOKEN_EPSILON};
use crate::vocabulary::{Vocabulary, DUMMY_VOCAB};

/// Set of disjoint intervals
///
/// Basically a set of integers but optimized for cases when it is sparse and created by adding
/// intervals of integers.
#[derive(Eq, PartialEq, Debug)]
pub struct IntervalSet {
    intervals: [Interval],
}

impl<'a> IntoIterator for &'a IntervalSet {
    type Item = &'a Interval;
    type IntoIter = std::slice::Iter<'a, Interval>;

    fn into_iter(self) -> Self::IntoIter {
        self.intervals.iter()
    }
}

impl ToOwned for IntervalSet {
    type Owned = IntervalSetBuf;

    fn to_owned(&self) -> Self::Owned {
        self.to_interval_set_buf()
    }
}

impl IntervalSet {
    pub fn new_empty() -> &'static IntervalSet {
        static EMPTY: [Interval; 0] = [];

        IntervalSet::from_slice(&EMPTY)
    }

    fn from_slice(intervals: &[Interval]) -> &IntervalSet {
        unsafe { &*(intervals as *const [Interval] as *const IntervalSet) }
    }

    fn from_mut_slice(intervals: &mut [Interval]) -> &mut IntervalSet {
        unsafe { &mut *(intervals as *mut [Interval] as *mut IntervalSet) }
    }

    pub fn from_interval(interval: &Interval) -> &IntervalSet {
        IntervalSet::from_slice(std::slice::from_ref(interval))
    }

    pub fn to_interval_set_buf(&self) -> IntervalSetBuf {
        IntervalSetBuf {
            intervals: self.intervals.to_vec(),
        }
    }

    pub fn as_slice(&self) -> &[Interval] {
        &self.intervals
    }

    pub fn get_min(&self) -> Option<i32> {
        self.intervals.first().map(|x| x.a)
    }

    pub fn complement(&self, start: i32, stop: i32) -> IntervalSetBuf {
        let mut vocablulary_is = IntervalSetBuf::new();
        vocablulary_is.add_range(start, stop);
        vocablulary_is.substract(self);
        vocablulary_is
    }

    pub fn contains(&self, _item: i32) -> bool {
        self.intervals
            .binary_search_by(|x| {
                if _item < x.a {
                    return Ordering::Greater;
                }
                if _item > x.b {
                    return Ordering::Less;
                }
                Ordering::Equal
            })
            .is_ok()
    }

    pub fn length(&self) -> i32 {
        self.intervals
            .iter()
            .fold(0, |acc, it| acc + it.b - it.a + 1)
    }

    pub fn to_index_string(&self) -> String {
        self.to_token_string(&DUMMY_VOCAB)
    }

    pub fn to_token_string(&self, vocabulary: &dyn Vocabulary) -> String {
        if self.intervals.is_empty() {
            return "{}".to_owned();
        }
        let mut buf = String::new();
        if self.length() > 1 {
            buf += "{";
        }
        let mut iter = self.intervals.iter();
        while let Some(int) = iter.next() {
            if int.a == int.b {
                buf += self.element_name(vocabulary, int.a).as_ref();
            } else {
                for i in int.a..(int.b + 1) {
                    if i > int.a {
                        buf += ", ";
                    }
                    buf += self.element_name(vocabulary, i).as_ref();
                }
            }
            if iter.len() > 0 {
                buf += ", ";
            }
        }

        if self.length() > 1 {
            buf += "}";
        }

        buf
    }

    fn element_name<'a>(&self, vocabulary: &'a dyn Vocabulary, a: i32) -> Cow<'a, str> {
        if a == TOKEN_EOF {
            Borrowed("<EOF>")
        } else if a == TOKEN_EPSILON {
            Borrowed("<EPSILON>")
        } else {
            vocabulary.get_display_name(a)
        }
    }
}

#[derive(Clone, Eq, PartialEq, Debug)]
pub struct IntervalSetBuf {
    intervals: Vec<Interval>,
}

impl Default for IntervalSetBuf {
    fn default() -> Self {
        Self::new()
    }
}

impl IntervalSetBuf {
    pub fn new() -> Self {
        Self {
            intervals: Vec::new(),
        }
    }

    pub fn as_interval_set(&self) -> &IntervalSet {
        IntervalSet::from_slice(&self.intervals)
    }

    pub fn into_static(self) -> &'static mut IntervalSet {
        IntervalSet::from_mut_slice(Box::leak(self.intervals.into_boxed_slice()))
    }

    pub fn add_one(&mut self, _v: i32) {
        self.add_range(_v, _v)
    }

    pub fn add_range(&mut self, l: i32, h: i32) {
        self.add_interval(Interval { a: l, b: h })
    }

    pub fn add_interval(&mut self, added: Interval) {
        if added.length() < 0 {
            return;
        }

        let mut i = 0;
        while let Some(r) = self.intervals.get_mut(i) {
            if *r == added {
                return;
            }

            if added.adjacent(r) || !added.disjoint(r) {
                // next to each other, make a single larger interval
                let bigger = added.union(r);
                *r = bigger;
                // make sure we didn't just create an interval that
                // should be merged with next interval in list
                loop {
                    i += 1;
                    let next = match self.intervals.get(i) {
                        Some(v) => v,
                        None => break,
                    };
                    if !bigger.adjacent(next) && bigger.disjoint(next) {
                        break;
                    }

                    // if we bump up against or overlap next, merge
                    self.intervals[i - 1] = bigger.union(next); // set to 3 merged ones
                    self.intervals.remove(i);
                }
                return;
            }
            if added.starts_before_disjoint(r) {
                // insert before r
                self.intervals.insert(i, added);
                return;
            }
            i += 1;
        }

        self.intervals.push(added);
    }

    pub fn add_set(&mut self, _other: &IntervalSet) {
        for i in _other {
            self.add_interval(*i)
        }
    }

    pub fn substract(&mut self, right: &IntervalSet) {
        let result = self;
        let mut result_i = 0usize;
        let mut right_i = 0usize;

        while result_i < result.intervals.len() && right_i < right.intervals.len() {
            let result_interval = result.intervals[result_i];
            let right_interval = right.intervals[right_i];

            if right_interval.b < result_interval.a {
                right_i += 1;
                continue;
            }

            if right_interval.a > result_interval.b {
                result_i += 1;
                continue;
            }

            let before_curr = if right_interval.a > result_interval.a {
                Some(Interval::new(result_interval.a, right_interval.a - 1))
            } else {
                None
            };
            let after_curr = if right_interval.b < result_interval.b {
                Some(Interval::new(right_interval.b + 1, result_interval.b))
            } else {
                None
            };

            match (before_curr, after_curr) {
                (Some(before_curr), Some(after_curr)) => {
                    result.intervals[result_i] = before_curr;
                    result.intervals.insert(result_i + 1, after_curr);
                    result_i += 1;
                    right_i += 1;
                }
                (Some(before_curr), None) => {
                    result.intervals[result_i] = before_curr;
                    result_i += 1;
                }
                (None, Some(after_curr)) => {
                    result.intervals[result_i] = after_curr;
                    right_i += 1;
                }
                (None, None) => {
                    result.intervals.remove(result_i);
                }
            }
        }

        //        return result;
    }

    pub fn remove_one(&mut self, el: i32) {
        for i in 0..self.intervals.len() {
            let int = &mut self.intervals[i];
            if el < int.a {
                break;
            }

            if el == int.a && el == int.b {
                self.intervals.remove(i);
                break;
            }

            if el == int.a {
                int.a += 1;
                break;
            }

            if el == int.b {
                int.b -= 1;
                break;
            }

            if el > int.a && el < int.b {
                let old_b = int.b;
                int.b = el - 1;
                self.add_range(el + 1, old_b);
            }
        }
    }
}

impl Deref for IntervalSetBuf {
    type Target = IntervalSet;

    fn deref(&self) -> &IntervalSet {
        self.as_interval_set()
    }
}

impl AsRef<IntervalSet> for IntervalSetBuf {
    fn as_ref(&self) -> &IntervalSet {
        self
    }
}

impl Borrow<IntervalSet> for IntervalSetBuf {
    fn borrow(&self) -> &IntervalSet {
        self
    }
}

impl From<&IntervalSet> for IntervalSetBuf {
    fn from(value: &IntervalSet) -> Self {
        value.to_interval_set_buf()
    }
}
