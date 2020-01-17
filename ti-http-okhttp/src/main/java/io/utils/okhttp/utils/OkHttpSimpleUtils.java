package io.utils.okhttp.utils;


import com.alibaba.fastjson.JSON;
import io.utils.okhttp.contract.OkHttpCont;
import io.utils.okhttp.core.HttpCore;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @ClassName OkHttpSimpleUtils
 * @Description
 * @Author Wch
 * @Date 2019-03-26 15:16
 **/
public class OkHttpSimpleUtils extends BaseOKHttpUtils {

    /**
     * doGet请求，同步方式，获取网络数据，是在主线程中执行的，需要新起线程，将其放到子线程中执行
     *
     * @param url
     * @return
     */
    public <T> T doGet(String url, Class<T> clazz) throws Exception {
        return doGet(url, null, clazz);
    }

    /**
     * doGet请求，同步方式，获取网络数据，是在主线程中执行的，需要新起线程，将其放到子线程中执行
     *
     * @param url     请求路径
     * @param headers 请求头
     * @return * @throws Exception
     */
    public <T> T doGet(String url, Map<String, String> headers, Class<T> clazz) throws Exception {
        Response response = null;
        //1 构造Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).headers(addHeaders(headers)).build();
        //2 将Request封装为Call
        Call call = getClient().newCall(request);
        //3 执行Call，得到response
        response = call.execute();
        return getResponse(response, clazz);
    }

    /**
     * doGet请求，同步方式，获取网络数据，是在主线程中执行的，需要新起线程，将其放到子线程中执行
     *
     * @param url
     * @return
     */
    public String doGet(String url) throws Exception {
        Map<String, String> headers = null;
        return doGet(url, headers);
    }

