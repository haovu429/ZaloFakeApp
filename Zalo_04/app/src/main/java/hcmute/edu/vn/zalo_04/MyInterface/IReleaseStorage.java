package hcmute.edu.vn.zalo_04.MyInterface;

public interface IReleaseStorage {
    //Do khi lấy dữ liệu thời gian xoá không được đồng bộ thời gian với hành động xoá tin nhắn, gây ra lỗi ứng dụng
    //Nên khi ràng buôc chỉ khi nào lấy được timeline thì ứng dụng mới bắt đầu load, xoá tin nhắn
    void getTimelineOK();

    //Hàm sẽ thưc thi khi có tín hiệu đã xoá tài nguyên thành công
    void releaseFinish();
}
