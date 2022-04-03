export default class AltDict {
    constructor() {
        this.data = {};
    }

    get(key) {
        key = "k-" + key;
        if (key in this.data) {
            return this.data[key];
        } else {
            return null;
        }
    }

    put(key, value) {
        key = "k-" + key;
        this.data[key] = value;
    }

    values() {
        const data = this.data;
        const keys = Object.keys(this.data);
        return keys.map(function (key) {
            return data[key];
        });
    }
}
