package hcmute.edu.vn.zalo_04.model;

public class User {

    private String id;
    private String username;
    private String imageURL;
    private String phone_number;
    private String status;
    private String search;


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
