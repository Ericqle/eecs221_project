package project1_0;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrimaryController {
    static String path = null;
    static int ROW = 40;
    static int COL = 25;


    ObservableList<Map> maps;
    ObservableList<Item> items;
    char[][] graph = new char[ROW][COL];
    /*
    store the real location info for the overview map
     */
    String[][] printFigure = new String[COL+1][ROW+1];

    /* Holds current path between only two vertices
    - takes place of ShortestPath module for future implementation
    */
    ArrayList<Vertex> currentShortestPath = new ArrayList<Vertex>();

    /*
    hold all items from the txt file
    */
    ArrayList<Item> allItemsList = new ArrayList<>();

    /*
    hold all selected items in the checktable
    */
    static ArrayList<String> checklist = new ArrayList<>();

    /*
    check if the file store the wrong data or the path of it is wrong
     */
    boolean checkFile() {
        try {
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            if(br.readLine().startsWith("ProductID")){
                return true;
            }
            else
                return false;
        }catch (IOException e){
            return false;
        }
    }

    /*
    read the txt file and store all items into allitemslist
     */
    void setAllItemsList() throws IOException {
        ArrayList<String> tempItemData;

        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));

        br.readLine();
        String itemData;

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
    void setGraph (){
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[0].length; j++) {
                graph[i][j] = '.';
            }
        }

        for (Item item: allItemsList) {
            graph[item.getX()][item.getY()] = 'X';
        }

        graph[0][0] = 'U';
    }

    /* Print ascii representation of graph
    - prints the transpose and horizontally flibbed graph matrix
        to get the more familiar x-y coordinate orientation
        */
     void graphToFigure(){
        printFigure[0][0] = Integer.toString(COL);
        for (int i = COL - 1; i >= 0 ; i--) {
            printFigure[COL-i][0] = Integer.toString(i);
            for (int j = 0; j < ROW; j++) {
                printFigure[COL-i-1][j+1] = String.valueOf(graph[j][i]);
            }
            //System.out.println("");
        }
        for(int k = 0; k<=ROW; k++) {
            printFigure[COL][k] = Integer.toString(k);
        }
    }

    /*
    other controllers can call functions here by this way
     */
    void setPrimaryControllor(PrimaryController p){
        p = this;
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
            currentShortestPath = findPathToItem(new Item(0,0,0), neededItem);

            return makeUserInstruction();
        }

        else {
            return "Item does not exist";
        }
    }

    /*
    Make user traversal insrtuctions from currentShortestPath
     */
    String makeUserInstruction() {
        StringBuilder instructions = new StringBuilder();

        ArrayList<String> directionList = new ArrayList<>();
        for (int i = 1; i < currentShortestPath.size()-1; i++) {
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
                if (i == directionList.size() - 1 && currentDirCount > 1) {
                    instructions.append("Move " + currDirection + " " + currentDirCount + " units  ");
                }
                else if(i == directionList.size() - 1 && currentDirCount == 1) {
                    instructions.append("Move " + currDirection + " " + currentDirCount + " units  ");
                }
            }
            else {
                if(currentDirCount > 1) {
                    instructions.append("Move " + currDirection + " " + currentDirCount + " units  ");
                }
                else if(currentDirCount == 1){
                    instructions.append("Move " + currDirection + " " + currentDirCount + " unit  ");
                }
                currDirection = directionList.get(i);
                currentDirCount = 1;
                if (i == directionList.size() - 1 && currentDirCount > 1) {
                    instructions.append("Move " + currDirection + " " + currentDirCount + " units  ");
                }
                else if(i == directionList.size() - 1 && currentDirCount == 1){
                    instructions.append("Move " + currDirection + " " + currentDirCount + " unit ");
                }
            }

        }
        return instructions.toString();
    }


    /* Call BFSShortestPath function
    - returns the path as a list of vertices
    */
    ArrayList<Vertex> findPathToItem(Item start, @NotNull Item finish) {
        Coordinate source = new Coordinate(0, 0);
        Coordinate dest = new Coordinate(finish.getX(), finish.getY());

        BFSShortestPath bfs = new BFSShortestPath();

        return bfs.findBFSPath(graph, source, dest);
    }

    /*
    return the items
     */
    ObservableList<Item> getProducts(){
        return items;
    }
    /*
    Get all products and save in observableList
    */
    ObservableList<Item> setProducts(ArrayList<Item> allItemsList){
        items = FXCollections.observableArrayList();
        int id, x, y;
        for(int i=0;i< allItemsList.size();i++){
            id = allItemsList.get(i).getProductID();
            x = allItemsList.get(i).getX();
            y = allItemsList.get(i).getY();;
            Item temp = new Item(id, x, y);
            items.add(temp);
        }
        return items;
    }

    /*
    set maps according to the printfigure
     */
    public ObservableList<Map> getMap() {
        maps = FXCollections.observableArrayList();
        StringProperty c0, c1, c2,c3,c4,c5,c6 ,c7, c8,c9,
                c10, c11, c12,c13,c14,c15,c16,c17, c18,c19,
                c20, c21, c22,c23,c24,c25,c26,c27, c28,c29,
                c30, c31, c32,c33,c34,c35,c36,c37, c38,c39, c40;

        for (String[] strings : printFigure) {
            c0 = new SimpleStringProperty(strings[0]);
            c1 = new SimpleStringProperty(strings[1]);
            c2 = new SimpleStringProperty(strings[2]);
            c3 = new SimpleStringProperty(strings[3]);
            c4 = new SimpleStringProperty(strings[4]);
            c5 = new SimpleStringProperty(strings[5]);
            c6 = new SimpleStringProperty(strings[6]);
            c7 = new SimpleStringProperty(strings[7]);
            c8 = new SimpleStringProperty(strings[8]);
            c9 = new SimpleStringProperty(strings[9]);
            c10 = new SimpleStringProperty(strings[10]);
            c11 = new SimpleStringProperty(strings[11]);
            c12 = new SimpleStringProperty(strings[12]);
            c13 = new SimpleStringProperty(strings[13]);
            c14 = new SimpleStringProperty(strings[14]);
            c15 = new SimpleStringProperty(strings[15]);
            c16 = new SimpleStringProperty(strings[16]);
            c17 = new SimpleStringProperty(strings[17]);
            c18 = new SimpleStringProperty(strings[18]);
            c19 = new SimpleStringProperty(strings[19]);
            c20 = new SimpleStringProperty(strings[20]);
            c21 = new SimpleStringProperty(strings[21]);
            c22 = new SimpleStringProperty(strings[22]);
            c23 = new SimpleStringProperty(strings[23]);
            c24 = new SimpleStringProperty(strings[24]);
            c25 = new SimpleStringProperty(strings[25]);
            c26 = new SimpleStringProperty(strings[26]);
            c27 = new SimpleStringProperty(strings[27]);
            c28 = new SimpleStringProperty(strings[28]);
            c29 = new SimpleStringProperty(strings[29]);
            c30 = new SimpleStringProperty(strings[30]);
            c31 = new SimpleStringProperty(strings[31]);
            c32 = new SimpleStringProperty(strings[32]);
            c33 = new SimpleStringProperty(strings[33]);
            c34 = new SimpleStringProperty(strings[34]);
            c35 = new SimpleStringProperty(strings[35]);
            c36 = new SimpleStringProperty(strings[36]);
            c37 = new SimpleStringProperty(strings[37]);
            c38 = new SimpleStringProperty(strings[38]);
            c39 = new SimpleStringProperty(strings[39]);
            c40 = new SimpleStringProperty(strings[40]);

            Map temp = new Map(c0, c1, c2, c3, c4, c5, c6, c7, c8, c9,
                    c10, c11, c12, c13, c14, c15, c16, c17, c18, c19,
                    c20, c21, c22, c23, c24, c25, c26, c27, c28, c29,
                    c30, c31, c32, c33, c34, c35, c36, c37, c38, c39, c40);

            maps.add(temp);
        }
        return maps;
    }

    /*
    set the mapping relations between tablecolumns and maps' info
     */
    void createMap(TableView<Map> table, TableColumn<Map, String> c0, TableColumn<Map, String> c1,
                   TableColumn<Map, String> c2, TableColumn<Map, String> c3,TableColumn<Map, String> c4,
                   TableColumn<Map, String> c5,TableColumn<Map, String> c6,TableColumn<Map, String> c7,TableColumn<Map, String> c8,
                   TableColumn<Map, String> c9,TableColumn<Map, String> c10,TableColumn<Map, String> c11,TableColumn<Map, String> c12,
                   TableColumn<Map, String> c13,TableColumn<Map, String> c14,TableColumn<Map, String> c15,TableColumn<Map, String> c16,
                   TableColumn<Map, String> c17,TableColumn<Map, String> c18,TableColumn<Map, String> c19,TableColumn<Map, String> c20,
                   TableColumn<Map, String>c21,TableColumn<Map, String> c22,TableColumn<Map, String> c23,TableColumn<Map, String> c24,
                   TableColumn<Map, String> c25,TableColumn<Map, String> c26,TableColumn<Map, String> c27,TableColumn<Map, String> c28,
                   TableColumn<Map, String> c29,TableColumn<Map, String> c30,TableColumn<Map, String> c31,TableColumn<Map, String> c32,
                   TableColumn<Map, String> c33,TableColumn<Map, String> c34,TableColumn<Map, String> c35,TableColumn<Map, String> c36,
                   TableColumn<Map, String> c37,TableColumn<Map, String> c38,TableColumn<Map, String> c39,TableColumn<Map, String> c40) {
        c0.setCellValueFactory(cellData -> cellData.getValue().col_0Property());
        c1.setCellValueFactory(cellData -> cellData.getValue().col_1Property());
        c2.setCellValueFactory(cellData -> cellData.getValue().col_2Property());
        c3.setCellValueFactory(cellData -> cellData.getValue().col_3Property());
        c4.setCellValueFactory(cellData -> cellData.getValue().col_4Property());
        c5.setCellValueFactory(cellData -> cellData.getValue().col_5Property());
        c6.setCellValueFactory(cellData -> cellData.getValue().col_6Property());
        c7.setCellValueFactory(cellData -> cellData.getValue().col_7Property());
        c8.setCellValueFactory(cellData -> cellData.getValue().col_8Property());
        c9.setCellValueFactory(cellData -> cellData.getValue().col_9Property());
        c10.setCellValueFactory(cellData -> cellData.getValue().col_10Property());
        c11.setCellValueFactory(cellData -> cellData.getValue().col_11Property());
        c12.setCellValueFactory(cellData -> cellData.getValue().col_12Property());
        c13.setCellValueFactory(cellData -> cellData.getValue().col_13Property());
        c14.setCellValueFactory(cellData -> cellData.getValue().col_14Property());
        c15.setCellValueFactory(cellData -> cellData.getValue().col_15Property());
        c16.setCellValueFactory(cellData -> cellData.getValue().col_16Property());
        c17.setCellValueFactory(cellData -> cellData.getValue().col_17Property());
        c18.setCellValueFactory(cellData -> cellData.getValue().col_18Property());
        c19.setCellValueFactory(cellData -> cellData.getValue().col_19Property());
        c20.setCellValueFactory(cellData -> cellData.getValue().col_20Property());
        c21.setCellValueFactory(cellData -> cellData.getValue().col_21Property());
        c22.setCellValueFactory(cellData -> cellData.getValue().col_22Property());
        c23.setCellValueFactory(cellData -> cellData.getValue().col_23Property());
        c24.setCellValueFactory(cellData -> cellData.getValue().col_24Property());
        c25.setCellValueFactory(cellData -> cellData.getValue().col_25Property());
        c26.setCellValueFactory(cellData -> cellData.getValue().col_26Property());
        c27.setCellValueFactory(cellData -> cellData.getValue().col_27Property());
        c28.setCellValueFactory(cellData -> cellData.getValue().col_28Property());
        c29.setCellValueFactory(cellData -> cellData.getValue().col_29Property());
        c30.setCellValueFactory(cellData -> cellData.getValue().col_30Property());
        c31.setCellValueFactory(cellData -> cellData.getValue().col_31Property());
        c32.setCellValueFactory(cellData -> cellData.getValue().col_32Property());
        c33.setCellValueFactory(cellData -> cellData.getValue().col_33Property());
        c34.setCellValueFactory(cellData -> cellData.getValue().col_34Property());
        c35.setCellValueFactory(cellData -> cellData.getValue().col_35Property());
        c36.setCellValueFactory(cellData -> cellData.getValue().col_36Property());
        c37.setCellValueFactory(cellData -> cellData.getValue().col_37Property());
        c38.setCellValueFactory(cellData -> cellData.getValue().col_38Property());
        c39.setCellValueFactory(cellData -> cellData.getValue().col_39Property());
        c40.setCellValueFactory(cellData -> cellData.getValue().col_40Property());

        table.setItems(getMap());
    }

    /* Mark the path using 'P' on graph
- path coordinates from currentShortestPath
*/
    void markPathOnGraph(){
        for (int i = 1; i < currentShortestPath.size() - 1; i++) {
            int x = currentShortestPath.get(i).coordinate.x;
            int y = currentShortestPath.get(i).coordinate.y;
            graph[x][y] = 'P';
        }
    }
    /*
    Set needed item to '$' on graph
        - return true if exist
     */
    boolean markItemInGraph(int id) {
        if (itemExist(id)) {
            Item item = getItemByID(id);
            graph[item.getX()][item.getY()] = '$';
            return true;
        }else {
            return false;
        }
    }

    /*
    Reset found item to a shelf 'X' on graph
     */
    void unmarkItemInGraph(int id) {
        Item item = getItemByID(id);
        graph[item.getX()][item.getY()] = 'X';
    }

    /* Unmark the path on graph
    - path coordinates from currentShortestPath
    */
    void unmarkPathOnGraph(){
        for (int i = 1; i < currentShortestPath.size() - 1; i++) {
            int x = currentShortestPath.get(i).coordinate.x;
            int y = currentShortestPath.get(i).coordinate.y;
            graph[x][y] = '.';
        }
    }
    /*
    Item lookup for ID
     */
    Item getItemByID(int id) {
        for (Item item: allItemsList) {
            if (id == item.getProductID())
                return item;
        }
        return null;
    }
    /*
    Check if item exists in allItemsList
     */
    boolean itemExist(int id) {
        for (Item item: allItemsList) {
            if (id == item.getProductID())
                return true;
        }
        return false;
    }
    /*
    return the checklist
     */
    ArrayList<String> getCheckList(){
        return checklist;
    }
    /*
    store the info of checklist in selectingcontroller
     */
    void setChecklist(ArrayList<String> checklist) {
        PrimaryController.checklist = checklist;
    }
    /*
    store the correct path of txt file
    */
    void setPath(String path){
        PrimaryController.path = path;
    }

    /*
    return the correct path
     */
    String getPath(){
        return path;
    }

    /*
    return the allitemslist
     */
    ArrayList<Item> getAllItemsList(){
        return allItemsList;
    }

}
