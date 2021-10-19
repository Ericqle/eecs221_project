package warehouse;

import java.util.*;

// Implementation of Lee Algorithm/ Dungeon Problem
class BFSShortestPath {
    int ROW = 40;
    int COL = 25;

    // Used to index the 4 neighbours of a given cell
    int[] rowNum = {-1, 0, 0, 1};
    int[] colNum = {0, -1, 1, 0};

    // Check if node is within the bounds of the matrix
    boolean isValid(int row, int col) {
        return (row >= 0) && (row < ROW) &&
                (col >= 0) && (col < COL);
    }

    // Backtrack parent pointers to get path
    ArrayList<Node> backtrackPath(Node q) {
        ArrayList<Node> nodePath = new ArrayList<>();
        Node tempNode = q;

        while(tempNode.parent != null){
            nodePath.add(tempNode);
            tempNode = tempNode.parent;
        }

        return nodePath;
    }

    // Find and return the shortest path
    ArrayList<Node> findBFSPath(char[][] mat, Coordinate src, Coordinate dest) {

        // Holds visited nodes
        boolean [][]visited = new boolean[ROW][COL];

        // Make sure src and dest are not a shelf
        if ((mat[src.x][src.y] == 'X') || (mat[dest.x][dest.y] == 'X')) {
            System.out.println("Source or Dest X");
            return null;
        }

        // Set root to visited
        visited[src.x][src.y] = true;

        // Queue for BFS
        Queue<Node> q = new LinkedList<>();

        // Enqueue source Node
        Node s = new Node(src, null);
        q.add(s);

        // Perform BFS from source
        while (!q.isEmpty())
        {
            // End condition id we arrive at dest cell
            Node curr = q.peek();
            Coordinate pt = curr.coordinate;
            if (pt.x == dest.x && pt.y == dest.y) {
                ArrayList<Node> returnPath = backtrackPath(curr);
                returnPath.add(new Node(src, null));
                Collections.reverse(returnPath);
                return returnPath;
            }

            // Move to next layer: dequeue parent and enqueue all adjacent cells
            Node p = q.remove();

            for (int i = 0; i < 4; i++)
            {
                int row = pt.x + rowNum[i];
                int col = pt.y + colNum[i];

                if (isValid(row, col) && (mat[row][col] != 'X') &&
                        !visited[row][col])
                {
                    // Enqueue now visited cells, set Nodes parent for path backtraacking
                    visited[row][col] = true;
                    Node adjacentNode = new Node(new Coordinate(row, col), p);
                    q.add(adjacentNode);

                }
            }
        }

        System.out.println("Dest cant be reached");
        return null;
    }

    // Test
    public static void main(String[] args)
    {
        char testMatrix[][] = {
                { '-', '-', '-', '-', '-', '-', 'X', '-', '-', 'X' },
                { '-', 'X', '-', 'X', '-', '-', '-', 'X', '-', '-' },
                { '-', '-', '-', '-', '-', '-', 'X', '-', 'X', '-' },
                { 'X', 'X', 'X', '-', '-', '-', '-', 'X', 'X', '-' },
                { '-', 'X', '-', 'X', '-', '-', '-', 'X', '-', 'X' },
                { '-', '-', '-', '-', '-', '-', 'X', '-', 'X', 'X' },
                { '-', 'X', 'X', 'X', 'X', 'X', '-', 'X', 'X', '-' },
                { 'X', 'X', '-', '-', '-', '-', 'X', '-', '-', '-' },
                { '-', '-', 'X', 'X', '-', 'X', '-', 'X', 'X', '-' }};

        Coordinate source = new Coordinate(0, 0);
        Coordinate dest = new Coordinate(1, 6);

        BFSShortestPath bfs = new BFSShortestPath();
        ArrayList<Node> path = bfs.findBFSPath(testMatrix, source, dest);

        for (Node node: path) {
            System.out.println(String.valueOf(node.coordinate.x) + " " + node.coordinate.y);
        }
    }
}