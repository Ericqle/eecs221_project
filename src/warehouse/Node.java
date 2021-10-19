package warehouse;

public class Node {
    Coordinate coordinate;
    Node parent = null;

    public Node(Coordinate coordinate, Node prev)
    {
        this.coordinate = coordinate;
        this.parent = prev;
    }
}
