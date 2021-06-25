package Model;

public class OrderList {
    private String currentaddress, phone, status, totalprice, orderId;

    public OrderList() {
    }

    public OrderList(String currentaddress, String phone, String status, String totalprice, String orderId) {
        this.currentaddress = currentaddress;
        this.phone = phone;
        this.status = status;
        this.totalprice = totalprice;
        this.orderId = orderId;
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
}
