package my.file.springbootmangadownloader.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MergeLinesToOne {

    public static void main(String[] args) {
        String inputFilePath = "D:\\PythonCode\\PythonTest\\DLIMG\\urlre.txt";  // 输入文件路径
        String outputFilePath = "D:\\PythonCode\\PythonTest\\DLIMG\\output.txt"; // 输出文件路径

        mergeLinesToOne(inputFilePath, outputFilePath);
    }

    public static void mergeLinesToOne(String inputFilePath, String outputFilePath) {
        // 使用 StringBuilder 以高效拼接字符串
        StringBuilder mergedLine = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            // 逐行读取文件内容
            while ((line = br.readLine()) != null) {
                // 拼接读取到的行内容，并加上分号
                if (mergedLine.length() > 0) {
                    mergedLine.append(";"); // 只在非空时添加分号
                }
                mergedLine.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 写入拼接后的字符串结果到输出文件
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            writer.write(mergedLine.toString());
            System.out.println("已成功将文件内容合并并输出到: " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
