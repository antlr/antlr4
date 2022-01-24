/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

class Hash {
  constructor() {
      /** @type {number} */
      this.count = 0;
      /** @type {number} */
      this.hash = 0;
  }

  /**
   * @param {...*} values - The values to hash.
   */
  update(values) {
      for(let i=0;i<arguments.length;i++) {
          const value = arguments[i];
          if (value == null)
              continue;
          if(Array.isArray(value))
              this.update.apply(this, value);
          else {
              let k = 0;
              switch (typeof(value)) {
                  case 'undefined':
                  case 'function':
                      continue;
                  case 'number':
                  case 'boolean':
                      k = value;
                      break;
                  case 'string':
                      k = value.hashCode();
                      break;
                  default:
                      if(value.updateHashCode)
                          value.updateHashCode(this);
                      else
                          console.log("No updateHashCode for " + value.toString())
                      continue;
              }
              k = k * 0xCC9E2D51;
              k = (k << 15) | (k >>> (32 - 15));
              k = k * 0x1B873593;
              this.count = this.count + 1;
              let hash = this.hash ^ k;
              hash = (hash << 13) | (hash >>> (32 - 13));
              hash = hash * 5 + 0xE6546B64;
              this.hash = hash;
          }
      }
  }

  /**
   * @returns {number}
   */
  finish() {
      let hash = this.hash ^ (this.count * 4);
      hash = hash ^ (hash >>> 16);
      hash = hash * 0x85EBCA6B;
      hash = hash ^ (hash >>> 13);
      hash = hash * 0xC2B2AE35;
      hash = hash ^ (hash >>> 16);
      return hash;
  }
}

module.exports = {
  Hash,
};
