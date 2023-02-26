import antlr4 from "../../src/antlr4/index.node.js";
import abc from "./generatedCode/abc.js";
import calc from "./generatedCode/calc.js";

/**
 * 
 * @param {antlr4.Lexer} lexerClass 
 * @param {string} input 
 */
function getRewriter(lexerClass, input) {
    const chars = new antlr4.InputStream(input);
    const lexer = new lexerClass(chars);
    const tokens = new antlr4.CommonTokenStream(lexer);
    tokens.fill();
    return new antlr4.TokenStreamRewriter(tokens);
}

describe("TokenStreamRewriter", () => {
    it("inserts '0' before index 0", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.insertBefore(0, "0");
    
        // Assert
        expect(rewriter.getText()).toEqual("0abc");
    });
    
    it("inserts 'x' after last index", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.insertAfter(2, "x");
    
        // Assert
        expect(rewriter.getText()).toEqual("abcx");
    });

    it("inserts 'x' after the 'b' token", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
        const bToken = rewriter.tokens.get(1);
    
        // Act
        rewriter.insertAfter(bToken, "x");
    
        // Assert
        expect(rewriter.getText()).toEqual("abxc");
    });

    it("inserts 'x' at the end if the index is out of bounds", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.insertAfter(100, "x");
    
        // Assert
        expect(rewriter.getText()).toEqual("abcx");
    });

    it("inserts 'x' before the 'b' token", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
        const bToken = rewriter.tokens.get(1);
    
        // Act
        rewriter.insertBefore(bToken, "x");
    
        // Assert
        expect(rewriter.getText()).toEqual("axbc");
    });
    
    it("inserts 'x' before and after middle index", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.insertBefore(1, "x");
        rewriter.insertAfter(1, "x");
    
        // Assert
        expect(rewriter.getText()).toEqual("axbxc");
    });
    
    it("replaces the first token with an 'x'", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.replaceSingle(0, "x");
    
        // Assert
        expect(rewriter.getText()).toEqual("xbc");
    });
    
    it("replaces the last token with an 'x'", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.replaceSingle(2, "x");
    
        // Assert
        expect(rewriter.getText()).toEqual("abx");
    });
    
    it("replaces the middle token with an 'x'", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.replaceSingle(1, "x");
    
        // Assert
        expect(rewriter.getText()).toEqual("axc");
    });
    
    it("calls getText() with different start/stop arguments (1 of 2)", () => {
        // Arrange
        const rewriter = getRewriter(calc, "x = 3 * 0;");
    
        // Act
        rewriter.replace(4, 8, "0"); // replace 3 * 0 with 0
    
        // Assert
        expect(rewriter.getTokenStream().getText()).toEqual("x = 3 * 0;");
        expect(rewriter.getText()).toEqual("x = 0;");
        expect(rewriter.getText(new antlr4.Interval(0, 9))).toEqual("x = 0;");
        expect(rewriter.getText(new antlr4.Interval(4, 8))).toEqual("0");
    });
    
    it("calls getText() with different start/stop arguments (2 of 2)", () => {
        // Arrange
        const rewriter = getRewriter(calc, "x = 3 * 0 + 2 * 0;");
    
        // Act/Assert
        expect(rewriter.getTokenStream().getText()).toEqual("x = 3 * 0 + 2 * 0;");
    
        rewriter.replace(4, 8, "0"); // replace 3 * 0 with 0
    
        expect(rewriter.getText()).toEqual("x = 0 + 2 * 0;");
        expect(rewriter.getText(new antlr4.Interval(0, 17))).toEqual("x = 0 + 2 * 0;");
        expect(rewriter.getText(new antlr4.Interval(4, 8))).toEqual("0");
        expect(rewriter.getText(new antlr4.Interval(0, 8))).toEqual("x = 0");
        expect(rewriter.getText(new antlr4.Interval(12, 16))).toEqual("2 * 0");
    
        rewriter.insertAfter(17, "// comment");
    
        expect(rewriter.getText(new antlr4.Interval(12, 18))).toEqual("2 * 0;// comment");
        expect(rewriter.getText(new antlr4.Interval(0, 8))).toEqual("x = 0");
    });
    
    it("replaces the middle index, twice", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.replaceSingle(1, "x");
        rewriter.replaceSingle(1, "y");
    
        // Assert
        expect(rewriter.getText()).toEqual("ayc");
    });
    
    it("inserts '_' at the beginning and then replaces the middle token, twice", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.insertBefore(0, "_");
        rewriter.replaceSingle(1, "x");
        rewriter.replaceSingle(1, "y");
    
        // Assert
        expect(rewriter.getText()).toEqual("_ayc");
    });
    
    it("replaces, then deletes the middle index", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.replaceSingle(1, "x");
        rewriter.delete(1);
    
        // Assert
        expect(rewriter.getText()).toEqual("ac");
    });
    
    it("throws an error when inserting into a replaced segment", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.replace(0, 2, "x");
        rewriter.insertBefore(1, "0");
    
        // Assert
        expect(() => rewriter.getText()).toThrowError(
            "insert op <InsertBeforeOp@[@1,1:1='b',<2>,1:1]:\"0\"> within boundaries of previous <ReplaceOp@[@0,0:0='a',<1>,1:0]..[@2,2:2='c',<3>,1:2]:\"x\">"
        );
    });

    it("throws an error when inserting into a deleted segment", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.delete(0, 2);
        rewriter.insertBefore(1, "0");
    
        // Assert
        expect(() => rewriter.getText()).toThrowError(
            "insert op <InsertBeforeOp@[@1,1:1='b',<2>,1:1]:\"0\"> within boundaries of previous <DeleteOp@[@0,0:0='a',<1>,1:0]..[@2,2:2='c',<3>,1:2]>"
        );
    });
    
    it("inserts '0' before the first token and then replaces it with an 'x'", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.insertBefore(0, "0");
        rewriter.replaceSingle(0, "x");
    
        // Assert
        expect(rewriter.getText()).toEqual("0xbc");
    });
    
    it("inserts texts in reverse order when multiple inserts occur at the same index", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.insertBefore(1, "x");
        rewriter.insertBefore(1, "y");
    
        // Assert
        expect(rewriter.getText()).toEqual("ayxbc");
    });
    
    it("inserts 'y' and 'x' before the first index and then replaces it with 'z'", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.insertBefore(0, "x");
        rewriter.insertBefore(0, "y");
        rewriter.replaceSingle(0, "z");
    
        // Assert
        expect(rewriter.getText()).toEqual("yxzbc");
    });
    
    it("replaces the last index with an 'x' and then inserts 'y' before it", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.replaceSingle(2, "x");
        rewriter.insertBefore(2, "y");
    
        // Assert
        expect(rewriter.getText()).toEqual("abyx");
    });
    
    it("replaces thte last index with an 'x' and then inserts 'y' after it", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.replaceSingle(2, "x");
        rewriter.insertAfter(2, "y");
    
        // Assert
        expect(rewriter.getText()).toEqual("abxy");
    });
    
    it("replaces a range with an 'x' and then inserts 'y' before the left edge of the range", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcccba");
    
        // Act
        rewriter.replace(2, 4, "x");
        rewriter.insertBefore(2, "y");
    
        // Assert
        expect(rewriter.getText()).toEqual("abyxba");
    });
    
    it("throws an error if an attempt is made to insert a token before the right edge of a replaced range", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcccba");
    
        // Act
        rewriter.replace(2, 4, "x");
        rewriter.insertBefore(4, "y");
    
        // Assert
        expect(() => rewriter.getText()).toThrowError(
            "insert op <InsertBeforeOp@[@4,4:4='c',<3>,1:4]:\"y\"> within boundaries of previous <ReplaceOp@[@2,2:2='c',<3>,1:2]..[@4,4:4='c',<3>,1:4]:\"x\">"
        );
    });
    
    it("replaces a range with an 'x' then inserts 'y' after the right edge of the range", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcccba");
    
        // Act
        rewriter.replace(2, 4, "x");
        rewriter.insertAfter(4, "y");
    
        // Assert
        expect(rewriter.getText()).toEqual("abxyba");
    });

    it("replaces a token range", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcba");
        const bToken = rewriter.tokens.get(1);
        const dToken = rewriter.tokens.get(3);
        
        // Act
        rewriter.replace(bToken, dToken, "x");

        // Assert
        expect(rewriter.getText()).toEqual("axa");
    });

    it("throws an error when replace is given an invalid range", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
        const badRanges = [
            [1, 0],   // from > to
            [-1, 1],  // from is negative
            [1, -1],  // to is negative
            [-2, -1], // both are negative
            [1, 4]    // to is out of bounds
        ];
        
        // Act/Assert
        for (const [from, to] of badRanges) {
            expect(() => rewriter.replace(from, to, "x")).toThrow();
        }
    });
    
    it("replaces all tokens with an 'x'", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcccba");
    
        // Act
        rewriter.replace(0, 6, "x");
    
        // Assert
        expect(rewriter.getText()).toEqual("x");
    });
    
    it("replaces the middle 'ccc' with 'xyz'", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcccba");
    
        // Act
        rewriter.replace(2, 4, "xyz");
    
        // Assert
        expect(rewriter.getText(new antlr4.Interval(0, 6))).toEqual("abxyzba");
    });
    
    it("throws an error if second replace operation overlaps the first one on the right", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcccba");
    
        // Act
        rewriter.replace(2, 4, "xyz");
        rewriter.replace(3, 5, "foo");
    
        // Assert
        expect(() => rewriter.getText()).toThrowError(
            "replace op boundaries of <ReplaceOp@[@3,3:3='c',<3>,1:3]..[@5,5:5='b',<2>,1:5]:\"foo\"> overlap with previous <ReplaceOp@[@2,2:2='c',<3>,1:2]..[@4,4:4='c',<3>,1:4]:\"xyz\">"
        );
    });
    
    it("throws an error if second replace operation overlaps the first one on the left", () => {
        // Arrange
        const chars = new antlr4.InputStream("abcccba");
        const lexer = new abc(chars);
        const tokens = new antlr4.CommonTokenStream(lexer);
        tokens.fill();
        const rewriter = new antlr4.TokenStreamRewriter(tokens);
    
        // Act
        rewriter.replace(2, 4, "xyz");
        rewriter.replace(1, 3, "foo");
    
        // Assert
        expect(() => rewriter.getText()).toThrowError(
            "replace op boundaries of <ReplaceOp@[@1,1:1='b',<2>,1:1]..[@3,3:3='c',<3>,1:3]:\"foo\"> overlap with previous <ReplaceOp@[@2,2:2='c',<3>,1:2]..[@4,4:4='c',<3>,1:4]:\"xyz\">"
        );
    });
    
    it("ignores first replace operation when the second one overlaps it on both sides (superset)", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcba");
    
        // Act
        rewriter.replace(2, 2, "xyz");
        rewriter.replace(0, 3, "foo");
    
        // Assert
        expect(rewriter.getText()).toEqual("fooa");
    });
    
    it("inserts 'x' and 'y' before the first token", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.insertBefore(0, "x");
        rewriter.insertBefore(0, "y");
    
        // Assert
        expect(rewriter.getText()).toEqual("yxabc");
    });
    
    it("performs 3 inserts at 2 locations", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.insertBefore(1, "x");
        rewriter.insertBefore(0, "y");
        rewriter.insertBefore(1, "z");
    
        // Assert
        expect(rewriter.getText()).toEqual("yazxbc");
    });
    
    it("replaces 'abc' with 'foo' and then inserts 'z' before it", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.replace(0, 2, "foo");
        rewriter.insertBefore(0, "z");
    
        // Assert
        expect(rewriter.getText()).toEqual("zfoo");
    });
    
    it("deletes 'abc' and then inserts 'z' before it", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.delete(0, 2);
        rewriter.insertBefore(0, "z");
    
        // Assert
        expect(rewriter.getText()).toEqual("z");
    });
    
    
    it("makes 3 inserts at 3 locations", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.insertBefore(1, "x");
        rewriter.insertBefore(2, "y");
        rewriter.insertBefore(0, "z");
    
        // Assert
        expect(rewriter.getText()).toEqual("zaxbyc");
    });
    
    it("throws an error if second replace operation affects a subset of a previous one", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcc");
    
        // Act
        rewriter.replace(0, 3, "bar");
        rewriter.replace(1, 2, "foo");
    
        // Assert
        expect(() => rewriter.getText()).toThrowError(
            "replace op boundaries of <ReplaceOp@[@1,1:1='b',<2>,1:1]..[@2,2:2='c',<3>,1:2]:\"foo\"> overlap with previous <ReplaceOp@[@0,0:0='a',<1>,1:0]..[@3,3:3='c',<3>,1:3]:\"bar\">"
        );
    });
    
    it("ignores the first replace operation when the secone one extends it to the left", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcc");
    
        // Act
        rewriter.replace(1, 2, "foo");
        rewriter.replace(0, 2, "bar");
    
        // Assert
        expect(rewriter.getText()).toEqual("barc");
    });
    
    it("ignores the first replace operation when the secone one extends it to the right", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcc");
    
        // Act
        rewriter.replace(1, 2, "foo");
        rewriter.replace(1, 3, "bar");
    
        // Assert
        expect(rewriter.getText()).toEqual("abar");
    });
    
    it("only applies one replace operation when identical ones are given", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcc");
    
        // Act
        rewriter.replace(1, 2, "foo");
        rewriter.replace(1, 2, "foo");
    
        // Assert
        expect(rewriter.getText()).toEqual("afooc");
    });
    
    it("drops the insert operation when it is covered by a subsequent replace operation", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.insertBefore(2, "foo");
        rewriter.replace(1, 2, "foo");
    
        // Assert
        expect(rewriter.getText()).toEqual("afoo");
    });
    
    it("performs the insert operation when disjoint from the replace operation (1 of 2)", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcc");
    
        // Act
        rewriter.insertBefore(1, "x");
        rewriter.replace(2, 3, "foo");
    
        // Assert
        expect(rewriter.getText()).toEqual("axbfoo");
    });
    
    it("performs the insert operation when disjoint from the replace operation (2 of 2)", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcc");
    
        // Act
        rewriter.replace(2, 3, "foo");
        rewriter.insertBefore(1, "x");
    
        // Assert
        expect(rewriter.getText()).toEqual("axbfoo");
    });
    
    it("inserts 'y' before the last token, then deletes it", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.insertBefore(2, "y");
        rewriter.delete(2);
    
        // Assert
        expect(rewriter.getText()).toEqual("aby");
    });

    it("deletes the 'a' token", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
        const aToken = rewriter.tokens.get(0);
    
        // Act
        rewriter.delete(aToken);
    
        // Assert
        expect(rewriter.getText()).toEqual("bc");
    });
    
    // Test for https://github.com/antlr/antlr4/issues/550
    it("distinguishes between insertAfter and insertBefore to preserve order (1 of 2)", () => {
        // Arrange
        const rewriter = getRewriter(abc, "aa");
    
        // Act
        rewriter.insertBefore(0, "<b>");
        rewriter.insertAfter(0, "</b>");
        rewriter.insertBefore(1, "<b>");
        rewriter.insertAfter(1, "</b>");
    
        // Assert
        expect(rewriter.getText()).toEqual("<b>a</b><b>a</b>");
    });
    
    it("distinguishes between insertAfter and insertBefore to preserve order (2 of 2)", () => {
        // Arrange
        const rewriter = getRewriter(abc, "aa");
    
        // Act
        rewriter.insertBefore(0, "<p>");
        rewriter.insertBefore(0, "<b>");
        rewriter.insertAfter(0, "</p>");
        rewriter.insertAfter(0, "</b>");
        rewriter.insertBefore(1, "<b>");
        rewriter.insertAfter(1, "</b>");
    
        // Assert
        expect(rewriter.getText()).toEqual("<b><p>a</p></b><b>a</b>");
    });
    
    it("preserves the order of contiguous inserts", () => {
        // Arrange
        const rewriter = getRewriter(abc, "ab");
    
        // Act
        rewriter.insertBefore(0, "<p>");
        rewriter.insertBefore(0, "<b>");
        rewriter.insertBefore(0, "<div>");
        rewriter.insertAfter(0, "</p>");
        rewriter.insertAfter(0, "</b>");
        rewriter.insertAfter(0, "</div>");
        rewriter.insertBefore(1, "!");
    
        // Assert
        expect(rewriter.getText()).toEqual("<div><b><p>a</p></b></div>!b");
    });
    
    it("accepts different types as text", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.insertBefore(0, false);
        rewriter.insertBefore(0, 0);
        rewriter.insertBefore(0, {});
        rewriter.insertBefore(0, []);
        rewriter.insertBefore(0, "");
        rewriter.insertBefore(0, null);
    
        // Assert
        expect(rewriter.getText()).toEqual("[object Object]0falseabc");
    });

    it("returns the original input if no rewrites have occurred", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        const result = rewriter.getText();
    
        // Assert
        expect(result).toEqual("abc");
    });

    it("segments operations by program name", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        rewriter.insertBefore(0, "b", "P1");
        rewriter.insertAfter(0, "c", "P2");
        rewriter.replaceSingle(2, "b", "P2");
        
        // Assert
        expect(rewriter.getText("P1")).toEqual("babc");
        expect(rewriter.getText("P2")).toEqual("acbb");
    });

    it("doesn't make a fuss if getText is supplied with an interval that exceeds the token range", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abc");
    
        // Act
        const unmodified = rewriter.getText(new antlr4.Interval(-1, 3));
        rewriter.insertAfter(2, "a");
        const modified = rewriter.getText(new antlr4.Interval(0, 200));
        
        // Assert
        expect(unmodified).toEqual("abc");
        expect(modified).toEqual("abca");
    });

    it("ignores inserts that occur within a removed range", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcba");
    
        // Act
        rewriter.insertAfter(2, "c");
        rewriter.delete(2, 3);
        
        // Assert
        expect(rewriter.getText()).toEqual("aba");
    });

    it("handles overlapping delete ranges", () => {
        // Arrange
        const rewriter = getRewriter(abc, "abcba");
    
        // Act
        rewriter.delete(1, 3);
        rewriter.delete(2, 4);
        
        // Assert
        expect(rewriter.getText()).toEqual("a");
    });
});
