// swift-tools-version:5.6

import PackageDescription

let package = Package(
    name: "Antlr4",
    products: [
        .library(
            name: "Antlr4",
            targets: ["Antlr4"]),
        .library(
            name: "Antlr4Static",
            type: .static,
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
                "./VisitorBasic.g4",
                "./VisitorCalc.g4",
                "./LexerA.g4",
                "./LexerB.g4",
                "./Threading.g4"
            ]
        )
    ]
)
