import { CommonToken } from "./CommonToken";

export declare class CommonTokenWithStates extends CommonToken {
    previousState: number;
    followState: number;
}
