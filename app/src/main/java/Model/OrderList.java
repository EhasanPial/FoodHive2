package Model;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderList implements Parcelable {
    private String currentaddress, phone, status, totalprice, orderId, uid, deliverytype, username;
    private String timestamp ;
    private long pausetime ;

    public OrderList() {
    }

    public OrderList(String currentaddress, String phone, String status, String totalprice, String orderId, String uid, String deliverytype,String timestamp, long pausetime, String username ) {
        this.currentaddress = currentaddress;
        this.phone = phone;
        this.status = status;
        this.totalprice = totalprice;
        this.orderId = orderId;
        this.uid = uid;
        this.deliverytype = deliverytype ;
        this.timestamp = timestamp ;
        this.pausetime = pausetime ;
        this.username = username ;
    }

    protected OrderList(Parcel in) {
        currentaddress = in.readString();
        phone = in.readString();
        status = in.readString();
        totalprice = in.readString();
        orderId = in.readString();
        uid = in.readString() ;
        deliverytype = in.readString() ;
        timestamp = in.readString() ;
        username = in.readString() ;
        pausetime = in.readLong();
    }

    public static final Creator<OrderList> CREATOR = new Creator<OrderList>() {
        @Override
        public OrderList createFromParcel(Parcel in) {
            return new OrderList(in);
        }

        @Override
        public OrderList[] newArray(int size) {
            return new OrderList[size];
        }
    };

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp ) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCurrentaddress() {
        return currentaddress;
    }

    public void setCurrentaddress(String currentaddress) {
        this.currentaddress = currentaddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(String totalprice) {
        this.totalprice = totalprice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDeliverytype() {
        return deliverytype;
    }

    public void setDeliverytype(String deliverytype) {
        this.deliverytype = deliverytype;
    }

    public long getPausetime() {
        return pausetime;
    }

    public void setPausetime(long pausetime) {
        this.pausetime = pausetime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currentaddress);
        dest.writeString(phone);
        dest.writeString(status);
        dest.writeString(totalprice);
        dest.writeString(orderId);
        dest.writeString(uid);
        dest.writeString(deliverytype);
        dest.writeString(timestamp);
        dest.writeLong(pausetime);
        dest.writeString(username);
    }
}
