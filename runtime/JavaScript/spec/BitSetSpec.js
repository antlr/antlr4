const {BitSet} = require("../src/antlr4/support/BitSet.js");

describe('BitSet', () => {
  it('emptyState', () => {
    const bs = new BitSet();
    expect(bs.find()).toBe(Number.MAX_SAFE_INTEGER);
    expect(bs.length).toBe(0);
    expect(bs.toString()).toBe('{}');
  });
});
