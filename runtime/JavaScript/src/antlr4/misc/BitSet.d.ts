export declare class BitSet {
    private data: Uint32Array;
    readonly length: number;
    
    constructor();

    set(index: number): void;

    get(index: number): number;

    clear(index: number): void;

    or(set: BitSet): void;

    values(): Array<number>;

    minValue(): number;

    hashCode(): number;

    equals(): boolean;

    toString(): string;

    _resize(index: number): void;

    static _checkIndex(index: number): void;

    static _bitCount(l: number): number; 
}
