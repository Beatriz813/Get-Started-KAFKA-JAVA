package br.com.alura.ecommerce.interfaces;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public interface ConsumerFunction<T> {
    void calback(ConsumerRecord<String, T> record) throws ExecutionException, InterruptedException;
}
