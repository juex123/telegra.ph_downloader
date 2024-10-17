package demo;

import java.io.File;

public class CreateDirectoryExample {
    /**
     * 创建单个文件夹
     * 
     * @param directoryPath 文件夹路径
     * @return 返回是否成功创建文件夹
     */
    public static boolean createDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        // 创建单个文件夹
        return directory.mkdir();
    }

    /**
     * 创建多级文件夹
     * 
     * @param directoryPath 文件夹路径
     * @return 返回是否成功创建文件夹
     */
    public static boolean createDirectories(String directoryPath) {
        File directory = new File(directoryPath);
        // 创建多级文件夹
        return directory.mkdirs();
    }

    public static void main(String[] args) {
//        // 示例：创建单个文件夹
//        boolean isCreatedSingle = createDirectory("C:/example/singleDir");
//        System.out.println("单个文件夹创建成功: " + isCreatedSingle);

        // 示例：创建多级文件夹
        boolean isCreatedMultiple = createDirectories("C:/example/multiDir/subDir");
        System.out.println("多级文件夹创建成功: " + isCreatedMultiple);
    }
}