    /**
     * doGet请求，同步方式，获取网络数据，是在主线程中执行的，需要新起线程，将其放到子线程中执行
     *
     * @param url
     * @return
     */
    public String doGet(String url, Map<String, String> headers) throws Exception {
        Response response = null;
        //1 构造Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).headers(addHeaders(headers)).build();
        //2 将Request封装为Call
        Call call = getClient().newCall(request);
        //3 执行Call，得到response
        response = call.execute();

        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            String result = body.string();
            body.close();
            return result;
        }
        String ex = response.toString();
        response.close();
        throw new IOException("Unexpected code " + ex);
    }


    /**
     * doPost请求，同步方式，
     *
     * @param url    请求路径
     * @param object 请求对象
     * @return * @throws Exception
     */
    public <T> T doPost(String url, Object object, Class<T> clazz) throws Exception {
        Response response = null;
        return doPostJson(url, JSON.toJSONString(object), clazz);
    }

    /**
     * doPost请求，同步方式，
     *
     * @param url     请求路径
     * @param object  请求对象
     * @param headers 请求头
     * @return
     * @throws Exception
     */
    public <T> T doPost(String url, Object object, Class<T> clazz, Map<String, String> headers) throws Exception {
        Response response = null;
        return doPostJson(url, JSON.toJSONString(object), headers, clazz);
    }

    /**
     * doPost请求，同步方式，（表单提交）
     *
     * @param url        请求路径
     * @param bodyParams 请求KV
     * @param headers    请求头
     * @return
     * @throws Exception
     */
    public <T> T doPost(String url, Map<String, String> bodyParams, Map<String, String> headers, Class<T> clazz) throws Exception {
        //1构造RequestBody
        RequestBody body = setRequestBody(bodyParams);
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(body).url(url).headers(addHeaders(headers)).build();
        //3 将Request封装为Call
        Call call = getClient().newCall(request);
        //4 执行Call，得到response
        Response response = null;
        response = call.execute();
        return getResponse(response, clazz);
    }

    /**
     * doGet请求，异步方式，获取网络数据，是在子线程中执行的，需要切换到主线程才能更新UI
     *
     * @param url       请求路径
     * @param CustomNetCall 回调函数
     * @return
     * @throws Exception
     */
    public void doGetAsyn(String url, final CustomNetCall CustomNetCall) throws Exception {
        doGetAsyn(url, null, CustomNetCall);
    }

    /**
     * doGet请求，异步方式，获取网络数据，是在子线程中执行的，需要切换到主线程才能更新UI
     *
     * @param url       请求路径
     * @param headers   请求头
     * @param CustomNetCall 回调函数
     * @return
     * @throws Exception
     */
    public void doGetAsyn(String url, Map<String, String> headers, final CustomNetCall CustomNetCall) throws Exception {
        //1 构造Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).headers(addHeaders(headers)).build();
        //2 将Request封装为Call
        Call call = getClient().newCall(request);
        //3 执行Call
        call.enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                CustomNetCall.failed(call, e);
            }

            public void onResponse(Call call, Response response) throws IOException {
                CustomNetCall.success(call, response);

            }
        });
    }

    /**
     * doPost请求，异步方式，
     *
     * @param url       请求路径
     * @param object    请求实体
     * @param CustomNetCall 回调函数
     * @throws Exception
     */
    public void doPostAsyn(String url, Object object, final CustomNetCall CustomNetCall) throws Exception {
        doPostJsonAysn(url, JSON.toJSONString(object), CustomNetCall);
    }

    /**
     * doPost请求，异步方式，
     *
     * @param url       请求路径
     * @param object    请求实体
     * @param headers   请求头
     * @param CustomNetCall 回调函数
     * @throws Exception
     */
    public void doPostAsyn(String url, Object object, Map<String, String> headers, final CustomNetCall CustomNetCall) throws Exception {
        doPostJsonAysn(url, JSON.toJSONString(object), CustomNetCall, headers);
    }

    /**
     * doPost请求，异步方式，
     *
     * @param url        请求路径
     * @param bodyParams 请求KV
     * @param headers    请求头
     * @param CustomNetCall  回调函数
     * @throws Exception
     */
    public void doPostAsyn(String url, Map<String, String> bodyParams, Map<String, String> headers, final CustomNetCall CustomNetCall) throws Exception {
        //1构造RequestBody
        RequestBody body = setRequestBody(bodyParams);
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(body).url(url).headers(addHeaders(headers)).build();
        //3 将Request封装为Call
        Call call = getClient().newCall(request);
        //4 执行Call
        call.enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                CustomNetCall.failed(call, e);
            }

            public void onResponse(Call call, Response response) throws IOException {
                CustomNetCall.success(call, response);

            }
        });
    }

    /**
     * doPost请求，同步方式，
     *
     * @param url     请求路径
     * @param json    请求json
     * @param headers 请求头
     * @return
     * @throws IOException
     */
    public <T> T doPostJson(String url, String json, Map<String, String> headers, Class<T> clazz) throws Exception {
        RequestBody body = RequestBody.create(OkHttpCont.JSON_MEDIATYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .headers(addHeaders(headers))
                .post(body)
                .build();
        Response response = getClient().newCall(request).execute();
        return getResponse(response, clazz);
    }

    /**
     * doPost请求，同步方式
     *
     * @param url  请求路径
     * @param json 请求json
     * @return
     * @throws IOException
     */
    public <T> T doPostJson(String url, String json, Class<T> clazz) throws Exception {
        return doPostJson(url, json, null, clazz);
    }

    /**
     * doPost请求，异步方式
     *
     * @param url
     * @param json
     * @param CustomNetCall
     * @param headers
     * @throws IOException
     */
    public void doPostJsonAysn(String url, String json, final CustomNetCall CustomNetCall, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(OkHttpCont.JSON_MEDIATYPE, json);
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(body).url(url).headers(addHeaders(headers)).build();
        //3 将Request封装为Call
        Call call = getClient().newCall(request);
        //4 执行Call
        call.enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                CustomNetCall.failed(call, e);
            }

            public void onResponse(Call call, Response response) throws IOException {
                CustomNetCall.success(call, response);

            }
        });
    }

    /**
     * doPost请求，异步方式
     *
     * @param url
     * @param json
     * @param CustomNetCall
     * @throws IOException
     */
    public void doPostJsonAysn(String url, String json, final CustomNetCall CustomNetCall) throws IOException {
        doPostJsonAysn(url, json, CustomNetCall, null);
    }

    /**
     * doPost的请求参数，构造RequestBody
     *
     * @param BodyParams
     * @return
     */
    private RequestBody setRequestBody(Map<String, String> BodyParams) {
        RequestBody body = null;
        FormBody.Builder formEncodingBuilder = new FormBody.Builder();
        if (BodyParams != null) {
            Iterator<String> iterator = BodyParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                formEncodingBuilder.add(key, BodyParams.get(key));
                getLogger().info("doPost http", "doPost_Params===" + key + "====" + BodyParams.get(key));
            }
        }
        body = formEncodingBuilder.build();
        return body;

    }


    /**
     * 添加header
     *
     * @param header
     * @return
     */
    private Headers addHeaders(Map<String, String> header) {
        if (null == header) {
            header = new HashMap<String, String>();
        }
        return Headers.of(header);

    }

    private <T> T getResponse(Response response, Class<T> clazz) throws Exception {
        if (response != null) {
            ResponseBody body = response.body();
            if (body != null) {
                String result = body.string();
                body.close();
                if (StringUtils.isBlank(result)) {
                    getLogger().info("doPost response is null ");
                    return null;
                }
                T t = JSON.parseObject(result, clazz);
                return t;
            }

        }
        return null;
    }

    /**
     * 自定义网络回调接口
     */
    public interface CustomNetCall {
        void success(Call call, Response response) throws IOException;

        void failed(Call call, IOException e);
    }


    /**
     * @param url 接口地址(无参数)
     * @param map 拼接参数集合
     * @Description get请求URL拼接参数(只拼接参数 ： ( 适用于拼接的参数中没有特殊字符))
     */
    public static String getAppendUrl(String url, Map<String, String> map) {
        if (map != null && !map.isEmpty()) {
            StringBuffer buffer = new StringBuffer();
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                if (StringUtils.isEmpty(buffer.toString())) {
                    buffer.append("?");
                } else {
                    buffer.append("&");
                }
                buffer.append(entry.getKey()).append("=").append(entry.getValue());
            }
            url += buffer.toString();
        }
        return url;
    }

    /**
     * @param url 接口地址(无参数)
     * @param map 拼接参数集合
     * @Description get请求URL拼接参数 & URL编码(拼接参数并且URL编码(适用于拼接参数中有特殊字符  eg:空格 之类的))
     */
    public static String getAppendUrlSpecial(String url, Map<String, String> map) throws Exception {
        StringBuffer buffer = new StringBuffer();
        if (map != null && !map.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                if (StringUtils.isEmpty(buffer.toString())) {
                    buffer.append("?");
                } else {
                    buffer.append("&");
                }
                buffer.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        // return url + URLEncoder.encodeURL(buffer.toString());
        return url + URLEncoder.encode(buffer.toString(), "UTF-8");
    }

    /**
     * doPut请求，同步方式，
     *
     * @param url     请求路径
     * @param object  请求json
     * @param headers 请求头
     * @return
     * @throws IOException
     */
    public <T> T doPut(String url, Object object, Map<String, String> headers, Class<T> clazz) throws Exception {
        RequestBody body = RequestBody.create(OkHttpCont.JSON_MEDIATYPE, JSON.toJSONString(object));
        Request request = new Request.Builder()
                .url(url)
                .headers(addHeaders(headers))
                .put(body)
                .build();
        Response response = getClient().newCall(request).execute();
        return getResponse(response, clazz);
    }

}



