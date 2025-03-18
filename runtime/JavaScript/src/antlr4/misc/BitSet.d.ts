export declare class BitSet {
    // write methods are not exposed based on this conversation https://github.com/antlr/antlr4/pull/4731#discussion_r1847139040
    readonly length: number;

    get(index: number): number;

    values(): Array<number>;

    minValue(): number;

    hashCode(): number;

    equals(): boolean;

    toString(): string;

    static _bitCount(l: number): number; 
}
