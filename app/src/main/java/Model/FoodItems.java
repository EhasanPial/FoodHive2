package Model;

public class FoodItems {
    private String name, price, imguri, type, itemkey, time;

    public FoodItems() {
    }

    public FoodItems(String name, String price, String imguri, String type, String itemkey, String time) {
        this.name = name;
        this.price = price;
        this.imguri = imguri;
        this.type = type;
        this.itemkey = itemkey;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImguri() {
        return imguri;
    }

    public void setImguri(String imguri) {
        this.imguri = imguri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItemkey() {
        return itemkey;
    }

    public void setItemkey(String itemkey) {
        this.itemkey = itemkey;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
