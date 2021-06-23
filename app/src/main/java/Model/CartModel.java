package Model;

public class CartModel {
    private String itemkey, foodname, foodtype, quantity;

    public CartModel() {
    }

    public CartModel(String itemkey, String foodname, String foodtype, String quantity) {
        this.itemkey = itemkey;
        this.foodname = foodname;
        this.foodtype = foodtype;
        this.quantity = quantity;
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
}
