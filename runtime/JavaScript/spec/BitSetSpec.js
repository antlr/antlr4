import BitSet from "../src/antlr4/misc/BitSet.js";

describe('test BitSet', () => {

    it("is empty", () => {
        const bs = new BitSet();
        expect(bs.length).toEqual(0);
    })

    it("sets 1 value", () => {
        const bs = new BitSet();
        bs.set(67);
        expect(bs.length).toEqual(1);
        expect(bs.get(67)).toBeTrue();
    })

    it("clears 1 value", () => {
        const bs = new BitSet();
        bs.set(67);
        bs.clear(67)
        expect(bs.length).toEqual(0);
        expect(bs.get(67)).toBeFalse();
    })

    it("sets 2 consecutive values", () => {
        const bs = new BitSet();
        bs.set(67);
        bs.set(68);
        expect(bs.length).toEqual(2);
        expect(bs.get(67)).toBeTrue();
        expect(bs.get(68)).toBeTrue();
    })

    it("sets 2 close values", () => {
        const bs = new BitSet();
        bs.set(67);
        bs.set(70);
        expect(bs.length).toEqual(2);
        expect(bs.get(67)).toBeTrue();
        expect(bs.get(70)).toBeTrue();
    })

    it("sets 2 distant values", () => {
        const bs = new BitSet();
        bs.set(67);
        bs.set(241);
        expect(bs.length).toEqual(2);
        expect(bs.get(67)).toBeTrue();
        expect(bs.get(241)).toBeTrue();
    })

    it("combines 2 identical sets", () => {
        const bs1 = new BitSet();
        bs1.set(67);
        const bs2 = new BitSet();
        bs2.set(67);
        bs1.or(bs2);
        expect(bs1.length).toEqual(1);
        expect(bs1.get(67)).toBeTrue();
    })

    it("combines 2 distinct sets", () => {
        const bs1 = new BitSet();
        bs1.set(67);
        const bs2 = new BitSet();
        bs2.set(69);
        bs1.or(bs2);
        expect(bs1.length).toEqual(2);
        expect(bs1.get(67)).toBeTrue();
        expect(bs1.get(69)).toBeTrue();
    })

    it("combines 2 overlapping sets", () => {
        const bs1 = new BitSet();
        bs1.set(67);
        bs1.set(69);
        const bs2 = new BitSet();
        bs2.set(69);
        bs2.set(71);
        bs1.or(bs2);
        expect(bs1.length).toEqual(3);
        expect(bs1.get(67)).toBeTrue();
        expect(bs1.get(69)).toBeTrue();
        expect(bs1.get(71)).toBeTrue();
    })

    it("returns values", () => {
        const bs = new BitSet();
        bs.set(67);
        bs.set(69);
        const values = bs.values();
        expect(values).toEqual([67, 69]);
    })

    it("counts bits", () => {
        for(let i= 0; i <= 0xFF; i++) {
            // count bits the slow but easy to understand way (Kernighan method)
            let count1 = 0;
            let value = i;
            while(value) {
                if(value & 1)
                    count1++;
                value >>= 1;
            }
            // count bits the fast way
            const count2 = BitSet._bitCount(i);
            expect(count2).toEqual(count1);
        }
    })
})
