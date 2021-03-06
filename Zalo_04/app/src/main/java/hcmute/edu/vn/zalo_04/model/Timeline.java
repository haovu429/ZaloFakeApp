package hcmute.edu.vn.zalo_04.model;

public class Timeline {
    String id; //id người dùng đặt thời gian xoá tài nguyên
    String day_num; // khoảng thời gian số ngày mà người dùng muốn xoá trước thời điểm hiện tại
    String month_num; // khoảng thời gian số tháng mà người dùng muốn xoá trước thời điểm hiện tại
    String year_num; // khoảng thời gian số năm mà người dùng muốn xoá trước thời điểm hiện tại

    public Timeline(String id, String day_num, String month_num, String year_num) {
        this.id = id;
        this.day_num = day_num;
        this.month_num = month_num;
        this.year_num = year_num;
    }

    public Timeline() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDay_num() {
        return day_num;
    }

    public void setDay_num(String day_num) {
        this.day_num = day_num;
    }

    public String getMonth_num() {
        return month_num;
    }

    public void setMonth_num(String month_num) {
        this.month_num = month_num;
    }

    public String getYear_num() {
        return year_num;
    }

    public void setYear_num(String year_num) {
        this.year_num = year_num;
    }
}
