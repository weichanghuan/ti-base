package test;

import io.utils.okhttp.utils.OkHttpSimpleUtils;
import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;

/**
 * @Author: wch
 * @Description:
 * @Date: 2020/1/17 4:29 PM
 */

public class HttpTest {
    public static void main(String[] args) throws Exception {

        new OkHttpSimpleUtils().doGetAsyn("http://localhost:4567/hello",new OkHttpSimpleUtils.CustomNetCall(){
            @Override
            public void success(Call call, Response response) throws IOException {
                System.out.println("http code="+response.code());
                System.out.println("response="+response.body().string());
                response.body().close();
                System.out.println("success");
            }

            @Override
            public void failed(Call call, IOException e) {
                System.out.println("failed");
            }
        });

    }

}
