import Token from "./Token";

declare class CommonToken extends Token {

  clone(): CommonToken;
  cloneWithType(type: any): CommonToken;
  toString(): string;
}

export default CommonToken;
