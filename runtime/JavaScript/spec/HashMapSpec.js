import HashMap from "../src/antlr4/misc/HashMap.js";
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

describe('test HashMap', () => {

    it("sets a thing", () => {
        const t1 = new Thing();
        const map = new HashMap();
        map.set("abc", t1);
        expect(map.containsKey("abc")).toBeTrue();
        expect(map.containsKey("def")).toBeFalse();
        expect(map.length).toEqual(1);
    })

    it("gets a thing", () => {
        const t1 = new Thing();
        const map = new HashMap();
        map.set("abc", t1);
        const t2 = map.get("abc");
        expect(t2).toEqual(t1);
    })

    it("replaces a thing", () => {
        const t1 = new Thing();
        const t2 = new Thing();
        const map = new HashMap();
        map.set("abc", t1);
        map.set("abc", t2);
        const t3 = map.get("abc");
        expect(t3).toEqual(t2);
    })

    it("returns correct length", () => {
        const t1 = new Thing();
        const t2 = new Thing();
        const map = new HashMap();
        expect(map.length).toEqual(0);
        map.set("abc", t1);
        expect(map.length).toEqual(1);
        map.set("def", t2);
        expect(map.length).toEqual(2);
    })

});
