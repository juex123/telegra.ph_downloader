package my.file.springbootmangadownloader.util;

import java.util.HashSet;
import java.util.Set;

public class FilenameUtils {

    // 一组Windows系统中保留的文件名，不能使用
    private static final Set<String> RESERVED_NAMES = new HashSet<>();
    
    static {
        RESERVED_NAMES.add("CON");
        RESERVED_NAMES.add("PRN");
        RESERVED_NAMES.add("AUX");
        RESERVED_NAMES.add("NUL");
        RESERVED_NAMES.add("COM1");
        RESERVED_NAMES.add("COM2");
        RESERVED_NAMES.add("COM3");
        RESERVED_NAMES.add("COM4");
        RESERVED_NAMES.add("COM5");
        RESERVED_NAMES.add("COM6");
        RESERVED_NAMES.add("COM7");
        RESERVED_NAMES.add("COM8");
        RESERVED_NAMES.add("COM9");
        RESERVED_NAMES.add("LPT1");
        RESERVED_NAMES.add("LPT2");
        RESERVED_NAMES.add("LPT3");
        RESERVED_NAMES.add("LPT4");
        RESERVED_NAMES.add("LPT5");
        RESERVED_NAMES.add("LPT6");
        RESERVED_NAMES.add("LPT7");
        RESERVED_NAMES.add("LPT8");
        RESERVED_NAMES.add("LPT9");
    }

    public static String sanitizeFileName(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "default_filename"; // 提供一个默认文件名
        }

        // 去除非法字符
        String sanitized = filename.replaceAll("[<>:\"/\\\\|?*]", "");

        // 去除文件名末尾的空格和点
        sanitized = sanitized.replaceAll("[\\s.]+$", "");

        // 检查是否为Windows保留名称
        if (RESERVED_NAMES.contains(sanitized.toUpperCase())) {
            sanitized = "_" + sanitized; // 简单地加上下划线前缀
        }

        // 限制文件名长度
        int maxLength = 255;
        if (sanitized.length() > maxLength) {
            sanitized = sanitized.substring(0, maxLength);
        }

        return sanitized.isEmpty() ? "default_filename" : sanitized;
    }

}
