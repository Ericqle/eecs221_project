package project1_0;

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
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button confirmButton, searchButton, addButton, deleteButton;

    @FXML
    private Label sLabel;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private TextField keywords;

    @FXML
    TableView<Product> selectTable, checkTable;


    private String[] sort = {"ProductID", "Categories", "Name"};
    private String text;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if(choiceBox!=null) {
            choiceBox.getItems().addAll(sort);
            choiceBox.getSelectionModel().selectFirst();
            //for test
            choiceBox.setOnAction(this::getSort);
        }
        if(selectTable != null)
            initSelectTable();
    }

    //for test
    public void getSort(ActionEvent event){
        String mysort = choiceBox.getValue();
        sLabel.setText(mysort);
    }

    public static  List  readTxtFile(String filePath) {
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

    public static String[][] createArray(String filePath){
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

    //test
    public static void printArray(String array[][]){
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

    //Get all items
    public ObservableList<Product> getProducts(String array[][]){
        ObservableList<Product> items = FXCollections.observableArrayList();
        int id, x, y;
        for(int i=0;i< array.length;i++){
            id = (int) Math.round(Double.parseDouble(array[i][0]));
            x = (int) Math.round(Double.parseDouble(array[i][1]));
            y = (int) Math.round(Double.parseDouble(array[i][2]));
            Product temp = new Product(id, x, y);
            items.add(temp);
        }
        return items;
    }

    public void initSelectTable(){
        TableColumn<Product, Integer> productIdColumn = new TableColumn<>("ProductID");
        productIdColumn.setMinWidth(100);
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));

        TableColumn<Product, Integer> xColumn = new TableColumn<>("xLocation");
        xColumn.setMinWidth(75);
        xColumn.setCellValueFactory(new PropertyValueFactory<>("x"));

        TableColumn<Product, Integer> yColumn = new TableColumn<>("yLocation");
        yColumn.setMinWidth(75);
        yColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("y"));

            String array[][] = createArray("E:\\JavaSpace\\project1.0\\src\\project1_0\\data_v1.txt");
            printArray(array);
            getProducts(array);
        checkTable.getColumns().addAll(productIdColumn,xColumn,yColumn);
        selectTable.setItems(getProducts(array));
        selectTable.getColumns().addAll(productIdColumn,xColumn,yColumn);

    }

    public void search(ActionEvent event){
        if(keywords != null) {
            text = keywords.getText();
//        System.out.println(text);
        }
    }

    public void add(){
        ObservableList<Product> selectedItem, allItems;
        allItems = selectTable.getItems();
        selectedItem = selectTable.getSelectionModel().getSelectedItems();
        checkTable.getItems().addAll(selectedItem.get(0));
       // selectedItem.forEach(allItems::remove);
    }

    public void delete(){
        ObservableList<Product> selectedItem, allItems;
        allItems = checkTable.getItems();
        selectedItem = checkTable.getSelectionModel().getSelectedItems();
        selectedItem.forEach(allItems::remove);
    }

    public void goToGuiding(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Guiding.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void backToSelecting(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Selecting.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
