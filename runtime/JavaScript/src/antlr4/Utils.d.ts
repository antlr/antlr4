/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

export declare function arrayToString(a: Array<any>): string

declare global {
    interface String {
        seed: number
        hashCode(): number
    }
}

export declare type HashableObject = { hashCode: () => number }
export declare type HashCallback = (a: HashableObject) => number

export declare type EqualityObject = { equals: (b: any) => boolean }
export declare type EqualsCallback = (a: EqualityObject, b: any) => boolean

export declare class Set<T> {
    data: { [index: string]: T }
    hashFunction: HashCallback
    equalsFunction: EqualsCallback

    constructor(hashFunction?: HashCallback, equalsFunction?: EqualsCallback)

    get length(): number

    add(value: T): T
    contains(value: T): boolean
    get(value: T): T | null
    values(): Array<T>
    toString(): string
}

export declare class BitSet {
    data: { [index: number]: boolean }

    constructor()

    get length(): number

    add(value: number): void
    or(set: BitSet): void
    remove(value: number): void
    contains(value: number): boolean
    values(): Array<number>
    minValue(): number
    hashCode(): number
    equals(other: any): boolean
    toString(): string
}

declare class Map_<K extends object, V> {
    data: { [index: string]: Array<{ key: K, value: V }> }
    hashFunction: HashCallback
    equalsFunction: EqualsCallback

    constructor(hashFunction?: HashCallback, equalsFunction?: EqualsCallback)

    get length(): number

    put(key: K, value: V): V
    containsKey(key: K): boolean
    get(key: K): { key: K, value: V } | null
    entries(): Array<{ key: K, value: V }>
    getKeys(): Array<K>
    getValues(): Array<V>
    toString(): string
}
export { Map_ as Map }

export declare class AltDict<K extends object, V> {
    data: { [index: string]: V }

    constructor()

    get(key: K): V | null
    put(key: K, value: V): void
    values(): Array<V>
}

export declare class DoubleDict<K1 extends object, K2 extends object, V> {
    defaultMapCtor: typeof Map_ | typeof AltDict

    protected cacheMap: { [index: string]: { [index: string]: V } }

    constructor(defaultMapCtor?: typeof Map_ | typeof AltDict)

    get(a: K1, b: K2): V | null
    set(a: K1, b: K2, o: V): void
}

export declare class Hash {
    count: number
    hash: number

    constructor()

    update(...args: Array<any>): void
    finish(): number
}

export declare function hashStuff(...args: Array<any>): number

export declare function escapeWhitespace(s: string, escapeSpaces: boolean): string

export declare function titleCase(str: string): string

export declare function equalArrays(a: Array<EqualityObject>, b: Array<EqualityObject>): boolean
