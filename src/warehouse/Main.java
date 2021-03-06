package warehouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/* Driver class of application
    - In this version, controls text-base UI
 */
public class Main {

    /* Driver function for application
         */
    public static void main(String[] args) {
        /* Member to make backed function calls
         */
        int[] start ={0,0};
        int[] end = {0,0};
        PrimaryController primaryController = new PrimaryController();
        TSP_GA tsp_ga = new TSP_GA();
        Scanner scanner = new Scanner(System.in);
        String filename = null;

        /* Reading data from data file and setting graph
         */

        /*
        Loop until successfully read the txt file
         */
        boolean readSuccessful = false;
        while (!readSuccessful){
            try {
                System.out.println("Please input the path of the warehouse stock txt file:");
                String filePath = scanner.nextLine();

                primaryController.readAllItems(filePath);
                readSuccessful = true;
            }

            catch (IOException e){
                System.out.println("Invalid file path!");
            }
        }
        /**
         * store the path of exported txt file
         */
        boolean exportFlag = false;
        while(!exportFlag) {
            System.out.println("If you wish to export order path instructions, enter the file name here: " +
                    "\nOtherwise, leave it blank and hit 'enter'");
            filename = scanner.nextLine();
            if (filename.isEmpty()) {
                break;
            }
            if(filename.endsWith(".txt"))
                exportFlag = true;
            else {
                filename = null;
                System.out.println("Invalid file path!");
            }
        }

        System.out.println();
        System.out.println("All items from data file have been stored and set in the warehouse map");

        /* Initiate user interaction
            - also show all warehouse data is reflected on map
         */
        primaryController.setStartAndEndPoint(start, end);
        primaryController.setWarehouseMatrix();
        System.out.println("Here is a layout of the warehouse with the loaded data");
        System.out.println("'S' = start point | 'E' = end point -or start and end if the same point- | 'X' = shelves/items | '.' = open space");
        primaryController.printWarehouseMatrix();
        System.out.println();

        int choice;
        boolean loopFlag = true;

        while (loopFlag) {
            System.out.println("What would you like to do?");
            System.out.println("1: navigate to a specific product from warehouse origin (coordinate 0 0)");
            System.out.println("2: find an order of products");
            System.out.println("3: load and find multiple orders from file");
            System.out.println("4: exit");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            } else {
                String str = scanner.next();
                System.out.println("Invalid input! Please input '1' or '2' or '3' or '4'")  ;
                continue;
            }
            System.out.println();

            switch (choice) {
                case 1:
                    System.out.println("Enter item id: ");
                    if (scanner.hasNextInt()) {
                        int itemID = scanner.nextInt();
                        // primaryController.markItemInGraph(itemID);
                        if (!primaryController.markItemInWarehouseMatrix(itemID)) {
                            System.out.println("The item you are looking for does NOT exist!");
                            continue;
                        }
                        System.out.println();

                        System.out.println("The item for id: " + itemID + " is marked as '$' on the map.");
                        System.out.println("The path from your location 'S' to the item '$' is marked with 'P' on the map.");
                        String shortestPathOutput = primaryController.findItemAndCallPath(start,itemID);
                        System.out.println(shortestPathOutput);
                        primaryController.markI2IPathOnWarehouseMatrix();
                        primaryController.markItemInWarehouseMatrix(itemID);
                        primaryController.warehouseMatrix[0][0] = 'S';
                        primaryController.printWarehouseMatrix();
                        System.out.println();
                    } else {
                        String str = scanner.next();
                        System.out.println("Invalid input! Please input a number");
                    }
                    primaryController.resetWareHouse();
                    break;

                case 2:
                    setStartAndEndLocations(primaryController, scanner, start, end);

                    setTimeoutTime(primaryController, scanner);

                    boolean sizeFlag = true;
                    int size = 0;
                    while(sizeFlag) {
                        System.out.println("Type the size of the order: ");
                        String j = scanner.next();
                        if(checkNumber(j)) {
                            size = Integer.parseInt(j);
                            sizeFlag = false;
                        }
                        else{
                            System.out.println("Please input a valid size (only positive integer number)");
                        }
                    }

                    boolean productFlag = true;
                    System.out.println("Please type id of products separated by spaces: \n" +
                            "(we will only accept the first "+size+" products entered)");
                    while(productFlag) {
                        int i = 0;
                        int a = 0, b = 0;
                        primaryController.currentOrderItems.clear();
                        for (i = 0; i < size; i++) {
                            String j = scanner.next();
                            if (checkNumber(j)) {
                                int productid = Integer.parseInt(j);
                                if(primaryController.getItemByID(productid) == null)
                                {
                                    a = 1;
                                }
                                else
                                primaryController.currentOrderItems.add(primaryController.getItemByID(productid));

                            } else {
                                    b = 1;
                            }
                        }
                        if(a==1 ){
                            System.out.println("Please input the items that are placed in the warehouse");
                            System.out.println("Please type id of products separated by spaces: \n" +
                                    "(we will only accept the first "+size+" products entered)");
                        }
                        else if(b == 1){
                            System.out.println("Please input " +size + " corresponding products");
                            System.out.println("Please type id of products separated by spaces: \n" +
                                    "(we will only accept the first "+size+" products entered)");
                        }
                        else{
                            productFlag = false;
                        }
                    }

                    ArrayList<Integer> id = new ArrayList<>();
                    for(Item item: primaryController.currentOrderItems)
                        id.add(item.id);

                    System.out.println("This order includes items as follows:"+ id);

                    primaryController.setStartAndEndPoint(start, end);
                    if(size <= 8){
                        primaryController.findPathsBruteForce(filename);
                    }
                    else {
                        primaryController.findPathGeneticAlgorithm(filename);
                    }
                    primaryController.resetWareHouse();
                    break;

                case 3:
                    String absordScannerNewline = scanner.nextLine();

                    boolean orderFileFlag = false;
                    String orderListFilePath = null;
                    while(!orderFileFlag) {
                        System.out.println("Please input the path of the order file:");
                        orderListFilePath = scanner.next();
                        if(orderListFilePath.endsWith(".txt"))
                            orderFileFlag = true;
                        else {
                            orderListFilePath = null;
                            System.out.println("Invalid file path!");
                        }
                    }


                    try {
                        primaryController.readOrderFile(orderListFilePath);
                        System.out.println("Orders Have been successfully loaded!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println();

                    boolean fileOrdeLoop = true;
                    int unfullfilledOrderIndex = 0;

                    while (fileOrdeLoop) {
                        boolean fileOrderActionFlag = false;
                        int fileOrderActionChoice = 0;
                        while(!fileOrderActionFlag) {
                            System.out.println("Would you like to: \n(1) find next unfulfilled order \n" +
                                    "(2) choose order number \n(3) exit file orders");

                            if (scanner.hasNextInt()) {
                                fileOrderActionChoice = scanner.nextInt();
                                if(fileOrderActionChoice == 1 ||fileOrderActionChoice == 2 || fileOrderActionChoice == 3)
                                fileOrderActionFlag = true;
                                else
                                    System.out.println("Invalid input! Please input '1' or '2' or '3' ");
                            } else {
                                String str = scanner.next();
                                System.out.println("Invalid input! Please input '1' or '2' or '3' ");
                            }
                        }

                        if ((fileOrderActionChoice == 1) || (fileOrderActionChoice == 2)) {
                            boolean continueFlag = true;
                            if (fileOrderActionChoice == 1) {
                                if (unfullfilledOrderIndex == 100) {
                                    System.out.println();
                                    System.out.println("This is the last order, there is no order after this to collect!");
                                    System.out.println();
                                    continueFlag = false;
                                }
                                else {
                                    primaryController.currentOrderItems.clear();
                                    primaryController.currentOrderItems = new ArrayList<>(primaryController.fileOrders.get(unfullfilledOrderIndex));
                                    unfullfilledOrderIndex++;
                                }
                            }
                            else {
                                System.out.println();
                                boolean fileOrderIndexFlag = false;
                                while(!fileOrderIndexFlag) {
                                    System.out.println("Please input the order number from the file:");
                                    if (scanner.hasNextInt()) {
                                        unfullfilledOrderIndex = scanner.nextInt();
                                        if(unfullfilledOrderIndex <= primaryController.fileOrders.size() && unfullfilledOrderIndex > 0)
                                            fileOrderIndexFlag = true;
                                        else
                                            System.out.println("Invalid Input! The valid order numbers are from: 1 to "
                                                    + primaryController.fileOrders.size());
                                    } else {
                                        String str = scanner.next();
                                        System.out.println("Invalid Input! The valid order numbers are from: 1 to "
                                                + primaryController.fileOrders.size());
                                    }
                                }
                                primaryController.currentOrderItems.clear();
                                primaryController.currentOrderItems = new ArrayList<>(primaryController.fileOrders.get(unfullfilledOrderIndex - 1));
                            }

                            if (continueFlag) {
                                System.out.println();
                                setStartAndEndLocations(primaryController, scanner, start, end);

                                setTimeoutTime(primaryController, scanner);

                                System.out.println("Displaying path for order number: " + unfullfilledOrderIndex + " in the order file");
                                id = new ArrayList<>();
                                for(Item item: primaryController.currentOrderItems)
                                    id.add(item.id);

                                System.out.println("This order includes items as follows:"+ id);
                                if (primaryController.currentOrderItems.size() <= 8) {
                                    primaryController.findPathsBruteForce(filename);
                                } else {
                                    primaryController.findPathGeneticAlgorithm(filename);
                                }
                                primaryController.resetWareHouse();
                            }
                        }

                        else {
                            fileOrdeLoop = false;
                            System.out.println();
                        }

                    }
                    break;

                case 4:
                    loopFlag = false;
                    System.out.println("Good Bye.");
                    break;

                default:
                    System.out.println("Invalid input! Please input '1' or '2' or '3' or '4'.");
            }
        }
    }

    /**
     * Helper to set dynamic timeout
     * @param primaryController
     * @param scanner
     */
    static void setTimeoutTime(PrimaryController primaryController, Scanner scanner) {
        boolean timeFlag2 = true;
        while(timeFlag2) {
            System.out.println("Please enter the time limit to find the path in seconds:");
            String j = scanner.next();
            if(checkNumber2(j) && Double.parseDouble(j) > 0){
                primaryController.timeOutMax = Double.parseDouble(j)* 1000;
                timeFlag2 = false;
            }
            else{
                System.out.println("Please input a valid time limit (only positive numbers)");
            }
        }
        System.out.println();

    }

    /**
     * Helper to set dynamic start and end locations
     * @param primaryController
     * @param scanner
     * @param start
     * @param end
     */
    static void setStartAndEndLocations(PrimaryController primaryController, Scanner scanner, int[] start, int[] end) {
        boolean startflag = true;
        while(startflag) {
            boolean isNumber = true;
            System.out.println("Please enter the START point warehouse coordinates separated by a single space:");
            for (int i = 0; i < 2; i++) {
                String j = scanner.next();
                if (checkNumber(j)) {
                    int temp = Integer.parseInt(j);
                    start[i] = temp;
                } else {
                    isNumber = false;
                }
            }
            if(!isNumber){
                System.out.println("Please input a valid START location(only Positive Integer Numbers)");
            }
            else if(start[0]>=40 || start[0]< 0 || start[1]>=25 || start[1] < 0)
                System.out.println("Please input the START location within warehouse coordinates of X:0-39 Y:0-24");
            else if (primaryController.warehouseMatrix[start[0]][start[1]] == 'X') {
                System.out.println("You can't start in one of item shelves");
            } else
                startflag = false;
        }

        boolean endflag1 = true;
        while(endflag1) {
            boolean isNumber = true;
            System.out.println("Please enter the END point warehouse coordinates separated by a single space:");
            for (int i = 0; i < 2; i++) {
                String j = scanner.next();
                if (checkNumber(j)) {
                    int temp = Integer.parseInt(j);
                    end[i] = temp;
                } else {
                    isNumber = false;
                }
            }
            if(!isNumber){
                System.out.println("Please input a valid END location(only Positive Integer Numbers)");
            }
            else if(end[0]>=40 || end[0]< 0 || end[1]>=25 || end[1] < 0)
                System.out.println("Please input the END location within warehouse coordinates of X:0-40 Y:0-25");
            else if (primaryController.warehouseMatrix[end[0]][end[1]] == 'X') {
                System.out.println("You can't end in one of item shelves");
            } else
                endflag1 = false;
        }

        System.out.println("Your start and end points are (" + start[0] + "," + start[1] + ") and (" + end[0] + "," + end[1] + ")\n");
    }

    /**
     * Check valid input for timeout
     * @param j
     * @return
     */
    static boolean checkNumber2(String j) {
        if(j.matches("[0-9]*\\.?[0-9]+") || j.matches("[0-9]+"))
            return true;
        else
            return false;
    }

    /**
     * Check valid input for ints
     * @param j
     * @return
     */
    static boolean checkNumber(String j) {
        if(j.matches("[0-9]+"))
            return true;
        else
            return false;
    }

}