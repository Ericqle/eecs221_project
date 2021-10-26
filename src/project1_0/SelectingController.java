package project1_0;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectingController implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    private Label sLabel, cLabel;

    @FXML
    private ChoiceBox<String> choiceBox;
    private String[] sort = {"ProductID"};
    private String text;

    @FXML
    private TextField keywords;

    @FXML
    private Button deleteButton;
    @FXML
    private TableView<Product> selectTable, checkTable;
    private ObservableList<Product> products;

    @FXML
    private TableColumn<Product, Integer> checkProductID, checkXLocation , checkYLocation ;
    @FXML
    private TableColumn<Product, Integer> selectProductID, selectXLocation, selectYLocation;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
//        if(choiceBox!=null) {
//            choiceBox.getItems().addAll(sort);
//            choiceBox.getSelectionModel().selectFirst();
//            //for test
//            choiceBox.setOnAction(this::getSort);
//        }
        if(selectTable != null) {
            try {
                initTable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//    //test
//    public void getSort(ActionEvent event){
//        String mysort = choiceBox.getValue();
//        sLabel.setText(mysort);
//    }

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
    public List readTxtFile(String filePath) {
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


        String array[][] = createArray("E:\\JavaSpace\\project1.0\\src\\project1_0\\data_v1.txt");
        selectTable.setItems(getProducts(array));
    }

    /*
    Search for products according to the keywords
    from the warehouse directory
     */

    public void search() throws NumberFormatException {
        if (keywords != null && !keywords.getText().isEmpty()) {
            text = keywords.getText();
            if (isNumeric(text)) {
                Integer id = Integer.valueOf(text);

                if (!LoadingController.markItemInGraph(id)) {
                    sLabel.setText("The item you are looking for does NOT exist!");
                }else {
                    sLabel.setText("Selecting");
                    ObservableList<Product> temp = products;
                    temp = FXCollections.observableArrayList();
                    temp.add(LoadingController.getItemByID(id));
                    selectTable.setItems(temp);
                }
            }
            else {
                sLabel.setText("Please input the valid productID");
            }
        }
        else {
                selectTable.setItems(products);
        }
    }

    /*
    Add the selected product to the checking tableview
    */
    public void add(){
        Product selectedItem = selectTable.getSelectionModel().getSelectedItem();
        int i = 0;
        if(!checkTable.getItems().isEmpty()){
            for(Product product:checkTable.getItems()){
                if(Objects.equals(product.getProductID(), selectedItem.getProductID())) {
                    cLabel.setText("the product is already in the checkTable");
                    i = 1;
                }
                }
        if(i == 0)
            checkTable.getItems().add(selectedItem);
        }else {
            if(checkTable.getItems().isEmpty()){
                deleteButton.setDisable(false);
            }
            checkTable.getItems().add(selectedItem);
            cLabel.setText("Added successfully");

        }
    }

    /*
    delete the selected product from the checking tableview
     */
    public void delete(){
            if(!checkTable.getItems().isEmpty() && checkTable.getItems().size() > 1) {
                products = checkTable.getItems();
                ObservableList<Product> selectedProduct = checkTable.getSelectionModel().getSelectedItems();
                selectedProduct.forEach(products::remove);
            }
            else if(checkTable.getItems().size() == 1){
                products = checkTable.getItems();
                Product selectedItem = checkTable.getSelectionModel().getSelectedItem();
                checkTable.getItems().remove(selectedItem);
                deleteButton.setDisable(true);
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

    public void goToLoading(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Loading.fxml")));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
}
