import junit.framework.TestCase;
import org.junit.Test;

public class TestGraphNodes extends TestCase {
	@Override
	protected void setUp() throws Exception {
		GraphNode.globalNodeCount = 1;
	}

	@Test public void testBothEmpty() {
		GraphNode a = GraphNode.EMPTY;
		GraphNode b = GraphNode.EMPTY;
		GraphNode r = GraphNode.merge(a, b);
		assertEquals("$:0", r.toString());
	}

	@Test public void testLeftEmpty() {
		GraphNode a = GraphNode.EMPTY;
		GraphNode b = createSingleton(GraphNode.EMPTY, "b");
		GraphNode r = GraphNode.merge(a, b);
		assertEquals("$:0", r.toString());
	}

	@Test public void testRightEmpty() {
		GraphNode a = GraphNode.EMPTY;
		GraphNode b = createSingleton(GraphNode.EMPTY, "b");
		GraphNode r = GraphNode.merge(a, b);
		assertEquals("$:0", r.toString());
	}

	@Test public void testSameSingleTops() {
		GraphNode a1 = createSingleton(GraphNode.EMPTY, "a");
		GraphNode a2 = createSingleton(GraphNode.EMPTY, "a");
		GraphNode r = GraphNode.merge(a1, a2);
		assertEquals("a:1", r.toString());
	}

	@Test public void testDiffSingleTops() {
		GraphNode a1 = createSingleton(GraphNode.EMPTY, "a");
		GraphNode a2 = createSingleton(GraphNode.EMPTY, "b");
		GraphNode r = GraphNode.merge(a1, a2);
		assertEquals("[a, b]:3", r.toString());
	}

	@Test public void test_ax_ax_id() {
		GraphNode x = createSingleton(GraphNode.EMPTY, "x");
		GraphNode a1 = createSingleton(x, "a");
		GraphNode a2 = createSingleton(x, "a");
		GraphNode r = GraphNode.merge(a1, a2);
		assertEquals("a:2", r.toString());
	}

	@Test public void test_ax_ax_eq() {
		GraphNode x1 = createSingleton(GraphNode.EMPTY, "x");
		GraphNode x2 = createSingleton(GraphNode.EMPTY, "x");
		GraphNode a1 = createSingleton(x1, "a");
		GraphNode a2 = createSingleton(x2, "a");
		GraphNode r = GraphNode.merge(a1, a2);
		assertEquals("a:3", r.toString());
	}

	public SingletonNode createSingleton(GraphNode parent, String payload) {
		SingletonNode a = new SingletonNode(parent, payload);
		return a;
	}
}
