package br.com.alura.ecommerce.ordermodule;


import br.com.alura.ecommerce.KafkaDispatcher;
import br.com.alura.ecommerce.Modelos.Email;
import br.com.alura.ecommerce.ordermodule.modelos.Order;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var orderKafkaDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<Email>()) {
                for (var i = 0; i < 10; i++) {
                    var orderId = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 5000 + 1);
                    String emailGerado = Math.random() + "@email.com";
                    var order = new Order(orderId, amount, emailGerado);
                    orderKafkaDispatcher.send("ECOMMERCE_NEW_ORDER", emailGerado, order);

                var email = new Email("Thank you for your order! We are processing your order!");
                emailDispatcher.send("ECOMMERCE_SEND_EMAIL", emailGerado, email);
                }
            }
        }

    }

}
