package my.file.springbootmangadownloader.util;

import java.io.File;

public class ImageRenamer {
    private String directory;
    private int n;

    public ImageRenamer(String directory, int n) {
        this.directory = directory;
        this.n = n;
    }

    public void renameImages() {
        File dir = new File(directory);
        if (!dir.isDirectory()) {
            System.out.println("指定的路径不是文件夹");
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            System.out.println("无法读取文件夹内容");
            return;
        }

        for (File file : files) {
            String filename = file.getName();
            String extension = "";

            int dotIndex = filename.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = filename.substring(dotIndex); // 包括点在内
            }

            String baseName = filename.substring(0, dotIndex);

            if (isFourDigitNumber(baseName)) {
                try {
                    int newNumber = Integer.parseInt(baseName) + n;
                    String newFilename = String.format("%04d%s", newNumber, extension);
                    File newFile = new File(directory, newFilename);

                    if (file.renameTo(newFile)) {
                        System.out.println("重命名成功: " + filename + " -> " + newFilename);
                    } else {
                        System.out.println("重命名失败: " + filename);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("文件名格式错误: " + baseName);
                }
            } else {
                System.out.println("跳过非四位数字命名的文件: " + filename);
            }
        }
    }

    private boolean isFourDigitNumber(String s) {
        return s.matches("\\d{4}");
    }

    // 示例使用
    public static void main(String[] args) {
        String directoryPath = "E:\\test\\あなたが望むなら（若这是你所期望的）【1-6】  451 - 563";
        int n = 450; // 要增加的数字量
        ImageRenamer renamer = new ImageRenamer(directoryPath, n);
        renamer.renameImages();
    }
}
