import * as path from 'path';
import * as fs from 'fs'
import { fileURLToPath } from 'url';
import { XMLParser } from 'fast-xml-parser';
import * as os from "os";
import { execSync } from 'child_process';
import YAML from 'yaml';
import { CharStreams, CommonTokenStream, Token, CommonTokenWithStatesFactory, RecordingErrorStrategy } from "antlr4";

const thisDirName = path.dirname(fileURLToPath(import.meta.url));
const antlrDirName = path.dirname(path.dirname(path.dirname(path.dirname(thisDirName))));

function readAntlrVersion(antlrDirName) {
    const pomData = fs.readFileSync(path.join(antlrDirName, "pom.xml"));
    const parser = new XMLParser();
    const dom = parser.parse(pomData);
    return dom.project.version;

}
function generateJavaParser() {
    const antlrVersion = readAntlrVersion(antlrDirName);
    const antlrTool = path.join(antlrDirName, "tool", "target", "antlr4-" + antlrVersion + "-complete.jar");
    // need to use a JS version of the grammar due to semantic predicates
    const javaGrammar = path.join(antlrDirName, "runtime", "JavaScript", "spec", "suggestions", "Java.g4");
    const genDir = fs.mkdtempSync(path.join(os.tmpdir(), "antlr4-suggestions-js"));
    const cmdLine = [ "java",
                                "-jar", antlrTool,
                                javaGrammar,
                                "-o", genDir,
                                "-no-listener", "-no-visitor", "-encoding", "UTF-8",
                                "-Dlanguage=JavaScript" ];
    execSync(cmdLine.join(" "));
    // create package.json and link antlr4
    execSync("npm link");
    fs.writeFileSync(path.join(genDir, "package.json"), '{"type": "module"}');
    execSync("npm link antlr4", { cwd: genDir});
    return genDir;
}

class Expectation {
    input;
    line;
    column;
    expectedTokens;
}

function parseExpectation(yamlPath) {
    const yamlData = fs.readFileSync(yamlPath, 'utf-8');
    const yaml = YAML.parse(yamlData);
    const expectation = new Expectation()
    expectation.input = yaml.input || "";
    expectation.line = yaml.caret.line;
    expectation.column = yaml.caret.column;
    expectation.expectedTokens = new Set(yaml.expected);
    return expectation;
}

function matchesExpectation(yamlPath, JavaLexer, JavaParser) {
    const expectation = parseExpectation(yamlPath);
    const input = CharStreams.fromString(expectation.input);
    const lexer = new JavaLexer(input);
    lexer.tokenFactory = CommonTokenWithStatesFactory.DEFAULT;
    const stream = new CommonTokenStream(lexer);
    const parser = new JavaParser(stream);
    parser._errHandler = new RecordingErrorStrategy();
    const context = parser.compilationUnit();
    const token = stream.lastTokenAt(expectation.line, expectation.column);
    const { intervalSet } = parser.getExpectedTokensAt(context, token);
    const actual = intervalSet.intervals
        .flatMap(interval => [...Array(interval.stop - interval.start).keys()].map(i => interval.start + i))
        .map(t => t===Token.EOF ? "EOF" : JavaParser.literalNames[t] || JavaParser.symbolicNames[t])
        .map(s => s.startsWith("'") ? s.substring(1, s.length - 1) : s);
    expect(new Set(actual)).toEqual(expectation.expectedTokens);
}

/*
async function matchOneExpectation() {
    const javaParserDir = generateJavaParser();
    const javaLexerModule = await import(path.join(javaParserDir, "JavaLexer.js"));
    const javaParserModule = await import(path.join(javaParserDir, "javaParser.js"));
    const suggestionsDir = path.join(antlrDirName, "runtime-testsuite", "resources", "org", "antlr", "v4", "test", "runtime", "expected-tokens");
    matchesExpectation(path.join(suggestionsDir, "java-start.yml"), javaLexerModule.default, javaParserModule.default);
}

await matchOneExpectation();
*/

describe("test SuggestionHelper", () => {

    it("matches each expectation", async function() {
        const javaParserDir = generateJavaParser();
        const javaLexerModule = await import(path.join(javaParserDir, "JavaLexer.js"));
        const javaParserModule = await import(path.join(javaParserDir, "javaParser.js"));
        const suggestionsDir = path.join(antlrDirName, "runtime-testsuite", "resources", "org", "antlr", "v4", "test", "runtime", "expected-tokens");
        const filenames = fs.readdirSync(suggestionsDir);
        filenames.filter(name => name.endsWith(".yml")).forEach(name => matchesExpectation(path.join(suggestionsDir, name), javaLexerModule.default, javaParserModule.default));
    })

})


