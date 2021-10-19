package warehouse;

import java.util.ArrayList;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrimaryController {
    int ROW = 40;
    int COL = 25;

    ArrayList<Item> allItemsList = new ArrayList<>();
    ArrayList<Node> currentShortestPath = new ArrayList<>();
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
                graph[i][j] = '.';
            }
        }

        for (Item item: allItemsList) {
            graph[item.row][item.col] = 'X';
        }

        graph[0][0] = 'U';
    }

    void printGraph(){
        for (int i = COL - 1; i >= 0 ; i--) {
            for (int j = 0; j < ROW; j++) {
                System.out.print(" " + String.valueOf(graph[j][i]) + " ");
            }
            System.out.println();
        }
    }

    boolean markItemInGraph(int id) {
        if (itemExist(id)) {
            Item item = getItemByID(id);
            graph[item.row][item.col] = '$';
            return true;
        }else {
            return false;
        }
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

    void markPathOnGraph(){
        for (int i = 1; i < currentShortestPath.size() - 1; i++) {
            int x = currentShortestPath.get(i).coordinate.x;
            int y = currentShortestPath.get(i).coordinate.y;
            graph[x][y] = 'P';
        }
    }

    void unmarkPathOnGraph(){
        for (int i = 1; i < currentShortestPath.size() - 1; i++) {
            int x = currentShortestPath.get(i).coordinate.x;
            int y = currentShortestPath.get(i).coordinate.y;
            graph[x][y] = '.';
        }
    }

    ArrayList<Node> findPathToItem(Item start, Item finish) {
        Coordinate source = new Coordinate(0, 0);
        Coordinate dest = new Coordinate(finish.row, finish.col);

        BFSShortestPath bfs = new BFSShortestPath();

        return bfs.findBFSPath(graph, source, dest);
    }

    String makeUserInstruction() {
        StringBuilder instructions = new StringBuilder();

        ArrayList<String> directionList = new ArrayList<>();
        for (int i = 1; i < currentShortestPath.size(); i++) {
            String xDirection = "East";
            String yDirection = "North";
            int x0 = currentShortestPath.get(i - 1).coordinate.x;
            int x1 = currentShortestPath.get(i).coordinate.x;
            int y0 = currentShortestPath.get(i - 1).coordinate.y;
            int y1 = currentShortestPath.get(i).coordinate.y;
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
        int currentDirCount = 1;

        for (int i = 1; i < directionList.size(); i++) {
            if (directionList.get(i).equals(currDirection)) {
                currentDirCount = currentDirCount + 1;
                if (i == directionList.size() - 1) {
                    instructions.append("Move " + currDirection + " " + currentDirCount + " unit(s) ");
                }
            }
            else {
                instructions.append("Move " + currDirection + " " + currentDirCount + " units(s) ");
                currDirection = directionList.get(i);
                currentDirCount = 1;
                if (i == directionList.size() - 1) {
                    instructions.append("Move " + currDirection + " " + currentDirCount + " unit(s) ");
                }
            }

        }

        return "Path to Item: " + instructions.toString();
    }

    String findItemAndCallPath(int id) {
        if (itemExist(id)) {
            Item neededItem = getItemByID(id);
            currentShortestPath = findPathToItem(new Item(0,0,0), neededItem);

            return makeUserInstruction();
        }

        else {
            return "Item does not exist";
        }
    }

}
