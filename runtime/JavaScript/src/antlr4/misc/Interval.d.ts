import Token from "../Token";

export default class Interval {

    start: number;
    stop: number;

    constructor(start: number, stop: number);
    constructor(start: Token, stop: Token | undefined);
}
