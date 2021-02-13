import antlr4 from "../antlr4/index.js";

it("merges interval sets properly", () => {

    const s1 = new antlr4.IntervalSet();
    s1.addOne(10);
    expect(s1.toString()).toEqual("10");
    const s2 = new antlr4.IntervalSet();
    s2.addOne(12);
    expect(s2.toString()).toEqual("12");
    const merged = new antlr4.IntervalSet();
    merged.addSet(s1);
    expect(merged.toString()).toEqual("10");
    merged.addSet(s2);
    expect(merged.toString()).toEqual("{10, 12}");
    let s3 = new antlr4.IntervalSet();
    s3.addOne(10);
    merged.addSet(s3);
    expect(merged.toString()).toEqual("{10, 12}");
    s3 = new antlr4.IntervalSet();
    s3.addOne(11);
    merged.addSet(s3);
    expect(merged.toString()).toEqual("10..12");
    s3 = new antlr4.IntervalSet();
    s3.addOne(12);
    merged.addSet(s3);
    expect(merged.toString()).toEqual("10..12");

});