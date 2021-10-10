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
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
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
    private TableView<Item> selectTable, checkTable;


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
//        choiceBox.setOnAction(this::getFood);
//        String sortBy = choiceBox.getValue();
    }

    //for test
    public void getSort(ActionEvent event){
        String mysort = choiceBox.getValue();
        sLabel.setText(mysort);
    }

    //Get all items
    public ObservableList<Item> getitems(){
        ObservableList<Item> items = FXCollections.observableArrayList();
        items.add(new Item(1,1,1));
        return items;
    }

    public void search(ActionEvent event){
        if(keywords != null) {
            text = keywords.getText();
//        System.out.println(text);
        }
    }

    public void add(){

    }

    public void delete(){
        ObservableList<Item> selectedItem, allItems;
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
