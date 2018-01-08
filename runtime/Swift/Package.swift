// swift-tools-version:4.0
// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

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
            dependencies: []),
        .testTarget(
            name: "Antlr4Tests",
            dependencies: ["Antlr4"]),
    ]
)
