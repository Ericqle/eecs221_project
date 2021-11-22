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
        String filename;

        /* Reading data from data file and setting graph
         */

        /*
        Loop until successfully read the txt file
         */
        boolean readSuccessful = false;
        while (!readSuccessful){
            try {
                System.out.println("Please input the path of the txt file:");
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
        Scanner console = new Scanner(System.in);
        System.out.println("Please enter the filename to export.");
        filename = console.nextLine();
        filename += ".txt";

        boolean startflag = true;
        while(startflag) {
            System.out.println("Please enter the START point location seperated by a blank.");
            for (int i = 0; i < 2; i++) {
                start[i] = scanner.nextInt();
            }
            if(primaryController.warehouseMatrix[start[0]][start[1]] != 'X') {
                startflag = false;
            }
            else
                System.out.println("You can't start in one of item shelves");
        }

        boolean endflag = true;
        while(endflag) {
            System.out.println("Please enter the END point location seperated by a blank.");
            for (int i = 0; i < 2; i++) {
                end[i] = scanner.nextInt();
            }
            if(primaryController.warehouseMatrix[end[0]][end[1]] != 'X') {
                endflag = false;
            }
            else
                System.out.println("You can't end in one of item shelves");
        }
        System.out.println("Your start and end points are (" + start[0] + "," + start[1] + ") and (" + end[0] + "," + end[1] + ")\n");

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
            System.out.println("3: exit");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            } else {
                String str = scanner.next();
                System.out.println("Invalid input! Please input '1' or '2' or '3'")   ;
                continue;
            }
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
                    break;
                case 2:
                    System.out.println("Type the size of the order: ");
                    int size = scanner.nextInt();
                    System.out.println("please type id of products separated by blanks: ");
                    for(int i = 0; i< size; i++){
                        int productid = scanner.nextInt();
                        primaryController.currentOrderItems.add(primaryController.getItemByID(productid));
                    }
//                    System.out.println("Please select the algorithm you want to use to get the route path-----> 1 for BF 2. for GA");
//                    int algorithm = scanner.nextInt();
//                    if(algorithm == 1){
                    primaryController.setStartAndEndPoint(start, end);
                    if(size <= 1){
                        primaryController.findPathsBruteForce();
                        primaryController.markFullPath();
                        primaryController.printWarehouseMatrix();
                        System.out.println();

                        System.out.println("Path Cost");
                        System.out.println(primaryController.shortestPathCost);
                        System.out.println();

                        primaryController.printFullPathInstructions();
                        System.out.println();
                    }
//                    else if(algorithm == 2){
                    else if(size > 1){
                        primaryController.findPathGeneticAlgorithm(filename);
                    }else {
                        System.out.println("Invalid input! Please input the correct number.");
                    }
                    break;
                case 3:
                    loopFlag = false;
                    System.out.println("Good Bye.");
                    break;
                default:
                    System.out.println("Invalid input! Please input '1' or '2' or '3'.");
            }
        }

//        System.out.println("What would you like to do?");
//        System.out.println("1: find object");
//        System.out.println("2: exit");
//
//        int choice = scanner.nextInt();
//
//        while (choice != 2) {
//            System.out.println("Enter item id: ");
//            int itemID = scanner.nextInt();
//            // primaryController.markItemInGraph(itemID);
//            if (!primaryController.markItemInGraph(itemID)) {
//                System.out.println("The item you are looking for does NOT exist!");
//                continue;
//            }
//            System.out.println();
//
//            System.out.println("The item for id: " + itemID + " is marked as '$' on the map.");
//            System.out.println("The path from your location 'U' to the item '$' is marked with 'P' on the map.");
//            String shortestPathOutput = primaryController.findItemAndCallPath(itemID);
//            System.out.println(shortestPathOutput);
//            primaryController.markPathOnGraph();
//            primaryController.printGraph();
//            primaryController.unmarkPathOnGraph();
//            primaryController.unmarkItemInGraph(itemID);
//            System.out.println();
//
//            System.out.println("What would you like to do?");
//            System.out.println("1: find object");
//            System.out.println("2: exit");
//            choice = scanner.nextInt();
//        }

    }

}