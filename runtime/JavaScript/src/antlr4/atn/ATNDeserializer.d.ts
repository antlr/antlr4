import ATNDeserializationOptions from "./ATNDeserializationOptions";
import ATN from "./ATN";

declare class ATNDeserializer {
    constructor(options?: ATNDeserializationOptions);
    deserialize(data:  number[]) : ATN;
}

export default ATNDeserializer;
