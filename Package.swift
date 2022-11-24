// swift-tools-version:5.6

import PackageDescription

let package = Package(
    name: "Antlr4",
    products: [
        .library(
            name: "Antlr4",
            targets: ["Antlr4"]),
    ],
    targets: [
        .target(
            name: "Antlr4",
            dependencies: [],
            path: "./runtime/Swift/Sources/Antlr4"),
        .testTarget(
            name: "Antlr4Tests",
            dependencies: ["Antlr4"],
            path: "./runtime/Swift/Tests/Antlr4Tests",
            exclude: [
                "gen/LexerB.tokens",
                "gen/VisitorCalcLexer.tokens",
                "gen/VisitorBasicLexer.tokens",
                "gen/VisitorBasic.tokens",
                "gen/Threading.interp",
                "gen/VisitorCalcLexer.interp",
                "gen/ThreadingLexer.interp",
                "gen/VisitorBasicLexer.interp",
                "gen/LexerB.interp",
                "gen/Threading.tokens",
                "gen/VisitorCalc.interp",
                "gen/LexerA.interp",
                "gen/ThreadingLexer.tokens",
                "gen/LexerA.tokens",
                "gen/VisitorCalc.tokens",
                "gen/VisitorBasic.interp",
                "VisitorBasic.g4",
                "VisitorCalc.g4",
                "LexerA.g4",
                "LexerB.g4",
                "Threading.g4"
            ]
        ),
    ]
)
