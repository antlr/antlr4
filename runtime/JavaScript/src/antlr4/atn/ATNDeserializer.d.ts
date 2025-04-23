import {ATNDeserializationOptions} from "./ATNDeserializationOptions.js";
import {ATN} from "./ATN.js";

export declare class ATNDeserializer {
    constructor(options?: ATNDeserializationOptions);
    deserialize(data:  number[]) : ATN;
}
