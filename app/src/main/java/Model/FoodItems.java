package Model;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodItems implements Parcelable{
    private String name, price, imguri, type, itemkey, time, des, rating;
    private float floatrating ;
    private String istop ;


    public FoodItems() {
    }

    public FoodItems(String name, String price, String imguri, String type, String itemkey, String time, String des, String rating,  String istop ) {
        this.name = name;
        this.price = price;
        this.imguri = imguri;
        this.type = type;
        this.itemkey = itemkey;
        this.time = time;
        this.des = des;
        this.rating = rating;
        this.istop = istop;
    }

    public FoodItems(String name, String price, String imguri, String type, String itemkey, String time, String des, String rating, float floatRating,  String istop ) {
        this.name = name;
        this.price = price;
        this.imguri = imguri;
        this.type = type;
        this.itemkey = itemkey;
        this.time = time;
        this.des = des;
        this.rating = rating;
        this.floatrating = floatRating;
        this.istop = istop ;
    }

    protected FoodItems(Parcel in) {
        name = in.readString();
        price = in.readString();
        imguri = in.readString();
        type = in.readString();
        itemkey = in.readString();
        time = in.readString();
        des = in.readString();
        rating = in.readString();
        istop = in.readString() ;
    }

    public static final Creator<FoodItems> CREATOR = new Creator<FoodItems>() {
        @Override
        public FoodItems createFromParcel(Parcel in) {
            return new FoodItems(in);
        }

        @Override
        public FoodItems[] newArray(int size) {
            return new FoodItems[size];
        }
    };

    public float getFloatrating() {
        return floatrating;
    }

    public void setFloatrating(float floatrating) {
        this.floatrating = floatrating;
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

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getIstop() {
        return istop;
    }

    public void setIstop(String istop) {
        this.istop = istop;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(imguri);
        dest.writeString(type);
        dest.writeString(itemkey);
        dest.writeString(time);
        dest.writeString(des);
        dest.writeString(rating);
        dest.writeString(istop);
    }





}
