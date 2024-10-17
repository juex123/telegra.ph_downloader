package utils;

import constant.Constant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * ClassName: KafkaUtil
 * Package: utils
 * Description:
 *
 * @Author JueX
 * @Create 2024/10/15 22:50
 * @Version 1.0
 */
public class KafkaUtil {

    /**
     * 创建并返回一个订阅了特定主题的Kafka消费者
     * 该方法配置了消费者连接的服务器地址、消费者组、自动提交偏移量等信息
     *
     * @return KafkaConsumer<String, String> 配置并订阅了主题的Kafka消费者实例
     */
    public static KafkaConsumer<String, String> getKafkaConsumer() {
        // 初始化Kafka消费者的配置属性
        Properties props = new Properties();

        // 设置Kafka服务器地址
        props.put("bootstrap.servers", "hadoop102:9092");

        // 设置消费者组ID，同一组内的消费者会竞争消费分区，不同组的消费者则各自独立消费
        props.put("group.id", "img_downloader");

        // 启用自动提交偏移量
        props.put("enable.auto.commit", "true");

        // 设置自动提交偏移量的时间间隔
        props.put("auto.commit.interval.ms", "1000");

        // 设置键和值的反序列化器
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        // 设置偏移量重置策略为从最新的位置开始
        props.put("auto.offset.reset", "latest");

        // 使用配置属性创建Kafka消费者实例
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // 订阅主题，这里使用单个主题的列表
        consumer.subscribe(Collections.singletonList(Constant.KAFKA_TOPIC));

        // 返回配置并订阅了主题的Kafka消费者实例
        return consumer;
    }
}
