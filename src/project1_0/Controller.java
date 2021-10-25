package project1_0;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.util.Callback;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button confirmButton, searchButton, addButton, deleteButton, nextButton;

    @FXML
    private Label sLabel, progressLabel, instruction, currentLocation;

    @FXML
    private ChoiceBox<String> choiceBox;
    private String[] sort = {"ProductID"};
    private String text;

    @FXML
    private ProgressBar progressBar;
    BigDecimal progress = new BigDecimal(String.format("%.2f",0.0));

    @FXML
    private TextField keywords, loadTextField;
    @FXML
    private TableView<Map> loadTable, guideTable;

    @FXML
    private TableView<Product> selectTable, checkTable;
    private ObservableList<Product> products, selectedProduct;
    @FXML
    private TableColumn<Product, Integer> checkProductID, checkXLocation , checkYLocation ;
    @FXML
    private TableColumn<Product, Integer> selectProductID, selectXLocation, selectYLocation;

    @FXML
    private TableColumn<Map, String> c0, c1, c2,c3,c4,c5,c6,c7, c8,c9, c10, c11, c12,c13,c14,c15,c16,c17, c18,c19, c20, c21, c22,c23,c24,c25,c26,c27, c28,c29, c30, c31, c32,c33,c34,c35,c36,c37, c38,c39, c40;
    private ObservableList<Map> maps;

    @FXML
    private ImageView image;

    int ROW = 40;
    int COL = 25;

    /* All items in warehouse list
     */
    ArrayList<Product> allItemsList = new ArrayList<>();

    /* Holds current path between only two vertice
        - takes place of ShortestPath module for future implementation
     */
    ArrayList<Vertex> currentShortestPath = new ArrayList<>();

    /* Abstraction of graph for the warehouse
        - all index-able spaces are considered vertices
     */
    char[][] graph = new char[ROW][COL];
    String[][] printFigure = new String[COL+1][ROW+1];

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if(choiceBox!=null) {
            choiceBox.getItems().addAll(sort);
            choiceBox.getSelectionModel().selectFirst();
            //for test
            choiceBox.setOnAction(this::getSort);
        }
        if(selectTable != null) {
            try {
                initTable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //test
    public void getSort(ActionEvent event){
        String mysort = choiceBox.getValue();
        sLabel.setText(mysort);
    }

    //test
    public void printArray(String array[][]){
//        for test
        for(int i=0;i< 10;i++){
//        for(int i=0;i< array.length;i++){
            for(int j=0;j<array[i].length;j++){
                if(j!=array[i].length-1){
                    System.out.print("array["+i+"]["+j+"]="+array[i][j]+",");
                }
                else{
                    System.out.print("array["+i+"]["+j+"]="+array[i][j]);
                }
            }
            System.out.println();
        }
    }

    /*
    Read the contents of .txt file line by line and store them in the list
     */
    public List  readTxtFile(String filePath) {
        List<String> list = new ArrayList<String>();
        try {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (!lineTxt.startsWith("P"))
                        list.add(lineTxt);
                }
                read.close();
            } else {
                System.out.println("can't find the file");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /*
    Convert the contents of the linked list into array
     */
    public String[][] createArray(String filePath){
        List<String> list = readTxtFile(filePath);
        int s = list.size();
        int l = list.get(0).length();
        String array[][] = new String[s][l];
        for(int i=0;i<s;i++){
            array[i] = new String[l-2];
            String line=list.get(i);
            String[] temp = line.split("\t");
            for(int j=0;j< l-2;j++){
                array[i][j]=temp[j];
            }
        }
        return array;
    }

    /*
    Get all products and save in observableList
     */
    public ObservableList<Product> getProducts(String array[][]){
        products = FXCollections.observableArrayList();
        int id, x, y;
        for(int i=0;i< array.length;i++){
            id = (int) Math.round(Double.parseDouble(array[i][0]));
            x = (int) Math.round(Double.parseDouble(array[i][1]));
            y = (int) Math.round(Double.parseDouble(array[i][2]));
            Product temp = new Product(id, x, y);
            products.add(temp);
        }
        return products;
    }
    
    /*
    initialize the tableview, set up the name of each tableColumn
    and input the warehouse's data
     */
    public void initTable() throws IOException {
        selectProductID.setCellValueFactory(new PropertyValueFactory<Product, Integer>("productID"));
        selectXLocation.setCellValueFactory(new PropertyValueFactory<Product, Integer>("x"));
        selectYLocation.setCellValueFactory(new PropertyValueFactory<Product, Integer>("y"));

        checkProductID.setCellValueFactory(new PropertyValueFactory<Product, Integer>("productID"));
        checkXLocation.setCellValueFactory(new PropertyValueFactory<Product, Integer>("x"));
        checkYLocation.setCellValueFactory(new PropertyValueFactory<Product, Integer>("y"));

//        TableColumn<Product, Integer> productIdColumn = new TableColumn<>("ProductID");
//        productIdColumn.setMinWidth(100);
//        productIdColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("productID"));
//
//        TableColumn<Product, Integer> xColumn = new TableColumn<>("xLocation");
//        xColumn.setMinWidth(75);
//        xColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("x"));
//
//        TableColumn<Product, Integer> yColumn = new TableColumn<>("yLocation");
//        yColumn.setMinWidth(75);
//        yColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("y"));

            String array[][] = createArray("E:\\JavaSpace\\project1.0\\src\\project1_0\\data_v1.txt");
            printArray(array);
            getProducts(array);
            readAllItems("E:\\JavaSpace\\project1.0\\src\\project1_0\\data_v1.txt");
        selectTable.setItems(getProducts(array));
        //selectTable.getColumns().addAll(productIdColumn,xColumn,yColumn);
        //selectTable.getColumns().addAll(selectProductID,selectXLocation,selectYLocation);

    }

    public void Load(ActionEvent event) throws IOException {
        String path = loadTextField.getText();
        readAllItems(path);
//        loadTable.setOpacity(1);
        setGraph();
        printGraph();
        createMap(loadTable);

//        Parent root = FXMLLoader.load(getClass().getResource("Selecting.fxml"));
//        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
    }

    /*
    Search for products according to the keywords
    from the warehouse directory
     */
    public void search() {
        if (keywords != null) {
            text = keywords.getText();
            Integer id = Integer.valueOf(text);
            System.out.println(id);
            if(!markItemInGraph(id)){
                sLabel.setText("The item you are looking for does NOT exist!");
            }
            products = FXCollections.observableArrayList();
            products.add(getItemByID(id));
            selectTable.setItems(products);
        }

//        if (scanner.hasNextInt()) {
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
//        } else {
//            String str = scanner.next();
//            System.out.println("Invalid input! Please input a number.");
//        }

    }

    /*
    Add the selected product to the checking tableview
     */
    public void add(){
        products = selectTable.getItems();
        Product selectedItem = selectTable.getSelectionModel().getSelectedItem();
        checkTable.getItems().add(selectedItem);
    }

    /*
    delete the selected product from the checking tableview
     */
    public void delete(){
        try{
            products = checkTable.getItems();
            selectedProduct = checkTable.getSelectionModel().getSelectedItems();
            selectedProduct.forEach(products::remove);
        }catch (Exception e){
            //System.out.println(e);
            System.out.println("there is nothing to delete!");
        }
        //products.remove(selectedProduct);
    }

    /*
    show the next instruction and worker's location
    update the progressbar and guiding graph
     */
    public void nextAndIncreaseProgress(){
        if(progress.doubleValue() < 1) {
            progress = new BigDecimal(String.format("%.2f",progress.doubleValue()+ 0.1));
            progressBar.setProgress(progress.doubleValue());
            progressLabel.setText(Integer.toString((int) Math.round(progress.doubleValue() * 100)) + "%");
        }
    }

    /*
    switch scene to Guiding view
     */
    public void goToGuiding(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Guiding.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /*
    switch scene to Selecting view
    */
    public void backToSelecting(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Selecting.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



    public void goToLoading(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Loading.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    /* Read warehouse data and save it in allItemsList
     */
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
    void printGraph(){
        printFigure[0][0] = Integer.toString(COL);
        for (int i = COL - 1; i >= 0 ; i--) {
            printFigure[COL-i][0] = Integer.toString(i);
            for (int j = 0; j < ROW; j++) {
                printFigure[COL-i-1][j+1] = String.valueOf(graph[j][i]);
                //   System.out.print("\t" + String.valueOf(graph[j][i]) + "\t");
            }
            //System.out.println("");
        }
        for(int k = 0; k<=ROW; k++) {
            printFigure[COL][k] = Integer.toString(k);
            //  System.out.print("\t" + k + "\t");
        }

//        for(int i = 0 ; i<COL+1 ; i++) {
//            for (int j = 0; j < ROW + 1; j++) {
//                System.out.print("\t" + printFigure[i][j] + "\t");
//            }
//            System.out.println("");
//        }
    }

    void createMap(TableView<Map> table) {
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

    private ObservableList<Map> getMap(String[][] printFigure) {
        maps = FXCollections.observableArrayList();
        StringProperty c0, c1, c2,c3,c4,c5,c6 ,c7, c8,c9,
                c10, c11, c12,c13,c14,c15,c16,c17, c18,c19,
                c20, c21, c22,c23,c24,c25,c26,c27, c28,c29,
                c30, c31, c32,c33,c34,c35,c36,c37, c38,c39, c40;

        for(int i=0;i< printFigure.length;i++){
            c0 = new SimpleStringProperty (printFigure[i][0]);
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

    /* Set needed item to '$' on graph
    - return true if exist
 */
    boolean markItemInGraph(int id) {
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

    Product getItemByID(int id) {
        for (Product item: allItemsList) {
            if (id == item.getProductID())
                return item;
        }
        return null;
    }

    boolean itemExist(int id) {
        for (Product item: allItemsList) {
            if (id == item.getProductID())
                return true;
        }
        return false;
    }

}
