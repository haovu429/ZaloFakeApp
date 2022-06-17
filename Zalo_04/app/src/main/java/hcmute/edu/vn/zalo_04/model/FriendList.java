package hcmute.edu.vn.zalo_04.model;

public class FriendList {
    private String id; //id những tài khoản đã là bạn bè của một tài khoản người dùng

    public FriendList(String id) {
        this.id = id;
    }

    public FriendList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
