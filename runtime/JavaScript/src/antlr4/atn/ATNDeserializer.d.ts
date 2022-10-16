import {ATNDeserializationOptions} from "./ATNDeserializationOptions";
import {ATN} from "./ATN";

export declare class ATNDeserializer {
    constructor(options?: ATNDeserializationOptions);
    deserialize(data:  number[]) : ATN;
}
