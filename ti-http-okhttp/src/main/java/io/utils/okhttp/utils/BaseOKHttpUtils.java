package io.utils.okhttp.utils;

import io.utils.okhttp.core.HttpCore;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: wch
 * @Description:
 * @Date: 2020/1/17 2:27 PM
 */
public class BaseOKHttpUtils {

    private Logger logger = LoggerFactory.getLogger(OkHttpSimpleUtils.class);

    private OkHttpClient client= HttpCore.getInstance().getmOkHttpClient();

    public Logger getLogger() {
        return logger;
    }

    public OkHttpClient getClient() {
        return client;
    }
}
