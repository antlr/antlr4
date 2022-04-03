export default function standardEqualsFunction(a, b) {
    return a ? a.equals(b) : a===b;
}
