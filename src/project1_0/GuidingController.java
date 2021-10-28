package project1_0;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class GuidingController implements Initializable{
    private Stage stage;
    private Scene scene;

    @FXML
    ProgressBar progressBar;

    @FXML
    Label progressLabel, hint1, hint2, instruction, currentLocation;

    @FXML
    TableColumn<Map, String> c0, c1, c2,c3,c4,c5,c6,c7, c8,c9, c10,
            c11, c12,c13,c14,c15,c16,c17, c18,c19, c20,
            c21, c22,c23,c24,c25,c26,c27, c28,c29, c30,
            c31, c32,c33,c34,c35,c36,c37, c38,c39, c40;

    @FXML
    TableView<Map> guideTable;


    @FXML
    ImageView imageView;

    ArrayList<String> itemsList = new ArrayList<>();
    String shortestPathOutput;
    BigDecimal progress = new BigDecimal(String.format("%.2f",0.0));
    int id;
    Item temp;

    PrimaryController primaryController;


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        File file = new File("src/project1_0/directions.jpg");
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);
        primaryController = new PrimaryController();
        primaryController.setPrimaryControllor(primaryController);
        String path = primaryController.getPath();
        hint1.setText("The path from your location 'U' to the item '$' is marked with 'P' on the map.");
        hint2.setText("'U' = you | 'X' = shelves/items | '.' = open space");
        if(guideTable!= null && !path.isEmpty()) {
            try {
                primaryController.setAllItemsList();
            } catch (IOException e) {
                e.printStackTrace();
            }
            primaryController.setGraph();
            itemsList = primaryController.getCheckList();
            id = Integer.parseInt(itemsList.get(0));
            primaryController.markItemInGraph(id);
            shortestPathOutput = primaryController.findItemAndCallPath(id);
            instruction.setText(shortestPathOutput);
            temp = primaryController.getItemByID(id);
            currentLocation.setText(" Current productID: "+temp.getProductID()+"\t location: ("+ temp.getX()+","+temp.getY()+") is marked as '$' on the map.");
            primaryController.markPathOnGraph();
            primaryController.graphToFigure();
            primaryController.createMap(guideTable,c0, c1, c2,c3,c4,c5,c6,c7, c8,c9, c10,
                    c11, c12,c13,c14,c15,c16,c17, c18,c19, c20,
                    c21, c22,c23,c24,c25,c26,c27, c28,c29, c30,
                    c31, c32,c33,c34,c35,c36,c37, c38,c39, c40);
            nextAndIncreaseProgress();
            primaryController.unmarkPathOnGraph();
            primaryController.unmarkItemInGraph(id);
        }
    }


    public void nextAndIncreaseProgress(){
        double i = primaryController.getCheckList().size();
        double j = 1/i;
        if(progress.doubleValue() < 1) {
            progress = new BigDecimal(String.format("%.2f",progress.doubleValue()+ j));
            progressBar.setProgress(progress.doubleValue());
            progressLabel.setText(Integer.toString((int) Math.round(progress.doubleValue() * 100)) + "%");
        }
    }

    public void backToSelecting(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Selecting.fxml")));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
