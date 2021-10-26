package project1_0;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

class BFSShortestPath {
    int ROW = 40;
    int COL = 25;

    /* Used to index the 4 neighbours of a given vertex
     */
    int[] rowNum = {-1, 0, 0, 1};
    int[] colNum = {0, -1, 1, 0};

    /* Check if vertex is within the bounds of the matrix
     */
    boolean isValid(int row, int col) {
        return (row >= 0) && (row < ROW) &&
                (col >= 0) && (col < COL);
    }

    /* Backtrack parent pointers to get path
     */
    ArrayList<Vertex> backtrackPath(Vertex q) {
        ArrayList<Vertex> vertexPath = new ArrayList<>();
        Vertex tempVertex = q;

        while(tempVertex.parent != null){
            vertexPath.add(tempVertex);
            tempVertex = tempVertex.parent;
        }

        return vertexPath;
    }

    /* Find and return the shortest path
        - Utilizes BFS
        - All open spaces on map are abstracted as vertices on the
        - Returns the path to the dest as list of vertices
     */
    ArrayList<Vertex> findBFSPath(char[][] graph, Coordinate src, Coordinate dest) {

        /* Holds visited vertices
         */
        boolean [][]visited = new boolean[ROW][COL];

        /* Make sure src and dest are not a shelf
         */
        if ((graph[src.x][src.y] == 'X') || (graph[dest.x][dest.y] == 'X')) {
            System.out.println("Source or Dest X");
            return null;
        }

        /* Set root to visited
         */
        visited[src.x][src.y] = true;

        /* Queue for BFS
         */
        Queue<Vertex> q = new LinkedList<>();

        /* Enqueue source vertices
         */
        Vertex s = new Vertex(src, null);
        q.add(s);

        /* Perform BFS from source
         */
        while (!q.isEmpty())
        {
            /* End condition if we arrive at dest cell
             */
            Vertex curr = q.peek();
            Coordinate pt = curr.coordinate;
            if (pt.x == dest.x && pt.y == dest.y) {
                ArrayList<Vertex> returnPath = backtrackPath(curr);
                returnPath.add(new Vertex(src, null));
                Collections.reverse(returnPath);
                return returnPath;
            }

            /* Move to next layer: dequeue parent and enqueue all adjacent cells
             */
            Vertex p = q.remove();

            /* Calculate adjacent vertices coordinates
             */
            for (int i = 0; i < 4; i++)
            {
                int row = pt.x + rowNum[i];
                int col = pt.y + colNum[i];

                if (isValid(row, col) && (graph[row][col] != 'X') &&
                        !visited[row][col])
                {
                    /* Enqueue now visited vertices, save vertices parent for path backtracking
                     */
                    visited[row][col] = true;
                    Vertex adjacentVertex = new Vertex(new Coordinate(row, col), p);
                    q.add(adjacentVertex);

                }
            }
        }

        System.out.println("Dest cant be reached");
        return null;
    }
}