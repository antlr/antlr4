// swift-tools-version:5.3


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
            path:"./runtime/Swift/Tests/Antlr4Tests"),
    ]
)
