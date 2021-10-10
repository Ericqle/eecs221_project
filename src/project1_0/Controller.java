package project1_0;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private Button scButton, gbButton, searchButton;

    @FXML
    private Label sLabel;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private TextField keywords;

    private String[] sort = {"ProductID", "Categories", "Name"};
    private String text;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        choiceBox.getItems().addAll(sort);
//        choiceBox.setValue("ProductID");
//        choiceBox.setOnAction(this::getFood);
//        String sortBy = choiceBox.getValue();
    }

    public void search(ActionEvent event){
        if(keywords != null) {
            text = keywords.getText();
//        System.out.println(text);
        }
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
