package warehouse;

public class Node {
    NodePoint nodePoint;
    Node parent = null;

    public Node(NodePoint nodePoint, Node prev)
    {
        this.nodePoint = nodePoint;
        this.parent = prev;
    }
}
