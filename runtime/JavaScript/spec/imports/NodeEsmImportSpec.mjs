import * as antlr4 from 'antlr4'

describe('Antlr4 Node Esm', () => {
    it('should use the Esm module on Node.js', () => {
        expect(antlr4).toBeDefined();
    });
});
export {};
