package warehouse;

import java.util.ArrayList;
import java.io.*;
import java.util.List;
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
    static ArrayList<Item> allItemsList = new ArrayList<>();

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
    void setWarehouseMatrix(){
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
    void printWarehouseMatrix(){
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
    boolean markItemInWarehouseMatrix(int id) {
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
    void unmarkItemInWarehouseMatrix(int id) {
        Item item = getItemByID(id);
        warehouseMatrix[item.row][item.col] = 'X';
    }

    /* Item lookup for ID
     */
    static Item getItemByID(int id) {
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
    void markI2IPathOnWarehouseMatrix(){
        for (int i = 1; i < currentItem2ItemPath.size(); i++) {
            int x = currentItem2ItemPath.get(i).coordinate.x;
            int y = currentItem2ItemPath.get(i).coordinate.y;
            if (warehouseMatrix[x][y] != 'U')
                warehouseMatrix[x][y] = 'P';
        }
    }

    /* Unmark the path on graph
        - path coordinates from currentShortestPath
     */
    void unmarkI2IPathOnWarehouseMatrix(){
        for (int i = 1; i < currentItem2ItemPath.size(); i++) {
            int x = currentItem2ItemPath.get(i).coordinate.x;
            int y = currentItem2ItemPath.get(i).coordinate.y;
            warehouseMatrix[x][y] = '.';
        }
    }

    /* Call BFSShortestPath function
        - returns the path as a list of vertices
     */
    ArrayList<Vertex> findPathToItem(Item start, Item finish) {
        Item2ItemPath item2ItemPath = new Item2ItemPath();
        Coordinate source = new Coordinate(start.row, start.col);
        Coordinate dest = new Coordinate(finish.row, finish.col);


        return item2ItemPath.findBFSPath(warehouseMatrix, source, dest);
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Beta Release Brute Force     ///////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    Graph currentOrderGraph = null;
    ArrayList<Item> currentOrderItems = new ArrayList<>();
    ArrayList<Coordinate> currentOrderCoordinates4N = null;
    ArrayList<ArrayList<Integer>> currentLookupTable = null;
    ArrayList<Integer> shortestPathCoordIndices = null;
    int shortestPathCost = 0;

    void setLookUpTable () {
        int orderSize = currentOrderItems.size();
        ArrayList<ArrayList<Integer>> groupLookupTable = new ArrayList<ArrayList<Integer>>();;
        int lookUpTableSize = 4*orderSize+1;

        ArrayList<Integer> firstRow = new ArrayList<>();
        firstRow.add(0);
        groupLookupTable.add(firstRow);

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

        currentLookupTable = groupLookupTable;
    }

    void setCurrentOrderGraph4N(){
        int orderSize = currentOrderItems.size();
        int numNodes = 4 * orderSize + 1;
        currentOrderGraph = new Graph(numNodes);

        int[] rowNum = {-1, 0, 1, 0};
        int[] colNum = {0, 1, 0, -1};
        currentOrderCoordinates4N = new ArrayList<>();

        for (Item item: currentOrderItems) {
            markItemInWarehouseMatrix(item.id);
        }

        currentOrderCoordinates4N.add(new Coordinate(0,0));
        for (Item item: currentOrderItems) {

            for (int i = 0; i < 4; i++) {
                int row = item.row + rowNum[i];
                int col = item.col + colNum[i];
                if(col == -1)
                    col = 0;
                if(row == -1)
                    row = 0;
                Coordinate tempCoordinate = new Coordinate(row, col);
                currentOrderCoordinates4N.add(tempCoordinate);
            }
        }

        for (Coordinate c: currentOrderCoordinates4N) {
            System.out.println(c.x + " " + c.y);
        }
        Item2ItemPath itemPath = new Item2ItemPath();
        for (int i = 0; i < numNodes - 1; i++) {
            for (int j = i + 1; j < numNodes; j++) {
                Coordinate start = currentOrderCoordinates4N.get(i);
                Coordinate finish = currentOrderCoordinates4N.get(j);
                if(((warehouseMatrix[start.x][start.y] == '.') || (warehouseMatrix[start.x][start.y] == 'U'))
                        && (warehouseMatrix[finish.x][finish.y] == '.')) {
                    ArrayList<Vertex> item2ItemPath = itemPath.findBFSPath(warehouseMatrix,
                            start, finish);
                    int weight = item2ItemPath.size() - 1;
                    currentOrderGraph.addEdge(i, j, weight);
                }
                else
                    currentOrderGraph.addEdge(i, j, 0);
            }
        }
    }

    void findPathsBruteForce() {
        setLookUpTable();
        BruteForcePath bruteForcePath = new BruteForcePath(currentLookupTable);
        bruteForcePath.findShortestPath(currentOrderGraph.matrix);
        shortestPathCoordIndices = bruteForcePath.minPath;
        shortestPathCost = bruteForcePath.minPathCost;
    }

    void markFullPath() {
        Item2ItemPath itemPath = new Item2ItemPath();
        for (int i = 0; i < shortestPathCoordIndices.size() - 1; i++) {
            Coordinate source = currentOrderCoordinates4N.get(shortestPathCoordIndices.get(i));
            Coordinate dest = currentOrderCoordinates4N.get(shortestPathCoordIndices.get(i + 1));
            currentItem2ItemPath = itemPath.findBFSPath(warehouseMatrix, source, dest);
            markI2IPathOnWarehouseMatrix();
        }
    }

    void printCurrentOrderGraph(){
        currentOrderGraph.printGraph();
    }

    public static void main(String[] args) {
        String filePath = "C:\\Users\\10720\\Desktop\\v01.txt";
        PrimaryController primaryController = new PrimaryController();
        TSP_GA tsp_ga;
        try {
            primaryController.readAllItems(filePath);
        }
        catch (Exception e) {
            System.out.println("file error");
        }
        primaryController.setWarehouseMatrix();

//        Integer[] items = {
//                108335
//        };

        Integer[] items = {
                108335, 391825, 340367, 286457, 661741
        };
//        Integer[] items = {
//                281610, 342706, 111873, 198029, 366109, 287261, 76283, 254489, 258540, 286457
//        };
//
//        Integer[] items = {
//                427230, 372539, 396879, 391680, 208660, 105912, 332555, 227534, 68048, 188856, 736830, 736831, 479020, 103313, 1
//        };

//        Integer[] items = {
//                633, 1321, 3401, 5329, 10438
//                , 372539, 396879, 16880, 208660, 105912,
//                332555, 227534, 68048, 188856, 736830,
//                736831, 479020, 103313, 1, 20373
//        };

        for (Integer i : items) {
            primaryController.currentOrderItems.add(primaryController.getItemByID(i));
        }

//        primaryController.currentOrderItems.add(primaryController.getItemByID(108335));
//        primaryController.currentOrderItems.add(primaryController.getItemByID(391825));
//        primaryController.currentOrderItems.add(primaryController.getItemByID(340367));
//        primaryController.currentOrderItems.add(primaryController.getItemByID(286457));
//        primaryController.currentOrderItems.add(primaryController.getItemByID(661741));

        System.out.println();
        tsp_ga = new TSP_GA(30, primaryController.currentOrderItems.size(), 1000, 0.8f, 0.9f);
        tsp_ga.init(primaryController.currentOrderItems, primaryController.warehouseMatrix);

        int timeOut = 60000;
        List<Integer> route = tsp_ga.solve(timeOut);
        System.out.println(route);

        for (Item item:
                primaryController.currentOrderItems) {
            System.out.println(item.id + " " + item.row + " " + item.col);
        };

        primaryController.setCurrentOrderGraph4N();
        System.out.println();

        primaryController.printWarehouseMatrix();
        System.out.println();

        primaryController.printCurrentOrderGraph();
        System.out.println();

        primaryController.findPathsBruteForce();
        primaryController.markFullPath();
        primaryController.printWarehouseMatrix();
        System.out.println();

        System.out.println(primaryController.shortestPathCoordIndices);
        System.out.println(primaryController.shortestPathCost);

    }
}