package project1_0;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class Main extends Application {


    @Override
    public void start(Stage Stage) throws IOException {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Loading.fxml")));
            Scene scene = new Scene(root);
            Stage.setScene(scene);
            Stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        int ROW = 40;
        int COL = 25;
        /* All items in warehouse list
         */
        ArrayList<Item> allItemsList = new ArrayList<>();

    /* Holds current path between only two vertice
        - takes place of ShortestPath module for future implementation
     */
        ArrayList<Vertex> currentShortestPath = new ArrayList<>();

    /* Abstraction of graph for the warehouse
        - all index-able spaces are considered vertices
     */
        char[][] graph = new char[ROW][COL];
        String[][] printFigure = new String[COL+1][ROW+1];

        String path;


        launch(args);
    }
}

