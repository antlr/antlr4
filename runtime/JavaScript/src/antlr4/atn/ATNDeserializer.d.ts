import ATNDeserializationOptions from "./ATNDeserializationOptions";
import ATN from "./ATN";

export default class ATNDeserializer {
    constructor(options?: ATNDeserializationOptions);
    deserialize(data:  number[]) : ATN;
}
