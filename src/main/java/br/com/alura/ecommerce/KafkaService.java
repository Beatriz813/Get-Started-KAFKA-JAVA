package br.com.alura.ecommerce;

import br.com.alura.ecommerce.Serializador.GsonDeserializer;
import br.com.alura.ecommerce.interfaces.ConsumerFunction;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {
    private final KafkaConsumer<String, T> consumer;
    private final ConsumerFunction<T> Callback;
    public KafkaService (String groupID, String topico, ConsumerFunction<T> callback, Class<T> type, Map<String, String> props) {
        this.Callback = callback;
        this.consumer = new KafkaConsumer<>(properties(type, groupID, props));
        consumer.subscribe(Collections.singletonList(topico));
    }

    public KafkaService(String groupId, Pattern topico, ConsumerFunction<T> callback, Class<T> type, Map<String, String> props) {
        this.Callback = callback;
        this.consumer = new KafkaConsumer<>(properties(type, groupId, props));
        consumer.subscribe(topico);
    }

    public Properties properties(Class<T> type, String groupId, Map<String, String> props) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());
        properties.putAll(props);
        return properties;
    }

    public  void run() {
        while (true) {
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println("encontrei " + records.count() + " registros");
                for (var record : records) {
                    try {
                        this.Callback.calback(record);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void close() {
        consumer.close();
    }
}
