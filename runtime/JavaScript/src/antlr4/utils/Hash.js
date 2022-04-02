export default class Hash {

    constructor() {
        this.count = 0;
        this.hash = 0;
    }

    update() {
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

    finish() {
        let hash = this.hash ^ (this.count * 4);
        hash = hash ^ (hash >>> 16);
        hash = hash * 0x85EBCA6B;
        hash = hash ^ (hash >>> 13);
        hash = hash * 0xC2B2AE35;
        hash = hash ^ (hash >>> 16);
        return hash;
    }

    static hashStuff() {
        const hash = new Hash();
        hash.update.apply(hash, arguments);
        return hash.finish();
    }
}
