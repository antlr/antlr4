import valueToString from "./valueToString.js";

export default function arrayToString(a) {
    return Array.isArray(a) ? ("[" + a.map(valueToString).join(", ") + "]") : "null";
}
