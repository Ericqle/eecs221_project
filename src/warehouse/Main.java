package warehouse;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        PrimaryController primaryController = new PrimaryController();

        try {
            primaryController.readAllItems("/Users/eric/Documents/EECS221/alphaTest/src/warehouse/qvBox-warehouse-data-f21-v01.txt");
        }
        catch (IOException e){
            System.out.println("invalid file exception");
        }

        primaryController.setGraph();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello, here is a layout  of the warehouse:");
        System.out.println("'U' = you | 'X' = shelves | '.' = open space");
        primaryController.printGraph();
        System.out.println();

        System.out.println("What would you like to do?");
        System.out.println("1: find object");
        System.out.println("2: exit");

        int choice = scanner.nextInt();

        while (choice != 2) {
            System.out.println("Enter item id: ");
            int itemID = scanner.nextInt();
            primaryController.markItemInGraph(itemID);
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
