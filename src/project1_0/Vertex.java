package project1_0;

/* Vertex in the graph
    - contains coordinate data
    - contains pointer to parent node -for path backtracking in BFS-
 */
public class Vertex {
    Coordinate coordinate;
    Vertex parent = null;

    public Vertex(Coordinate coordinate, Vertex prev)
    {
        this.coordinate = coordinate;
        this.parent = prev;
    }
}
