mod interval;
mod set;

pub use interval::Interval;
pub use set::IntervalSet;
pub use set::IntervalSetBuf;

#[cfg(test)]
mod tests;
