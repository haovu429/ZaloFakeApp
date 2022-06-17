package hcmute.edu.vn.zalo_04.notifications;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Class này sử dụng cho chức năng thông báo (chưa thực hiện được)
public class Client {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String url){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
            .build();
        }

        return retrofit;
    }
}
