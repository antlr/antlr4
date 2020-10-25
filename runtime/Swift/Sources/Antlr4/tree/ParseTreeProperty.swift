/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import Foundation

public class ParseTreeProperty<V> {
  var annotations = Dictionary<ObjectIdentifier, V>()
  
  public init() {}
  
  open func get(_ node: ParseTree) -> V? { return annotations[ObjectIdentifier(node)] }
  open func put(_ node: ParseTree, _ value: V) { annotations[ObjectIdentifier(node)] = value }
  open func removeFrom(_ node: ParseTree) { annotations.removeValue(forKey: ObjectIdentifier(node)) }
}
