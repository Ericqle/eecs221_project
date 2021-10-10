package project1_0;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage Stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Selecting.fxml"));
            Scene scene = new Scene(root);
            Stage.setScene(scene);
            Stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

