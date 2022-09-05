package br.com.alura.ecommerce;

import br.com.alura.ecommerce.Modelos.Order;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.HashMap;
import java.util.Properties;

public class FraudDetectorService {
    public static void main(String[] args) {
        FraudDetectorService fraudDetectorService = new FraudDetectorService();
        try(KafkaService service = new KafkaService<Order>(FraudDetectorService.class.getName(),
                "ECOMMERCE_NEW_ORDER",
                fraudDetectorService::Callback, Order.class, new HashMap<>())) {
            service.run();
        }
    }

    private void Callback(ConsumerRecord<String, Order> record) {
        System.out.println("==============================================================");
        System.out.println("Precossing new order, checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Order processed");
    }

    public static Properties properties() {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, FraudDetectorService.class.getSimpleName());
        // Configura o consumidor para confirmar (commitar) ao kafka a cada uma mensagem processada
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        return properties;
    }
}
