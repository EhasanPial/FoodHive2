package Model;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderList implements Parcelable {
    private String currentaddress, phone, status, totalprice, orderId, uid, deliverytype;

    public OrderList() {
    }

    public OrderList(String currentaddress, String phone, String status, String totalprice, String orderId, String uid, String deliverytype) {
        this.currentaddress = currentaddress;
        this.phone = phone;
        this.status = status;
        this.totalprice = totalprice;
        this.orderId = orderId;
        this.uid = uid;
        this.deliverytype = deliverytype ;
    }

    protected OrderList(Parcel in) {
        currentaddress = in.readString();
        phone = in.readString();
        status = in.readString();
        totalprice = in.readString();
        orderId = in.readString();
        uid = in.readString() ;
        deliverytype = in.readString() ;
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
    }
}
