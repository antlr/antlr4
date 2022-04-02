export default function escapeWhitespace(s, escapeSpaces) {
    s = s.replace(/\t/g, "\\t")
        .replace(/\n/g, "\\n")
        .replace(/\r/g, "\\r");
    if (escapeSpaces) {
        s = s.replace(/ /g, "\u00B7");
    }
    return s;
}
