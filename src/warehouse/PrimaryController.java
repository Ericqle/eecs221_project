package warehouse;

import java.util.ArrayList;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrimaryController {
    int ROW = 40;
    int COL = 25;

    ArrayList<Item> allItemsList = new ArrayList<>();
    char[][] graph = new char[ROW][COL];

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

    ArrayList<String> getFloatsFromString(String raw) {
        ArrayList<String> listBuffer = new ArrayList<String>();

        Pattern p = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher m = p.matcher(raw);

        while (m.find()) {
            listBuffer.add(m.group());
        }

        return listBuffer;
    }

    void setGraph (){
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[0].length; j++) {
                graph[i][j] = '-';
            }
        }

        for (Item item: allItemsList) {
            graph[item.row][item.col] = 'X';
        }

        graph[0][0] = 'U';
    }

    void printGraph(){
        for (int i = 0; i < graph.length ; i++) {
            for (int j = 0; j < graph[0].length; j++) {
                System.out.print(" " + String.valueOf(graph[i][j]) + " ");
            }
            System.out.println();
        }
    }

    void markItemInGraph(int id) {
        Item item = getItemByID(id);
        graph[item.row][item.col] = '$';
    }

    void unmarkItemInGraph(int id) {
        Item item = getItemByID(id);
        graph[item.row][item.col] = 'X';
    }

    Item getItemByID(int id) {
        for (Item item: allItemsList) {
            if (id == item.id)
                return item;
        }
        return null;
    }

    boolean itemExist(int id) {
        for (Item item: allItemsList) {
            if (id == item.id)
                return true;
        }
        return false;
    }

    String findPathToItem(Item start, Item finish) {
        NodePoint source = new NodePoint(0, 0);
        NodePoint dest = new NodePoint(finish.row, finish.col);

        BFSShortestPath bfs = new BFSShortestPath();
        ArrayList<Node> path = bfs.backtrackPath(graph, source, dest);

        StringBuilder instructions = new StringBuilder();
        for (Node node: path) {
            instructions.append( "(" + String.valueOf(node.nodePoint.x) + " "
                    + String.valueOf(node.nodePoint.y) + ") ");
        }

        return "Path to Item: " + instructions.toString();
    }

    String findItemAndCallPath(int id) {
        if (itemExist(id)) {
            Item neededItem = getItemByID(id);
            return findPathToItem(new Item(0,0,0), neededItem);
        }

        else {
            return "Item does not exist";
        }
    }

}
