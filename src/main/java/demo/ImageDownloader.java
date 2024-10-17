package demo;

import constant.Constant;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class ImageDownloader {

    private String url;
    private Proxy proxy;

    public ImageDownloader(String url, String proxyHost, int proxyPort) {
        this.url = url;
        if (proxyHost != null && !proxyHost.isEmpty()) {
            this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        }
    }

    public void downloadImages(String downloadFolder) {
        try {
            Document doc;
            if (proxy != null) {
                doc = Jsoup.connect(url).proxy(proxy).get();
            } else {
                doc = Jsoup.connect(url).get();
            }

            Elements imgElements = doc.select("img");
            for (Element imgElement : imgElements) {
                String imgUrl = imgElement.absUrl("src");
                downloadImage(imgUrl, downloadFolder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadImage(String imgUrl, String downloadFolder) {
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) (proxy != null ? url.openConnection(proxy) : url.openConnection());
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000); // 增加连接超时时间到10秒
            connection.setReadTimeout(20000);

            try (InputStream in = connection.getInputStream()) {
                String fileName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
                File outputFile = new File(downloadFolder, fileName);
                try (OutputStream out = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("Downloaded: " + imgUrl);
            }
        } catch (Exception e) {
            System.err.println("Failed to download image: " + imgUrl);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String url = "https://telegra.ph/ehentai-3088123-c736206f1c-10-15";
        String proxyHost = "localhost"; // Replace with your proxy host
        int proxyPort = 10809; // Replace with your proxy port
        String downloadFolder = Constant.SPECIFIED_FOLDER;

        ImageDownloader downloader = new ImageDownloader(url, proxyHost, proxyPort);
        downloader.downloadImages(downloadFolder);
    }
}
