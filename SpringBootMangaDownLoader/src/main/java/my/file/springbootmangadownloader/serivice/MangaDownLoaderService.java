package my.file.springbootmangadownloader.serivice;

import java.util.concurrent.BlockingQueue;

/**
 * ClassName: MangaDownLoaderService
 * Package: my.file.springbootmangadownloader.serivice
 * Description:
 *
 * @Author JueX
 * @Create 2024/10/17 12:43
 * @Version 1.0
 */
public interface MangaDownLoaderService {

    void download(String url);

}
