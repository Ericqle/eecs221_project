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
    ArrayList<Vertex> currentItem2ItemPath = new ArrayList<>();

    /* Abstraction of graph for the warehouse
        - all index-able spaces are considered vertices
     */
    char[][] warehouseMatrix = new char[ROW][COL];

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
        for (int i = 0; i < warehouseMatrix.length; i++) {
            for (int j = 0; j < warehouseMatrix[0].length; j++) {
                warehouseMatrix[i][j] = '.';
            }
        }

        for (Item item: allItemsList) {
            warehouseMatrix[item.row][item.col] = 'X';
        }

        warehouseMatrix[0][0] = 'U';
    }

    /* Print ascii representation of graph
        - prints the transpose and horizontally flibbed grraph matrix
            to get the more familiar x-y coordinate orientation
     */
    void printGraph(){
        for (int i = COL - 1; i >= 0 ; i--) {
            System.out.print( i < 10 ? i + "  " : i + " ");
            for (int j = 0; j < ROW; j++) {
                System.out.print(String.valueOf(warehouseMatrix[j][i]) + "  ");
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
            warehouseMatrix[item.row][item.col] = '$';
            return true;
        }else {
            return false;
        }
    }

    /* Reset found item to a shelf 'X' on graph
     */
    void unmarkItemInGraph(int id) {
        Item item = getItemByID(id);
        warehouseMatrix[item.row][item.col] = 'X';
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
        for (int i = 1; i < currentItem2ItemPath.size() - 1; i++) {
            int x = currentItem2ItemPath.get(i).coordinate.x;
            int y = currentItem2ItemPath.get(i).coordinate.y;
            warehouseMatrix[x][y] = 'P';
        }
    }

    /* Unmark the path on graph
        - path coordinates from currentShortestPath
     */
    void unmarkPathOnGraph(){
        for (int i = 1; i < currentItem2ItemPath.size() - 1; i++) {
            int x = currentItem2ItemPath.get(i).coordinate.x;
            int y = currentItem2ItemPath.get(i).coordinate.y;
            warehouseMatrix[x][y] = '.';
        }
    }

    /* Call BFSShortestPath function
        - returns the path as a list of vertices
     */
    ArrayList<Vertex> findPathToItem(Item start, Item finish) {
        Coordinate source = new Coordinate(start.row, start.col);
        Coordinate dest = new Coordinate(finish.row, finish.col);


        return BFSShortestPath.findBFSPath(warehouseMatrix, source, dest);
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
            currentItem2ItemPath = findPathToItem(new Item(0,0,0), neededItem);

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
        for (int i = 1; i < currentItem2ItemPath.size()-1; i++) {
            String xDirection = "East";
            String yDirection = "North";
            int x0 = currentItem2ItemPath.get(i - 1).coordinate.x;
            int x1 = currentItem2ItemPath.get(i).coordinate.x;
            int y0 = currentItem2ItemPath.get(i - 1).coordinate.y;
            int y1 = currentItem2ItemPath.get(i).coordinate.y;
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
        System.out.println(directionList);
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

    //  New data structures     ////////////////////////////////////////////////////////
    Graph currentOrderGraph = null;
    ArrayList<Item> currentOrderItems = new ArrayList<>();

    //  Make look up table     ////////////////////////////////////////////////////////
    ArrayList<ArrayList<Integer>> makeLookUpTable (int orderSize) {
        ArrayList<ArrayList<Integer>> groupLookupTable = new ArrayList<ArrayList<Integer>>();;
        int lookUpTableSize = 4*orderSize+1;

        int powerOf4 = 0;
        for (int i = 1; i < lookUpTableSize; i++) {
            ArrayList<Integer> tempRow = new ArrayList<>();
            for (int j = 0; j < 4 ; j++) {
                if (i != j + powerOf4 + 1)
                    tempRow.add(j + powerOf4 + 1);
            }
            if (i % 4 ==0 )
                powerOf4 += 4;
            groupLookupTable.add(tempRow);
        }

        return groupLookupTable;
    }

    //  Make Graph that includes 4 nodes   /////////////////////////////////////////////
    void setCurrentOrderGraph4Nodes(){
        int orderSize = currentOrderItems.size();
        int numNodes = 4 * orderSize + 1;
        currentOrderGraph = new Graph(numNodes);

        int[] rowNum = {-1, 0, 1, 0};
        int[] colNum = {0, 1, 0, -1};
        ArrayList<Coordinate> orderCoordinates4Nodes = new ArrayList<>();

        ArrayList<ArrayList<Integer>> lookUpTable = makeLookUpTable(orderSize);

        for (Item item: currentOrderItems) {
            markItemInGraph(item.id);
        }

        orderCoordinates4Nodes.add(new Coordinate(0,0));
        for (Item item: currentOrderItems) {

            for (int i = 0; i < 4; i++) {
                int row = item.row + rowNum[i];
                int col = item.col + colNum[i];
                Coordinate tempCoordinate = new Coordinate(row, col);
                orderCoordinates4Nodes.add(tempCoordinate);
            }
        }

        for (Coordinate c: orderCoordinates4Nodes) {
            System.out.println(c.x + " " + c.y);
        }

        for (int i = 0; i < numNodes - 1; i++) {
            for (int j = i + 1; j < numNodes; j++) {
                Coordinate start = orderCoordinates4Nodes.get(i);
                Coordinate finish = orderCoordinates4Nodes.get(j);
                if(((warehouseMatrix[start.x][start.y] == '.') || (warehouseMatrix[start.x][start.y] == 'U'))
                        && (warehouseMatrix[finish.x][finish.y] == '.')) {
                    ArrayList<Vertex> item2ItemPath = BFSShortestPath.findBFSPath(warehouseMatrix,
                            start, finish);
                    int weight = item2ItemPath.size() - 1;
                    currentOrderGraph.addEdge(i, j, weight);
                }
                else
                    currentOrderGraph.addEdge(i, j, -1);
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

        primaryController.currentOrderItems.add(primaryController.getItemByID(633));
        primaryController.currentOrderItems.add(primaryController.getItemByID(1321));

        for (Item item:
                primaryController.currentOrderItems) {
            System.out.println(item.id + " " + item.row + " " + item.col);
        };

        primaryController.setCurrentOrderGraph4Nodes();
        System.out.println();
        primaryController.printGraph();
        System.out.println();
        primaryController.printCurrentOrderGraph();

//        ShortestPath path = BruteForce.travllingSalesmanProblem(primaryController.currentOrderGraph.matrix, 0);
//
//        for (int i:
//             path.shortestPathVertices) {
//            System.out.print(i + " ");
//        }
//        System.out.println();
//        System.out.println(path.shortestPathDistance);
//
//        for (int i = 0; i < path.shortestPathVertices.length - 1; i++) {
//            Item start = primaryController.currentOrderItems.get(path.shortestPathVertices[i]);
//            Item finish = primaryController.currentOrderItems.get(path.shortestPathVertices[i+1]);
//            Coordinate source = new Coordinate(start.row, start.col);
//            Coordinate dest = new Coordinate(finish.row, finish.col);
//            primaryController.currentItem2ItemPath = BFSShortestPath.findBFSPath(primaryController.warehouseMatrix, source, dest);
//            primaryController.markPathOnGraph();
//        }
//        Item start = primaryController.currentOrderItems.get(path.shortestPathVertices[
//                path.shortestPathVertices[path.shortestPathVertices.length-1]]);
//        Item finish = primaryController.currentOrderItems.get(path.shortestPathVertices[0]);
//        Coordinate source = new Coordinate(start.row, start.col);
//        Coordinate dest = new Coordinate(finish.row, finish.col);
//        primaryController.currentItem2ItemPath = BFSShortestPath.findBFSPath(primaryController.warehouseMatrix, source, dest);
//        primaryController.markPathOnGraph();
//
//
//        System.out.println();
//        primaryController.printGraph();
    }
}