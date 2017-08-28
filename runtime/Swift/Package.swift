// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

import PackageDescription

let package = Package(
    name: "Antlr4"
)

products.append(
    Product(
        name: "Antlr4",
        type: .Library(.Dynamic),
        modules: [
            "Antlr4"
        ]
    )
)
