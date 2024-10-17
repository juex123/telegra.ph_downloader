package app;

import constant.Constant;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.FileUtils;
import utils.KafkaUtil;
import utils.ProxyUtil;
import utils.TimeUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * ClassName: ImgDownLoader
 * Package: app
 * Description:
 *
 * @Author JueX
 * @Create 2024/10/15 23:29
 * @Version 1.0
 */
public class ImgDownLoader {

    private static OkHttpClient client;

    public static void main(String[] args) {

        client = ProxyUtil.getOkHttpClient();

        String timeForDirName = TimeUtil.getTimeForDirName();
        String mainDirPath = Paths.get(Constant.SPECIFIED_FOLDER, timeForDirName).toString();
        FileUtils.createDirectories(mainDirPath);

        KafkaConsumer<String, String> kafkaConsumer = KafkaUtil.getKafkaConsumer();

        while (true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                executeTask(consumerRecord.value(), mainDirPath);
            }
        }
    }

    /**
     * 执行任务，根据给定的URL下载漫画图片
     * 该方法首先发送HTTP请求获取漫画页面的内容，然后解析页面中的漫画图片URL，并下载这些图片到指定的目录
     *
     * @param url 漫画页面的URL
     * @param mainDirPath 保存漫画图片的主要目录路径
     */
    private static void executeTask(String url, String mainDirPath) {
        try {
            // 构建HTTP请求
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            // 发送请求并处理响应
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    // 解析响应内容
                    String content = response.body().string();
                    Document doc = Jsoup.parse(content);
                    Elements articles = doc.select("article");
                    for (Element article : articles) {
                        // 查找并处理每个漫画文章的标题
                        Element h1 = article.select("h1").first();
                        String mangaPath = "";
                        if (h1 != null) {
                            System.out.println("开始下载: " + h1.text());
                            mangaPath = Paths.get(mainDirPath, h1.text()).toString();
                            FileUtils.createDirectory(mangaPath);
                        } else {
                            System.out.println("没有找到 <h1> tag");
                        }
                        // 查找并处理每个漫画文章的图片
                        Elements images = article.select("img");
                        int currentIndex = 0;
                        for (Element img : images) {
                            int size = images.size();
                            currentIndex++;
                            String imageUrl = img.absUrl("src");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                downloadImage(imageUrl, mangaPath, currentIndex, size);
                            }
                        }
                        System.out.println(h1.text() + " 下载完成");
                        FileUtils.zip(mangaPath);
                        FileUtils.deleteFolder(mangaPath);
                    }
                } else {
                    System.out.println("Request failed: " + response.code());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 根据指定的URL下载图片并保存到指定目录
     *
     * @param imageUrl 图片的URL地址
     * @param saveDirectory 图片保存的目录
     * @param currentIndex 当前下载的图片索引，用于显示下载进度
     * @param size 图片总数量，用于显示下载进度
     */
    private static void downloadImage(String imageUrl, String saveDirectory, int currentIndex, int size) {
        try {
            // 构建HTTP请求对象
            Request request = new Request.Builder()
                    .url(imageUrl)
                    .build();
            // 发送HTTP请求并获取响应
            try (Response response = client.newCall(request).execute()) {
                // 检查HTTP响应状态码
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                // 获取响应体的字节流
                InputStream inputStream = response.body().byteStream();
                // 从URL中提取图片文件名
                String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
                // 构建图片保存的完整路径
                String savePath = Paths.get(saveDirectory, fileName).toString();
                // 将字节流写入文件
                try (FileOutputStream outputStream = new FileOutputStream(savePath)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                }
                // 打印下载进度和保存路径
                System.out.println("正在下载第" + currentIndex + "张，共" + size + "张，图片已保存到：" + savePath);
            }
        } catch (IOException e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }

}
