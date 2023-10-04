import antlr4 from "../src/antlr4/index.node.js";
const IntervalSet = antlr4.IntervalSet;

describe('IntervalSet', () => {
  it("computes interval set length", () => {
      const s1 = new IntervalSet();
      s1.addOne(20);
      s1.addOne(154);
      s1.addRange(169, 171);
      expect(s1.length).toEqual(5);
  });

  it("merges simple interval sets", () => {
      const s1 = new IntervalSet();
      s1.addOne(10);
      expect(s1.toString()).toEqual("10");
      const s2 = new IntervalSet();
      s2.addOne(12);
      expect(s2.toString()).toEqual("12");
      const merged = new IntervalSet();
      merged.addSet(s1);
      expect(merged.toString()).toEqual("10");
      merged.addSet(s2);
      expect(merged.toString()).toEqual("{10, 12}");
      let s3 = new IntervalSet();
      s3.addOne(10);
      merged.addSet(s3);
      expect(merged.toString()).toEqual("{10, 12}");
      s3 = new IntervalSet();
      s3.addOne(11);
      merged.addSet(s3);
      expect(merged.toString()).toEqual("10..12");
      s3 = new IntervalSet();
      s3.addOne(12);
      merged.addSet(s3);
      expect(merged.toString()).toEqual("10..12");

  });

  it("merges complex interval sets", () => {
      const s1 = new IntervalSet();
      s1.addOne(20);
      s1.addOne(141);
      s1.addOne(144);
      s1.addOne(154);
      s1.addRange(169, 171);
      s1.addOne(173);
      expect(s1.toString()).toEqual("{20, 141, 144, 154, 169..171, 173}");
      const s2 = new IntervalSet();
      s2.addRange(9, 14);
      s2.addOne(53);
      s2.addRange(55, 63);
      s2.addRange(65, 72);
      s2.addRange(74, 117);
      s2.addRange(119, 152);
      s2.addRange(154, 164);
      expect(s2.toString()).toEqual("{9..14, 53, 55..63, 65..72, 74..117, 119..152, 154..164}");
      s1.addSet(s2);
      expect(s1.toString()).toEqual("{9..14, 20, 53, 55..63, 65..72, 74..117, 119..152, 154..164, 169..171, 173}");
  });
});
