package warehouse;

import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* Primary Control of program
    - Contains all the functionality need for Driver class to implement the applications flow
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
            if (warehouseMatrix[x][y] != 'S' && warehouseMatrix[x][y] != 'E')
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
        Coordinate source = new Coordinate(start.row, start.col);
        Coordinate dest = new Coordinate(finish.row, finish.col);


        return Item2ItemPath.findBFSPath(warehouseMatrix, source, dest);
    }

    /* Wrapper for findPathToItem
        - checks if items exist
        - calls shortest path algorithm
        - saves path into currentShortestPath
        - calls makeUserInstructions
     */
    String findItemAndCallPath(int[] start, int id) {
        if (itemExist(id)) {
            Item neededItem = getItemByID(id);
            currentItem2ItemPath = findPathToItem(new Item(0,start[0],start[1]), neededItem);

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
        for (int i = 1; i < currentItem2ItemPath.size() ; i++) {
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
        String currDirection;
        currDirection = directionList.get(0);

        int currentDirCount = 1;

        if (directionList.size() == 1) {
            instructions.append("Move " + currDirection + " 1 unit \n");
        }
        else {
            for (int i = 1; i < directionList.size(); i++) {
                if (directionList.get(i).equals(currDirection)) {
                    currentDirCount = currentDirCount + 1;
                    if (i == directionList.size() - 1 && currentDirCount > 1) {
                        instructions.append("Move " + currDirection + " " + currentDirCount + " units \n");
                    } else if (i == directionList.size() - 1 && currentDirCount == 1) {
                        instructions.append("Move " + currDirection + " " + currentDirCount + " units \n");
                    }
                } else {
                    if (currentDirCount > 1) {
                        instructions.append("Move " + currDirection + " " + currentDirCount + " units \n");
                    } else if (currentDirCount == 1) {
                        instructions.append("Move " + currDirection + " " + currentDirCount + " unit \n");
                    }
                    currDirection = directionList.get(i);
                    currentDirCount = 1;
                    if (i == directionList.size() - 1 && currentDirCount > 1) {
                        instructions.append("Move " + currDirection + " " + currentDirCount + " units \n");
                    } else if (i == directionList.size() - 1 && currentDirCount == 1) {
                        instructions.append("Move " + currDirection + " " + currentDirCount + " unit \n");
                    }
                }
            }
        }

        return instructions.toString().trim();
    }

    /**
     *  store start and end location
     */
    int[] start = {0,0};
    int[] end = {0,0};
    static boolean append;
    TSP_GA tsp_ga = new TSP_GA();
    /* Graph that holds the 4 nodes per item for all items in an order
     */
    Graph currentOrderGraph = null;

    /* list of items in an order
     */
    ArrayList<Item> currentOrderItems = new ArrayList<>();

    /* list of item orders
     */
    ArrayList<ArrayList<Item>> fileOrders = new ArrayList<>();

    /* list of all coordinates for the 4 nodes of each item
     */
    ArrayList<Coordinate> currentOrderCoordinates4N = null;

    /* Table to retrieve what nodes belong to the same item
     */
    ArrayList<ArrayList<Integer>> currentLookupTable = null;

    /* Map that contains data for items on the same shelf
        - key is an items index in currentOrderItems
        - value is a list of indices in currentOrderItems that are on the same shelf as key
     */
    HashMap<Integer, ArrayList<Integer>> itemsOnSameShelfMap = null;

    /* List of items for an order but only taking into consideration unique shelves
        - only one arbitrary item per shelf -if multiple items are needed from same shelf-
            is used in shortest path calculations
     */
    ArrayList<Item> currentOrderItemsByShelf = null;

    /* The indices of a shortest path that correspond to coordinates in currentOrderCoordinates4N
     */
    ArrayList<Integer> shortestPathCoordIndices = null;

    /* The ids of the items in the shortest path in order
     */
    ArrayList<Integer> shortestPathByID = new ArrayList<>();

    /* Shortest path cost
     */
    int shortestPathCost = 0;

    /* Max algorithm timout time
     */
    double timeOutMax = 60000;

    /* Helper to check if an item is sharing a shelf with other items in an order
     */
    int isSharingShelf(Item item){
        for (int i = 0; i < currentOrderItemsByShelf.size(); i++) {
            if ((item.row == currentOrderItemsByShelf.get(i).row) && (item.col == currentOrderItemsByShelf.get(i).col)) {
                return i;
            }
        }
        return -1;
    }

    /* Set the list of items by unique shelves and the map that
        contains which items share shelves
        - called by setCurrentOrderGraph4N()
     */
    void setOrderItemsByShelves() {
        itemsOnSameShelfMap = new HashMap<>();
        currentOrderItemsByShelf = new ArrayList<>();

        for (int i = 0; i < currentOrderItems.size(); i++) {
            Item item = currentOrderItems.get(i);
            int currentSharedShelfIndex = isSharingShelf(item);
            if (currentSharedShelfIndex == -1) {
                currentOrderItemsByShelf.add(item);
            }
            else{
                if (itemsOnSameShelfMap.get(currentSharedShelfIndex) == null) {
                    itemsOnSameShelfMap.put(currentSharedShelfIndex, new ArrayList<>());
                }
                itemsOnSameShelfMap.get(currentSharedShelfIndex).add(i);
            }
        }

    }

    /* Set 4 adjacent nodes lookup table
        - called by findPathsBruteForce()
     */
    void setLookUpTable () {
        int orderSize = currentOrderItemsByShelf.size();
        ArrayList<ArrayList<Integer>> groupLookupTable = new ArrayList<ArrayList<Integer>>();;
        int lookUpTableSize = (4 * orderSize) + 1;

        ArrayList<Integer> firstRow = new ArrayList<>();
        firstRow.add(0);
        firstRow.add(0);
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

        ArrayList<Integer> lastRow = new ArrayList<>();
        lastRow.add(0);
        lastRow.add(0);
        lastRow.add(0);
        groupLookupTable.add(lastRow);

        currentLookupTable = groupLookupTable;
    }

    /* Construct a complete graph with the items by unique shelves and
        4 adjacent nodes around the shelves.
        - called by findPathsBruteForce()
     */
    void setCurrentOrderGraph4N(){
        setOrderItemsByShelves();

        int orderSize = currentOrderItemsByShelf.size();
        int numNodes = (4 * orderSize) + 2;
        currentOrderGraph = new Graph(numNodes);

        int[] rowNum = {-1, 0, 1, 0};
        int[] colNum = {0, 1, 0, -1};
        currentOrderCoordinates4N = new ArrayList<>();

        for (Item item: currentOrderItemsByShelf) {
            markItemInWarehouseMatrix(item.id);
        }

        currentOrderCoordinates4N.add(new Coordinate(start[0],start[1]));
        for (Item item: currentOrderItemsByShelf) {

            for (int i = 0; i < 4; i++) {
                int row = item.row + rowNum[i];
                int col = item.col + colNum[i];
                if(col == -1)
                    col = 0;
                Coordinate tempCoordinate = new Coordinate(row, col);
                currentOrderCoordinates4N.add(tempCoordinate);
            }
        }
        currentOrderCoordinates4N.add(new Coordinate(end[0],end[1]));

        for (int i = 0; i < numNodes - 1; i++) {
            for (int j = i + 1; j < numNodes; j++) {
                Coordinate start = currentOrderCoordinates4N.get(i);
                Coordinate finish = currentOrderCoordinates4N.get(j);
                if(((warehouseMatrix[start.x][start.y] == '.') || (warehouseMatrix[start.x][start.y] == 'S')
                        || (warehouseMatrix[start.x][start.y] == 'E'))
                        && ((warehouseMatrix[finish.x][finish.y] == '.')
                        || (warehouseMatrix[finish.x][finish.y] == 'E'))) {
                    ArrayList<Vertex> item2ItemPath = Item2ItemPath.findBFSPath(warehouseMatrix,
                            start, finish);
                    int weight = item2ItemPath.size() - 1;
                    currentOrderGraph.addEdge(i, j, weight);
                }
                else
                    currentOrderGraph.addEdge(i, j, -1);
            }
        }
    }


    /* Mark the full shortest path on the warehouse matrix
     */
    void markFullPath() {
        for (int i = 0; i < shortestPathCoordIndices.size() - 1; i++) {
            Coordinate source = currentOrderCoordinates4N.get(shortestPathCoordIndices.get(i));
            Coordinate dest = currentOrderCoordinates4N.get(shortestPathCoordIndices.get(i + 1));
            currentItem2ItemPath = Item2ItemPath.findBFSPath(warehouseMatrix, source, dest);
            markI2IPathOnWarehouseMatrix();
        }
        warehouseMatrix[start[0]][start[1]] = 'S';
        warehouseMatrix[end[0]][end[1]] = 'E';
    }

    /* Helper to get relative direction of a shelf to the users location
     */
    String getShelfDirection(int pathX, int pathY, int itemX, int itemY) {
        String direction = null;
        if (pathX - itemX == 1)
            direction = "west";
        else if (pathX - itemX == -1)
            direction = "east";
        else if (pathY - itemY == 1)
            direction = "south";
        else if (pathY - itemY == -1)
            direction = "north";
        return direction;
    }

    /* Print the full path user instructions to console
     */
    void printFullPathInstructions (String file) {
        StringBuilder inst = new StringBuilder();
        ArrayList<Integer> orderListID = new ArrayList<>();

        inst.append("\n Path Instructions:");
        System.out.println("Path Instructions:");
        for (int i = 0; i < shortestPathCoordIndices.size() -1; i++) {
            int itemIndex = (int) (Math.ceil(shortestPathCoordIndices.get(i + 1) / 4.0) - 1);

            Coordinate source = currentOrderCoordinates4N.get(shortestPathCoordIndices.get(i));
            Coordinate dest = currentOrderCoordinates4N.get(shortestPathCoordIndices.get(i + 1));
            currentItem2ItemPath = Item2ItemPath.findBFSPath(warehouseMatrix, source, dest);

            if (currentItem2ItemPath.size() - 1 != 0) {
                String item2itemPathInstructions = makeUserInstruction();
                System.out.println(item2itemPathInstructions);
                inst.append("\n" + item2itemPathInstructions);
            }

            if ((itemIndex >= 0) && (itemIndex < currentOrderItemsByShelf.size())) {
                Item item = currentOrderItemsByShelf.get(itemIndex);
                int id = item.id;
                orderListID.add(id);
                System.out.print("Pickup item(s) (" + item.id + ")");
                inst.append("\nPickup item(s) (" + item.id + ") ");
                if (itemsOnSameShelfMap.get(itemIndex) != null) {
                    for (int itemOnSameShelfIndex : itemsOnSameShelfMap.get(itemIndex)) {
                        Item itemOnSameShelf = currentOrderItems.get(itemOnSameShelfIndex);
                        int idSameShelf = itemOnSameShelf.id;
                        orderListID.add(idSameShelf);
                        System.out.print(" (" + itemOnSameShelf.id + ")");
                        inst.append(" (" + itemOnSameShelf.id + ") ");
                    }
                }
                System.out.print(" from the shelf directly " + getShelfDirection(dest.x, dest.y, item.row, item.col)
                        + " to you" );
                inst.append(" from the shelf directly " + getShelfDirection(dest.x, dest.y, item.row, item.col)
                        + " to you" );
                System.out.println();
            }
        }
        System.out.println("Path complete");
        inst.append("\nPath complete");
        exportTxt(file, inst.toString().trim(), orderListID);
    }

    /**
     * Dereference and save the shortest path in terms of ids
     */
    void setShortestPathByID() {
        for (int i = 0; i < shortestPathCoordIndices.size() -1; i++) {
            int itemIndex = (int) (Math.ceil(shortestPathCoordIndices.get(i + 1) / 4.0) - 1);
            if ((itemIndex >= 0) && (itemIndex < currentOrderItemsByShelf.size())) {
                Item item = currentOrderItemsByShelf.get(itemIndex);
                int id = item.id;
                shortestPathByID.add(id);

                if (itemsOnSameShelfMap.get(itemIndex) != null) {
                    for (int itemOnSameShelfIndex : itemsOnSameShelfMap.get(itemIndex)) {
                        Item itemOnSameShelf = currentOrderItems.get(itemOnSameShelfIndex);
                        int idSameShelf = itemOnSameShelf.id;
                        shortestPathByID.add(idSameShelf);
                    }
                }
            }
        }
    }

    /* Print the adjacency matrix for the order graph
        - FOR DEVELOPER DEBUGING
     */
    void printCurrentOrderGraph(){
        System.out.println("Item graph -4 adjacent nodes per item; no duplicate shelves-");
        currentOrderGraph.printGraph();
        System.out.println();
    }

    /* Primary function call to solve the shortest path using brute force
        - sets graph
        - sets lookup table
        - calls brute force algorithm
        - saves path indices and cost
        - prints information to console
     */
    void findPathsBruteForce(String filename) {
        setCurrentOrderGraph4N();
        setLookUpTable();
        BruteForcePath bruteForcePath = new BruteForcePath(currentLookupTable);
        bruteForcePath.TIMEOUT = timeOutMax;
        bruteForcePath.findShortestPath(currentOrderGraph.matrix);
        shortestPathCoordIndices = bruteForcePath.minPath;
        shortestPathCost = bruteForcePath.minPathCost;
        setShortestPathByID();

        System.out.println();
        System.out.println("Path Distance: " + shortestPathCost + " units");
        System.out.println("Items by ID Pickup Order: " + shortestPathByID);
        markFullPath();
        printWarehouseMatrix();
        System.out.println();
        System.out.println("'S' = start point | 'E' = end point -or start and end if the same point- | 'X' = shelves/items | '.' = open space");
        System.out.println("The path from your location 'S' to the item '$' is marked with 'P' on the map.");
        System.out.println();

        printFullPathInstructions(filename);
        System.out.println();
    }

    /**
     * Primary function call to solve the shortest path using Genetic Algorithm
     *  - initiate the basic parameters of GA
     *      - chromosomes' length and size
     *      - generation's size
     *      - possibility of crossover and mutation
     *  - sets graph and pickup items
     *  - calls genetic algorithm
     *  - prints information to console
     *  - save the instructions and route to the export file
     * @param file the path of export file
     */
    void findPathGeneticAlgorithm(String file){
        tsp_ga = new TSP_GA(30, currentOrderItems.size(), 1000, 0.8f, 0.9f);
        tsp_ga.init(start, end, currentOrderItems, warehouseMatrix);

        double timeOut = timeOutMax;
        System.out.println();
        ArrayList<Integer> route = tsp_ga.solve(timeOut);
        route.removeAll(Collections.singleton(0));
        System.out.println("Items by ID Pickup Order: " + route);
        warehouseMatrix = tsp_ga.getMatrix();
        printWarehouseMatrix();
        System.out.println();
        System.out.println("'S' = start point | 'E' = end point -or start and end if the same point- | 'X' = shelves/items | '.' = open space");
        System.out.println("The path from your location 'S' to the item '$' is marked with 'P' on the map.");
        System.out.println();
        String inst = tsp_ga.getInstructions();
        System.out.println(inst);
        exportTxt(file, "" + inst, route);
        System.out.println();
    }

    /**
     * Clear the changes made to the warehouse matrix from an item order operation
     */
    void resetWareHouse() {
        setWarehouseMatrix();
        currentOrderItems.clear();
        shortestPathByID.clear();
        currentOrderItemsByShelf = null;
        currentOrderGraph = null;
        currentOrderCoordinates4N = null;
        currentLookupTable = null;
        shortestPathCoordIndices = null;
        shortestPathCost = 0;
        timeOutMax = 60000;
    }

    /* Read file of orders and store in fileOrders
     */
    void readOrderFile(String filePath) throws IOException {
        fileOrders.clear();

        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String itemData = null;

        while ((itemData = br.readLine()) != null){
            ArrayList<Item> tempOrder = new ArrayList<>();

            ArrayList<String> orderIDs;
            orderIDs = getFloatsFromString(itemData);

            for(String id: orderIDs) {
                tempOrder.add(getItemByID((int) Float.parseFloat(id)));
            }

            fileOrders.add(tempOrder);
        }
    }

    /**
     * export a txt with direction
     * @param direction: string of route instruction
     */
    static void exportTxt(String filename, String direction, ArrayList<Integer> orderIds) {
        if (filename.isEmpty())
            return;
        try {
            creatfile(filename);
            FileWriter myWriter = new FileWriter(filename, append);
            myWriter.write("Path Traversal for Order including Items:" + orderIds + "\n\n");
            myWriter.write(direction);
            myWriter.close();
            System.out.printf("Successfully wrote to %s.\n", filename);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Create file
     */
    static void creatfile(String pathname) {
        try {
            File file = new File(pathname);

            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
                append=false;
            } else {
                System.out.println("File already exists. Content will be overwritten.");
                FileWriter fileWriter =new FileWriter(file);
                fileWriter.write("");
                fileWriter.flush();
                fileWriter.close();
                append=false;
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Set start and end points of an order traversal
     * @param s
     * @param e
     */
    public void setStartAndEndPoint(int[] s, int[] e) {
        start = s;
        end = e;
    }

//    public static void main(String[] args) {
//        String filePath = "src/warehouse/qvBox-warehouse-data-f21-v01.txt";
//        PrimaryController primaryController = new PrimaryController();
//
//        try {
//            primaryController.readAllItems(filePath);
//            primaryController.readOrderFile("/Users/eric/Downloads/qvBox-warehouse-orders-list-part01.txt");
//            for (ArrayList<Item> order: primaryController.fileOrders) {
//                for (Item i : order) {
//                    System.out.print(i.id + " ");
//                }
//                System.out.println();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            primaryController.readAllItems(filePath);
//        }
//        catch (Exception e) {
//            System.out.println("file error");
//        }
//
//        primaryController.setStartAndEndPoint(new int[]{7, 20}, new int[]{32, 10});
//        primaryController.setWarehouseMatrix();
//
//        Integer[] items = {633, 1321, 3401, 5329, 10438, 372539, 396879, 16880, 208660, 105912, 332555, 227534, 68048, 188856, 736830, 736831, 479020, 103313, 1, 20373};
//
//
//        for (Integer i : items) {
//            primaryController.currentOrderItems.add(primaryController.getItemByID(i));
//        }
//
//        System.out.println("Items in current order");
//        for (Item item:
//                primaryController.currentOrderItems) {
//            System.out.println(item.id + " " + item.row + " " + item.col);
//        };
//        System.out.println();
//    }
}