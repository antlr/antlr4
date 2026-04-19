use super::*;

#[test]
fn test_add_1() {
    let mut set = IntervalSetBuf::new();
    set.add_range(1, 2);
    assert_eq!(set.as_slice(), &[Interval { a: 1, b: 2 }]);
    set.add_range(2, 3);
    assert_eq!(set.as_slice(), &[Interval { a: 1, b: 3 }]);
    set.add_range(1, 5);
    assert_eq!(set.as_slice(), &[Interval { a: 1, b: 5 }]);
}

#[test]
fn test_add_2() {
    let mut set = IntervalSetBuf::new();
    set.add_range(1, 3);
    set.add_range(5, 6);
    assert_eq!(
        set.as_slice(),
        &[Interval { a: 1, b: 3 }, Interval { a: 5, b: 6 }]
    );
    set.add_range(3, 4);
    assert_eq!(set.as_slice(), &[Interval { a: 1, b: 6 }]);
}

#[test]
fn test_remove() {
    let mut set = IntervalSetBuf::new();
    set.add_range(1, 5);
    set.remove_one(3);
    assert_eq!(
        set.as_slice(),
        &[Interval { a: 1, b: 2 }, Interval { a: 4, b: 5 }]
    );
}

#[test]
fn test_substract() {
    let mut set1 = IntervalSetBuf::new();
    set1.add_range(1, 2);
    set1.add_range(4, 5);
    let mut set2 = IntervalSetBuf::new();
    set2.add_range(2, 4);
    set1.substract(&set2);
    assert_eq!(
        set1.as_slice(),
        &[Interval { a: 1, b: 1 }, Interval { a: 5, b: 5 }]
    );
}
