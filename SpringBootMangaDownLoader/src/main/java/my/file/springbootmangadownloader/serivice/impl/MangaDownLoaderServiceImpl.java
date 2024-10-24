package my.file.springbootmangadownloader.serivice.impl;

import my.file.springbootmangadownloader.constant.Constant;
import my.file.springbootmangadownloader.serivice.MangaDownLoaderService;
import my.file.springbootmangadownloader.util.FileUtils;
import my.file.springbootmangadownloader.util.FilenameUtils;
import my.file.springbootmangadownloader.util.ProxyUtil;
import my.file.springbootmangadownloader.util.TimeUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.*;

/**
 * ClassName: MangaDownLoaderServiceImpl
 * Package: my.file.springbootmangadownloader.serivice.impl
 * Description:
 *
 * @Author JueX
 * @Create 2024/10/17 12:43
 * @Version 1.0
 */
@Service
public class MangaDownLoaderServiceImpl implements MangaDownLoaderService {

    private static OkHttpClient client;

    @Override
    public void download(String url) {
        client = ProxyUtil.getOkHttpClient();

        String timeForDirName = TimeUtil.getTimeForDirName();
        String mainDirPath = Paths.get(Constant.SPECIFIED_FOLDER, timeForDirName).toString();
        FileUtils.createDirectories(mainDirPath);

        executeTask(url, mainDirPath);
    }

    private static final int THREAD_POOL_SIZE = 5;
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

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

                    // 获取页面标题并创建下载目录
                    String title = doc.title().replace(" – Telegraph", "");
                    String folderName = FilenameUtils.sanitizeFileName(title); // 确保文件名合法
                    System.out.println("开始下载: " + folderName);
                    String folderPath = Paths.get(mainDirPath, folderName).toString();
                    FileUtils.createDirectory(folderPath);

                    // 查找所有图片，无论嵌套多深
                    Elements images = doc.select("img");
                    int size = images.size();
                    System.out.println("size = " + size);
                    CountDownLatch latch = new CountDownLatch(size);

                    // 下载每张图片
                    for (Element img : images) {
//                        String imageUrl = "https://telegra.ph/" + img.attr("src");  // 使用 absUrl 获取绝对路径
//                        String imageUrl = img.attr("src");  // 使用 absUrl 获取绝对路径

                        String imageUrl;
                        if (img.attr("src").startsWith("http")) {
                            imageUrl = img.attr("src");
                        } else {
                            imageUrl = "https://telegra.ph/" + img.attr("src");
                        }

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            final int index = images.indexOf(img) + 1;  // 确保序号一致
                            executor.submit(() -> {
                                downloadImage(imageUrl, folderPath, index, size);
                                latch.countDown(); // 每个任务完成后减一
                            });
                        } else {
                            latch.countDown();
                        }
                    }

                    try {
                        // 等待所有任务完成
                        latch.await();
                        System.out.println(folderName + " 下载完成");
                        FileUtils.zip(folderPath);
                        FileUtils.deleteFolder(folderPath);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("下载过程中被中断");
                    }
                } else {
                    System.out.println("请求失败: " + response.code());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 关闭线程池执行器
     * 本方法确保在优雅关闭线程池时，所有任务有足够的时间完成，
     * 如果任务在指定的时间内未完成，则会强制关闭线程池
     */
    private static void shutdownExecutor() {
        // 关闭线程池，不再接受新任务，但会继续执行已提交的任务
        executor.shutdown();
        try {
            // 等待线程池在60秒内终止
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                // 如果线程池未能在60秒内终止，则强制关闭所有线程
                executor.shutdownNow();
                // 再次尝试终止线程池
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    // 如果线程池仍然未能终止，输出错误信息
                    System.err.println("Executor did not terminate");
                }
            }
        } catch (InterruptedException e) {
            // 如果线程在等待终止时被中断，则强制关闭所有线程
            executor.shutdownNow();
            // 重新设置当前线程的中断状态，以便外部知道该线程被中断过
            Thread.currentThread().interrupt();
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
                // 将文件重命名为4位数的数字，不足前面补零
                String fileName = String.format("%04d.jpg", currentIndex);
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
                System.out.println("正在下载第" + currentIndex + "页，共" + size + "页，图片已保存为：" + savePath);
            }
        } catch (IOException e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }
}
