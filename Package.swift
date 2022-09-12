// swift-tools-version:5.6

import PackageDescription

let package = Package(
    name: "Antlr4",
    products: [
        .library(
            name: "Antlr4",
            type: .dynamic,
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
                "./runtime/Swift/Tests/VisitorBasic.g4",
                "./runtime/Swift/Tests/VisitorCalc.g4",
                "./runtime/Swift/Tests/LexerA.g4",
                "./runtime/Swift/Tests/LexerB.g4",
                "./runtime/Swift/Tests/Threading.g4"
            ]
        )
    ]
)
