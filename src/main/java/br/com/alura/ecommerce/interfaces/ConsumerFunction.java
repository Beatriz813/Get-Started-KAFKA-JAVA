package br.com.alura.ecommerce.interfaces;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerFunction<T> {
    void calback(ConsumerRecord<String, T> record);
}
