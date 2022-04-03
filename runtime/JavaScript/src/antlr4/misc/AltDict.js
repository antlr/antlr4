export default class AltDict {

    constructor() {
        this.data = {};
    }

    get(key) {
        return this.data["k-" + key] || null;
    }

    set(key, value) {
        this.data["k-" + key] = value;
    }

    values() {
        return Object.keys(this.data).filter(key => key.startsWith("k-")).map(key => this.data[key], this);
    }
}
