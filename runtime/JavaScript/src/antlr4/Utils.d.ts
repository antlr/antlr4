/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

export function arrayToString(a: Array<any>): string

declare global {
    interface String {
        seed: number
        hashCode(): number
    }
}

type HashObject = { hashCode: () => number }
type HashFunction = (a: HashObject) => number
type EqualsObject = { equals: (b: any) => boolean }
type EqualsFunction = (a: EqualsObject, b: any) => boolean

export class Set<T> {
    public data: Map<string, T>
    public hashFunction: HashFunction
    public equalsFunction: EqualsFunction

    constructor(hashFunction?: HashFunction, equalsFunction?: EqualsFunction)

    get length(): number

    add(value: T): T
    contains(value: T): boolean
    get(value: T): T | null
    values(): Array<T>
    toString(): string
}

export class BitSet {
    public data: Map<number, boolean>

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

interface Container<I, J> {
    key: I,
    value: J
}
export class Map<K, V> {
    public data: Map<string, Array<Container<K, V>>>

    constructor(hashFunction?: HashFunction, equalsFunction?: EqualsFunction)

    get length(): number

    put(key: K, value: V): V
    containsKey(key: K): boolean
    get(key: K): Container<K, V> | null
    entries(): Array<Container<K, V>>
    getKeys(): Array<K>
    getValues(): Array<V>
    toString(): string
}

export class AltDict<string, V> {
    public data: Map<string, V>

    constructor()

    get(key: string): V | null
    put(key: string, value: V): void
    values(): Array<V>
}

export class DoubleDict<K1, K2, V> {
    public defaultMapCtor: typeof Map
    public cacheMap: Map<K1, Map<K2, V>>

    constructor(defaultMapCtor?: typeof Map)

    get(a: K1, b: K2): V | null
    set(a: K1, b: K2, o: V): void
}

export class Hash {
    public count: number
    public hash: number

    constructor()

    update(...arguments): void
    finish(): number
}

export function hashStuff(...arguments): number

export function escapeWhitespace(s: string, escapeSpaces: boolean): string

export function titleCase(str: string): string

export function equalArrays(a: Array<EqualsObject>, b: Array<EqualsObject>): boolean
