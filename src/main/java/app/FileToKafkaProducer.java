package app;

import constant.Constant;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class FileToKafkaProducer {

    private static final String FILE_PATH = "E:\\test\\urls.txt"; // 文件路径

    /**
     * 主函数入口
     * 该方法配置Kafka生产者，读取文件内容，并将每一行作为消息发送到Kafka主题
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 配置Kafka生产者
        Properties props = new Properties();
        props.put("bootstrap.servers", Constant.KAFKA_BROKER);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 创建Kafka生产者
        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            // 打开文件并逐行读取
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null) {
                    // 创建并发送消息
                    ProducerRecord<String, String> record = new ProducerRecord<>(Constant.KAFKA_TOPIC, Integer.toString(lineCount), line);
                    producer.send(record);
                    lineCount++;
                }
                // 确保所有消息都被发送出去
                producer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
