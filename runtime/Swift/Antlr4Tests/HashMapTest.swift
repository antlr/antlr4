//
//  HashMapTest.swift
//  Antlr4
//
//  Created by janyou on 16/4/8.
//  Copyright © 2016 jlabs. All rights reserved.
//

import XCTest
import Antlr4
class HashMapTest: XCTestCase {

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    func testExample() {
        let map = HashMap<String,Int>()
        map["a"] = 1
        map["b"] = 2
        XCTAssert(map["a"] == 1)
        XCTAssert(map["b"] == 2)
        
        for (k,v) in map {
            print("\(k) : \(v)")
        }
        for k in map.keys {
            print("key:\(k) ")
        }
        for v in map.values   {
            print("value:\(v) ")
        }
        print("isEmpty:\(map.isEmpty) ")
        XCTAssert(map.isEmpty  == false)
        print("\(map)")
        
        map.remove("a")
        print("\(map.count)")
        
        map["b"] = nil
        
        print("\(map.count)")
        for (k,v) in map {
            print("\(k) : \(v)")
        }

        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
    }

    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }

}
