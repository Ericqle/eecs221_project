package project1_0;

import javafx.collections.ObservableList;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class GuidingController implements Initializable{
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ProgressBar progressBar;
    BigDecimal progress = new BigDecimal(String.format("%.2f",0.0));
    @FXML
    private Label progressLabel;

    @FXML
    private TableColumn<Map, String> c0, c1, c2,c3,c4,c5,c6,c7, c8,c9, c10, c11, c12,c13,c14,c15,c16,c17, c18,c19, c20, c21, c22,c23,c24,c25,c26,c27, c28,c29, c30, c31, c32,c33,c34,c35,c36,c37, c38,c39, c40;
    private ObservableList<Map> maps;

    @FXML
    private TableView<Map> guideTable;

    LoadingController loadingController = new LoadingController();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if(guideTable!= null && !loadingController.getPath().isEmpty()) {
            try {
                loadingController.readAllItems(loadingController.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadingController.setGraph();
            loadingController.graphToFigure();
            loadingController.createMap(guideTable);
        }
    }


    public void nextAndIncreaseProgress(){
        if(progress.doubleValue() < 1) {
            progress = new BigDecimal(String.format("%.2f",progress.doubleValue()+ 0.1));
            progressBar.setProgress(progress.doubleValue());
            progressLabel.setText(Integer.toString((int) Math.round(progress.doubleValue() * 100)) + "%");
        }
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
