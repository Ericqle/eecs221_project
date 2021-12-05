package warehouse;

import java.util.ArrayList;

/* Brute Force Solution
    - Recursively begin to find all paths depth first with backtracking
        - each node is abstracted as 4 nodes, which are associated with each other
            through a lookup table
        - prunes dfs tree and stops calculating/considering subtrees (paths) if the distance
            exceeds the current shortest complete path which prevents us from
            having to go through every possible path when we already know it wont
            be optimal
        - dynamic start and end locations supported
            - we just abstract the end location as a vertex and add it as the last item
             to the path
        - dynamic timeout supported; returns current best route if timeout occurs
     */
class BruteForcePath {

    int[][] currentLookupTable = null;
    int minPathCost = Integer.MAX_VALUE;
    ArrayList<Integer> minPath= null;
    long startTime;
    long endTime;
    int TIMEOUTFLAG = 0;
    double TIMEOUT = 60000;

    /**
     * Constructor for the Brute Force class
     * - initializes min path and lookup table for 4 nodes consideration
     */
    BruteForcePath(ArrayList<ArrayList<Integer>> currentLookupTable) {
        this.currentLookupTable = new int[currentLookupTable.size()][3];
        for (int i = 0; i < this.currentLookupTable.length; i++) {
            for (int j = 0; j < 3; j++) {
                this.currentLookupTable[i][j] = currentLookupTable.get(i).get(j);
            }
        }
        this.minPath = new ArrayList<>();
    }

    /**
     * Check if next node is safe to be added to the current path
     */
    boolean isSafe(int v, int graph[][], ArrayList<Integer> path, int pos) {
        // not adj to prev added vertex
        if (graph[path.get(pos - 1)][v] == -1)
            return false;

        // not already in path
        for (int i = 0; i < pos; i++)
            if (path.get(i) == v)
                return false;

        return true;
    }

    /**
     * Wrapper to find all possible paths
     */
    void findShortestPath(int graph[][]) {
        startTime = System.currentTimeMillis();
        ArrayList<Integer> path = new ArrayList<>();
        path.add(0);

        boolean[] visited = new boolean[graph.length];

        // initialize visited table to false
        for (int i = 0; i < visited.length; i++) {
            for (int k = 0; k < 3; k++) {
                visited[currentLookupTable[i][k]] = false;
            }
            visited[i] = false;
        }

        // Set start vertex to true
        for (int k = 0; k < 1; k++) {
            visited[currentLookupTable[0][k]] = true;
        }
        visited[0] = true;
        visited[graph.length - 1] = true;

        // primary function call to find all paths
        findPaths(graph, 1, path, visited, 0);

        if(TIMEOUTFLAG == 1)
            System.out.println("Time Out! Current best past will be used!");
        TIMEOUTFLAG = 0;
    }

    /**
     *  Recursive function to find all paths and keep track of the shortest
     */
    void findPaths(int graph[][], int pos, ArrayList<Integer> path, boolean[] visited, int runningDist) {
        endTime = System.currentTimeMillis();
        if ((endTime-startTime)>TIMEOUT){
            TIMEOUTFLAG = 1;
            return;
        }

        // Prune distance and stop considering paths starting from this subpath
        if (runningDist + graph[path.get(path.size()-1)][graph.length-1] > minPathCost) {
            return;
        }

        // Exit condition: find a path that is of length equal to all the vertices -all vertices are unique-
        if (pos == (graph.length - 1) - (3* (pos -1))) {

            if (graph[path.get(path.size() - 1)][path.get(0)] != 0) {

                path.add(graph.length - 1);

                int pathCost = runningDist + graph[path.get(path.size()-1)][path.get(path.size()-2)];

                // Keep track of shortest full length path
                if (pathCost < minPathCost) {
                    minPathCost = pathCost;
                    minPath = new ArrayList<>(path);
                }

                path.remove(path.size() - 1);
            }
            return;
        }

        // Try other vertices as next vertex in path
        for (int v = 0; v < graph.length; v++) {
            if (isSafe(v, graph, path, pos) && !visited[v]) {

                path.add(v);
                for (int k = 0; k < 3; k++) {
                    visited[currentLookupTable[v][k]] = true;
                }
                visited[v] = true;
                runningDist += graph[path.get(path.size()-1)][path.get(path.size()-2)];

                // Recur to construct the rest of the path if valid
                findPaths(graph, pos + 1, path, visited, runningDist);

                // Remove current vertext from path and process all other vertices
                for (int k = 0; k < 3; k++) {
                    visited[currentLookupTable[v][k]] = false;
                }
                visited[v] = false;

                runningDist -= graph[path.get(path.size()-1)][path.get(path.size()-2)];
                path.remove(path.size() - 1);
            }
        }
    }

}
