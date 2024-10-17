package my.file.springbootmangadownloader.controller;

import my.file.springbootmangadownloader.controller.thread.UrlProcesser;
import my.file.springbootmangadownloader.serivice.MangaDownLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ClassName: MangaDownLoaderController
 * Package: my.file.springbootmangadownloader.controller
 * Description:
 *
 * @Author JueX
 * @Create 2024/10/17 10:58
 * @Version 1.0
 */
@RestController
@RequestMapping("/mangadownloader")
public class MangaDownLoaderController {

    @Autowired
    private static MangaDownLoaderService mangaDownLoaderService;

    private static final BlockingQueue<String> urlQueue = new LinkedBlockingQueue<>();

    static {
        new Thread(new UrlProcesser(urlQueue)).start();
    }

    @PostMapping("/addUrls")
    public void mangaDownloader(@RequestBody String text) {
        String[] urls = text.split(";");
        for (String url : urls) {
            try {
                urlQueue.put(url);
                System.out.println("put : " + url);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
