const {BitSet} = require("../src/antlr4/support/BitSet.js");

describe('BitSet', () => {
  it('emptyState', () => {
    const bs = new BitSet();
    expect(bs.find()).toBe(Number.MAX_SAFE_INTEGER);
    expect(bs.length).toBe(0);
    expect(bs.toString()).toBe('{}');
    expect(bs.equals(bs)).toBeTrue();
    expect((new BitSet()).equals(bs)).toBeTrue();
    expect(bs.equals(new BitSet())).toBeTrue();
  });
  it('zeroThroughOneTwentySeven', () => {
    const bs = new BitSet();
    for (let bit = 0; bit < 128; bit++) {
      bs.set(bit);
      expect(bs.test(bit)).toBeTrue();
      expect(bs.find()).toBe(bit);
      expect(bs.length).toBe(1);
      expect(bs.toString()).toBe('{' + String(bit) + '}');
      expect(bs.equals(bs)).toBeTrue();
      expect((new BitSet()).equals(bs)).toBeFalse();
      expect(bs.equals(new BitSet())).toBeFalse();
      bs.clear(bit);
    }
  });
  it('equality', () => {
    const BS0 = new BitSet();
    BS0.set(0);
    const BS31 = new BitSet();
    BS31.set(31);
    const BS63 = new BitSet();
    BS63.set(63);
    const BS127 = new BitSet();
    BS127.set(127);

    const bs1 = new BitSet();
    expect(bs1.equals(bs1)).toBeTrue();
    bs1.set(0);
    expect(bs1.equals(bs1)).toBeTrue();
    const bs2 = new BitSet();
    expect(bs2.equals(bs2)).toBeTrue();
    expect(bs1.equals(bs2)).toBeFalse();
    bs2.set(127);
    expect(bs1.equals(bs2)).toBeFalse();
    expect(bs2.equals(bs1)).toBeFalse();
    bs2.clear(127);
    bs2.set(0);
    expect(bs1.equals(bs2)).toBeTrue();
    expect(bs2.equals(bs1)).toBeTrue();
    bs1.clear(0);
    bs1.set(127);
    bs2.clear(0);
    bs2.set(128);
    expect(bs1.equals(bs2)).toBeFalse();
    expect(bs2.equals(bs1)).toBeFalse();
  });
  it('bitwiseOr', () => {
    const BS0 = new BitSet();
    BS0.set(0);
    const BS31 = new BitSet();
    BS31.set(31);
    const BS63 = new BitSet();
    BS63.set(63);
    const BS127 = new BitSet();
    BS127.set(127);

    let bs = new BitSet();

    bs.or(BS0);
    expect(bs.equals(BS0));
    bs.clear(0);

    bs.or(BS31);
    expect(bs.equals(BS31));
    bs.clear(31);

    bs.or(BS63);
    expect(bs.equals(BS63));
    bs.clear(63);

    bs.or(BS127);
    expect(bs.equals(BS127));
    bs.clear(127);

    bs.or(BS63);
    expect(bs.equals(BS63));
    bs.clear(63);

    bs.or(BS31);
    expect(bs.equals(BS31));
    bs.clear(31);

    bs.or(BS0);
    expect(bs.equals(BS0));
    bs.clear(0);
  });
});
