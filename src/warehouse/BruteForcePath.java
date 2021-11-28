package warehouse;

import java.util.ArrayList;

/* Brute Force Solution
    - Recursively find all hamiltonian cycles
        - calculated cost of each cycle
        - track, compare and save the shortest cycle
     */
class BruteForcePath {

    int[][] currentLookupTable = null;
    int minPathCost = Integer.MAX_VALUE;
    ArrayList<Integer> minPath= null;
    long startTime;
    long endTime;
    int TIMEOUTFLAG = 0;
    int TIMEOUT = 60000;

    BruteForcePath(ArrayList<ArrayList<Integer>> currentLookupTable) {
        this.currentLookupTable = new int[currentLookupTable.size()][3];
        for (int i = 0; i < this.currentLookupTable.length; i++) {
            for (int j = 0; j < 3; j++) {
                this.currentLookupTable[i][j] = currentLookupTable.get(i).get(j);
            }
        }
        this.minPath = new ArrayList<>();
    }

    boolean isSafe(int v, int graph[][], ArrayList<Integer> path, int pos) {
        if (graph[path.get(pos - 1)][v] == -1)
            return false;

        for (int i = 0; i < pos; i++)
            if (path.get(i) == v)
                return false;

        return true;
    }

    void findShortestPath(int graph[][]) {
        startTime = System.currentTimeMillis();
        ArrayList<Integer> path = new ArrayList<>();
        path.add(0);

        boolean[] visited = new boolean[graph.length];

        for (int i = 0; i < visited.length; i++) {
            for (int k = 0; k < 3; k++) {
                visited[currentLookupTable[i][k]] = false;
            }
            visited[i] = false;
        }

        for (int k = 0; k < 1; k++) {
            visited[currentLookupTable[0][k]] = true;
        }
        visited[0] = true;
        visited[graph.length - 1] = true;

        findPaths(graph, 1, path, visited);

        if(TIMEOUTFLAG == 1)
            System.out.println("Time Out! Current vest past will be used!");
        TIMEOUTFLAG = 0;
    }

    void findPaths(int graph[][], int pos, ArrayList<Integer> path, boolean[] visited) {
        endTime = System.currentTimeMillis();
        if ((endTime-startTime)>TIMEOUT){
            TIMEOUTFLAG = 1;
            return;
        }

        if (pos == (graph.length - 1) - (3* (pos -1))) {

            if (graph[path.get(path.size() - 1)][path.get(0)] != 0) {

                path.add(graph.length - 1);
                int pathSize = path.size();
                int pathCost = 0;

                for (int i = 0; i < pathSize; i++) {
                    if (i < pathSize -1) {
                        pathCost += graph[path.get(i)][path.get(i + 1)];
                    }
                }
                if (pathCost < minPathCost) {
                    minPathCost = pathCost;
                    minPath = new ArrayList<>(path);
                }

                path.remove(path.size() - 1);
            }
            return;
        }

        for (int v = 0; v < graph.length; v++) {
            if (isSafe(v, graph, path, pos) && !visited[v]) {

                path.add(v);
                for (int k = 0; k < 3; k++) {
                    visited[currentLookupTable[v][k]] = true;
                }
                visited[v] = true;

                findPaths(graph, pos + 1, path, visited);

                for (int k = 0; k < 3; k++) {
                    visited[currentLookupTable[v][k]] = false;
                }
                visited[v] = false;
                path.remove(path.size() - 1);
            }
        }
    }

}
