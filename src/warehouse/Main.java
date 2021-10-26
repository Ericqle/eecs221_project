package warehouse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/* Driver class of application
    - In this version, controls text-base UI
    - Will be responsible for launching GUI in future versions
 */
public class Main {

    public static void main(String[] args) {
        /* Member to make backed function calls
         */
        PrimaryController primaryController = new PrimaryController();
        Scanner scanner = new Scanner(System.in);

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


        primaryController.setGraph();
        System.out.println("All items from data file have been stored and set in the warehouse map");

        /* Initiate user interaction
            - also show all warehouse data is reflected on map
         */
//        Scanner scanner = new Scanner(System.in);
        System.out.println("Here is a layout of the warehouse with the loaded data");
        System.out.println("'U' = you | 'X' = shelves/items | '.' = open space");
        primaryController.printGraph();
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
            System.out.println("1: find object");
            System.out.println("2: exit");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            } else {
                String str = scanner.next();
                System.out.println("Invalid input! Please input '1' or '2'.");
                continue;
            }
            switch (choice) {
                case 1:
                    System.out.println("Enter item id: ");
                    if (scanner.hasNextInt()) {
                        int itemID = scanner.nextInt();
                        // primaryController.markItemInGraph(itemID);
                        if (!primaryController.markItemInGraph(itemID)) {
                            System.out.println("The item you are looking for does NOT exist!");
                            continue;
                        }
                        System.out.println();

                        System.out.println("The item for id: " + itemID + " is marked as '$' on the map.");
                        System.out.println("The path from your location 'U' to the item '$' is marked with 'P' on the map.");
                        String shortestPathOutput = primaryController.findItemAndCallPath(itemID);
                        System.out.println(shortestPathOutput);
                        primaryController.markPathOnGraph();
                        primaryController.printGraph();
                        primaryController.unmarkPathOnGraph();
                        primaryController.unmarkItemInGraph(itemID);
                        System.out.println();
                    } else {
                        String str = scanner.next();
                        System.out.println("Invalid input! Please input a number.");
                    }
                    break;
                case 2:
                    loopFlag = false;
                    System.out.println("Good Bye.");
                    break;
                default:
                    System.out.println("Invalid input! Please input '1' or '2'.");
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