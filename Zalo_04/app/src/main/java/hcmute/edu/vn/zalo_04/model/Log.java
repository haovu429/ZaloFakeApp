package hcmute.edu.vn.zalo_04.model;

//Chức năng ghi lỗi hệ thống (chưa thực hiên hoàn thiện)
public class Log {
    private String tag; //Nhãn lỗi ghi trong hệ thống
    private String error; //Nội dụng lỗi
    private String time; //Thời gian xảy ra lỗi

    public Log(String tag, String error, String time) {
        this.tag = tag;
        this.error = error;
        this.time = time;
    }

    public Log() {
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
