package Model;

public class UsersModel {
    private String name, email, pass, address, phone, totalOrders, uid;

    public UsersModel() {
    }

    public UsersModel(String name, String email, String pass, String address, String phone, String uid) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.address = address;
        this.phone = phone;
        this.uid = uid;
    }

    public UsersModel(String name, String email, String pass, String address, String phone, String totalOrders, String uid) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.address = address;
        this.phone = phone;
        this.totalOrders = totalOrders;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTotalOrders() {
        return totalOrders;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTotalOrders(String totalOrders) {
        this.totalOrders = totalOrders;
    }
}
