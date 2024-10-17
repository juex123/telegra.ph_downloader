package demo;

import constant.Constant;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Paths;

public class ProxyExample {

    private static void executeTask(String url, String mainDirPath) {
        try {
            // 设置代理服务器信息
            String proxyHost = "localhost";
            int proxyPort = 10809;

            // 创建代理对象
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

            // 创建 OkHttpClient 并设置代理
            OkHttpClient client = new OkHttpClient.Builder()
                    .proxy(proxy)
                    .build();

            // 创建请求
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            // 发送请求并获取响应
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    // 读取响应内容
                    String content = response.body().string();
                    Document doc = Jsoup.parse(content);
//                    String title = parse.title();
                    Elements articles = doc.select("article");
//                    System.out.println(title);
                    for (Element article : articles) {
//                        System.out.println("Article Content: " + article.html());
//                        System.out.println("-------------------------------");
                        Element h1 = article.select("h1").first();
                        if (h1 != null) {
                            System.out.println("Article Title: " + h1.text());
                        } else {
                            System.out.println("No <h1> tag found in the article.");
                        }

                        // 获取所有的 <img> 标签
                        Elements images = article.select("img");
                        for (Element img : images) {
                            String imageUrl = img.absUrl("src");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                downloadImage(imageUrl, mainDirPath);
                            }
                        }
                    }
                } else {
                    System.out.println("Request failed: " + response.code());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadImage(String imageUrl, String saveDirectory) {
        try {
            // 设置代理服务器信息
            String proxyHost = "localhost";
            int proxyPort = 10809;

            // 创建代理对象
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

            // 创建 OkHttpClient 并设置代理
            OkHttpClient client = new OkHttpClient.Builder()
                    .proxy(proxy)
                    .build();

            // 创建请求
            Request request = new Request.Builder()
                    .url(imageUrl)
                    .build();

            // 发送请求并获取响应
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // 获取响应的输入流
                InputStream inputStream = response.body().byteStream();

                // 构建保存路径
                String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
                String savePath = Paths.get(saveDirectory, fileName).toString();

                // 将输入流写入到本地文件
                try (FileOutputStream outputStream = new FileOutputStream(savePath)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                }

                System.out.println("Image downloaded and saved to: " + savePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String url = "https://telegra.ph/ehentai-3088123-c736206f1c-10-15";
        String mainDirPath = Constant.SPECIFIED_FOLDER;
        executeTask(url, mainDirPath);
    }
}
