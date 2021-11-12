package warehouse;

import java.util.ArrayList;
class BruteForcePath {

    ArrayList<ArrayList<Integer>> currentLookupTable = null;
    int minPathCost = Integer.MAX_VALUE;
    ArrayList<Integer> minPath= null;

    BruteForcePath(ArrayList<ArrayList<Integer>> currentLookupTable) {
        this.currentLookupTable= currentLookupTable;
        this.minPath = new ArrayList<>();
    }

    boolean isSafe(int v, int graph[][], ArrayList<Integer> path, int pos) {
        if (graph[path.get(pos - 1)][v] == 0)
            return false;

        for (int i = 0; i < pos; i++)
            if (path.get(i) == v)
                return false;

        return true;
    }

    boolean hasCycle;

    void findShortestPath(int graph[][]) {
        hasCycle = false;

        ArrayList<Integer> path = new ArrayList<>();
        path.add(0);

        boolean[] visited = new boolean[graph.length];

        for (int i = 0; i < visited.length; i++) {
            for (Integer k : currentLookupTable.get(i)) {
                visited[k] = false;
            }
            visited[i] = false;
        }

        for (Integer k : currentLookupTable.get(0)) {
            visited[k] = false;
        }
        visited[0] = true;

        findPaths(graph, 1, path, visited);

        if (!hasCycle) {
            System.out.println("No Hamiltonian Cycle possible ");
            return;
        }

    }

    void findPaths(int graph[][], int pos, ArrayList<Integer> path, boolean[] visited) {
        if (pos == graph.length - (3* (pos -1))) {

            if (graph[path.get(path.size() - 1)][path.get(0)] != 0) {

                path.add(0);
                int pathSize = path.size();
                int pathCost = 0;

                for (int i = 0; i < pathSize; i++) {
                    if (i < pathSize -1) {
                        pathCost += graph[path.get(i)][path.get(i + 1)];
                    }
//                    System.out.print(path.get(i) + " ");
                }
//                System.out.println(pathCost);
                if (pathCost < minPathCost) {
                    minPathCost = pathCost;
                    minPath = new ArrayList<>(path);
                }

                path.remove(path.size() - 1);

                hasCycle = true;
            }
            return;
        }

        for (int v = 0; v < graph.length; v++) {
            if (isSafe(v, graph, path, pos) && !visited[v]) {

                path.add(v);
                for (Integer k : currentLookupTable.get(v)) {
                    visited[k] = true;
                }
                visited[v] = true;

                findPaths(graph, pos + 1, path, visited);

                for (Integer k : currentLookupTable.get(v)) {
                    visited[k] = false;
                }
                visited[v] = false;
                path.remove(path.size() - 1);
            }
        }
    }

}
