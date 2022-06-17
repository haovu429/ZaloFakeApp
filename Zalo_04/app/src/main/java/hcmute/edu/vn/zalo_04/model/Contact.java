package hcmute.edu.vn.zalo_04.model;

public class Contact {
    private String name; //Tên danh bạ của số điện thoại trong danh bạ điện thoại
    private String phone_number; // Số điện thoại trong danh bạ điện thoại

    public Contact(String name, String phone_number) {
        this.name = name;
        this.phone_number = phone_number;
    }

    public Contact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
