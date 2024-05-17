import HashSet from "../src/antlr4/misc/HashSet.js";
import HashCode from "../src/antlr4/misc/HashCode.js";

class Thing {

    value1 = Math.random();
    value2 = Math.random();

    hashCode() {
        return HashCode.hashStuff(this.value1);
    }

    equals(other) {
        return other instanceof Thing
            && other.value1 === this.value1
            && other.value2 === this.value2;
    }
}
describe('test HashSet', () => {

    it("adds a thing", () => {
        const t1 = new Thing();
        const t2 = new Thing();
        const set = new HashSet();
        set.add(t1);
        expect(set.has(t1)).toBeTrue();
        expect(set.has(t2)).toBeFalse();
        expect(set.length).toEqual(1);
    })

    it("adds a thing once only", () => {
        const t1 = new Thing();
        const set = new HashSet();
        set.add(t1);
        set.add(t1);
        expect(set.has(t1)).toBeTrue();
        expect(set.length).toEqual(1);
    })

    it("adds 2 things with same hash code", () => {
        const t1 = new Thing();
        const t2 = new Thing();
        t2.value1 = t1.value1;
        const set = new HashSet();
        set.add(t1);
        set.add(t2);
        expect(set.has(t1)).toBeTrue();
        expect(set.has(t2)).toBeTrue();
        expect(set.length).toEqual(2);
    })

})
