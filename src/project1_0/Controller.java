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
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
    private TextField keywords;

    @FXML
    private TableView<Product> selectTable, checkTable;
    private ObservableList<Product> products, selectedProduct;

    @FXML
    private ImageView image;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if(choiceBox!=null) {
            choiceBox.getItems().addAll(sort);
            choiceBox.getSelectionModel().selectFirst();
            //for test
            choiceBox.setOnAction(this::getSort);
        }
        if(selectTable != null)
            initTable();
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
    public void initTable(){
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

        //checkTable.getColumns().addAll(productIdColumn,xColumn,yColumn);
        selectTable.setItems(getProducts(array));
        selectTable.getColumns().addAll(productIdColumn,xColumn,yColumn);

    }

    /*
    Search for products according to the keywords
    from the warehouse directory
     */
    public void search(ActionEvent event){
        if(keywords != null) {
            text = keywords.getText();
//        System.out.println(text);
        }
    }

    /*
    Add the selected product to the checking tableview
     */
    public void add(){
        products = selectTable.getItems();
        Product selectedItem = selectTable.getSelectionModel().getSelectedItem();
        checkTable.getItems().add(new Product(selectedItem.getProductID(),selectedItem.getX(),selectedItem.getY()));
    }

    /*
    delete the selected product from the checking tableview
     */
    public void delete(){
        products = checkTable.getItems();
        selectedProduct = checkTable.getSelectionModel().getSelectedItems();
        selectedProduct.forEach(products::remove);
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
}
