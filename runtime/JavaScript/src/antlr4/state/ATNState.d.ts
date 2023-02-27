import {ATN} from "../atn";
import {Transition} from "../transition";

export declare class ATNState {
    atn: ATN;
    stateNumber: number;
    transitions: Transition[];
}
