package warehouse;

import java.util.ArrayList;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* Primary Control of program
    - Is used with GUI when implemented, but for now just contains all the functions
        that the GUIS method calls will utilize -Main module is using these functions
        for a text UI instead of a GUI for this release-
 */
public class PrimaryController {
    int ROW = 40;
    int COL = 25;

    /* All items in warehouse list
     */
    ArrayList<Item> allItemsList = new ArrayList<>();

    /* Holds current path between only two vertice
        - takes place of ShortestPath module for future implementation
     */
    ArrayList<Vertex> currentShortestPath = new ArrayList<>();

    /* Abstraction of graph for the warehouse
        - all index-able spaces are considered vertices
     */
    char[][] graph = new char[ROW][COL];

    /* Read warehouse data and save it in allItemsList
     */
    void readAllItems(String filePath) throws IOException {
        ArrayList<String> tempItemData;

        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));

        br.readLine();
        String itemData = null;

        while ((itemData = br.readLine()) != null){
            tempItemData = getFloatsFromString(itemData);
            int id = (int) Float.parseFloat(tempItemData.get(0));
            int x = (int) Float.parseFloat(tempItemData.get(1));
            int y = (int) Float.parseFloat(tempItemData.get(2));
            Item tempItem = new Item(id, x, y);
            allItemsList.add(tempItem);

        }
    }

    /* Helper for readAllItems to extract numbers from a line of data
     */
    ArrayList<String> getFloatsFromString(String raw) {
        ArrayList<String> listBuffer = new ArrayList<String>();

        Pattern p = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher m = p.matcher(raw);

        while (m.find()) {
            listBuffer.add(m.group());
        }

        return listBuffer;
    }

    /* Initialize the graph based off allAddedItems
        - marks items/shelves as 'X'
        - marks user as 'U'
     */
    void setGraph (){
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[0].length; j++) {
                graph[i][j] = '.';
            }
        }

        for (Item item: allItemsList) {
            graph[item.row][item.col] = 'X';
        }

        graph[0][0] = 'U';
    }

    /* Print ascii representation of graph
        - prints the transpose and horizontally flibbed grraph matrix
            to get the more familiar x-y coordinate orientation
     */
    void printGraph(){
        for (int i = COL - 1; i >= 0 ; i--) {
            System.out.print( i < 10 ? i + "  " : i + " ");
            for (int j = 0; j < ROW; j++) {
                System.out.print(String.valueOf(graph[j][i]) + "  ");
            }
            System.out.println();
        }
        System.out.print("   ");
        for(int k = 0; k <= ROW - 1; k++)
            System.out.print(k < 10 ? k + "  " : k + " ");
        System.out.println();
    }

    /* Set needed item to '$' on graph
        - return true if exist
     */
    boolean markItemInGraph(int id) {
        if (itemExist(id)) {
            Item item = getItemByID(id);
            graph[item.row][item.col] = '$';
            return true;
        }else {
            return false;
        }
    }

    /* Reset found item to a shelf 'X' on graph
     */
    void unmarkItemInGraph(int id) {
        Item item = getItemByID(id);
        graph[item.row][item.col] = 'X';
    }

    /* Item lookup for ID
     */
    Item getItemByID(int id) {
        for (Item item: allItemsList) {
            if (id == item.id)
                return item;
        }
        return null;
    }

    /* Check if item exists in allItemsList
     */
    boolean itemExist(int id) {
        for (Item item: allItemsList) {
            if (id == item.id)
                return true;
        }
        return false;
    }

    /* Mark the path using 'P' on graph
        - path coordinates from currentShortestPath
     */
    void markPathOnGraph(){
        for (int i = 1; i < currentShortestPath.size() - 1; i++) {
            int x = currentShortestPath.get(i).coordinate.x;
            int y = currentShortestPath.get(i).coordinate.y;
            graph[x][y] = 'P';
        }
    }

    /* Unmark the path on graph
        - path coordinates from currentShortestPath
     */
    void unmarkPathOnGraph(){
        for (int i = 1; i < currentShortestPath.size() - 1; i++) {
            int x = currentShortestPath.get(i).coordinate.x;
            int y = currentShortestPath.get(i).coordinate.y;
            graph[x][y] = '.';
        }
    }

    /* Call BFSShortestPath function
        - returns the path as a list of vertices
     */
    ArrayList<Vertex> findPathToItem(Item start, Item finish) {
        Coordinate source = new Coordinate(start.row, start.col);
        Coordinate dest = new Coordinate(finish.row, finish.col);


        return BFSShortestPath.findBFSPath(graph, source, dest);
    }

    /* Wrapper for findPathToItem
        - checks if items exist
        - calls shortest path algorithm
        - saves path into currentShortestPath
        - calls makeUserInstructions
     */
    String findItemAndCallPath(int id) {
        if (itemExist(id)) {
            Item neededItem = getItemByID(id);
            currentShortestPath = findPathToItem(new Item(0,0,0), neededItem);

            return makeUserInstruction();
        }

        else {
            return "Item does not exist";
        }
    }

    /* Make user traversal insrtuctions from currentShortestPath
     */
    String makeUserInstruction() {
        StringBuilder instructions = new StringBuilder();

        ArrayList<String> directionList = new ArrayList<>();
        for (int i = 1; i < currentShortestPath.size()-1; i++) {
            String xDirection = "East";
            String yDirection = "North";
            int x0 = currentShortestPath.get(i - 1).coordinate.x;
            int x1 = currentShortestPath.get(i).coordinate.x;
            int y0 = currentShortestPath.get(i - 1).coordinate.y;
            int y1 = currentShortestPath.get(i).coordinate.y;
            if ((x1 - x0) == 0) {
                if ((y1 - y0) < 0)
                    yDirection = "South";
                directionList.add(yDirection);
            }
            else if ((y1 - y0) == 0){
                if ((x1 - x0) < 0)
                    xDirection = "West";
                directionList.add(xDirection);
            }
        }

        String currDirection = directionList.get(0);
        int currentDirCount = 1;

        for (int i = 1; i < directionList.size(); i++) {
            if (directionList.get(i).equals(currDirection)) {
                currentDirCount = currentDirCount + 1;
                if (i == directionList.size() - 1 && currentDirCount > 1) {
                    instructions.append("Move " + currDirection + " " + currentDirCount + " units \n");
                }
                else if(i == directionList.size() - 1 && currentDirCount == 1) {
                    instructions.append("Move " + currDirection + " " + currentDirCount + " units \n");
                }
            }
            else {
                if(currentDirCount > 1) {
                    instructions.append("Move " + currDirection + " " + currentDirCount + " units \n");
                }
                else if(currentDirCount == 1){
                    instructions.append("Move " + currDirection + " " + currentDirCount + " unit \n");
                }
                currDirection = directionList.get(i);
                currentDirCount = 1;
                if (i == directionList.size() - 1 && currentDirCount > 1) {
                    instructions.append("Move " + currDirection + " " + currentDirCount + " units \n");
                }
                else if(i == directionList.size() - 1 && currentDirCount == 1){
                    instructions.append("Move " + currDirection + " " + currentDirCount + " unit \n");
                }
            }

        }

        return "Path to Item: \n" + instructions.toString();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    Graph currentOrderGraph = null;
    ArrayList<Item> currentOrderItems = new ArrayList<>();

    void setCurrentOrderGraph(){
        int orderSize = currentOrderItems.size();
        currentOrderGraph = new Graph(orderSize);

        for (Item item: currentOrderItems) {
            markItemInGraph(item.id);
        }

        for (int i = 0; i < orderSize - 1; i++) {

            for (int j = i + 1; j < orderSize; j++) {
                Item start = currentOrderItems.get(i);
                Item finish = currentOrderItems.get(j);
                Coordinate source = new Coordinate(start.row, start.col);
                Coordinate dest = new Coordinate(finish.row, finish.col);

                ArrayList<Vertex> item2ItemPath = BFSShortestPath.findBFSPath(graph, source, dest);

                int weight = item2ItemPath.size() - 1;
                currentOrderGraph.addEdge(i, j, weight);
            }
        }
    }

    void printCurrentOrderGraph(){
        currentOrderGraph.printGraph();
    }

    public static void main(String[] args) {
        String filePath = "/Users/eric/Desktop/eecs221_project/src/warehouse/qvBox-warehouse-data-f21-v01.txt";
        PrimaryController primaryController = new PrimaryController();
        try {
            primaryController.readAllItems(filePath);
        }
        catch (Exception e) {
            System.out.println("file error");
        }
        primaryController.setGraph();

        primaryController.currentOrderItems.add(new Item(0,0,0));
        primaryController.currentOrderItems.add(primaryController.allItemsList.get(0));
        primaryController.currentOrderItems.add(primaryController.allItemsList.get(1));
        primaryController.currentOrderItems.add(primaryController.allItemsList.get(5));
        primaryController.currentOrderItems.add(primaryController.allItemsList.get(36));

        for (Item item:
                primaryController.currentOrderItems) {
            System.out.println(item.id + " " + item.row + " " + item.col);
        };

        primaryController.setCurrentOrderGraph();
        System.out.println();
        primaryController.printGraph();
        System.out.println();
        primaryController.printCurrentOrderGraph();

        ShortestPath path = BruteForce.travllingSalesmanProblem(primaryController.currentOrderGraph.matrix, 0);
        System.out.println(path.shortestPathVertices);
        System.out.println(path.shortestPathDistance);

        for (int i = 0; i < path.shortestPathVertices.size() - 1; i++) {
            Item start = primaryController.currentOrderItems.get(path.shortestPathVertices.get(i));
            Item finish = primaryController.currentOrderItems.get(path.shortestPathVertices.get(i + 1));
            Coordinate source = new Coordinate(start.row, start.col);
            Coordinate dest = new Coordinate(finish.row, finish.col);
            primaryController.currentShortestPath = BFSShortestPath.findBFSPath(primaryController.graph, source, dest);
            primaryController.markPathOnGraph();
        }
        Item start = primaryController.currentOrderItems.get(path.shortestPathVertices.get
                (path.shortestPathVertices.get(path.shortestPathVertices.size()-1)));
        Item finish = primaryController.currentOrderItems.get(path.shortestPathVertices.get(0));
        Coordinate source = new Coordinate(start.row, start.col);
        Coordinate dest = new Coordinate(finish.row, finish.col);
        primaryController.currentShortestPath = BFSShortestPath.findBFSPath(primaryController.graph, source, dest);
        primaryController.markPathOnGraph();


        System.out.println();
        primaryController.printGraph();

    }
}