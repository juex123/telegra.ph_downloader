package utils;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ClassName: FileUtils
 * Package: utils
 * Description:
 *
 * @Author 庄鹏
 * @Create 2024/10/15 23:40
 * @Version 1.0
 */
public class FileUtils {

    private static final int BUFFER_SIZE = 1024;

    /**
     * 创建目录
     * 如果指定路径的目录不存在，则创建该目录
     *
     * @param directoryPath 目录路径
     */
    public static void createDirectory(String directoryPath) {
        // 检查目录路径是否已存在，如果不存在则继续创建
        if (!Files.exists(Path.of(directoryPath))) {
            // 根据目录路径创建File对象
            File directory = new File(directoryPath);
            // 创建目录
            directory.mkdir();
        }
    }


    /**
     * 创建目录（包括多级目录）
     * 如果目录路径不存在，则创建对应的目录
     *
     * @param directoryPath 目录路径，可以是单级或多级目录
     */
    public static void createDirectories(String directoryPath) {
        // 检查目录路径是否存在，如果不存在则创建
        if (!Files.exists(Path.of(directoryPath))) {
            File directory = new File(directoryPath);
            // 创建多级文件夹
            directory.mkdirs();
        }
    }

    /**
     * 将指定路径的文件夹打包为 ZIP 文件
     *
     * @param mangaPath 要打包的文件夹路径
     */
    public static void zip(String mangaPath) {
        try {
            Path sourceDir = Paths.get(mangaPath);
            Path zipFilePath = Paths.get(mangaPath + ".zip");

            try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
                 ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos))) {

                zipDirectory(sourceDir, sourceDir, zos);
            }

            System.out.println("文件夹已成功压缩为: " + zipFilePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归地将文件夹中的文件和子文件夹添加到 ZIP 输出流中
     *
     * @param currentPath 当前处理的文件或文件夹路径
     * @param sourceDir   源文件夹路径
     * @param zos         ZIP 输出流
     * @throws IOException 如果发生 I/O 错误
     */
    private static void zipDirectory(Path currentPath, Path sourceDir, ZipOutputStream zos) throws IOException {
        Files.walkFileTree(currentPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetFile = sourceDir.relativize(file);
                ZipEntry zipEntry = new ZipEntry(targetFile.toString());
                zos.putNextEntry(zipEntry);

                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file.toFile()))) {
                    byte[] bytes = new byte[BUFFER_SIZE];
                    int length;
                    while ((length = bis.read(bytes)) >= 0) {
                        zos.write(bytes, 0, length);
                    }
                }

                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Path targetDir = sourceDir.relativize(dir);
                ZipEntry zipEntry = new ZipEntry(targetDir.toString() + "/");
                zos.putNextEntry(zipEntry);
                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 删除指定文件夹及其所有内容
     *
     * @param folderPath 要删除的文件夹路径
     * @return 如果删除成功返回 true，否则返回 false
     */
    public static boolean deleteFolder(String folderPath) {
        Path path = Paths.get(folderPath);
        if (!Files.exists(path)) {
            System.out.println("文件夹不存在: " + folderPath);
            return false;
        }

        try {
            deleteDirectory(path);
            System.out.println("文件夹已成功删除: " + folderPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("删除文件夹时发生错误: " + folderPath);
            return false;
        }
    }

    /**
     * 递归地删除文件夹及其所有内容
     *
     * @param path 要删除的文件或文件夹路径
     * @throws IOException 如果发生 I/O 错误
     */
    private static void deleteDirectory(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    deleteDirectory(entry);
                }
            }
        }
        Files.delete(path);
    }


}
