package hcmute.edu.vn.zalo_04.model;

public class User {

    private String id; // id người tài khoản người dùng trong hệ thống
    private String username; //Tên người dùng trong hệ thống
    private String imageURL; //Link ảnh người dùng
    private String phone_number; //Số điện thoại đă đăng ký
    private String status; // Trạng thái người dùng hoạt động
    private String search; // Tên thay thế của người dùng khi search (Vì trên firebase không có chức năng search cả chữ hoa và thường)


    public User(String id, String username, String imageURL, String phone_number, String status, String search) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.phone_number = phone_number;
        this.status = status;
        this.search = search;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
