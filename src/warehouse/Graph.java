package warehouse;

/* For future implementation
 */
public class Graph {
    int[][] matrix = null;

    Graph(int numVertices) {
        this.matrix = new int[numVertices][numVertices];

        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    void addEdge(int v1, int v2, int weight) {
        this.matrix[v1][v2] = weight;
        this.matrix[v2][v1] = weight;
    }

    void printGraph(){
        int size = this.matrix.length;

        for (int i = 0; i < size ; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(" " + String.valueOf(this.matrix[i][j]) + " ");
            }
            System.out.println();
        }
    }
}
