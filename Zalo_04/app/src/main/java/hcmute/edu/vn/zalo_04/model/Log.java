package hcmute.edu.vn.zalo_04.model;

public class Log {
    private String tag;
    private String error;
    private String time;

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
