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
    private TextField keywords;

    @FXML
    private Button deleteButton;

    @FXML
    TableView<Item> checkTable;

    @FXML
    TableView<Item> selectTable;

    @FXML
    private TableColumn<Item, Integer> checkProductID, checkXLocation , checkYLocation ;
    @FXML
    private TableColumn<Item, Integer> selectProductID, selectXLocation, selectYLocation;

    ObservableList<Item> items;
    ArrayList<String> checklist = new ArrayList<>();
    String text;

    PrimaryController primaryController;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        primaryController = new PrimaryController();
        primaryController.setPrimaryControllor(primaryController);
        if(selectTable != null) {
            try {
                initTable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /*
    initialize the tableview, set up the name of each tableColumn
    and input the warehouse's data
     */
    public void initTable() throws IOException {
        selectProductID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        selectXLocation.setCellValueFactory(new PropertyValueFactory<>("x"));
        selectYLocation.setCellValueFactory(new PropertyValueFactory<>("y"));

        checkProductID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        checkXLocation.setCellValueFactory(new PropertyValueFactory<>("x"));
        checkYLocation.setCellValueFactory(new PropertyValueFactory<>("y"));

        primaryController.setAllItemsList();
        primaryController.setProducts(primaryController.getAllItemsList());
        selectTable.setItems(primaryController.getProducts());
    }

    /*
    Search for products according to the keywords
    from the warehouse directory
     */
    public void search() throws NumberFormatException {
        if (keywords != null && !keywords.getText().isEmpty()) {
            text = keywords.getText();
            if (isNumeric(text)) {
                int id = Integer.parseInt(text);

                if (!primaryController.markItemInGraph(id)) {
                    sLabel.setText("The item you are looking for does NOT exist!");
                }else {
                    sLabel.setText("Selecting");
                    ObservableList<Item> temp = items;
                    temp = FXCollections.observableArrayList();
                    temp.add(primaryController.getItemByID(id));
                    selectTable.setItems(temp);
                }
            }
            else {
                sLabel.setText("Please input the valid productID");
            }
        }
        else {
                selectTable.setItems(primaryController.getProducts());
        }
    }

    /*
    Add the selected product to the checking tableview
    */
    public void add() {
        Item selectedItem = selectTable.getSelectionModel().getSelectedItem();
        int flag = 0;
        if (selectedItem == null) {
            sLabel.setText("Please select an item before press the button");
        } else if (!checkTable.getItems().isEmpty() || flag == 0) {
            int i = 0;
            for (Item item : checkTable.getItems()) {
                if (Objects.equals(item.getProductID(), selectedItem.getProductID())) {
                    sLabel.setText("the product is already in the checkTable");
                    i = 1;
                }
            }
            if (i == 0) {
                checkTable.getItems().add(selectedItem);
                sLabel.setText("Added successfully");
                flag = 1;
                deleteButton.setDisable(false);
            }
        }
        else if (checkTable.getItems().isEmpty()) {
            deleteButton.setDisable(true);
            sLabel.setText("deleteButton is disable");
        }
    }

    /*
    delete the selected product from the checking tableview
     */
    public void delete(){
        if(checkTable.getSelectionModel().getSelectedItem() == null){
            cLabel.setText("Please select an item before press the button");
        }
        else if(!checkTable.getItems().isEmpty() && checkTable.getItems().size() > 1) {
                items = checkTable.getItems();
                ObservableList<Item> selectedItem = checkTable.getSelectionModel().getSelectedItems();
                selectedItem.forEach(items::remove);
                cLabel.setText("Deleted successfully");
            }
            else if(checkTable.getItems().size() == 1){
                items = checkTable.getItems();
                Item selectedItem = checkTable.getSelectionModel().getSelectedItem();
                checkTable.getItems().remove(selectedItem);
                deleteButton.setDisable(true);
                cLabel.setText("Deleted successfully");
            }
    }

    /*
    switch scene to Guiding view
    */
    public void goToGuiding(ActionEvent event) throws IOException {
        if(!checkTable.getItems().isEmpty()) {
            setCheckList();
            primaryController.setChecklist(checklist);
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Guiding.fxml")));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        else{
            cLabel.setText("There is nothing in the checklist!");
        }
    }

    /*
    switch scene to Loading view
    */
    public void backToLoading(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Loading.fxml")));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /*
    check if the inputs are numeric
     */
    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    /*
    store all the items from checktable into checklist
     */
    void setCheckList(){
        for(Item item :checkTable.getItems()){
            checklist.add(item.getProductID().toString());
        }
    }

}
