package utils;

import okhttp3.OkHttpClient;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * ClassName: ProxyUtil
 * Package: utils
 * Description:
 *
 * @Author JueX
 * @Create 2024/10/16 1:07
 * @Version 1.0
 */
public class ProxyUtil {// 设置代理服务器信息

    private static String proxyHost = "localhost";
    private static int proxyPort = 10809;

    public static OkHttpClient getOkHttpClient() {

        // 创建代理对象
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

        // 创建 OkHttpClient 并设置代理
        return new OkHttpClient.Builder()
                .proxy(proxy)
                .build();
    }

}
