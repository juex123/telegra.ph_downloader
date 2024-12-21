package my.file.springbootmangadownloader.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SubtitleConverter {

    public static void main(String[] args) {
        String inputDirectoryPath = ""; // 输入的目录路径
        File inputDirectory = new File(inputDirectoryPath);

        // 检查目录是否存在
        if (!inputDirectory.exists() || !inputDirectory.isDirectory()) {
            System.out.println("指定的目录不存在或不是一个目录。");
            return;
        }

        // 获取目录下的所有 TXT 文件
        File[] files = inputDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        // 遍历每个 TXT 文件
        if (files != null) {
            for (File file : files) {
                String outputFilePath = file.getAbsolutePath().replace(".txt", ".lrc"); // 输出文件路径
                convertSubtitle(file.getAbsolutePath(), outputFilePath);
            }
            System.out.println("转换完成。");
        } else {
            System.out.println("没有找到任何 TXT 文件。");
        }
    }

    private static void convertSubtitle(String inputFilePath, String outputFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // 处理时间戳和文本
                if (line.matches("\\d+")) {
                    // 跳过序号行
                    continue;
                } else if (line.matches("\\d{2}:\\d{2}:\\d{2}\\.\\d{3} --> \\d{2}:\\d{2}:\\d{2}\\.\\d{3}")) {
                    // 处理时间戳行
                    String[] times = line.split(" --> ");
                    String startTime = convertToLRCFormat(times[0]);
                    // 读取下一行作为字幕文本
                    String subtitleText = reader.readLine();
                    if (subtitleText != null) {
                        writer.write("[" + startTime + "]" + subtitleText);
                        writer.newLine();
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("处理文件 " + inputFilePath + " 时出错: " + e.getMessage());
        }
    }

    private static String convertToLRCFormat(String time) {
        // 将时间从 "HH:MM:SS.mmm" 转换为 "MM:SS.mm"
        String[] parts = time.split(":");
        String minutes = parts[1];
        String secondsAndMillis = parts[2].split("\\.")[0] + "." + parts[2].substring(0, 2); // 取前两位作为毫秒
        return minutes + ":" + secondsAndMillis;
    }
}
