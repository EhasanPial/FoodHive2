package Model;

public class CartModel {
    private String itemkey, foodname, foodtype, quantity, price;

    public CartModel() {
    }

    public CartModel(String itemkey, String foodname, String foodtype, String quantity, String price) {
        this.itemkey = itemkey;
        this.foodname = foodname;
        this.foodtype = foodtype;
        this.quantity = quantity;
        this.price = price;
    }

    public String getItemkey() {
        return itemkey;
    }

    public void setItemkey(String itemkey) {
        this.itemkey = itemkey;
    }

    public String getFoodname() {
        return foodname;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public String getFoodtype() {
        return foodtype;
    }

    public void setFoodtype(String foodtype) {
        this.foodtype = foodtype;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
