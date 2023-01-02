import Token from "../Token";

declare class Interval {

    start: number;
    stop: number;

    constructor(start: number, stop: number);
    constructor(start: Token, stop: Token | undefined);
}

export default Interval;
