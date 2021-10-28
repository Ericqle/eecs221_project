package project1_0;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Item {

    Integer productID = 0;
    Integer x = 0;
    Integer y = 0;

    public Item(){
        this.productID = 0;
        this.x = 0;
        this.y = 0;
    }

    public Item(Integer id, Integer x, Integer y){
        this.productID = id;
        this.x = x;
        this.y = y;
    }

    public Integer getProductID() {
        return productID;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public void setX(Integer xLocation) {
        this.x = xLocation;
    }

    public void setY(Integer yLocation) {
        this.y = yLocation;
    }

}