export default function stringToCharArray(str) {
    let result = new Uint16Array(str.length);
    for (let i = 0; i < str.length; i++) {
        result[i] = str.charCodeAt(i);
    }
    return result;
}
