package warehouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/* Driver class of application
    - In this version, controls text-base UI
    - Will be responsible for launching GUI in future versions
 */
public class Main {

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
//        try {
//            primaryController.readAllItems("src/warehouse/qvBox-warehouse-data-f21-v01.txt");
//        }
            catch (IOException e){
                System.out.println("Invalid file path!");
            }
        }
        /**
         * store the path of exported txt file
         */
        boolean exportFlag = false;
        while(!exportFlag) {
            System.out.println("Please enter the filename to export the instructions to:");
            filename = scanner.nextLine();
            if(filename.endsWith(".txt"))
                exportFlag = true;
            else {
                filename = null;
                System.out.println("Invalid file path!");
            }
        }

        System.out.println("All items from data file have been stored and set in the warehouse map");

        /* Initiate user interaction
            - also show all warehouse data is reflected on map
         */
//        Scanner scanner = new Scanner(System.in);
        primaryController.setStartAndEndPoint(start, end);
        primaryController.setWarehouseMatrix();
        System.out.println("Here is a layout of the warehouse with the loaded data");
        System.out.println("'S' = start point | 'E' = end point | 'X' = shelves/items | '.' = open space");
        primaryController.printWarehouseMatrix();
        System.out.println();

        /* Primary program flow -loop-
            - either search for item or exit
            - if search for item
                - prompt for then input item id
                - backend path algorithm called
                - display location and path to get to item on map
            - otherwise exit program
         */
        int choice;
        boolean loopFlag = true;

        while (loopFlag) {
            System.out.println("What would you like to do?");
            System.out.println("1: find a specific product");
            System.out.println("2: find an order of products");
            System.out.println("3: load and find orders from file");
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
                        System.out.println("The path from your location 'U' to the item '$' is marked with 'P' on the map.");
                        String shortestPathOutput = primaryController.findItemAndCallPath(start,itemID);
                        System.out.println(shortestPathOutput);
                        primaryController.markI2IPathOnWarehouseMatrix();
                        primaryController.printWarehouseMatrix();
                        primaryController.unmarkI2IPathOnWarehouseMatrix();
                        primaryController.unmarkItemInWarehouseMatrix(itemID);
                        System.out.println();
                    } else {
                        String str = scanner.next();
                        System.out.println("Invalid input! Please input a number.");
                    }
                    primaryController.resetWareHouse();
                    break;

                case 2:
                    boolean startflag = true;
                    while(startflag) {
                        boolean isNumber = true;
                        System.out.println("Please enter the START point location seperated by a blank.");
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
                            System.out.println("please input the correct START location(only Positive Integer Number)");
                        }
                        else if(start[0]>=40 || start[0]< 0 || start[1]>=25 || start[1] < 0)
                            System.out.println("please input the START location within the COL[0,40) ROW[0,25)");
                        else if (primaryController.warehouseMatrix[start[0]][start[1]] == 'X') {
                            System.out.println("You can't start in one of item shelves");
                        } else
                            startflag = false;
                    }

                    boolean endflag1 = true;
                    while(endflag1) {
                        boolean isNumber = true;
                        System.out.println("Please enter the END point location seperated by a blank.");
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
                            System.out.println("please input the correct END location(only Positive Integer Number)");
                        }
                        else if(end[0]>=40 || end[0]< 0 || end[1]>=25 || end[1] < 0)
                            System.out.println("please input the END location within the COL[0,40) ROW[0,25)");
                        else if (primaryController.warehouseMatrix[end[0]][end[1]] == 'X') {
                            System.out.println("You can't end in one of item shelves");
                        } else
                            endflag1 = false;
                    }

                    System.out.println("Your start and end points are (" + start[0] + "," + start[1] + ") and (" + end[0] + "," + end[1] + ")\n");

                    boolean timeFlag = true;
                    while(timeFlag) {
                        System.out.println("Please enter the time limit to find the path in seconds");
                        String j = scanner.next();

                        if(checkNumber2(j) && Double.parseDouble(j) > 0){
                            primaryController.timeOutMax = Double.parseDouble(j)* 1000;
                            timeFlag = false;
                        }
                        else{
                            System.out.println("please input the correct time limit (only positive number)");
                        }
                    }
                    System.out.println();

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
                            System.out.println("please input the correct size (only positive integer number)");
                        }
                    }

                    boolean productFlag = true;
                    while(productFlag) {
                        System.out.println("please type id of products separated by blanks: \n" +
                                "(we will only accept the first "+size+" products)");
                        for (int i = 0; i < size; i++) {
                            String j = scanner.next();
                            if (checkNumber(j)) {
                                int productid = Integer.parseInt(j);
                                if(primaryController.getItemByID(productid) == null)
                                {
                                    System.out.println("please input the items that are placed in the warehouse");
                                    break;
                                }
                                primaryController.currentOrderItems.add(primaryController.getItemByID(productid));
                            } else {
                                System.out.println("please input " +size + " corresponding products");
                                break;
                            }
                        }
                        if(primaryController.currentOrderItems.size() == size)
                        productFlag = false;
                    }

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
                        System.out.println("Orders Have been successfully loaded! \n");
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
                                    System.out.println("Invalid input! please input '1' or '2' or '3' ");
                            } else {
                                String str = scanner.next();
                                System.out.println("Invalid input! please input '1' or '2' or '3' ");
                            }
                        }

                        if ((fileOrderActionChoice == 1) || (fileOrderActionChoice == 2)) {
                            if (fileOrderActionChoice == 1) {
//                                primaryController.currentOrderItems = primaryController.fileOrders.get(unfullfilledOrderIndex);
                                primaryController.currentOrderItems = new ArrayList<>(primaryController.fileOrders.get(unfullfilledOrderIndex));
                                unfullfilledOrderIndex++;
                            } else {
                                System.out.println("Please input the order number from the file");
                                unfullfilledOrderIndex = scanner.nextInt();
//                                primaryController.currentOrderItems = primaryController.fileOrders.get(unfullfilledOrderIndex - 1);
                                primaryController.currentOrderItems = new ArrayList<>(primaryController.fileOrders.get(unfullfilledOrderIndex - 1));
                            }

                            boolean startflag2 = true;
                            while(startflag2) {
                                boolean isNumber = true;
                                System.out.println("Please enter the START point location seperated by a blank.");
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
                                    System.out.println("please input the correct START location(only Positive Integer Number)");
                                }
                                else if(start[0]>=40 || start[0]< 0 || start[1]>=25 || start[1] < 0)
                                    System.out.println("please input the START location within the COL[0,40) ROW[0,25)");
                                else if (primaryController.warehouseMatrix[start[0]][start[1]] == 'X') {
                                    System.out.println("You can't start in one of item shelves");
                                } else
                                    startflag2 = false;
                            }

                            boolean endflag2 = true;
                            while(endflag2) {
                                boolean isNumber = true;
                                System.out.println("Please enter the END point location seperated by a blank.");
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
                                    System.out.println("please input the correct END location(only Positive Integer Number)");
                                }
                                else if(end[0]>=40 || end[0]< 0 || end[1]>=25 || end[1] < 0)
                                    System.out.println("please input the END location within the COL[0,40) ROW[0,25)");
                                else if (primaryController.warehouseMatrix[end[0]][end[1]] == 'X') {
                                    System.out.println("You can't end in one of item shelves");
                                } else
                                    endflag2 = false;
                            }

                            primaryController.setStartAndEndPoint(start, end);
                            System.out.println("Your start and end points are (" + start[0] + "," + start[1] + ") and (" + end[0] + "," + end[1] + ")\n");

                            boolean timeFlag2 = true;
                            while(timeFlag2) {
                                System.out.println("Please enter the time limit to find the path in seconds");
                                String j = scanner.next();
                                if(checkNumber2(j) && Double.parseDouble(j) > 0){
                                    primaryController.timeOutMax = Double.parseDouble(j)* 1000;
                                    timeFlag2 = false;
                                }
                                else{
                                    System.out.println("please input the correct time limit (only positive number)");
                                }
                            }
                            System.out.println();

//                            System.out.println("Please enter the time limit to find the path in seconds");
//                            primaryController.timeOutMax = scanner.nextDouble()*1000;
//                            System.out.println();

                            if(primaryController.currentOrderItems.size() <= 8){
                                primaryController.findPathsBruteForce(filename);
                            }
                            else {
                                primaryController.findPathGeneticAlgorithm(filename);
                            }
                            primaryController.resetWareHouse();
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

    static boolean checkNumber2(String j) {
        if(j.matches("[0-9]*\\.?[0-9]+") || j.matches("[0-9]+"))
            return true;
        else
            return false;
    }

    static boolean checkNumber(String j) {
        if(j.matches("[0-9]+"))
            return true;
        else
            return false;
    }

}