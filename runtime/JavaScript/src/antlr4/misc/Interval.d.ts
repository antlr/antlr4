import {Token} from "../Token";

export declare class Interval {

    start: number;
    stop: number;

    constructor(start: number, stop: number);
    constructor(start: Token, stop: Token | undefined);
}
