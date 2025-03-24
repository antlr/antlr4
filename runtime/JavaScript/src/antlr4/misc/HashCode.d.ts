
export declare class HashCode {
	count: number;
	hash: number;

	update(): void;
	finish(): number;

	static hashStuff(): number;
}