package warehouse;

/* Data container for Vertex objects
    - stores x and y indices of vertex in context of
        the graph matrix
 */
class Coordinate {
    int x;
    int y;

    public Coordinate(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
};