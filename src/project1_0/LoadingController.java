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

    @FXML
    private TableView<Map> loadTable;

    @FXML
    private TextField loadTextField;

    @FXML
    Label loadLabel, hint1, hint2;

    @FXML
    TableColumn<Map, String> c0, c1, c2,c3,c4,c5,c6,c7, c8,c9, c10, c11, c12,c13,c14,c15,c16,c17, c18,c19, c20, c21, c22,c23,c24,c25,c26,c27, c28,c29, c30, c31, c32,c33,c34,c35,c36,c37, c38,c39, c40;


    PrimaryController primaryController;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        primaryController = new PrimaryController();
        String path = primaryController.getPath();
        primaryController.setPrimaryControllor(primaryController);
        if(loadTable!= null || path !=null ||!path.endsWith("txt")) {
            path = loadTextField.getText();
            primaryController.setPath(path);
            primaryController.setGraph();
            primaryController.graphToFigure();
            primaryController.createMap(loadTable,c0, c1, c2,c3,c4,c5,c6,c7, c8,c9, c10,
                    c11, c12,c13,c14,c15,c16,c17, c18,c19, c20,
                    c21, c22,c23,c24,c25,c26,c27, c28,c29, c30,
                    c31, c32,c33,c34,c35,c36,c37, c38,c39, c40);
            hint1.setText("Here is a layout of the warehouse without items/shelves");
            hint2.setText("'U' = you | '.' = open space");
        }
    }


    @FXML
    void Load(ActionEvent event) throws IOException {
        if(loadTextField.getText().endsWith("txt")) {
            loadLabel.setText("Loading");
            primaryController.setPath(loadTextField.getText());
            if(!primaryController.checkFile()){
                loadLabel.setText("Please input the path of correct txt file:");
                return;
            }
            primaryController.setAllItemsList();
            primaryController.setGraph();
            primaryController.graphToFigure();
            primaryController.createMap(loadTable,c0, c1, c2,c3,c4,c5,c6,c7, c8,c9, c10,
                    c11, c12,c13,c14,c15,c16,c17, c18,c19, c20,
                    c21, c22,c23,c24,c25,c26,c27, c28,c29, c30,
                    c31, c32,c33,c34,c35,c36,c37, c38,c39, c40);
            hint1.setText("Here is a layout of the warehouse with the loaded data ");
            hint2.setText("'U' = you | 'X' = shelves/items | '.' = open space");
        }
        else{
            loadLabel.setText("Please input the valid path of the txt file:");
        }
    }

    @FXML
    void gotoSelecting(ActionEvent event) throws IOException {
        if(loadTextField.getText().endsWith("txt")) {
        Parent root = FXMLLoader.load(getClass().getResource("Selecting.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
        else{
            loadLabel.setText("Please input the txt file before you go to the next page");
        }
    }
}
