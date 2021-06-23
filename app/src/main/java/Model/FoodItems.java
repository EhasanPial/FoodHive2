package Model;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodItems implements Parcelable, Comparable<FoodItems> {
    private String name, price, imguri, type, itemkey, time, des, rating;


    public FoodItems() {
    }

    public FoodItems(String name, String price, String imguri, String type, String itemkey, String time, String des, String rating) {
        this.name = name;
        this.price = price;
        this.imguri = imguri;
        this.type = type;
        this.itemkey = itemkey;
        this.time = time;
        this.des = des;
        this.rating = rating;
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

    public Float getFloatRating() {
        Float f = Float.parseFloat(rating);
        if (f != null)
            return f;

        return 0f;
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
    }


    @Override
    public int compareTo(FoodItems o) {

        return this.getFloatRating().compareTo(o.getFloatRating());
    }


}
