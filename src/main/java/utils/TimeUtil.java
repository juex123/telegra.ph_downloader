package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ClassName: TimeUtil
 * Package: utils
 * Description:
 *
 * @Author JueX
 * @Create 2024/10/15 23:36
 * @Version 1.0
 */
public class TimeUtil {

    /**
     * 获取以当前日期为目录名称的时间字符串
     * 该方法用于生成目录名，以便根据日期对文件进行分类和归档
     *
     * @return 返回格式为"yyyyMMdd"的当前日期字符串
     */
    public static String getTimeForDirName() {
        // 获取当前时间戳
        long currentTimeMillis = System.currentTimeMillis();
        // 创建一个简单日期格式化对象，指定日期格式为"yyyyMMdd"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 将当前时间戳转换为Date对象
        Date currentDate = new Date(currentTimeMillis);
        // 根据指定格式化日期对象，并返回格式化后的日期字符串
        return sdf.format(currentDate);
    }


}
