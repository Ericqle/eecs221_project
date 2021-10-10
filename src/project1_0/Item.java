package project1_0;

public class Item {

    private int productID;
    private int xLocation, yLocation;

    public Item(){
        this.productID = 0;
        this.xLocation = 0;
        this.yLocation = 0;
    }

    public Item(int id, int x, int y){
        this.productID = id;
        this.xLocation = x;
        this.yLocation = y;
    }

    public int getProductID() {
        return productID;
    }

    public int getxLocation() {
        return xLocation;
    }

    public int getyLocation() {
        return yLocation;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setxLocation(int xLocation) {
        this.xLocation = xLocation;
    }

    public void setyLocation(int yLocation) {
        this.yLocation = yLocation;
    }
}
