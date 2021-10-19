package warehouse;

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

        /* Reading data from data file and setting graph
         */
        try {
            primaryController.readAllItems("/Users/eric/Documents/EECS221/alphaTest/src/qvBox-warehouse-data-f21-v01.txt");
        }
        catch (IOException e){
            System.out.println("invalid file exception");
        }
        primaryController.setGraph();
        System.out.println("All items from data file have been stored and set in warehouse map");

        /* Initiate user interaction
            - also show all warehouse data is reflected on map
         */
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello, here is a layout  of the warehouse");
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
        System.out.println("What would you like to do?");
        System.out.println("1: find object");
        System.out.println("2: exit");

        int choice = scanner.nextInt();

        while (choice != 2) {
            System.out.println("Enter item id: ");
            int itemID = scanner.nextInt();
            // primaryController.markItemInGraph(itemID);
            if (!primaryController.markItemInGraph(itemID)) {
                System.out.println("The item you are looking for does NOT exist!");
                continue;
            }
            System.out.println();

            System.out.println("The item for id: " + itemID + " is marked as '$' on the map.");
            System.out.println("The path to the item is marked with 'P' on the map.");
            String shortestPathOutput = primaryController.findItemAndCallPath(itemID);
            System.out.println(shortestPathOutput);
            primaryController.markPathOnGraph();
            primaryController.printGraph();
            primaryController.unmarkPathOnGraph();
            primaryController.unmarkItemInGraph(itemID);
            System.out.println();

            System.out.println("What would you like to do?");
            System.out.println("1: find object");
            System.out.println("2: exit");
            choice = scanner.nextInt();
        }

    }

}
