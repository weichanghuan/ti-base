package io.utils.okhttp.contract;

import okhttp3.MediaType;

/**
 * @Author: wch
 * @Description:
 * @Date: 2020/1/17 1:53 PM
 */
public class OkHttpCont {

    public final static String JSONTYPE = "application/json; charset=utf-8";
    public final static int READ_TIMEOUT = 100;
    public final static int CONNECT_TIMEOUT = 60;
    public final static int WRITE_TIMEOUT = 60;
    public final static MediaType JSON_MEDIATYPE = MediaType.parse(JSONTYPE);
    public static final byte[] LOCKER = new byte[0];


    private OkHttpCont(){}


}
