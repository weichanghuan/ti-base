package io.utils.okhttp.core;

import io.utils.okhttp.contract.OkHttpCont;
import okhttp3.OkHttpClient;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wch
 * @Description:
 * @Date: 2020/1/17 2:07 PM
 */
public class HttpCore {

    private volatile static HttpCore mInstance;

    private OkHttpClient mOkHttpClient;

    /**
     * 单例模式获取NetUtils
     *
     * @return
     */
    public static HttpCore getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpCont.LOCKER) {
                if (mInstance == null) {
                    mInstance = new HttpCore();
                }
            }
        }
        return mInstance;
    }


    private HttpCore() {
        OkHttpClient.Builder ClientBuilder = new OkHttpClient.Builder();
        ClientBuilder.readTimeout(OkHttpCont.READ_TIMEOUT, TimeUnit.SECONDS);//读取超时
        ClientBuilder.connectTimeout(OkHttpCont.CONNECT_TIMEOUT, TimeUnit.SECONDS);//连接超时
        ClientBuilder.writeTimeout(OkHttpCont.WRITE_TIMEOUT, TimeUnit.SECONDS);//写入超时
        //支持HTTPS请求，跳过证书验证
        ClientBuilder.sslSocketFactory(createSSLSocketFactory());
        ClientBuilder.hostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        mOkHttpClient = ClientBuilder.build();
    }

    /**
     * 生成安全套接字工厂，用于https请求的证书跳过
     *
     * @return
     */
    public SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new HttpCore.TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }

    /**
     * 用于信任所有证书
     */
    class TrustAllCerts implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public OkHttpClient getmOkHttpClient() {
        return mOkHttpClient;
    }

    public void setmOkHttpClient(OkHttpClient mOkHttpClient) {
        this.mOkHttpClient = mOkHttpClient;
    }
}
