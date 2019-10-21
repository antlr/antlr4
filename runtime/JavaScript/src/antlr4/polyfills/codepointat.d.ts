/*! https://mths.be/codepointat v0.2.0 by @mathias */
declare global {
    interface String {
        codePointAt(position: number): number
    }
}
