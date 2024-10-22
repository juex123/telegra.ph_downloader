package my.file.springbootmangadownloader.demo;

import my.file.springbootmangadownloader.util.ProxyUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class AsyncImageDownloader {

    private static final OkHttpClient httpClient = ProxyUtil.getOkHttpClient();

    public static void main(String[] args) throws Exception {
        String url = "https://telegra.ph/%E5%8D%B0%E5%BA%A6%E3%82%AB%E3%83%AA%E3%83%BC-%E7%97%B4%E5%A5%B3%E5%B0%82%E7%94%A8%E8%BB%8ABitch-Only-%E7%89%B9%E8%A3%9D%E7%89%88-%E4%B8%AD%E5%9B%BD%E7%BF%BB%E8%A8%B3-DL%E7%89%88-08-07";  // 替换为目标 URL

        // 使用 Jsoup 解析 HTML 文档
        Document doc = Jsoup.connect(url).get();

        // 获取网页标题并创建有效的文件夹名称
        String title = getValidFilename(doc.title());
        File downloadDir = new File(title);
        if (!downloadDir.exists()) {
            downloadDir.mkdir();
        }

        // 提取所有图片的 URL
        Elements images = doc.select("img");
        List<String> imgUrls = images.stream()
                .map(img -> img.absUrl("src"))
                .collect(Collectors.toList());

        // 使用 CountDownLatch 同步所有异步下载任务
        CountDownLatch latch = new CountDownLatch(imgUrls.size());

        // 下载每一张图片
        for (String imgUrl : imgUrls) {
            downloadImage(imgUrl, title, latch);
        }

        // 等待所有图片下载完成
        latch.await();
        System.out.println("All images downloaded!");
    }

    private static String getValidFilename(String title) {
        return title.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private static void downloadImage(String imgUrl, String directory, CountDownLatch latch) {
        Request request = new Request.Builder().url(imgUrl).build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("Failed to download image: " + imgUrl + " - " + e.getMessage());
                latch.countDown();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        InputStream inputStream = body.byteStream();
                        String fileName = Paths.get(new URI(imgUrl).getPath()).getFileName().toString();  // 使用 URI 解析文件名
                        File file = new File(directory, fileName);
                        try (FileOutputStream outputStream = new FileOutputStream(file)) {
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                        }
                        System.out.println("Downloaded: " + file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    System.err.println("Error saving image: " + imgUrl + " - " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            }
        });
    }
}
