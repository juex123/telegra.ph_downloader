package my.file.springbootmangadownloader.controller.thread;

import my.file.springbootmangadownloader.serivice.MangaDownLoaderService;
import my.file.springbootmangadownloader.serivice.impl.MangaDownLoaderServiceImpl;

import java.util.concurrent.BlockingQueue;

/**
 * ClassName: UrlProcesser
 * Package: my.file.springbootmangadownloader.controller.thread
 * Description:
 *
 * @Author JueX
 * @Create 2024/10/17 13:04
 * @Version 1.0
 */
public class UrlProcesser implements Runnable{

    private BlockingQueue<String> urlQueue;
    private MangaDownLoaderService mangaDownLoaderService = new MangaDownLoaderServiceImpl();

    public UrlProcesser(BlockingQueue<String> urlQueue) {
        this.urlQueue = urlQueue;
    }

    @Override
    public void run() {
        while (true) {
            String take = null;
            try {
                take = urlQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("正在连接 :" + take);
            mangaDownLoaderService.download(take);
        }
    }
}
