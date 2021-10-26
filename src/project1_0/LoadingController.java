package project1_0;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadingController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TableView<Map> loadTable;

    @FXML
    private TextField loadTextField;

    @FXML
    Label loadLabel;

    @FXML
    private TableColumn<Map, String> c0, c1, c2,c3,c4,c5,c6,c7, c8,c9, c10, c11, c12,c13,c14,c15,c16,c17, c18,c19, c20, c21, c22,c23,c24,c25,c26,c27, c28,c29, c30, c31, c32,c33,c34,c35,c36,c37, c38,c39, c40;
    private ObservableList<Map> maps;

    static int ROW = 40;
    static int COL = 25;

    static ArrayList<Product> allItemsList = new ArrayList<>();

    ArrayList<Vertex> currentShortestPath = new ArrayList<>();

    static char[][] graph = new char[ROW][COL];
    String[][] printFigure = new String[COL+1][ROW+1];

    String path = null;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if(loadTable!= null || path!=null ||!path.endsWith("txt")) {
            path = loadTextField.getText();
            setGraph();
            graphToFigure();
            createMap(loadTable);
        }
    }

    public void Load(ActionEvent event) throws IOException {
        if(loadTextField.getText().endsWith("txt")) {
            loadLabel.setText("Loading");
            path = loadTextField.getText();
            readAllItems(path);
            setGraph();
            graphToFigure();
            createMap(loadTable);
        }
        else{
            loadLabel.setText("Please input the valid path of the txt file:");
        }
    }

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
            Product tempItem = new Product(id, x, y);
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

        for (Product item: allItemsList) {
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

    void createMap(TableView<Map> table) {
//      c0.setCellValueFactory(new PropertyValueFactory<Map, String>("col0"));
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

        table.setItems(getMap(printFigure));
    }

    public ObservableList<Map> getMap(String[][] printFigure) {
        maps = FXCollections.observableArrayList();
        StringProperty c0, c1, c2,c3,c4,c5,c6 ,c7, c8,c9,
                c10, c11, c12,c13,c14,c15,c16,c17, c18,c19,
                c20, c21, c22,c23,c24,c25,c26,c27, c28,c29,
                c30, c31, c32,c33,c34,c35,c36,c37, c38,c39, c40;

        for(int i=0;i< printFigure.length;i++){
            c0 = new SimpleStringProperty(printFigure[i][0]);
            c1 = new SimpleStringProperty (printFigure[i][1]);
            c2 = new SimpleStringProperty (printFigure[i][2]);
            c3 = new SimpleStringProperty (printFigure[i][3]);
            c4 = new SimpleStringProperty (printFigure[i][4]);
            c5 = new SimpleStringProperty (printFigure[i][5]);
            c6 = new SimpleStringProperty (printFigure[i][6]);
            c7 = new SimpleStringProperty (printFigure[i][7]);
            c8 = new SimpleStringProperty (printFigure[i][8]);
            c9 = new SimpleStringProperty (printFigure[i][9]);
            c10 = new SimpleStringProperty (printFigure[i][10]);
            c11 = new SimpleStringProperty (printFigure[i][11]);
            c12 = new SimpleStringProperty (printFigure[i][12]);
            c13 = new SimpleStringProperty (printFigure[i][13]);
            c14 = new SimpleStringProperty (printFigure[i][14]);
            c15 = new SimpleStringProperty (printFigure[i][15]);
            c16 = new SimpleStringProperty (printFigure[i][16]);
            c17 = new SimpleStringProperty (printFigure[i][17]);
            c18 = new SimpleStringProperty (printFigure[i][18]);
            c19 = new SimpleStringProperty (printFigure[i][19]);
            c20 = new SimpleStringProperty (printFigure[i][20]);
            c21 = new SimpleStringProperty (printFigure[i][21]);
            c22 = new SimpleStringProperty (printFigure[i][22]);
            c23 = new SimpleStringProperty (printFigure[i][23]);
            c24 = new SimpleStringProperty (printFigure[i][24]);
            c25 = new SimpleStringProperty (printFigure[i][25]);
            c26 = new SimpleStringProperty (printFigure[i][26]);
            c27 = new SimpleStringProperty (printFigure[i][27]);
            c28 = new SimpleStringProperty (printFigure[i][28]);
            c29 = new SimpleStringProperty (printFigure[i][29]);
            c30 = new SimpleStringProperty (printFigure[i][30]);
            c31 = new SimpleStringProperty (printFigure[i][31]);
            c32 = new SimpleStringProperty (printFigure[i][32]);
            c33 = new SimpleStringProperty (printFigure[i][33]);
            c34 = new SimpleStringProperty (printFigure[i][34]);
            c35 = new SimpleStringProperty (printFigure[i][35]);
            c36 = new SimpleStringProperty (printFigure[i][36]);
            c37 = new SimpleStringProperty (printFigure[i][37]);
            c38 = new SimpleStringProperty (printFigure[i][38]);
            c39 = new SimpleStringProperty (printFigure[i][39]);
            c40 = new SimpleStringProperty (printFigure[i][40]);

            Map temp = new Map(c0, c1, c2,c3,c4,c5,c6,c7, c8,c9,
                    c10, c11, c12,c13,c14,c15,c16,c17, c18,c19,
                    c20, c21, c22,c23,c24,c25,c26,c27, c28,c29,
                    c30, c31, c32,c33,c34,c35,c36,c37, c38,c39, c40);

//            System.out.println(c1+ "" +printFigure[i][1]);
            maps.add(temp);
        }
        return maps;
    }

    public void gotoSelecting(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Selecting.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    static boolean markItemInGraph(int id) {
        if (itemExist(id)) {
            Product item = getItemByID(id);
            graph[item.getX()][item.getY()] = '$';
            return true;
        }else {
            return false;
        }
    }

    void unmarkItemInGraph(int id) {
        Product item = getItemByID(id);
        graph[item.getX()][item.getY()] = 'X';
    }

    static Product getItemByID(int id) {
        for (Product item: allItemsList) {
            if (id == item.getProductID())
                return item;
        }
        return null;
    }

    static boolean itemExist(int id) {
        for (Product item: allItemsList) {
            if (id == item.getProductID())
                return true;
        }
        return false;
    }

}
