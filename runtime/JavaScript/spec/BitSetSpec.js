import BitSet from "../src/antlr4/misc/BitSet.js";

describe('test BitSet', () => {

    it("is empty", () => {
        const bs = new BitSet();
        expect(bs.length).toEqual(0);
    })

    it("sets 1 value", () => {
        const bs = new BitSet();
        bs.add(67);
        expect(bs.length).toEqual(1);
        expect(bs.has(67)).toBeTrue();
    })

    it("clears 1 value", () => {
        const bs = new BitSet();
        bs.add(67);
        bs.remove(67)
        expect(bs.length).toEqual(0);
        expect(bs.has(67)).toBeFalse();
    })

    it("sets 2 consecutive values", () => {
        const bs = new BitSet();
        bs.add(67);
        bs.add(68);
        expect(bs.length).toEqual(2);
        expect(bs.has(67)).toBeTrue();
        expect(bs.has(68)).toBeTrue();
    })

    it("sets 2 close values", () => {
        const bs = new BitSet();
        bs.add(67);
        bs.add(70);
        expect(bs.length).toEqual(2);
        expect(bs.has(67)).toBeTrue();
        expect(bs.has(70)).toBeTrue();
    })

    it("sets 2 distant values", () => {
        const bs = new BitSet();
        bs.add(67);
        bs.add(241);
        expect(bs.length).toEqual(2);
        expect(bs.has(67)).toBeTrue();
        expect(bs.has(241)).toBeTrue();
    })

    it("combines 2 identical sets", () => {
        const bs1 = new BitSet();
        bs1.add(67);
        const bs2 = new BitSet();
        bs2.add(67);
        bs1.or(bs2);
        expect(bs1.length).toEqual(1);
        expect(bs1.has(67)).toBeTrue();
    })

    it("combines 2 distinct sets", () => {
        const bs1 = new BitSet();
        bs1.add(67);
        const bs2 = new BitSet();
        bs2.add(69);
        bs1.or(bs2);
        expect(bs1.length).toEqual(2);
        expect(bs1.has(67)).toBeTrue();
        expect(bs1.has(69)).toBeTrue();
    })

    it("combines 2 overlapping sets", () => {
        const bs1 = new BitSet();
        bs1.add(67);
        bs1.add(69);
        const bs2 = new BitSet();
        bs2.add(69);
        bs2.add(71);
        bs1.or(bs2);
        expect(bs1.length).toEqual(3);
        expect(bs1.has(67)).toBeTrue();
        expect(bs1.has(69)).toBeTrue();
        expect(bs1.has(71)).toBeTrue();
    })

    it("returns values", () => {
        const bs = new BitSet();
        bs.add(67);
        bs.add(69);
        const values = bs.values();
        expect(values).toEqual(['67', '69']);
    })
})
